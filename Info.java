

import java.util.LinkedList;

public class Info {
	/**
	 * classe che contiene le informazione generiche di un progetto
	 */
	
	/**
	 * nome del progetto
	 */
	private String name;
	/**
	 * indirizzo ip multicast utilizzato per la chat del progetto
	 */
	private String address;
	/**
	 * lista dei membri appartenenti al progetto
	 */
	private LinkedList<String> members;
	
	public Info() {
		super();
	}
	/**
	 * 
	 * @param _name nome del progetto
	 * @param _creator username del creatore del progetto
	 */
	public Info(String _name, String _creator) {
		this.name = _name;
		this.address = RandMulticastAddress.getMulticastAddress();
		this.members = new LinkedList<String>();
		this.members.add(_creator);
	}
	
	/*
	public Info(String _name, String _address, LinkedList<String> _members) {
		this.name = _name;
		this.address = _address;
		this.members = _members;
	}
	*/
	
	/**
	 * 
	 * @return restituisce una stringa contenente il nome del progetto
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * restituisce una stringa contenente l'indirizzo multicast del progetto
	 */
	public String getAddress() {
		return this.address;
	}
	
	/**
	 * restituisce la lista dei membri appartenenti al progetto
	 */
	public LinkedList<String> getMembers() {
		return this.members;
	}
	
	/**
	 * aggiunge l'utente alla lista dei membri del gruppo
	 * @param name username dell'utente 
	 */
	public void addMember(String name) {
		this.members.add(name);
	}
	
	/**
	 * verifica se l'utente è un membro del progetto
	 * @param name username dell'utente
	 * @return true se l'utente è già un membro del progetto, false altrimenti
	 */
	public boolean containsMember(String name) {
		if(this.members.contains(name))
			return true;
		else
			return false;
	}
	
	
}
