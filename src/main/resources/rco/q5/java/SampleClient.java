package rco.q5.java;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SampleClient {
	public static void main(String[] args) throws Exception {
		Socket s = new Socket();
		s.connect(new InetSocketAddress(8001));
		InputStream is = s.getInputStream();
		OutputStream os = s.getOutputStream();
		byte[] buf = new byte[100];
		for(int i = 0; i < 10000; i++){
			os.write("AAA\n".getBytes());
			is.read(buf);
		}
		s.close();
	}
}
