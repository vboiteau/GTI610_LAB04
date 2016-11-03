import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Cette classe permet la reception d'un paquet UDP sur le port de reception
 * UDP/DNS. Elle analyse le paquet et extrait le hostname
 * 
 * Il s'agit d'un Thread qui ecoute en permanance pour ne pas affecter le
 * deroulement du programme
 * 
 * @author Max
 *
 */

public class UDPReceiver extends Thread {
	/**
	 * Les champs d'un Packet UDP 
	 * --------------------------
	 * En-tete (12 octects) 
	 * Question : l'adresse demande 
	 * Reponse : l'adresse IP
	 * Autorite :
	 * info sur le serveur d'autorite 
	 * Additionnel : information supplementaire
	 */

	/**
	 * Definition de l'En-tete d'un Packet UDP
	 * --------------------------------------- 
	 * Identifiant Parametres 
	 * QDcount
	 * Ancount
	 * NScount 
	 * ARcount
	 * 
	 * L'identifiant est un entier permettant d'identifier la requete. 
	 * parametres contient les champs suivant : 
	 * 		QR (1 bit) : indique si le message est une question (0) ou une reponse (1). 
	 * 		OPCODE (4 bits) : type de la requete (0000 pour une requete simple). 
	 * 		AA (1 bit) : le serveur qui a fourni la reponse a-t-il autorite sur le domaine? 
	 * 		TC (1 bit) : indique si le message est tronque.
	 *		RD (1 bit) : demande d'une requete recursive. 
	 * 		RA (1 bit) : indique que le serveur peut faire une demande recursive. 
	 *		UNUSED, AD, CD (1 bit chacun) : non utilises. 
	 * 		RCODE (4 bits) : code de retour.
	 *                       0 : OK, 1 : erreur sur le format de la requete,
	 *                       2: probleme du serveur, 3 : nom de domaine non trouve (valide seulement si AA), 
	 *                       4 : requete non supportee, 5 : le serveur refuse de repondre (raisons de s�ecurite ou autres).
	 * QDCount : nombre de questions. 
	 * ANCount, NSCount, ARCount : nombre d�entrees dans les champs �Reponse�, Autorite,  Additionnel.
	 */

	protected final static int BUF_SIZE = 1024;
	protected String SERVER_DNS = null;//serveur de redirection (ip)
	protected int portRedirect = 53; // port  de redirection (par defaut)
	protected int port; // port de r�ception
	private String adrIP = null; //bind ip d'ecoute
	private String domainName = "none";
	private String DNSFile = null;
	private boolean RedirectionSeulement = false;
	
	private class ClientInfo { //quick container
		public String client_ip = null;
		public int client_port = 0;
	};
	private HashMap<Integer, ClientInfo> Clients = new HashMap<>();
	
	private boolean stop = false;

	
	public UDPReceiver() {
	}

	public UDPReceiver(String SERVER_DNS, int Port) {
		this.SERVER_DNS = SERVER_DNS;
		this.port = Port;
	}
	
	
	public void setport(int p) {
		this.port = p;
	}

	public void setRedirectionSeulement(boolean b) {
		this.RedirectionSeulement = b;
	}

	public String gethostNameFromPacket() {
		return domainName;
	}

	public String getAdrIP() {
		return adrIP;
	}

	private void setAdrIP(String ip) {
		adrIP = ip;
	}

	public String getSERVER_DNS() {
		return SERVER_DNS;
	}

	public void setSERVER_DNS(String server_dns) {
		this.SERVER_DNS = server_dns;
	}



	public void setDNSFile(String filename) {
		DNSFile = filename;
	}

