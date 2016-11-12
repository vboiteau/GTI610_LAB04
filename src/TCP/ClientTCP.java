package TCP;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class ClientTCP{
	private Socket clientSocket = null;
	public ClientTCP (String adrIP, int port){
		try {
			String outputString = "";
			BufferedReader inputPrompt = new BufferedReader( new InputStreamReader(System.in));
			System.out.println("Say someting:");
			String inputSentence = inputPrompt.readLine();
			clientSocket = new Socket(InetAddress.getByName(adrIP), port);
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output.writeBytes(inputSentence + '\n');
			outputString = input.readLine();
			close();
			Date date= new Date();
			System.out.println("["+new Timestamp(date.getTime())+"]\t" + outputString);
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
