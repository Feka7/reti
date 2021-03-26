

import java.rmi.*;
import java.rmi.server.*;
import java.util.LinkedList;

public class NotifyEventImpl extends RemoteObject implements NotifyEventInterface {
	/**
	 * classe che implementa i metodi di notifica che deve utilizzare il server 
	 * per informare il client 
	 * la classe contiene anche i metodi per interagire con la struttura dati
	 * aggiornata dalle notifiche del client
	 */
	
	/**
	 * lista aggiornata contenete gli utenti registrati
	 * e il loro attuale status on/off
	 */
	private LinkedList<UtenteStatus> listStatus;
	
	/* crea un nuovo callback client */
	public NotifyEventImpl() throws RemoteException { 
		super();
		this.listStatus = new LinkedList<UtenteStatus>();
	}
	
	/*registrazione di un nuovo utente*/
	@Override
	public void notifyEvent(String name) throws RemoteException {
		// TODO Auto-generated method stub
		UtenteStatus u = new UtenteStatus(name);
		this.listStatus.add(u);
	}
	
	/*cambio stato on/off di un utente*/
	@Override
	public void notifyEvent(String name, boolean status) throws RemoteException {
		// TODO Auto-generated method stub
		for(UtenteStatus u : this.listStatus) {
			if(u.getName().equals(name)) {
				u.setStatus(status);
				break;
			}	
		}	
	}
	
	/*metodo utilizzato dopo la registrazione al servizio RMI callback*/
	public void setInitList(LinkedList<UtenteStatus> list) throws RemoteException {
		this.listStatus = list;
	}
	
	/*restituisce la lista degli utenti e il loro stato on/off*/
	public void getListUsers() throws RemoteException {
		String status = null;
		for(UtenteStatus u : this.listStatus) {
			if(u.getStatus()) 
				status = "online";
			else status = "offline";
			System.out.println("<Utente: "+u.getName()+", status: "+status);
		}
	}
	
	/*restituisce la lista degli utenti con stato on*/
	public void getListUsersOnline() throws RemoteException {
		for(UtenteStatus u : this.listStatus) {
			if(u.getStatus())
				System.out.println(u.getName());
		}
	}
	
}