	public void run() {
		try {
			DatagramSocket serveur = new DatagramSocket(this.port); // *Creation d'un socket UDP
		
			
			// *Boucle infinie de recpetion
			while (!this.stop) {
				byte[] buff = new byte[0xFF];
				DatagramPacket paquetRecu = new DatagramPacket(buff,buff.length);
				System.out.println("Serveur DNS  "+serveur.getLocalAddress()+"  en attente sur le port: "+ serveur.getLocalPort());

				// *Reception d'un paquet UDP via le socket
				serveur.receive(paquetRecu);
				
				System.out.println("paquet recu du  "+paquetRecu.getAddress()+"  du port: "+ paquetRecu.getPort());
				

				// *Creation d'un DataInputStream ou ByteArrayInputStream pour
				// manipuler les bytes du paquet

				ByteArrayInputStream tabInputStream = new ByteArrayInputStream (paquetRecu.getData());
				
				System.out.println(Arrays.toString(buff));
				tabInputStream.read(buff);
				String input = new String(buff);
				int lengthName = buff[12];
				int lengthExtention = buff[12+lengthName+1];
				int length;
				
				int current = 12;
				domainName = new String();
				
				while(true)
				{
					length = buff[current];
					for (int i = current+1; i < current+length+1; i++){
						domainName += (char)buff[i];
					}
					current = current + length + 1;
					if(buff[current]==0){
						break;
					}
					domainName += ".";
				}
				QueryFinder qF = new QueryFinder(DNSFile);
				AnswerRecorder aR = new AnswerRecorder(DNSFile);
				// ****** Dans le cas d'un paquet requete (0) ***** QR = 17 bits
				if (((buff[2] >> 7) & 1) == 0)
				{
					// *Lecture du Query Domain name, a partir du 13 byte
					
					System.out.println(domainName);
						
					// *Sauvegarde de l'adresse, du port et de l'identifiant de la requete
					String id = String.valueOf((char)buff[0]+(char)buff[1]);
					adrIP = paquetRecu.getAddress().toString();
					port = paquetRecu.getPort();
					
					System.out.println(serveur.getLocalAddress());
					System.out.println(Inet4Address.getByName(SERVER_DNS));

					// *Si le mode est redirection seulement
					if (RedirectionSeulement) {
						// *Rediriger le paquet vers le serveur DNS
						InetAddress dns = Inet4Address.getByName(SERVER_DNS);
						paquetRecu.setAddress(dns);
						paquetRecu.setPort(serveur.getLocalPort());
						serveur.send(paquetRecu);
					}
					// *Sinon
					else {
						// *Rechercher l'adresse IP associe au Query Domain name
						// dans le fichier de correspondance de ce serveur	
						QueryFinder qf = new QueryFinder(DNSFile);
						List<String> list = qf.StartResearch(domainName);

						// *Si la correspondance n'est pas trouvee
						if (list == null) {
							// *Rediriger le paquet vers le serveur DNS
							serveur.send(paquetRecu);
						}
						// *Sinon	
						else {
							// *Creer le paquet de reponse a l'aide du UDPAnswerPaquetCreator
							UDPAnswerPacketCreator creatorUDP = UDPAnswerPacketCreator.getInstance();
							creatorUDP.CreateAnswerPacket(paquetRecu.getData(), list);
							UDPAnswerPacketCreator.Answerpacket answer = creatorUDP.getAnswrpacket();
							// *Placer ce paquet dans le socket
							// *Envoyer le paquet
						}
					}
				}
				// ****** Dans le cas d'un paquet reponse *****
				else {
						// *Lecture du Query Domain name, a partir du 13 byte
					System.out.println("reponse");
					current += 33;
					int ttl = ByteBuffer.wrap(Arrays.copyOfRange(buff, current, current+4)).getInt();
					current += 4;
					/*int rDLength = ByteBuffer.wrap(Arrays.copyOfRange(buff, current, current+2)).getInt();
					current+=2;
					String rData = ByteBuffer.wrap(Arrays.copyOfRange(buff, current, current+rDLength)).toString();
					System.out.format("%s\t%d\t%h\t%s\n", domainName, ttl, rDLength, rData);
					List<String> addresses = qF.StartResearch(domainName);
					boolean found = false;
					if(addresses.size()>0){
						for (String address : addresses) {
							if(address==rData){
								found = true;
							}
						}
					}
					if(!found){
						aR.StartRecord(domainName, rData);
					}
					UDPSender UDPS = new UDPSender(paquetRecu.getAddress().toString(), paquetRecu.getPort(), serveur);
					UDPS.SendPacketNow(paquetRecu);*/
			 
					// *Lecture du Query Domain name, a partir du 13 byte
					
						// *Passe par dessus Type et Class
						
						// *Passe par dessus les premiers champs du ressource record
						// pour arriver au ressource data qui contient l'adresse IP associe
						//  au hostname (dans le fond saut de 16 bytes)
						
						// *Capture de ou des adresse(s) IP (ANCOUNT est le nombre
						// de r�ponses retourn�es)			
					
						// *Ajouter la ou les correspondance(s) dans le fichier DNS
						// si elles ne y sont pas deja
						
						// *Faire parvenir le paquet reponse au demandeur original,
						// ayant emis une requete avec cet identifiant				
						// *Placer ce paquet dans le socket
						// *Envoyer le paquet
				}
			}
//			serveur.close(); //closing server
		} catch (Exception e) {
			System.err.println("Probl�me � l'ex�cution :");
			e.printStackTrace(System.err);
		}
	}
}
