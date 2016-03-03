package arraystring;

import java.util.Scanner;

public class Main4 {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        int output[] = count(input);
        outO("B", output[1]);
        outO("S",output[0]);
        outO("O",output[2]);

    }

    public static void outO(String s, int x) {
        if(x == 0){
            System.out.print(s);
        } else if(x>0){
            System.out.print(s);
            for(int i=0;i<x;i++){
                System.out.print("o");
            }
        }
        System.out.println();

    }

    public static int[] count(String s) {
        int score[] = new int[3];
        int a = 0;
        int b = 0;
        int c = 0;
        for(int i = 0; i<s.length(); i++) {
            if(s.charAt(i) == 'S'){
                a++;
                if(a == 3) {
                    a = 0;
                    b = 0;
                    c++;
                }
                if(c == 3){
                    c = 0;
                }

            } else if(s.charAt(i) == 'B') {
                b++;
                if(b == 4) {
                    a = 0;
                    b = 0;
                }

            }
        }
        score[0]=a;score[1]=b;score[2]=c;
        return score;
    }
}
