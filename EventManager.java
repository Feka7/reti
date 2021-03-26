


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.LinkedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * EventManager si occupa della registrazione degli utenti a Worth
 * e dei servizi callback
 *
 */
public class EventManager extends RemoteServer implements EventManagerInterface {
	
	/**
	 * file da cui leggo gli utenti già registrati a WORTH
	 */
	private static final String FILENAME = "lista-utenti-registrati.json";
	/**
	 * directory contenente tutti i file di Worth
	 */
	private static final String DIRNAME = "Worth_project";
	/**
	 * lista utenti registarti
	 */
    private LinkedList<Utente> users;
    /**
     * lista dei clients ai quale viene notificato lo stato aggiornato off/on
     * degli utenti registrati a Worth
     */
    private ArrayList<NotifyEventInterface> clients;
    /**
     * lista aggiornata contenente lo stato aggiornato off/on
     * degli utenti registrati a Worth
     * viene utilizzata solamente durante la registrazione
     * di un client al servizio RMI
     */
    private LinkedList<UtenteStatus> usersStatus;

    /**
     * La classe si occupa di creare un ambiente per ospitare i file di Worth,
     * se invece esso è già presente recupera la lista dei clienti precedentemente iscritti
     * @throws IOException 
     */
    public EventManager() throws IOException {
        this.clients = new ArrayList<NotifyEventInterface>();
    	this.users = new LinkedList<Utente>();
    	this.usersStatus = new LinkedList<UtenteStatus>();
    	
    	//creazione/recupero ambiente
    	File dir = new File(DIRNAME);
    	File file = new File(DIRNAME+"/"+FILENAME);
    	
    	if(!dir.exists()) {
    		dir.mkdir();
    	}
    	else if(!dir.isDirectory()) {
    		dir.mkdir();
    	}
    	
    	if (!file.exists()) {
    		file.createNewFile();
    		return;
    	}
   
    	ByteBuffer buffer=ByteBuffer.allocateDirect(4096);
		String output = new String();
		
		/*se presente all' interno del file system,
		* recupero il contenuto del file e lo memorizzo all'interno di una stringa
		*/
		try (ReadableByteChannel readChannel= FileChannel.open(Paths.get(DIRNAME+"/"+FILENAME), StandardOpenOption.READ);) {
			
			while(readChannel.read(buffer)!=-1) {
				buffer.flip();
				while(buffer.hasRemaining()) {
					 char ch = (char) buffer.get();
		             output += ch;
				}
				buffer.compact();
			}
			readChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode root;
		
		if(!output.isEmpty()) {
			//recupero la lista degli utenti registrati sotto forma di JsonNode
			try {
				root = objectMapper.readTree(output).get("listaUtenti");
				if (root.isArray()) {
					for (JsonNode objNode : root) {
						Utente e = objectMapper.treeToValue(objNode, Utente.class);
						this.users.add(e);
						UtenteStatus u = new UtenteStatus(e.getName());
						this.usersStatus.add(u);
					}
				}
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		
    }

    /**
     * @param username nome dell'utente che si vuole registare
     * @param password password dell'utente
     * @return true se la registrazione avviene, false altrimenti
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public synchronized boolean registerUser(String username, String password) throws IllegalArgumentException, IOException {
        System.out.println("Un utente ha richiesto la registrazione all'evento");
        boolean notFound = true;
        for(Utente user: users) {
        	if(user.getName().equals(username)) { 
        		notFound = false;		//nome utente già registrato
        		break;
        	}
        }
        if(notFound) {
        	Utente e = new Utente(username, password); 					//utente registrato correttamente
        	this.users.add(e);
        	UtenteStatus u = new UtenteStatus(e.getName());
			this.usersStatus.add(u);
			this.update(username);
        	ByteBuffer buffer=ByteBuffer.allocateDirect(1024*1024);
    		//istanza utilizzata per serializzare una classe in JSON
    		ObjectMapper objectMapper = new ObjectMapper();
    		ListaUtenti listaUtenti = new ListaUtenti(this.users);
    		//scrivo sul file l'elenco dei conti correnti in formato JSON
    		try(WritableByteChannel outChannel= FileChannel.open(Paths.get(DIRNAME+"/"+FILENAME), StandardOpenOption.WRITE)) {
    			buffer.clear();
    			//Serializzazione in JSON
    			buffer.put(objectMapper.writeValueAsBytes(listaUtenti));
    			buffer.flip();
    			while(buffer.hasRemaining()) {
    				// scrive il contenuto di buffer nel file
    				outChannel.write(buffer);
    			}
    			outChannel.close();
    		}
        	
        }
        return notFound;
    }
    
    
    public synchronized LinkedList<UtenteStatus> getListUsers() {
        return this.usersStatus;
    }
   
    public synchronized boolean loginUser(String name, String password) {
    	boolean login = false;
    	for (Utente user: users)
    		if(user.getName().equals(name) && user.getPassword().equals(password)) {
    			login = true;
    			break;
    		}
    	return login;
    }
    
    public synchronized boolean checkUser(String name) {
    	boolean found = false;
    	for (Utente user: users)
    		if(user.getName().equals(name)) {
    			found = true;
    			break;
    		}
    	return found;
    }


	@Override
	public synchronized void registerForCallback(NotifyEventInterface ClientInterface, String name) throws RemoteException {
		// TODO Auto-generated method stub
		if (!clients.contains(ClientInterface)) {
			clients.add(ClientInterface);
		}
		this.update(name, true);
	}


	@Override
	public synchronized void unregisterForCallback(NotifyEventInterface ClientInterface, String name) throws RemoteException {
		// TODO Auto-generated method stub
		if (clients.remove(ClientInterface)) {
			this.update(name, false);
			System.out.println("Client disconnesso con successo");
		}
		else
			System.out.println("Impossibile disconnettere il client");
	}
	
	public synchronized void update(String name) throws RemoteException {
		for(NotifyEventInterface client : this.clients) {
			client.notifyEvent(name);
		}
	}
	
	public synchronized void update(String name, boolean status) throws RemoteException {
		for(UtenteStatus u : this.usersStatus) {
			if(u.getName().equals(name)) {
				u.setStatus(status);
				break;
			}
		}
		for(NotifyEventInterface client : this.clients) {
			client.notifyEvent(name, status);
		}
	}	

}