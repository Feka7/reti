

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class Client {
	/**
	 * porta utilizzata per la connessione RMI
	 */
    private static final int PORT_DEFAULT_RMI = 5000;
    /**
     * porta utilizzata per la connessione TCP
     */
    private static final int PORT_DEFAULT_TCP = 5001;
    /**
     * frase utilizzata come risposta standard
     */
    private static final String NO_LOGIN = "<Operazione non valida, è necessario aver effettuato il login";
    /**
     * true se l'utente ha effettuato il login, false altrimenti
     */
    private boolean connected;
    /**
     * flag che rappresenta l'intenzione dell'utente di uscire dal client 
     */
    private boolean out;
    /**
     * username dell'utente attualmente loggato
     */
    private String user_session;
    /**
     * canale utilizzato dal client per connettersi al server tramite connessione TCP
     */
    private SocketChannel client;
    /**
     * lista delle chat dei progetti richieste dall'utente
     */
    private ArrayList<ChatClient> chat_projects;
    /**
     * dimensione del buffer utilizzato per la lettura
     */
    private final static int BUFFER_DIMENSION = 1024;
    
    public Client() {
    	this.connected = false;		
        this.out = false;								
        this.user_session = new String();
        this.chat_projects = new ArrayList<ChatClient>();
    }
   

    /**
     * registra l'utente attraverso remoteEventManager
     *
     * @param name username da registrare
     * @param password usata per username
     * @param remoteEventManager remote reference (EventManager)
     * @throws IOException 
     */
    private static void registerUser(String name, String password, EventManagerInterface remoteEventManager) throws IOException{
        System.out.println("<Richiedo la registrazione di " + name);
        try {
            if (remoteEventManager.registerUser(name, password)) {
                System.out.printf("<L'utente \"%s\" è stato registrato con successo\n", name);
            }
            else {
                System.out.printf("<Nome utente \"%s\" o password non validi\n", name);
            }
        }
        catch (RemoteException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * restituisce la chat del progetto richiesto se presente
     * @param name nome progetto da cercare
     * @return il progetto se presente, null altrimenti
     */
    private ChatClient getChatProject(String name) {
    	ChatClient c = null;
    	for(ChatClient i : this.chat_projects) {
    		if(i.getName().equals(name))
    			c = i;
    	}
    	return c;
    }
    
    /**
     * metodo utilizzato per l'esecuzione del client
     * @throws InterruptedException
     */
    public void start() throws InterruptedException{
        ChatClient c;
        //buffer nel quale scrivo i comandi
        try (BufferedReader in  = new BufferedReader(new InputStreamReader(System.in))){  
        	// ottiene una reference per il registro
            Registry r = LocateRegistry.getRegistry(PORT_DEFAULT_RMI);
            // ottiene una reference all'event manager
            EventManagerInterface remoteEventManager = (EventManagerInterface) r.lookup("EVENT_MANAGER");
        	//interfaccia client rmi
            NotifyEventInterface callbackObj = new NotifyEventImpl();
            NotifyEventInterface stub = (NotifyEventInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
            System.out.print("Benvenuto! Digita help per vedere la lista dei comandi disponibili\n");
            //ciclo nel quale scrivo i comandi (true da sostituire con variabile)
            while(!out) {
	            System.out.print(">");
	            //input inserito dall'utente
	            String input = in.readLine();
	            //variabile utilizzata per il parsing dell'input
	            StringTokenizer st = new StringTokenizer(input);
	            /**
	             * lettura input: 
	             * viene effettuato un controllo sulla prima parola inserita,
	             * la quale si riferisce sempre alla funzione da eseguire.
	             * Se la funzione non è valida stampa l'errore, altrimenti
	             * esegue la funzione.
	             */
	            try {
	            	switch(st.nextToken()) {
	            		
	            		//chiusura del client
		        		case "quit":
		        			if(!connected) out = true;
		        			break;
		        		//restituisce la lista dei comandi disponibili
		        		case "help":
		        			System.out.println(Help.getHelp());
		        			break;
		        		//registra l'utente nel server
		        		case "register":
		        			registerUser(st.nextToken(), st.nextToken(), remoteEventManager);
		        			break;
		        			
		        		//richiede la lista degli utenti registrati e il loro stato online/offline
		        		case "list_users":
		        			if(!connected) {
		        				System.out.println(NO_LOGIN);
		        			}
		        			else stub.getListUsers();
		        			break;
		        		
		        		//richiede il login al servizio
		        		case "login":
		        			String tmp_user = st.nextToken();
		        			if(connected) {
		        				System.out.println("<C’è un utente già collegato, deve essere prima scollegato");
		        			}
		        			else if(remoteEventManager.loginUser(tmp_user, st.nextToken())) {
		        				try {
		        					client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), PORT_DEFAULT_TCP));
		        				}
		        				 catch (IOException e) {
		        			            e.printStackTrace();
		        			     }
		        				connected = true;
		        				user_session = tmp_user;
		        				stub.setInitList(remoteEventManager.getListUsers());
		        				remoteEventManager.registerForCallback(stub, user_session);
		        				System.out.println("<Utente "+tmp_user+" connesso");
		        				stub.getListUsers();
		        			}
		        			else {
		        				System.out.println("<Nome utente e/o password errati");
		        			}
		        			break;
		        			
		        		//stampa la lista degli utenti registrati al servizio online
		        		case "list_users_on":
		        			if(!connected) {
		        				System.out.println(NO_LOGIN);
		        			}
		        			else stub.getListUsersOnline();
		        			break;
		        			
		        		/*
		        		 * invia un pacchetto UDP all'indirizzo multicast del progetto
		        		 * contenente un messaggio
		        		 */
		        		case "send":
		        			if(!connected) {
		        				System.out.println(NO_LOGIN);
		        				break;
		        			}
		        			c = this.getChatProject(st.nextToken());
		        			if(c!=null) {
		        				String message = user_session+": "+ st.nextToken()+" ";
		        				while(st.hasMoreTokens()) {
		        					message += st.nextToken()+" ";
		        				}
		        				c.sendMessage(message);
		        				System.out.println("<Messaggio inviato correttamente");
		        			}
		        			else {
		        				System.out.println("<Operazione non valida");
		        			}
		        			break;
		        			
		        		/*
		        		 * stampa i messaggi ricevuti sulla chat di progetto
		        		 * in modalità asincrona
		        		 */
		        		case "read_chat":
		        			if(!connected) {
		        				System.out.println(NO_LOGIN);
		        				break;
		        			}
		        			c = this.getChatProject(st.nextToken());
		        			if(c!=null) 
		        				System.out.println(c.readChat());
		        			else
		        				System.out.println("<Operazione non valida");
		        		break;
		        		
		        		/*
		        		 * aggiunge un utente come membro ad un progetto
		        		 */
		        		case "add_member":
		        			if(!connected) {
		        				System.out.println(NO_LOGIN);
		        				break;
		        			}
		        			st.nextToken();
		        			if(!remoteEventManager.checkUser(st.nextToken())) {
		        				System.out.println("<Operazione non valida");
		        				break;
		        			}
		        			
		        		//effettua il logout dal servizio
		        		case "logout":
		        			if(input.equals("logout")) {
	                        	connected = false;
	                        	client.close();
	                        	remoteEventManager.unregisterForCallback(stub, user_session);
	                        	for(ChatClient i : this.chat_projects) {
	                        		i.join();
	                        	}
	                        	System.out.println("<Disconnesso");
	                        	break;
	                        }
		        		//crea un nuovo progetto
		        		case "create_project":
		        		
		        		/*restituisce tutti i progetti di cui
		        		 * l'utente connesso è membro
		        		 */
		        		case "list_projects":
		        		
		        		//entra nella chat del progetto
		        		case "join_chat":
		        		
		        		//stampa tutti i membri del progetto
		        		case "show_members":
		        		
		        		//stampa tutte le card del progetto
		        		case "show_cards":
		        		
		        		//stampa i dettagli della card
		        		case "show_card":
		        		
		        		//aggiunge la card al progetto
		        		case "add_card":
		        		
		        		//muove la card all'interno del progetto
		        		case "move_card":
		        		
		        		//stampa la storia della card 
		        		case "show_history":
		        		
		        		//elimina il progetto concluso
		        		case "cancel_project":
		        			
		        			if(!connected) {
		        				System.out.println(NO_LOGIN);
		        			}
		        			else {
		        				
		        				/*all'input viene aggiunto l'utente della
		        				 * attuale sessione in modo che il server
		        				 * possa sapere con chi sta comunicando
		        				 */
		        				input += " "+user_session;
		        				
		        				
		        				/*
		        				 * scrittura e invio del messaggio al server tramite connessione tcp
		        				 */
		                        ByteBuffer readBuffer = ByteBuffer.wrap(input.getBytes());
		                        client.write(readBuffer);
		                        readBuffer.clear();
		                        
		                        /*
		                         * ricezione e lettura della risposta da parte del server
		                         */
		                        ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
		                        client.read(reply);
		                        reply.flip();
		                        String output = new String(reply.array()).trim();
		                        StringTokenizer ans = new StringTokenizer(output);
		                        
		                        /*
		                         * controlla se la risposta del server contiene
		                         * l'indirizzo ip di un progetto, altrimenti stampa
		                         */
		                        if(ans.nextToken().equals("IP")) {
		                        	c = new ChatClient(st.nextToken(), ans.nextToken());
                        			if(!this.chat_projects.contains(c)) {
                        				c.start();
                        				this.chat_projects.add(c);
                        				System.out.println("<Ti sei unito correttamente alla chat");
                        			}
                        			else System.out.println("<Sei già unito alla chat");
		                        }
		                        else
		                        	System.out.println(output);
		                        reply.clear();
		        			}
		        			break;
		        		default:
		        			System.out.println("<Operazione non valida");
		        			
	            	}
	            }
	            catch (NoSuchElementException e) {
	            	System.out.println("<Parametri insufficienti");
	            }
        	}

        } catch(IOException | NotBoundException e){
            e.printStackTrace();
        }
    }
}
