package rco.q6.java.src.dhm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Card {
	public static final Map<String, String> COLOR = new HashMap<String, String>();
	public static final String D3 = "d3", JKR = "JKR";
	private static final Map<String, Card> MAP = new HashMap<String, Card>();
	public static final List<String> NAMES = new ArrayList<String>();
	public static final String[] NCHRS = "3,4,5,6,7,8,9,10,J,Q,K,A,2".split(",");
	private static final StringBuilder SB = new StringBuilder();
	public static final String[] SUITS = {"s", "c", "d", "h"};
	static {
		String[] color = {"44", "42", "41", "45"};
		for(int i = 0; i <= 3; i++) COLOR.put(SUITS[i], "\u001B[" + color[i] + 'm');
		Map<String, Integer> nMap = new HashMap<String, Integer>();
		for(int i = 0; i < NCHRS.length; i++) nMap.put(NCHRS[i], i + 3);
		for(String nc : NCHRS){
			int n = nMap.get(nc);
			for(String s : SUITS) new Card(s + nc, n, s);
		}
		new Card("JKR", 16, null);
	}
	
	public static String join(Object j, Collection<String> args){ //util
		SB.setLength(0);
		for(String s : args) SB.append(s).append(j);
		SB.setLength(SB.length() - 1);
		return SB.toString();
	}

	public static String join(Object j, Object[] args){ //util
		SB.setLength(0);
		for(Object o : args) SB.append(o).append(j);
		SB.setLength(SB.length() - 1);
		return SB.toString();
	}

	public static Card load(String cn) throws Exception {
		Card c = MAP.get(cn);
		if(c == null) throw new Exception(cn);
		return c;
	}

	private String name;
	private int num;
	private String suit;

	Card(String name, int num, String suit){
		this.name = name;
		this.num = num;
		this.suit = suit;
		MAP.put(name, this);
		NAMES.add(name);
	}

	public String name() { return name; };

	public int num() { return num; }

	public String suit() { return suit; }
}
