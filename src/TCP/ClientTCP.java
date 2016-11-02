package TCP;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public class ClientTCP extends Thread {
	private Socket clientSocket = null;
	public ClientTCP (String adrIP, int port){
		try {
			clientSocket = new Socket(InetAddress.getByName(adrIP), port);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void run () {
		int MAXLENGTH=256;
		byte[ ] buff = new byte[MAXLENGTH];
		String output = "";
		try {
			InputStream in = clientSocket.getInputStream();
			in.read(buff);
			output = new String(buff);
			System.out.println(output);
		} catch (Exception e) {
		    System.out.println("Error occured while server creation");
		}
	}
}
