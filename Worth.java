

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Worth {
	/**
	 * la classe contiene la lista di tutti i progetti creati sull'applicazione
	 * Worth e il loro stato
	 * contiene i metodi per interagire con l'applicazione e la
	 * sua struttura dati
	 */
	
	/**
	 * lista dei progetti contenuti nell'applicazione Worth
	 */
	private LinkedList<Project> projects;
	/**
	 * directory contenente tutti i file di Worth sul file system
	 */
	private static final String DIRNAME = "Worth_project";
	/**
	 * stringa restituita da Worth per segnalare l'errore di progetto
	 * non trovato
	 */
	private static final String PROJECT_NOT_FOUND = "Errore, progetto non esistente";
	/**
	 * stringa restituita da Worth per segnalare l'errore di utente non
	 * membro del progetto
	 */
	private static final String MEMBER_NOT_FOUND = "Errore, l'utente non è un membro del progetto";
	/**
	 * stringa restituita da Worth per segnalare un errore generico dovuto
	 * all'impossibilità di eseguire l'operazione
	 */
	private static final String ERROR_GENERIC = "Impossibile completare l'operazione";
	/**
	 * nome file contenente le info del progetto
	 */
	private static final String INFO = "info.json";
	
	/**
	 * il metodo costruttore controlla se nel file system è contenuta 
	 * una copia persistente di Worth, se si la recupera attraverso
	 * la lettura di file json
	 * recupera tutti i progetti e le relative card
	 */
	public Worth(){
		this.projects = new LinkedList<Project>();
		File dir = new File(DIRNAME);
		if(!dir.exists()) {
    		dir.mkdir();
    	}
    	else if(!dir.isDirectory()) {
    		dir.mkdir();
    	}
		else {
			/*
			 * trovata una copia di Worth nel file system
			 */
			ObjectMapper objectMapper = new ObjectMapper();
			
            File[] listProjects = dir.listFiles();
            /*
             * questa condizione risulterà sempre falsa perché la lista directories
             * contiente SOLO file di tipo directory (a scopo di debugging)
             */
            if (listProjects == null){
                System.out.println("Il file contenuto nella lista non è una directory");
                System.exit(-1);
            }
            /*
             * ogni cartella rappresenta un progetto
             * all'interno di ogni cartella viene recuperato un file info.json
             * contenente le info generali del progetto e tutti gli altri file
             * json che rappresentano i task del progetto
             */
            for (File curr_f : listProjects) {
                if (curr_f.isDirectory()) {
                	File[] filesInCurrentProject = curr_f.listFiles();
                	Info i = null;
                	LinkedList<Card> listCards = new LinkedList<Card>();
                	for (File curr_p : filesInCurrentProject) {
                		if(curr_p.isFile() && curr_p.getName().equals(INFO)) {
                			//lettura e recupero file info.json
                			try {
                				 i = objectMapper.readValue(curr_p, Info.class);
                			}
                			catch (IOException e) {
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}
                		}
                		//lettura e recupero dei task
                		else if(curr_p.isFile() && curr_p.getName().endsWith(".json")) {
                			try {
               				 Card c = objectMapper.readValue(curr_p, Card.class);
               				 listCards.add(c);
                			}
                			catch (IOException e) {
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}                 			
                		}
                	}
                	//dopo aver recuperato i dati, viene ricreata la struttura dati
                	Project p = new Project(i, listCards);
                	this.projects.add(p);		
                }
            } 
            
        }
	}
	
	/**
	 * restituisce una stringa contenente il nome di tutti i progetti
	 * all'interno di Worth
	 * @return stringa contenente il risultato dell'operazione
	 */
	public String getProjects() {
		StringBuilder output = new StringBuilder();
		if(this.projects.isEmpty())
			return "Nessun progetto presente";
		for(Project p : this.projects) {
			output.append(p.getName()).append("\n");
		}
		return output.toString();
	}
		
	/**
	 * 
	 * @param name nome del progetto
	 * @return true se l'oggetto è presente, false altrimenti
	 */
	public Project containsProject(String name) {
		Project found = null;
		for(Project p : this.projects) {
			if(p.getName().equals(name))
				found = p;
		}
		return found;
	}
	
	/**
	 * crea il nuovo progetto name se non è già presente un'altro progetto con
	 * lo stesso nome, l'utente che effettua l'operazione viene aggiunto come
	 * primo membro del progetto
	 * @param name nome del progetto
	 * @param creator nome del creatore
	 * @return stringa contenente il risultato dell'operazione
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public String createProject(String name, String creator) throws IllegalArgumentException, IOException {
		if(this.containsProject(name) != null)
			return "Progetto "+name+" già esistente";
		Project p = new Project(name, creator);
		this.projects.add(p);
		this.writeProject(p);
		return "Progetto "+name+" creato con successo da "+creator;
	}
	
	/**
	 * operazione per recuperare la lista dei progetti di cui l’utente è membro
	 * @param name username dell'utente
	 * @return stringa contenente il risultato dell'operazione
	 */
	public String listProjects(String name) {
		StringBuilder output = new StringBuilder();
		for(Project p : this.projects) {
			if(p.containsMember(name))
				output.append("Nome progetto: ").append(p.getName()).append("\n");
		}
		if(output.length() == 0)
			return "L'utente "+name+" non fa parte di nessun progetto";
		else 
			return output.toString();
	}
	
	/**
	 * aggiungi un membro al progetto se non è già presente
	 * e se il progetto esiste
	 * @param name nome del progetto
	 * @param member username dell'utente da aggiungere
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public String addMember(String name, String member, String user) throws IllegalArgumentException, IOException {
		Project c = this.containsProject(name);
		if(c!=null)
			if(!c.containsMember(user)) {
				return MEMBER_NOT_FOUND;
			}
			else if(c.containsMember(member)) {
				return "L'utente è già un membro del progetto";
			}
			else {
				c.addMember(member);
				this.writeProject(c);
				return "L'utente "+member+" è stato aggiunto con successo al progetto "+name;
		}
		else {
			return ERROR_GENERIC;
		}
	}
	
	/**
	 * restituisce la lista dei membri appartenenti ad un progetto
	 * @param name nome del progetto
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 */
	public String showMembers(String name, String user) {
		Project p = this.containsProject(name);
		if(p == null) 
			return PROJECT_NOT_FOUND;
		if(!p.containsMember(user))
			return MEMBER_NOT_FOUND;
		LinkedList<String> listMembers = p.getMembers();
		StringBuilder output = new StringBuilder();
		for(String u : listMembers) {
				output.append("Utente: ").append(u).append("\n");
		}
		return output.toString();
	}
	
	/**
	 * restituisce il nome di tutte le card associate ad un progetto
	 * @param name nome del progetto
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 */
	public String showCards(String name, String user) {
		StringBuilder output = new StringBuilder();
		Project p = this.containsProject(name);
		if(p == null) 
			return PROJECT_NOT_FOUND;
		if(!p.containsMember(user))
			return MEMBER_NOT_FOUND;
		for(Card c : p.getCards()) {
			output.append("Nome task: ").append(c.getName()).append("\n");
		}
		if(output.length() == 0)
			return "Nessuna carta associata al progetto "+name;
		else
			return output.toString();
	}
	
	/**
	 * restituisce l'intera descrizione della carta all'interno del progetto
	 * @param name nome del progetto
	 * @param card nome della card di cui è richiesta la descrizione
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 */
	public String showCard(String name, String card, String user) {
		StringBuilder output = new StringBuilder();
		Project p = this.containsProject(name);
		if(p == null) 
			return PROJECT_NOT_FOUND;
		if(!p.containsMember(user))
			return MEMBER_NOT_FOUND;
		Card c = p.containsCard(card);
		if(c != null) {
			output.append(c.getName()).append("\n");
			output.append(c.getDescrizione()).append("\n");
			output.append(c.getType()).append("\n");
			return output.toString();
		} else {
			return ERROR_GENERIC;
		}
		
	}
	
	/**
	 * aggiungi la card al progetto se non ne è già presente un'altra
	 * con lo stesso nome
	 * @param name nome del progetto
	 * @param card nome della card
	 * @param desc breve descrizione della card
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public String addCard(String name, String card, String desc, String user) throws IllegalArgumentException, IOException {
		Project p = this.containsProject(name);
		if(p == null) 
			return PROJECT_NOT_FOUND;
		if(!p.containsMember(user))
			return MEMBER_NOT_FOUND;
		if(p.containsCard(card) == null) {
			Card c = new Card(card, desc);
			this.containsProject(name).addCard(c);
			this.writeCard(name, c);
			return "Card "+card+" aggiunta con successo al progetto "+name;
		}
		else return ERROR_GENERIC;
	}
	

	/**
	 * muove la card dallo stato del flusso di lavoro attuale a quello
	 * richiesto nell'operazione se vengono rispettati i tutti i vincoli
	 * @param name nome del progetto
	 * @param card nome della card
	 * @param destination nuovo stato del flusso di lavoro della card
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public String moveCard(String name, String card, String destination, String user) throws IllegalArgumentException, IOException {
		Project p = this.containsProject(name);
		boolean valid;
		if(p == null) 
			return PROJECT_NOT_FOUND;
		if(!p.containsMember(user))
			return MEMBER_NOT_FOUND;
		Card c = p.containsCard(card);
		if(c == null) {
			return ERROR_GENERIC;
		}
		String start = c.getType();
		if(start.equals("TODO") && destination.equals("INPROGRESS")) {
			p.moveCard(c, destination);
			this.writeCard(name, c);
			valid = true;
		}
		else if(start.equals("INPROGRESS") && (destination.equals("TOBEREVISED") || 
				destination.equals("DONE"))) {
			p.moveCard(c, destination);
			this.writeCard(name, c);
			valid = true;
		}
		else if(start.equals("TOBEREVISED") && (destination.equals("INPROGRESS") || 
				destination.equals("DONE"))) {
			p.moveCard(c, destination);
			this.writeCard(name, c);
			valid = true;
		}
		else valid = false;
		
		if(valid) {
			String msg = "Card "+name+" è stata spostata correttamente da "+start+" a "+destination+" ";
			ChatClient chat = new ChatClient(p.getName(), p.getInfo().getAddress());
			chat.sendMessage(msg);
			return "Ok";
		}
		else return ERROR_GENERIC;
	}
	
	/**
	 * restituisce la storia del flusso di lavoro del task
	 * @param name nome del progetto
	 * @param card nome della card
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 */
	public String getHistory(String name, String card, String user) {
		Project p = this.containsProject(name);
		String output = new String();
		if(p == null) 
			return PROJECT_NOT_FOUND;
		if(!p.containsMember(user))
			return MEMBER_NOT_FOUND;
		LinkedList<Card> l = p.getCards();
		for(Card c : l) {
			if(c.getName().equals(card)) {
				 for(String s : c.getHistory()) {
					 output += s;
				 }
			 break;
			}
		}
		return output;
	}
	
	/**
	 * cancella un progetto se tutte le sue card sono 
	 * nella lista done
	 * @param name nome del progetto
	 * @param user username che richiede l'operazione
	 * @return stringa contenente il risultato dell'operazione
	 * @throws UnknownHostException
	 * @throws IllegalArgumentException
	 * @throws InterruptedException
	 */
	public String cancelProject(String name, String user) throws UnknownHostException, IllegalArgumentException, InterruptedException {
		Project p = this.containsProject(name);
		if(p != null) {
			if(!p.containsMember(user))
				return MEMBER_NOT_FOUND;
			else if(p.finish()) {
				ChatClient c = new ChatClient(name, p.getInfo().getAddress());
				c.sendMessage("CLOSE");
				c.join();
				this.projects.remove(p);
				this.deleteProject(DIRNAME+"/"+name);
				return "Progetto "+name+" eliminato con successo";
			}
			else return ERROR_GENERIC;
		}
		else return PROJECT_NOT_FOUND;
	}
	
	/**
	 * scrive i dati del progetto sul file system
	 * viene creata una cartella con il nome del progetto e 
	 * contenente un file con le info di tale progetto
	 * @param p
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void writeProject(Project p) throws IllegalArgumentException, IOException{
		File dir = new File(DIRNAME);
		File dirProject = null;
		File[] listProjects = dir.listFiles();
		for(File f : listProjects) {
			if(f.getName().equals(p.getName())) {
				dirProject = f;
				break;
			}
		}
		if(dirProject == null) {
			dirProject = new File(DIRNAME+"/"+p.getName());
			dirProject.mkdir();
		}

    	ByteBuffer buffer=ByteBuffer.allocateDirect(1024*1024);
		//istanza utilizzata per serializzare una classe in JSON
		ObjectMapper objectMapper = new ObjectMapper();
		Info i = p.getInfo();
		//scrive sul file le info del progetto in formato JSON
		try(WritableByteChannel outChannel= FileChannel.open(Paths.get(DIRNAME+"/"+p.getName()+"/"+INFO), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			buffer.clear();
			//Serializzazione in JSON
			buffer.put(objectMapper.writeValueAsBytes(i));
			buffer.flip();
			while(buffer.hasRemaining()) {
				// scrive il contenuto di buffer nel file
				outChannel.write(buffer);
			}
			outChannel.close();
		}
		
	}
	
	/**
	 * scrive i dati della card sul file system in un
	 * file json
	 * il file creato o sovrascritto all'interno della cartella
	 * del progetto di cui fa parte
	 * @param name nome del progetto
	 * @param c card da scrivere sul file
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public void writeCard(String name, Card c) throws IllegalArgumentException, IOException{
		String path = DIRNAME+"/"+name+"/"+c.getName()+".json";
		ByteBuffer buffer=ByteBuffer.allocateDirect(1024*1024);
		ObjectMapper objectMapper = new ObjectMapper();
		try(WritableByteChannel outChannel= FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			buffer.clear();
			//Serializzazione in JSON
			buffer.put(objectMapper.writeValueAsBytes(c));
			buffer.flip();
			while(buffer.hasRemaining()) {
				// scrive il contenuto di buffer nel file
				outChannel.write(buffer);
			}
			outChannel.close();
		}
	}
	
	/**
	 * elimina la cartella del progetto e tutti i suoi file all'interno
	 * @param path path della cartella da eliminare all'interno del file system
	 */
	public void deleteProject(String path) {
	        File file  = new File(path);
	        if(file.isDirectory()){
	            String[] childFiles = file.list();
	            if(childFiles == null) {
	                //Directory vuota, si può eliminare
	            	file.delete();
	        }
	        else {
	            //Directory con file, per eliminarla
	        	//vanno prima eliminati tutti i suoi files
	        	//all'interno
	            for (String childFilePath :  childFiles) {
	                deleteProject(childFilePath);
	            }
	        }    
	    }
	    else {
	        //se è un semplice file viene eliminato
	        file.delete();
	    }
	 }
	 
	/**
	 * restituisce una stringa contenente l'indirizzo ip
	 * multicast del progetto
	 * @param name nome del progetto
	 * @param member username che richiede l'operazione
	 * @return stringa contenente i risultati dell'operazione
	 */
	 public String getAddressProject(String name, String member) {
		 Project p = this.containsProject(name);
		 if(p!=null) {
			 if(p.containsMember(member))
				 return "IP "+p.getInfo().getAddress();
			 else return MEMBER_NOT_FOUND;
		 }
		 else return PROJECT_NOT_FOUND;
	 }
}
