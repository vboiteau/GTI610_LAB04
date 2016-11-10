package TCP;

public class ServerTCP {
    public static void main (String[] args) {
		if (args.length>2||args.length==0) {
		    System.out.println("The input format must be 2 arguments long: java ServerTCP IP PORT");
		    System.exit(1);
		}
		String type = (args.length==1?"server":"client");
		int port=0;
		ClientTCP client = null;
		if(type == "server") {
			try {
				port = Integer.parseInt(args[0]);
	    		TCPReciever reciever = new TCPReciever(port);
	    		reciever.start();
			} catch (Exception e) {
				System.out.println(e);
				System.exit(1);
			}
			
	    } else if (type =="client") {
	    	try {
	    		port = Integer.parseInt(args[1]);
    			while (true) {
    				client = new ClientTCP(args[0], port);
    			}
	    	} catch (Exception e) {
	   			System.out.println(e);
	   			System.exit(1);
	   		}
	   		client.close();
	    }
    }
};