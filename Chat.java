


import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Chat implements Runnable {
	/**
	 * la classe viene utilizzata per implementare la
	 * chat del progetto
	 */
	
	/**
	 * dimensione del buffer di lettura
	 */
	private final int BUFFER_SIZE = 1024;
    /**
     * porta su cui è in ascolto il server
     */
    private final int PORT = 30000;
    /**
     * nome del gruppo multicast
     */
    private final InetAddress welcomeGroup;
    /**
     * flag per l'abbandono al gruppo multicast
     */
    private boolean go;
    /**
     * lista contenente tutti i messaggi ricevuti sul
     * gruppo multicast
     */
    private LinkedList<String> notify;
    /**
     * 
     * @param addr indirizzo ip del gruppo multicast
     * @throws UnknownHostException
     * @throws IllegalArgumentException
     */
   public Chat(String addr) throws UnknownHostException, IllegalArgumentException{
       this.welcomeGroup = InetAddress.getByName(addr);
       // verifica che l'indirizzo passato come argomento sia valido
       if (!this.welcomeGroup.isMulticastAddress())
           throw new IllegalArgumentException();
       this.notify = new LinkedList<String>();
       this.go = true;
   }
   
   /**
    * esegue la join al gruppo multicast e rimane in ascolto
    * per ricevere i messaggi. Ogni messaggio ricevuto viene
    * aggiunto alla lista notify. Questo metodo è un task
    * da far eseguire ad un thread
    */
   @SuppressWarnings("deprecation")
	public void run() {
	   //join all'indirizzo multicast
       try (MulticastSocket multicastWelcome = new MulticastSocket(PORT)){
       	multicastWelcome.joinGroup(this.welcomeGroup);
       		//rimane attivo fino al cambiamento del flag
       		while(this.go) {
       			//buffer ricezione del pacchetto
	            byte[] buffer = new byte[BUFFER_SIZE];
	            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
	            // rimane in attesa della risposta del server
	            multicastWelcome.receive(receivedPacket);
	            //ricezione e lettura del pacchetto
	            String out = new String(receivedPacket.getData(),  0, receivedPacket.getLength());
				//richiede la chiusura connessione multicast
	            if(out.equals("CLOSE")) {
	            	this.stop();
				}
	            /*chiusura della connessione all'indirizzo multicast
	             * già effettuata
	             */
				else if(out.equals("END"))
					continue;
	            /*
	             * scrittura del pacchetto ricevuto nella lista notify
	             */
				else
					this.write(out);
       		}
       }
       catch (BindException e){
           System.out.println("Porta già occupata");
       }
       catch (IOException e) {         // NB: SocketException è una sottoclasse di IOException
           e.printStackTrace();
       }
   }
   
   /**
    * aggiunge il messaggio alla lista notify
    * @param msg messaggio ricevuto sul gruppo multicast
    */
   private synchronized void write(String msg) {
   	this.notify.add(msg);
   }
   /**
    * lettura della lista notify, dopodichè viene svuotata
    * @return contenuto della lista notify
    */
   public synchronized String read() {
	   StringBuilder output = new StringBuilder();
	   	for(String s : this.notify)
	   		output.append(s).append("\n");
	   	this.notify.clear();
	   	return output.toString();
   }
   /**
    * richiede la chiusura della connessione multicast
    * viene inviato un messaggio per non far rimanere
    * il client in ascolto in stallo
    */
   public synchronized void stop() {
   	this.go = false;
   	this.sendMex("END");
   }
   /**
    * invia il messaggio sul gruppo multicast
    * @param msg messaggio da inviare sul gruppo multicast
    */
   public void sendMex(String msg) {
   	try (DatagramSocket serverSocket = new DatagramSocket() ){
               //creazione pacchetto contenete il messaggio
               DatagramPacket packetToSend = new DatagramPacket(
                       msg.getBytes(),
                       msg.length(),
                       this.welcomeGroup,
                       PORT
               );
               //invio pacchetto
               serverSocket.send(packetToSend);
       }
       catch (BindException e){
           System.out.println("Porta già occupata");
       }
       catch (IOException e) {         // NB: SocketException è una sottoclasse di IOException
           e.printStackTrace();
       }
   }

}
