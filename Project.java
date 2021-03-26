

import java.util.LinkedList;

public class Project {
	/**
	 * classe che rappresenta un progetto all'interno di Worth
	 * contiene le info generali e la lista dei task associati
	 */
	
	/**
	 * info generali del progetto
	 */
	private Info info;
	/**
	 * lista dei task nello stato TODO
	 */
	private LinkedList<Card> todo;
	/**
	 * lista dei task nello stato INPROGRESS
	 */
	private LinkedList<Card> inprogress;
	/**
	 * lista dei task nello stato TOBEREVISED
	 */
	private LinkedList<Card> toberevised;
	/**
	 * lista dei task nello stato DONE
	 */
	private LinkedList<Card> done;
	
	/**
	 * metodo costruttore utilizzato durante la creazione di un nuovo progetto
	 * @param _name nome del progetto
	 * @param _member username del creatore
	 */
	public Project(String _name, String _member) {
		Info i = new Info(_name, _member);
		this.info = i;
		this.todo = new LinkedList<Card>();
		this.inprogress = new LinkedList<Card>();
		this.toberevised = new LinkedList<Card>();
		this.done = new LinkedList<Card>();
		
	}
	
	/**
	 * metodo costruttore utilizzato durante il recupero di un
	 * progetto salvato sul file system
	 * @param _i info generali del progetto
	 * @param _cards lista di tutti i task associati al progetto
	 */
	public Project(Info _i, LinkedList<Card> _cards) {
		this.info = _i;
		this.todo = new LinkedList<Card>();
		this.inprogress = new LinkedList<Card>();
		this.toberevised = new LinkedList<Card>();
		this.done = new LinkedList<Card>();
		//ogni task viene aggiunto alla lista corrispondete
		//al suo stato del flusso di lavoro attuale
		for(Card c : _cards) {
			switch(c.getType()) {
				case "TODO":
					this.todo.add(c);
					break;
				case "INPROGRESS":
					this.inprogress.add(c);
					break;
				case "TOBEREVISED":
					this.toberevised.add(c);
					break;
				case "DONE":
					this.done.add(c);
					break;
				default:
					break;
			}
		}
	}
	/**
	 * 
	 * @return Oggetto Info contenete le info generali del progetto
	 */
	public Info getInfo() {
		return this.info;
	}
	
	/**
	 * 
	 * @return stringa contenente il nome del progetto
	 */
	public String getName() {
		return this.info.getName();
	}
	
	/**
	 * 
	 * @return lista di tutti i membri appartenti al progetto
	 */
	public LinkedList<String> getMembers() {
		return this.info.getMembers();
	}
	
	/**
	 * 
	 * @return lista di tutti i task appartenenti al progetto
	 */
	public LinkedList<Card> getCards() {
		LinkedList<Card> all = new LinkedList<Card>();
		for(Card c : this.todo)
			all.add(c);
		for(Card c : this.inprogress)
			all.add(c);
		for(Card c : this.toberevised)
			all.add(c);
		for(Card c : this.done)
			all.add(c);
		return all;
	}
	
	/**
	 * aggiunge un nuovo membro al progetto
	 * @param name username del nuovo membro da aggiungere
	 */
	public void addMember(String name) {
		this.info.addMember(name);
	}
	
	/**
	 * verifica se l'utente è già un membro del progetto
	 * @param name username dell'utente da controllare
	 * @return true se è gia membro, false altrimenti
	 */
	public boolean containsMember(String name) {
		return this.info.containsMember(name);
	}
	
	/**
	 * aggiunge una nuova card al progetto (sarà per forza
	 * nella lista TODO)
	 * @param c Card da aggiungere
	 */
	public void addCard(Card c) {
		this.todo.add(c);
	}
	
	/**
	 * verifica se la card è già presente all'interno del progetto in una qualsiasi lista
	 * e la restituisce
	 * @param name nome della card 
	 * @return Card se presente, altrimenti null
	 */
	public Card containsCard(String name) {
		Card found = null;
		for(Card c : this.todo) {
			if(c.getName().equals(name)) {
				found = c;
				break;
			}
		}
		if(found == null) {
			for(Card c : this.inprogress) {
				if(c.getName().equals(name)) {
					found = c;
					break;
				}
			}
		}
		if(found == null) {
			for(Card c : this.toberevised) {
				if(c.getName().equals(name)) {
					found = c;
					break;
				}
			}
		}
		if(found == null) {
			for(Card c : this.done) {
				if(c.getName().equals(name)) {
					found = c;
					break;
				}
			}
		}
		return found;
	}
	
	
	
	/**
	 * verifica se un progetto può essere eliminato
	 * @return true se può essere eliminato, false altrimenti
	 */
	public boolean finish() {
		if(this.todo.isEmpty() &&
			this.inprogress.isEmpty() &&
			this.toberevised.isEmpty() &&
			!this.done.isEmpty()) return true;
		else return false;
	}
	
	/**
	 * muove la carta da una lista all'altra, la legittimità di tale
	 * operazione non viene controllata all'interno del metodo
	 * @param c card da muovere
	 * @param destination destinazione della card, corrispondente al nuovo
	 * 		  stato del flusso di lavoro
	 */
	public void moveCard(Card c, String destination) {

		switch(c.getType()) {
			case("TODO"):
				this.todo.remove(c);
				break;
			case("INPROGRESS"):
				this.inprogress.remove(c);
				break;
			case("TOBEREVISED"):
				this.toberevised.remove(c);
				break;
			case("DONE"):
				this.done.remove(c);
				break;
		}
		
		c.setType(destination);
		
		switch(destination) {
			case("TODO"):
				this.todo.add(c);
				break;
			case("INPROGRESS"):		
				this.inprogress.add(c);
				break;
			case("TOBEREVISED"):
				this.toberevised.add(c);
				break;
			case("DONE"):
				this.done.add(c);
				break;
		}
	}

}
