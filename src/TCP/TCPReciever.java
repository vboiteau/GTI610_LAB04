package TCP;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class TCPReciever extends Thread {
    protected int port;
    protected ServerSocket server =null;
    
    public TCPReciever(int Port) {
        port = Port;
        System.out.println(this.port);
        try {
            server = new ServerSocket(this.port); 
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void run () {
		try {
		    while(true){
			    Socket connexionSocket = this.server.accept();
		    	BufferedReader input = new BufferedReader(new InputStreamReader(connexionSocket.getInputStream()));
		        DataOutputStream output = new DataOutputStream(connexionSocket.getOutputStream());
		        String inputString = input.readLine();
		        String clientString = connexionSocket.getRemoteSocketAddress().toString().substring(1);
		        output.writeBytes(inputString.toUpperCase()+"\t"+clientString+"\n");
		    }
		} catch (Exception e) {
			System.out.println(e);
		}
    }
};
