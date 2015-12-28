package arraystring;

import java.util.Scanner;

public class Main1 {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int x = sc.nextInt();
        int count = 0;
        for(int i = 0; i < 32; i++ ){
            if ((x&1) == 1) {
                count++;
            }
            x = x >>> 1;
        }
        if(count == 1) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }
}
