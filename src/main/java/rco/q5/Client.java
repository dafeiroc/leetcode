package rco.q5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
  static final Map<Character, List<Character>> map = new HashMap<>();
  static {
    map.put('A', Arrays.asList('A','B'));
    map.put('B', Arrays.asList('A','B','C'));
    map.put('C', Arrays.asList('B','C','D'));
    map.put('D', Arrays.asList('C','D','E'));
    map.put('E', Arrays.asList('D','E','F'));
    map.put('F', Arrays.asList('E','F','G'));
    map.put('G', Arrays.asList('F','G','H'));
    map.put('H', Arrays.asList('G','H','I'));
    map.put('I', Arrays.asList('H','I','J'));
    map.put('J', Arrays.asList('I','J','K'));
    map.put('K', Arrays.asList('J','K','L'));
    map.put('L', Arrays.asList('K','L','M'));
    map.put('M', Arrays.asList('L','M','N'));
    map.put('N', Arrays.asList('M','N','O'));
    map.put('O', Arrays.asList('N','O','P'));
    map.put('P', Arrays.asList('O','P','Q'));
    map.put('Q', Arrays.asList('P','Q','R'));
    map.put('R', Arrays.asList('Q','R','S'));
    map.put('S', Arrays.asList('R','S','T'));
    map.put('T', Arrays.asList('S','T','U'));
    map.put('U', Arrays.asList('T','U','V'));
    map.put('V', Arrays.asList('U','V','W'));
    map.put('W', Arrays.asList('V','W','X'));
    map.put('X', Arrays.asList('W','X','Y'));
    map.put('Y', Arrays.asList('X','Y','Z'));
    map.put('Z', Arrays.asList('Y','Z'));
  }

  static String generateInput() {
    char first = (char) ('A' + Math.random() * 26);
    List<Character> firstVals = map.get(first);
    char second = firstVals.get((int) (Math.random() * firstVals.size()));
    List<Character> secondVals = map.get(second);
    char third = secondVals.get((int) (Math.random() * secondVals.size()));
    return new StringBuilder().append(first).append(second).append(third).toString();
  }

  public static int startFromX(char x, InputStream is, OutputStream os, byte[] buf) throws IOException {
    int minCost = 79;
    String data;
    List<Character> secondVals = map.get(x);
    for (char secondVal : secondVals) {
      List<Character> thirdVals = map.get(secondVal);
      for (char thirdVal : thirdVals) {
        data = new StringBuilder().append(x).append(secondVal).append(thirdVal).toString();
        minCost = Math.min(minCost, cal(is, os, buf, data));
      }
    }
    return minCost;
  }

  public static int findMinCost(InputStream is, OutputStream os, byte[] buf) throws IOException {
    int minCost = 79;
    for (Map.Entry<Character, List<Character>> entry : map.entrySet()) {
      minCost = Math.min(minCost, startFromX(entry.getKey(), is, os, buf));
    }
    return minCost;
  }

  public static void main(String[] args) throws Exception {

    Socket s = new Socket();
    s.connect(new InetSocketAddress(8001));
    InputStream is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    byte[] buf = new byte[100];
    int minCost = findMinCost(is, os, buf);

    System.out.printf("min cost is %d\n", minCost);
//    int sum = 0;
//    int request = 10000;
//    for(int i = 0; i < request; i++){
//      int cost = cal(is, os, buf, generateInput());
//      sum += cost;
//    }
//    System.out.printf("total requests: %d, avg cost is %.2f%n", request, (double) sum / 10000);

    s.close();
  }

  private static int cal(InputStream is, OutputStream os, byte[] buf, String input) throws IOException {
    os.write((input+"\n").getBytes());
    is.read(buf);
    int cost = getCost(buf);
    System.out.printf("%s %d\n", input, cost);
    return cost;

  }


  private static int getCost(byte[] buf) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    while (buf[i] != '\r') {
      int d = buf[i] - '0';
      sb.append(d);
      i++ ;
    }
    return Integer.parseInt(sb.toString());
  }

}
