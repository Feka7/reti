

import java.io.Serializable;

public class UtenteStatus implements Serializable {
	/**
	 * classe che rappresenta lo stato online/offline di un utente
	 * registrato all'applicazione Worth
	 */
	
	private static final long serialVersionUID = 1L;
	/**
	 * username dell'utente registrato
	 */
	private String name;
	/**
	 * stato dell'utente
	 * true se online, false altrimenti
	 */
	private boolean status;
	
	/**
	 * metodo costruttore chiamato durante l'avvio del server
	 * o la registrazione di un nuovo utente
	 * lo stato viene automaticamente settato a offline
	 * @param _name username dell'utente registrato
	 */
	public UtenteStatus(String _name) {
		this.name = _name;
		this.status = false;
	}
	
	/**
	 * 
	 * @return stringa contenete lo username dell'utente
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 
	 * @return booleano contenete lo stato dell'utente
	 */
	public boolean getStatus() {
		return this.status;
	}
	
	/**
	 * setta il nuovo stato on/off dell'utente
	 * @param _status nuovo stato dell'utente
	 */
	public void setStatus(boolean _status) {
		this.status = _status;
	}
}
