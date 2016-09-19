package rco.q5;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
  static final int A = 0, Z = 25;
  static final int[][] NWS = new int[3][];
  static {
    ArrayList<Integer> nw = new ArrayList<>();
    for(int c = A; c <= Z; c++) nw.add(c + 1);
    for(int i = 0; i < NWS.length; i++){
      Collections.shuffle(nw);
      NWS[i] = new int[nw.size()];
      for(int j = 0; j < nw.size(); j++) NWS[i][j] = nw.get(j);
    }
  }

  public static void main(String[] args) throws Exception {
    int l = 8001, n = 100000, p = 10000;
    for(int i = 0; i < args.length; i++){
      if(args[i].equals("-l")){
        l = Integer.parseInt(args[++i]);
      }else if(args[i].equals("-n")){
        n = Integer.parseInt(args[++i]);
      }else if(args[i].equals("-p")){
        p = Integer.parseInt(args[++i]);
      }
    }

    ServerSocket ss = new ServerSocket(l);
    System.out.println("listening: " + l);
    Socket s;
    while((s = ss.accept()) != null){
      InputStream is = s.getInputStream();
      OutputStream os = s.getOutputStream();
      byte[] buf = new byte[100];
      long req = 0, sum = 0;
      while(is.read(buf) > 0){
        int c = req(buf);
        System.out.printf("%d\n", c);
        os.write((c + "\r\n").getBytes());
        sum += c;
        if(++req % p == 0) System.out.printf(
            "req: %d, avg: %.2f%n", req, (double) sum / req);
        if(req >= n) break;
      }
      s.close();
    }
    ss.close();
  }

  static int req(byte[] buf) {
    int c = 0, l = A, r = Z;
    for(int i = 0; i < NWS.length; i++){
      int j = buf[i] - 'A';
      if(j < l || j > r) throw new RuntimeException(new String(buf));
      if((l = j - 1) < A) l = A;
      if((r = j + 1) > Z) r = Z;
      int[] nw = NWS[i];
      c += nw[j];
      swap(nw);
    }
    return c;
  }

  static void swap(int[] nw) {
    int i = (int) ((Z + 1) * Math.random()), j = i, c = nw[i];
    while(j == i) j = (int) ((Z + 1) * Math.random());
    nw[i] = nw[j];
    nw[j] = c;
  }
}
