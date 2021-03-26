

import java.util.LinkedList;

public class ListaUtenti {
	/**
	 * classe contenente la lista degli utenti registrati a Worth
	 */
	
	/**
	 * lista utenti registrati
	 */
	private LinkedList<Utente> listaUtenti;
	
	public ListaUtenti(LinkedList<Utente> _listaUtenti) {
		this.listaUtenti = new LinkedList<Utente>(_listaUtenti);	
	}
	/**
	 * 
	 * @return restituisce la lista degli utenti registrati
	 */
	public LinkedList<Utente> getListaUtenti() {
		return this.listaUtenti;
	}
}
