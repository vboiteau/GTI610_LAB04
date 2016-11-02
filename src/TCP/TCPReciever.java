package TCP;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.net.InetAddress;

public class TCPReciever extends Thread {
    private String adrIp = null;
    protected int port;
    protected ServerSocket server =null;
    
    public TCPReciever(String AdrIp, int Port) {
        this.adrIp = AdrIp;
        port = Port;
        System.out.println(this.port);
        try {
            this.server = new ServerSocket(this.port, 50, InetAddress.getByName(this.adrIp)); 
        } catch (Exception e) {
            System.out.println("wait");
        }
    }
    
    public void run () {
	try {
	    String data = "Hello from server";
	    Socket connexionSocket = this.server.accept();
	    OutputStream out = connexionSocket.getOutputStream();
	    out.write(data.getBytes());
	} catch (Exception e) {
	    System.out.println("Error occured while server creation");
	}
    }
};
