

import java.util.LinkedList;

public class Card {
	/**
	 * rappresenta un task da eseguire all'interno del progetto
	 */
	
	/**
	 * nome della card
	 */
	private String name;
	/**
	 * descrizione della card
	 */
	private String descrizione;
	/**
	 * stato del flusso di lavoro della card
	 */
	private String type;
	/**
	 * storia del flusso di lavoro della card 
	 */
	private LinkedList<String> history;
	
	public Card() {
		super();
	}
	/**
	 * 
	 * @param _name nome della card
	 * @param _descrizione descrizione della card
	 */
	public Card(String _name, String _descrizione) {
		this.name = _name;
		this.descrizione = _descrizione;
		this.type = "TODO";
		this.history = new LinkedList<String>();
		this.history.add(this.type);
	}
	
	/**
	 * 
	 * @return nome della card
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * 
	 * @return descrizione della card
	 */
	public String getDescrizione() {
		return this.descrizione;
	}
	/**
	 * 
	 * @return stato della card
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * cambia lo stato del flusso di lavoro della card,
	 * la legittimità di tale operazione viene controllata
	 * ad un livello superiore
	 * @param _type nuovo stato del flusso di lavoro
	 */
	public void setType(String _type) {
		if(this.history == null)
			this.history = new LinkedList<String>();
		this.type = _type;
		this.history.add("->"+_type);
	}
	/**
	 * 
	 * @return lista contenete tutti i cambiamenti di stato 
	 * del flusso di lavoro della card
	 */
	public LinkedList<String> getHistory() {
		return this.history;
	}
	
}
