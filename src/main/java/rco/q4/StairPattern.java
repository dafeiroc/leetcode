package rco.q4;

import java.math.BigInteger;
import java.util.Scanner;

public class StairPattern {

  /**
   * if m = 2, matrix is
   * 1 1
   * 1 0
   * <p>
   * if m = 3, matrix is
   * 1 1 1
   * 1 0 0
   * 0 1 0
   * <p>
   * if m = 4, matrix is
   * 1 1 1 1
   * 1 0 0 0
   * 0 1 0 0
   * 0 0 1 0
   * <p>
   * ...
   *
   * @param m you can go up at most m stairs every time.
   * @return the 0 1 matrix used to calculate the pow matrix
   */
  public static BigInteger[][] construct01Matrix(int m) {
    BigInteger[][] matrix = new BigInteger[m][m];
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < m; j++) {
        matrix[i][j] = BigInteger.ZERO;
      }
    }
    for (int i = 0; i <= m - 1; i++) {
      matrix[0][i] = BigInteger.ONE;
      if (i <= m - 2) {
        matrix[i + 1][i] = BigInteger.ONE;
      }
    }
    return matrix;
  }

  /**
   * Big integer matrix multiply, for each element.
   *
   * @param a matrix a
   * @param b matrix b
   * @return a * b
   */
  public static BigInteger[][] multiply(BigInteger[][] a, BigInteger[][] b) {
    BigInteger[][] c = new BigInteger[a.length][b[0].length];
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < b[0].length; j++) {
        c[i][j] = BigInteger.ZERO;
      }
    }
    for (int i = 0; i < a.length; i++) {
      for (int j = 0; j < b[0].length; j++) {
        BigInteger temp = BigInteger.ZERO;
        for (int k = 0; k < b.length; k++) {
          temp = temp.add(a[i][k].multiply(b[k][j]));
        }
        c[i][j] = temp;
      }
    }
    return c;

  }

  /**
   * Calculate the pow of matrix.
   *
   * @param a   big integer matrix
   * @param pow max value is @{code Long.MAX_VALUE} 2^63-1
   * @return
   */
  public static BigInteger[][] multiplyPow(BigInteger[][] a, long pow) {
    BigInteger[][] b = new BigInteger[a.length][a.length];
    for (int i = 0; i < b.length; i++) {
      for (int j = 0; j < b.length; j++) {
        b[i][j] = BigInteger.ZERO;
      }
    }

    if (pow == 1) {
      return a;
    } else if (pow == 2) {
      return multiply(a, a);
    } else if (pow % 2 == 0) {
      b = multiplyPow(multiply(a, a), pow / 2);
      return b;
    } else {
      b = multiplyPow(multiply(a, a), pow / 2);
      return multiply(b, a);
    }

  }

  public static String cal(long n, int m) {
    if (n == 1) {
      return "1";
    } else if (n == 2) {
      if (m == 1) {
        return "1";
      } else if (m == 2) {
        return "2";
      }
    } else {
      BigInteger[][] matrix = construct01Matrix(m);
      BigInteger[][] result = multiplyPow(matrix, n); // result[0][0] is the total pattern.
      if (result[0][0].bitCount() > 9) {// only output the last 9 digits
        String s = String.valueOf(result[0][0]);
        return s.substring(s.length() - 9, s.length());
      }
      return String.valueOf(result[0][0]);
    }
    throw new IllegalStateException("no answer");
  }


  public static void main(String[] args) {
    System.out.println("please input N: ");
    Scanner scanner = new Scanner(System.in);
    long N = scanner.nextLong(); // Long.MAX_VALUE is 2^63 -1 less than 1*1e18
    System.out.println("please input M: ");
    int M = scanner.nextInt();
    if (M < 1 || M > 10 || N < 1 || N > 1000000000000000000L) {
      throw new IllegalArgumentException(" 1 <= m <= 10 and 1 <= n <= 10^18 is required!");
    }
    System.out.println("total stairs: " + N + ", max jump: " + M);
    System.out.println("total patterns: " + cal(N, M));

  }
}
