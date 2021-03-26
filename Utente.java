

public class Utente {
	/**
	 * classe che rappresenta un utente registrato all'applicazione Worth
	 */
	
	/**
	 * username dell'utente
	 */
	private String name;
	/**
	 * password dell'utente
	 */
	private String password;
	
	public Utente() {
		super();
	}
	/**
	 * metodo costruttore
	 * @param _name username dell'utente
	 * @param _password password dell'utente
	 */
	public Utente(String _name, String _password) {
		this.name = _name;
		this.password = _password;
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
	 * @return stringa contenente la password dell'utente
	 */
	public String getPassword() {
		return this.password;
	}
	
}
