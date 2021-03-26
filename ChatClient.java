

import java.net.UnknownHostException;

public class ChatClient {
	/**
	 * classe per la gestione della chat di un progetto,
	 * contiene i metodi necessari per il corretto utilizzo
	 * della classe Chat
	 */
	
	/**
	 * istanza della chat di progetto
	 */
	private Chat client;
	/**
	 * nome del progetto associato alla chat
	 */
	private String name;
	/**
	 * thread utilizzato per avviare la connessione udp
	 * multicast
	 */
	private Thread t;
	/**
	 * 
	 * @param _name nome del progetto 
	 * @param _address indirizzo ip multicast del progetto
	 * @throws UnknownHostException
	 * @throws IllegalArgumentException
	 */
	public ChatClient(String _name, String _address) throws UnknownHostException, IllegalArgumentException {
		this.name = _name;
		this.client = new Chat(_address);
		this.t = new Thread(this.client);
	}
	/**
	 * 
	 * @return nome del progetto alla quale è associata la chat
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * metodo per avviare la connessione all'indirizzo ip multicast
	 */
	public void start() {
		this.t.start();
	}
	/**
	 * invia il messaggio all'indirizzo ip multicast del client
	 * @param msg
	 */
	public void sendMessage(String msg) {
		this.client.sendMex(msg);
	}
	/**
	 * legge i messaggi ricevuti dal client
	 * @return stringa contenente l'elenco dei messaggi ricevuti
	 */
	public String readChat() {
		return this.client.read();
	}
	/**
	 * chiude la connessione udp e termina il thread
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException {
		this.client.stop();
		this.t.join();
	}

}
