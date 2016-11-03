package TCP;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;

public class ClientTCP extends Thread {
	private BufferedReader br = null;
	private Socket clientSocket = null;
	public ClientTCP (String adrIP, int port){
		try {
			clientSocket = new Socket(InetAddress.getByName(adrIP), port);
			int MAXLENGTH=256;
			byte[ ] buff = new byte[MAXLENGTH];
			String outputString = "";
			BufferedReader inputPrompt = new BufferedReader( new InputStreamReader(System.in));
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Say someting:");
			String inputSentence = inputPrompt.readLine();
			output.writeBytes(inputSentence + '\n');
			outputString = input.readLine();
			Date date= new Date();
			System.out.println("["+new Timestamp(date.getTime())+"]\t" + outputString);
			clientSocket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public void close(){
		try {
			clientSocket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	};
}
