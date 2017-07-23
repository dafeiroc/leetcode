package arraystring;

import java.util.Scanner;

public class Main0 {
    /**
     * Calculate 11-jinzhi shu recursively
     * @param n
     * @return 11-jinzhi shu
     */
    public static String cal(int n) {
        String s = "";
        String base = "0123456789a";

        if (n == 0) {
            return "";
        } else {
            s = cal(n / 11);
            return s + base.charAt(n % 11);
        }
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        String N = sc.next();

        if (N.length() < 1 || N.length() > 109) {
            System.out.println("N is too large!");
            System.exit(-1);
        } else {
            int n = Integer.parseInt(N);
            System.out.printf("%s", cal(n));
        }
    }

}
