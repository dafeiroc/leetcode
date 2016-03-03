package arraystring;

import java.util.*;

public class Main5 {

    public static int compare(String pass1, String pass2){
        int min = Math.min(pass1.length(), pass2.length());

        for(int i=0; i< min;i++){
            if (Character.compare(pass1.charAt(i), pass2.charAt(i)) == 0)
                continue;
            else
                return Character.compare(pass1.charAt(i), pass2.charAt(i));
        }
        return -1;
    }

    public static String encrypt(String pass, HashMap<String, String> rule){
        StringBuffer result = new StringBuffer();
        for(int i=0; i< pass.length(); i++){
            result.append(rule.get(String.valueOf(pass.charAt(i))));
        }
        return result.toString();
    }

    public static void main(String args[]){
//        System.out.println(compare("tpf", "abde"));

        Scanner sc = new Scanner(System.in);
        int N = Integer.parseInt(sc.nextLine());
        String plainPass[] = new String[N];
        for(int i=0; i<N; i++){
            plainPass[i] = sc.nextLine();
        }
        int Q = Integer.parseInt(sc.nextLine());
        int AB[][] = new int[Q][2];
        //List<HashMap<String, String>> ruleList = new ArrayList();
        for(int i=0; i<Q; i++){
            String line[] = sc.nextLine().split(" ");
            AB[i][0] = Integer.parseInt(line[0]);
            AB[i][1] = Integer.parseInt(line[1]);
            HashMap<String, String> rule = new HashMap<String, String>();
            rule.put("a", String.valueOf(line[2].charAt(0)));
            rule.put("b", String.valueOf(line[2].charAt(1)));
            rule.put("c", String.valueOf(line[2].charAt(2)));
            rule.put("d", String.valueOf(line[2].charAt(3)));
            rule.put("e", String.valueOf(line[2].charAt(4)));
            rule.put("f", String.valueOf(line[2].charAt(5)));
            rule.put("g", String.valueOf(line[2].charAt(6)));
            rule.put("h", String.valueOf(line[2].charAt(7)));
            rule.put("i", String.valueOf(line[2].charAt(8)));
            rule.put("j", String.valueOf(line[2].charAt(9)));
            rule.put("k", String.valueOf(line[2].charAt(10)));
            rule.put("l", String.valueOf(line[2].charAt(11)));
            rule.put("m", String.valueOf(line[2].charAt(12)));
            rule.put("n", String.valueOf(line[2].charAt(13)));
            rule.put("o", String.valueOf(line[2].charAt(14)));
            rule.put("p", String.valueOf(line[2].charAt(15)));
            rule.put("q", String.valueOf(line[2].charAt(16)));
            rule.put("r", String.valueOf(line[2].charAt(17)));
            rule.put("s", String.valueOf(line[2].charAt(18)));
            rule.put("t", String.valueOf(line[2].charAt(19)));
            rule.put("u", String.valueOf(line[2].charAt(20)));
            rule.put("v", String.valueOf(line[2].charAt(21)));
            rule.put("w", String.valueOf(line[2].charAt(22)));
            rule.put("x", String.valueOf(line[2].charAt(23)));
            rule.put("y", String.valueOf(line[2].charAt(24)));
            rule.put("z", String.valueOf(line[2].charAt(25)));


            String encrypt1 = encrypt(plainPass[AB[i][0]-1], rule);
            String encrypt2 = encrypt(plainPass[AB[i][1]-1], rule);
            int flag = compare(encrypt1, encrypt2);
            String temp = new String() ;
            if(flag > 0){
                temp = plainPass[AB[i][1]-1];
                plainPass[AB[i][1]-1] = plainPass[AB[i][0]-1];
                plainPass[AB[i][0]-1] = temp;
            }

        }
        for(int i=0; i<N; i++){
            System.out.println(plainPass[i]);
        }

    }


}
