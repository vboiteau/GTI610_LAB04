package TCP;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.net.InetAddress;

public class ServerTCP {
    public static void main (String[] args) {
	if (args.length!=2) {
	    System.out.println("The input format must be 2 arguments long: java ServerTCP IP PORT");
	    System.exit(1);
	}
	int port=0;
	try {
	    port = Integer.parseInt(args[1]);
	} catch (Exception e) {
	    System.out.println("The port argument must be an integer as port");
	    System.exit(1);
	}
	System.out.format("Port:\t%d\n\n", port);
	String data = "Hello from server";
	ClientTCP client = null;
	try {
	    TCPReciever reciever = new TCPReciever(args[0], port);
	    reciever.start();
		while (true) {
			client = new ClientTCP(args[0], port);
		}
	    //Socket connexionSocket = server.accept();
	    //OutputStream out = connexionSocket.getOutputStream();
	    //out.write(data.getBytes());
	} catch (Exception e) {
		System.out.println(e);
	    System.exit(1);
	}
	client.close();
    }
};