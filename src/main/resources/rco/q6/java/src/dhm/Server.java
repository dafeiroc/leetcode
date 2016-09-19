package rco.q6.java.src.dhm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server {
	class Player {
		Set<String> cSet;
		boolean debug;
		char help;
		int msec;
		String name;
		int rank, rankSum;
		StringBuilder sb;
		Socket sock;
		BufferedReader sr;
		int turn;

		Player(Socket sock, Map<String, Integer> opt) throws Exception {
			cSet = new HashSet<String>(); 
			debug = opt.containsKey("-d");
			this.sock = sock;
			sb = new StringBuilder();
			sr = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}

		String[] cns() {
			String[] cns = new String[cSet.size()];
			int i = 0;
			for(String cn : Card.NAMES) if(cSet.contains(cn)) cns[i++] = cn;
			return cns;
		}

		Player flush(String msg) throws Exception {
			if(msg != null) sb.append(msg);
			sock.getOutputStream().write(sb.toString().getBytes());
			sb.setLength(0);
			return this;
		}

		String[] recv() throws Exception {
			long nt = System.nanoTime();
			flush("<<\r\n");
			String[] cns = sr.readLine().split("[\r\n ,]+");
			msec += (System.nanoTime() - nt) / 1000 / 1000;
			return cns;
		}

		Player send(Object... args) throws Exception {
			String msg = Card.join(' ', args);
			sb.append(msg).append('\r').append('\n');
			if(debug && !msg.startsWith("TURN"))
				System.out.println(">>" + name + ' ' + msg);
			return this;
		}
	}

	static Comparator<Player> RANK_SUM = new Comparator<Player>() {
		public int compare(Player a, Player b) { return a.rankSum - b.rankSum; }
	};

	public static void main(String[] args) throws Exception {
		Map<String, Integer> opt = new HashMap<String, Integer>();
		for(int i = 0; i < args.length; i++)
			opt.put(args[i], args[i].matches("^-[pn]$") ? Integer.parseInt(args[++i]) : 1);
		new Server().listen(opt);
	}

	boolean debug;
	LinkedList<Player> game;
	ArrayList<Player> players;
	long start_at;

	Server bCast(Player x, Object... args) throws Exception {
		String msg = Card.join(' ', args);
		if(debug) System.out.println(msg);
		for(Player p : players){
			if(x != null && p == x) continue;
			p.sb.append(msg).append('\r').append('\n');
		}
		return this;
	}

	void cMove(Player from, Player to, String[] cns) throws Exception {
		for(String cn : cns){
			from.cSet.remove(cn);
			to.cSet.add(cn);
		}
		to.send("DEAL", Card.join(' ', cns));
	}

	Server exch(Player f, Player h, int n) throws Exception {
		String[] cns = h.send("EXCH", -n).recv();
		Set<String> tmp = new HashSet<String>();
		for(String cn : h.cSet) tmp.add(cn);

		if(cns.length != n) cns = null;
		if(cns != null) for(String cn : cns) if(!tmp.remove(cn)) cns = null;
		if(cns != null){
			int max = 0;
			for(String cn : tmp){
				int i = Card.load(cn).num();
				if(i > max) max = i;
			}
			for(String cn : cns) if(Card.load(cn).num() < max) cns = null;
		}
		if(cns == null){
			String[] _cns = h.cns();
			cns = new String[n];
			cns[0] = _cns[_cns.length - 1];
			if(n == 2) cns[1] = _cns[_cns.length - 2];
		}
		cMove(h, f, cns);

		cns = f.send("EXCH", n).recv();
		if(cns.length != n) cns = null;
		if(cns != null) for(String cn : cns) if(!f.cSet.contains(cn)) cns = null;
		if(cns == null){
			String[] _cns = f.cns();
			cns = new String[n];
			cns[0] = _cns[0];
			if(n == 2) cns[1] = _cns[1];
		}
		cMove(f, h, cns);

		return this;
	}

	void game(Player p1) throws Exception {
		for(Player p : players) p.rank = 0;
		LinkedList<Player> ps = game = new LinkedList<Player>(players);
		while(!ps.peek().cSet.contains(Card.D3)) ps.add(ps.poll());

		List<String> args = new ArrayList<String>();
		for(Player p : ps) args.add(p.name + '=' + p.cSet.size());
		bCast(null, "REDY", Card.join(' ', args));

		boolean rev = false;
		while(ps.size() > 1){
			LinkedList<Player> t = new LinkedList<Player>(ps);
			String bind = null;
			CardGroup pre = null;
			Player nextP = null;
			while(t.size() > 1){
				Player p = t.poll();
				p.turn++;

				args.clear();
				args.add(p.name);
				if(p.help > 0){
					List<String> cns = new ArrayList<String>();
					for(String cn : p.cns()){
						if(p.help == '!' && !cn.equals(Card.JKR))
							cn = Card.COLOR.get(cn.substring(0, 1)) + cn + "\u001B[0m";
						cns.add(cn);
					}
					args.add("c=" + Card.join(',', cns));
					if(bind != null) args.add("b=" + bind);
					if(rev) args.add("r=1");
					if(p.help == '!') for(int i = 2; i < args.size(); i++)
						args.set(i, "\u001B[31m" + args.get(i) + "\u001B[0m");
					if(pre != null) args.add("p=" + Card.join(',', pre.cns()));
				}

				String[] cns = p.send("TURN", Card.join(' ', args)).recv();
				if(cns[0].isEmpty()){ //PASS
					bCast(p, "TURN", p.name);
					continue;
				}
				CardGroup now = null;
				String f = null;
				for(String cn : cns) if(!p.cSet.remove(cn)) f = "Not owner";
				if(f == null){
					now = CardGroup.create(cns);
					f = now == null ? "Broken cards" :
						bind != null && now.isBound(bind) ? "Bound " + bind :
							pre != null && !now.isValidFor(pre, rev) ?
									"Invalid for " + Card.join(',', pre.cns()) : null;
				}
				if(f == null && p.cSet.isEmpty() && !now.isFinOk(rev)) f = "Invalid fin";
				if(f != null){
					bCast(null, "FAIL", p.name, f + ": " + Card.join(',', cns)).lose(p);
					if(nextP != null && nextP == p) nextP = t.peek();
					continue;
				}
				bCast(p, "TURN", p.name, Card.join(',', cns));
				if(cns.length >= 4) bCast(null, "REVO", (rev = !rev) ? 1 : 0); //革命
				if(p.cSet.isEmpty()){
					win(p);
					if(p1 != null && p1.rank == 0 && p != p1){ //都落ち
						lose(p1);
						t.remove(p1);
					}
					p1 = null;
					nextP = t.peek();
				}
				if(!now.isSeq() && now.num() == 8 //8切り
						|| pre != null && pre.isJkr() && now.isS3()){ //スペ3
					if(p.rank == 0) nextP = p;
					break;
				}
				if(pre != null && bind == null && !now.hasJkr()){
					String s1 = pre.suits(), s2 = now.suits();
					if(s1 != null && s2 != null && s1.equals(s2))
						bCast(null, "BIND", bind = s1);
				}
				pre = now;
				if(p.rank == 0) t.add(nextP = p);
			}
			bCast(null, "TEND");
			if(nextP != null) while(ps.peek() != nextP) ps.add(ps.poll());
		}
		if(!ps.isEmpty()) lose(ps.poll());
		for(Player p : players) p.flush(null);
	}

	Server gend(Player p, int r) throws Exception {
		bCast(null, "GEND", p.name, p.rank = r);
		game.remove(p);
		return this;
	}

	void listen(Map<String, Integer> opt) throws Exception {
		if(!opt.containsKey("-p")) throw new Exception("No port");
		ServerSocket ss = new ServerSocket(opt.get("-p"));
		players = new ArrayList<Player>();
		while(players.size() < 5){
			Socket s = ss.accept();
			Player p = new Player(s, opt);
			p.sock.getOutputStream().write(("NAME?\r\n").getBytes());
			String nm = p.sr.readLine().trim();
			if(nm.equals("") || nm.matches("[ =]"))
				throw new Exception("Invalid name '" + nm + "'");
			for(Player ex : players) if(ex.name.equals(nm))
				throw new Exception("Existing name '" + nm + "'");
			System.out.println("Accept player '" + nm + "'");
			p.name = nm;
			p.help = nm.endsWith("?") ? '?' : nm.endsWith("!") ? '!' : 0;
			players.add(p);
		}
		ss.close();
		debug = opt.containsKey("-d");
		start(opt.containsKey("-n") ? opt.get("-n") : 10);
	}

	Server lose(Player p) throws Exception {
		Player[] ps = ranking();
		for(int i = 5; i >= 1; i--) if(ps[i - 1] == null) return gend(p, i);
		throw new Exception("?");
	}

	Player[] ranking(){
		Player[] ps = new Player[players.size()];
		for(Player p : players) if(p.rank > 0) ps[p.rank - 1] = p;
		return ps;
	}

	void report(int n) {
		double t = System.currentTimeMillis() - start_at;
		int ms = 0;
		for(Player p : players) ms += p.msec;
		List<Player> ps = new ArrayList<Player>(players);
		Collections.sort(ps, RANK_SUM);
		System.err.println("#GAME " + n);
		for(Player p : ps) System.out.printf(
				"name: %s, avg: %.2f (%.2fsec, %dt, %.2fms/t)\r\n",
				p.name, (double) p.rankSum / n, (double) p.msec / 1000, p.turn, (double) p.msec / p.turn);
		System.out.printf("total: %.2fsec, server: %.2fsec\r\n", t / 1000, (t - ms) / 1000);
	}

	void start(int n) throws Exception {
		if(n == 0) n = 10;
		start_at = System.currentTimeMillis();
		int i = 0;
		while(++i <= n){
			if(i % 10 == 1) Collections.shuffle(players); //席替え
			bCast(null, "GAME", i);

			for(Player p : players) p.cSet.clear();
			LinkedList<String> cns = new LinkedList<String>(Card.NAMES);
			Collections.shuffle(cns);
			while(!cns.isEmpty()){
				for(Player p : players){
					String cn = cns.poll();
					if(cn == null) break;
					p.cSet.add(cn);
				}
			}
			for(Player p : players) p.send("DEAL", Card.join(' ', p.cns()));
			if(i > 1){ //EXCH
				Player[] ps = ranking();
				exch(ps[0], ps[4], 2).exch(ps[1], ps[3], 1).game(ps[0]);
			}else{
				game(null);
			}
			for(Player p : players) p.rankSum += p.rank;
			if(i % 100 == 0) report(i);
		}
		if(n % 100 != 0) report(n);
	}

	Server win(Player p) throws Exception {
		Player[] ps = ranking();
		for(int i = 1; i <= 5; i++) if(ps[i - 1] == null) return gend(p, i);
		throw new Exception("?");
	}
}
