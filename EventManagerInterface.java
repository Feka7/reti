

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 * EventManagerInterface rappresenta l'interfaccia offerta al client
 *
 */
public interface EventManagerInterface extends Remote {

    /**
     *
     * @param username nome dell'utente da registrare
     * @param password password associata all'utente da registrare
     * @return      true se l'utente è stato correttamente registrato
     *              false se l'utente era già registrato
     * @throws RemoteException se si verificano durante l'esecuzione della chiamata remota
     * @throws IllegalArgumentException se l'argomento passato risulta invalido
     * @throws IOException 
     */
    boolean registerUser(String username, String password) throws RemoteException, IllegalArgumentException, IOException;

    /**
     *
     * @return lista degli utenti registrati al servizio
     * @throws RemoteException se si verificano durante l'esecuzione della chiamata remota
     */
    LinkedList<UtenteStatus> getListUsers() throws RemoteException;
    
    /**
    * @param username nome dell'utente che vuole loggarsi
    * @param password password associata all'utente
    * @return true se username esiste e la password è corretta, false altrimenti
    * @throws RemoteException se si verificano durante l'esecuzione della chiamata remota
    */
    boolean loginUser(String username, String password) throws RemoteException;
    
    /**
     * @param name nome dell'utente da cercare nel servizio
     * @return true se l'utente è già registrato, false altrimenti
     * @throws RemoteException
     */
    boolean checkUser(String name) throws RemoteException;
    
    /**
     * 
     * @param ClientInterface client che si vuole registare al servizio di notifica
     * @param name username dell'utente loggato sul client
     * @throws RemoteException
     */
    public void registerForCallback (NotifyEventInterface ClientInterface, String name) throws RemoteException;
    
    /**
     * 
     * @param ClientInterface client che vuole disiscriversi dal servizio di notifica
     * @param name nome dell'utente loggato sul client
     * @throws RemoteException
     */
    public void unregisterForCallback (NotifyEventInterface
    ClientInterface, String name) throws RemoteException;
    
    /**
     * aggiorna tutti i client collegati al server RMI che
     * un nuovo utente si è registrato a Worth
     * @param name nome del nuovo utente registrato
     * @throws RemoteException
     */
    public void update(String name) throws RemoteException;
    
    /**
     * 
     * @param name nome dell'utente che ha aggiornato il suo stato on/off
     * @param status stato attuale dell'utente
     * @throws RemoteException
     */
    public void update(String name, boolean status) throws RemoteException;
    
    
}
