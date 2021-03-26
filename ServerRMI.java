

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server RMI che espone varie operazioni al client:
 * -registrazione all'applicazione Worth
 * -login sull'applicazione Worth
 * -lista utenti registrati e il loro status on/off
 * -verifica della registrazione di un utente all'applicazione Worth
 * -registrazione al servizio RMI callback per la notifica della stato
 *  on/off degli utenti registrati
 * -disiscrizione al servizio RMI callback di notifica
 * -metodo per l'invio della notifica ai client registrati al servizio RMI callback 
 */
public class ServerRMI {
    
	/**
	 * porta utilizzata dal server di default
	 */
	private static final int PORT_DEFAULT = 5000;

   
    public void start() throws IOException, NotBoundException{
        int port = PORT_DEFAULT;
        
        try {
        	
            EventManager eventManager = new EventManager();
            // esporta l'oggetto
            EventManagerInterface stub = (EventManagerInterface) UnicastRemoteObject.exportObject(eventManager, port);

            // crea il registro
            LocateRegistry.createRegistry(port);
            Registry register = LocateRegistry.getRegistry(port);

            // binding
            register.rebind("EVENT_MANAGER", stub);
            System.out.println("Server RMI è pronto");
            
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
