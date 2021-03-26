

import java.rmi.*;
import java.util.LinkedList;
public interface NotifyEventInterface extends Remote {
	/**
	 * interfaccia del client che espone i metodi di notifica che
	 * devono essere utilizzati dal client e i metodi per accedere
	 * alla struttura dati modificata dalle notifiche
	 */
	
	/**
	 * metodo invocato dal server per notificare l'iscrizione di un nuovo utente 
	 * @param name username del nuovo utente registrato a Worth
	 * @throws RemoteException
	 */
	public void notifyEvent(String name) throws RemoteException;
	
	/**
	 * metodo invocato dal server per notificare il cambiamento dello stato di un utente
	 * @param name username dell'utente che ha aggiornato il suo stato
	 * @param status stato aggiornato dell'utente
	 * @throws RemoteException
	 */
	public void notifyEvent(String name, boolean status) throws RemoteException;
	
	/**
	 * restituisce la lista aggiornata degli utenti registrati e il loro status attuale
	 * @throws RemoteException
	 */
	public void getListUsers() throws RemoteException;
	
	/**
	 * restituisce la lista aggiornata degli utenti registrati online
	 * @throws RemoteException
	 */
	public void getListUsersOnline() throws RemoteException;
	
	/**
	 * setta la lista degli utenti registrati
	 * in seguito alla nuova registrazione di un client al servizio di notifica.
	 * In questo caso la lista viene passata dal server per poi essere aggiornata
	 * solamente tramite il servizio di notifica
	 * @param list lista attuale degli utenti registrati (viene passata dal server)
	 * @throws RemoteException
	 */
	public void setInitList(LinkedList<UtenteStatus> list) throws RemoteException;
	
}
