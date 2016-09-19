package rco.q6.java.src.dhm.player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import dhm.Card;
import dhm.CardGroup;

public class Zako {
	public static void main(String[] args) throws Exception {
		if(args.length < 1) throw new Exception("No name");
		if(args.length < 2) throw new Exception("No port");
		Zako z = new Zako(args[0], new Socket("127.0.0.1", Integer.parseInt(args[1])));
		z.sr.readLine(); //NAME
		z.writeln(z.name);
		z.run();
	}

	String bind;            //しばり状態
	List<CardGroup> cgs;    //持ち札を分類したもの
	LinkedList<String> cns; //持ち札
	CardGroup fin;          //上がり札
	String name;
	String pre;             //直前のCard
	boolean rev;            //革命状態
	Socket sock;
	BufferedReader sr;

	Zako(String name, Socket sock) throws Exception {
		this.name = name;
		this.sock = sock;
		sr = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		init();
	}

	void addCg(Set<String> cSet, List<String> cns, List<CardGroup> cgs) throws Exception {
		for(String cn : cns) cSet.remove(cn);
		cgs.add(CardGroup.create(cns.toArray(new String[0])));
	}

	List<CardGroup> classify() throws Exception {
		Set<String> cSet = new HashSet<String>(cns);
		List<CardGroup> cgs = new LinkedList<CardGroup>();

		for(String s : Card.SUITS){ //sequence
			for(int i = 0; i <= 11; i++){
				String cn = s + Card.NCHRS[i];
				if(!cSet.contains(cn)) continue;
				List<String> cns = new ArrayList<String>();
				cns.add(cn);
				while(i <= 11 && cSet.contains(cn = s + Card.NCHRS[++i])) cns.add(cn);
				if(cns.size() >= 3) addCg(cSet, cns, cgs);
			}
		}

		for(String nc : Card.NCHRS){ //pair
			List<String> cns = new ArrayList<String>();
			for(String s : Card.SUITS){
				String cn = s + nc;
				if(cSet.contains(cn)) cns.add(cn);
			}
			if(cns.size() >= 2) addCg(cSet, cns, cgs);
		}

		for(String cn : cSet) cgs.add(CardGroup.create(new String[]{cn}));
		return cgs;
	}

	void init() {
		bind = null;
		cgs  = null;
		cns  = new LinkedList<String>();
		fin  = null;
		pre  = null;
		rev  = false;
	}

	void run() throws Exception {
		String line;
		while((line = sr.readLine()) != null){
			String ev = line.substring(0, 4);
			String[] args = line.length() > 4 ? line.substring(5).split(" ") : null;
			if(ev.equals("GAME")){
				init();
			}else if(ev.equals("DEAL")){
				for(String cn : args) cns.add(cn);
			}else if(ev.equals("EXCH")){
				sr.readLine(); //<<
				int n = Integer.parseInt(args[0]);
				int abs = n > 0 ? n : -n;
				List<String> x = new ArrayList<String>();
				for(int i = 1; i <= abs; i++) x.add(n > 0 ? cns.poll() : cns.pollLast());
				writeln(Card.join(' ', x));
			}else if(ev.equals("REDY")){
				CardGroup.sort(cgs = classify(), false);
			}else if(ev.equals("TURN")){
				if(!args[0].equals(name)){
					if(args.length > 1) pre = args[1];
					continue;
				}
				sr.readLine(); //<<
				writeln(select());
			}else if(ev.equals("BIND")){
				bind = args[0];
			}else if(ev.equals("REVO")){
				CardGroup.sort(cgs, rev = args[0].equals("1"));
				setFin();
			}else if(ev.equals("FAIL")){
				if(args[0].equals(name)) System.err.print(line);
			}else if(ev.equals("TEND")){
				pre = null;
				bind = null;
			}else if(ev.equals("GEND")){
			}else{
				throw new Exception(ev);
			}
		}
	}

	String select() throws Exception {
		CardGroup pre = null, cg = null;
		if(this.pre != null) pre = CardGroup.create(this.pre.split(","));
		for(CardGroup _ : cgs){
			if(pre != null && !_.isValidFor(pre, rev)
					|| bind != null && _.isBound(bind)
					|| fin != null && _ == fin) continue;
			cgs.remove(cg = _);
			break;
		}
		if(cg == null) return ""; //PASS
		if(cgs.size() == 1 || cg.isFinOk(rev)) setFin();
		return Card.join(' ', cg.cns());
	}

	CardGroup setFin() { //上がり札予約
		if(cgs.size() == 1) return fin = null;
		CardGroup fin = null;
		for(CardGroup cg : cgs){
			if(!cg.isFinOk(rev)) continue;
			if(fin == null){
				fin = cg;
			}else{
				fin = null;
				break;
			}
		}
		return this.fin = fin;
	}

	void writeln(String msg) throws Exception {
		sock.getOutputStream().write((msg + "\n").getBytes());
	}
}
