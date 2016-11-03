package TCP;

public class ServerTCP {
    public static void main (String[] args) {
	if (args.length!=2) {
	    System.out.println("The input format must be 2 arguments long: java ServerTCP IP PORT");
	    System.exit(1);
	}
	int port=0;
	ClientTCP client = null;
	try {
	    port = Integer.parseInt(args[1]);

	    TCPReciever reciever = new TCPReciever(port);
	    reciever.start();
		while (true) {
			client = new ClientTCP(args[0], port);
		}
	} catch (Exception e) {
		System.out.println(e);
	    System.exit(1);
	}
	client.close();
    }
};