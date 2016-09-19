package rco.q6.java.src.dhm;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class CardGroup {
	static Comparator<CardGroup> C_NORMAL = new Comparator<CardGroup>() {
		public int compare(CardGroup a, CardGroup b) {
			return a.num == b.num ? a.type.compareTo(b.type) : a.num - b.num;
		}
	};

	static Comparator<CardGroup> C_REV = new Comparator<CardGroup>() {
		public int compare(CardGroup a, CardGroup b) {
			return a.num == b.num ? a.type.compareTo(b.type) :
				(a.isJkr() || b.isJkr()) ? a.num - b.num : b.num - a.num;
		}
	};

	public static CardGroup create(String[] cns) throws Exception {
		CardGroup cg = new CardGroup();
		if(cns.length == 0) throw new Exception("No cards");
		cg.cns = cns;
		if(cns.length == 1){
			Card c = Card.load(cns[0]);
			cg.num = c.num();
			cg.suits = c.suit();
			cg.type = "1";
			cg.hasJkr = cg.num == 16;
			return cg;
		}
		Map<String, Card> cMap = new HashMap<String, Card>();
		for(String cn : cns) cMap.put(cn, Card.load(cn));
		cg.hasJkr = (cMap.remove(Card.JKR) != null);
		TreeSet<Integer> nSet = new TreeSet<Integer>();
		TreeSet<String> sSet = new TreeSet<String>();
		for(Card c : cMap.values()){
			nSet.add(c.num());
			sSet.add(c.suit());
		}
		if(nSet.size() == 1){
			cg.num = nSet.first();
			cg.suits = Card.join(',', sSet);
			cg.type = "p" + cns.length;
		}else{
			if(sSet.size() > 1) return null;
			int n = nSet.last() - nSet.first() + 1;
			if(nSet.size() != n && (!cg.hasJkr || nSet.size() != n - 1)) return null;
			cg.num = nSet.first();
			cg.suits = sSet.first();
			cg.type = "s" + cns.length;
		}
		return cg;
	}

	public static void sort(List<CardGroup> cgs, boolean rev) {
		Collections.sort(cgs, rev ? C_REV : C_NORMAL);
	}

	private String[] cns;
	private int num;
	private String suits, type;
	private boolean hasJkr;

	public String[] cns() { return cns; };

	public boolean hasJkr() { return hasJkr; }

	public boolean isBound(String bind) { //しばり判定
		if(suits == null) return false; //JKR
		for(String s : suits.split(",")) if(!bind.contains(s)) return true;
		return false;
	}

	public boolean isFinOk(boolean rev){ //上がりOK
		return isSeq() || !(num == 16 || num == 8 || (rev ? num == 3 : num == 15));
	}

	public boolean isJkr() { return num == 16; }

	public boolean isS3() { return num == 3 && type.equals("1") && suits.equals("s"); }

	public boolean isSeq() { return type.startsWith("s"); }

	public boolean isValidFor(CardGroup pre, boolean rev){
		return !type.equals(pre.type) ? false :
			isJkr() ? !pre.isS3() : pre.isJkr() ? isS3() :
				rev ? num < pre.num : num > pre.num;
	}

	public int num() { return num; };

	public String suits() { return suits; };

	public String type() { return type; };
}
