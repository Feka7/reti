

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Server di Worth
 * utilizza multiplexing dei canali mediante NIO. 
 *
 */
class ServerWorth {
	
 
	/**
     * dimensione del buffer utilizzato per la lettura
     */
    private final int BUFFER_DIMENSION = 4096;
   
    /**
     * porta su cui aprire il listening socket
     */
    private final int port;
    /**
     * messaggio numero parametri sbagliati 
     */
    private final String ERROR_ANSWER = "<Numero di parametri errato";   
    /**
     * messaggio di risposta
     */
    private final String ADD_ANSWER = "<";
    /**
     * Struttura dati del servizio
     */
    private Worth worth;
    /**
     *
     * @param port porta su cui aprire il listening socket
     */
    public ServerWorth(){
        this.port = 5001;
        this.worth = new Worth();
    }

    /**
     * avvia l'esecuzione del server
     * @throws InterruptedException 
     * @throws IllegalArgumentException 
     */
    public void start() throws IllegalArgumentException, InterruptedException {
        
        try (
                ServerSocketChannel s_channel = ServerSocketChannel.open();
        		
        ){
             
            s_channel.socket().bind(new InetSocketAddress(this.port));
            s_channel.configureBlocking(false);
            Selector sel = Selector.open();
            s_channel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.printf("Server: in attesa di connessioni sulla porta %d\n", this.port);
            while(true){
                if (sel.select() == 0)
                    continue;
                // insieme delle chiavi corrispondenti a canali pronti
                Set<SelectionKey> selectedKeys = sel.selectedKeys();
                // iteratore dell'insieme sopra definito
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    try {       // utilizzo la try-catch per gestire la terminazione improvvisa del client
                        if (key.isAcceptable()) {               // ACCETTABLE
                            /*
                             * accetta una nuova connessione creando un SocketChannel per la
                             * comunicazione con il client che la richiede
                             */
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel c_channel = server.accept();
                            c_channel.configureBlocking(false);
                            System.out.println("Server: accettata nuova connessione dal client: " + c_channel.getRemoteAddress());
                            this.registerRead(sel, c_channel);
                        } else if (key.isReadable()) {                  // READABLE
                            this.readClientMessage(sel, key);
                        }
                        if (key.isWritable()) {                 // WRITABLE
                            this.echoAnswer(sel, key);
                        }
                    }
                    catch (IOException e) {             // terminazione improvvisa del client
                        e.printStackTrace();
                        key.channel().close();
                        key.cancel();
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * registra l'interesse all'operazione di READ sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param c_channel socket channel relativo al client
     * @throws IOException se si verifica un errore di I/O
     */
    private void registerRead(Selector sel, SocketChannel c_channel) throws IOException {

        // crea il buffer
        ByteBuffer bfs = ByteBuffer.allocate(BUFFER_DIMENSION);
        // aggiunge il canale del client al selector con l'operazione OP_READ
        // e aggiunge bytebuffer come attachment
        c_channel.register(sel, SelectionKey.OP_READ, bfs);
    }

    /**
     * legge il messaggio inviato dal client e registra l'interesse all'operazione di WRITE sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     * @throws InterruptedException s
     * @throws IllegalArgumentException 
     */
    private void readClientMessage(Selector sel, SelectionKey key) throws IOException, IllegalArgumentException, InterruptedException {
    	
        SocketChannel c_channel = (SocketChannel) key.channel();
        // recupera l'array di bytebuffer (attachment)
        ByteBuffer bfs = (ByteBuffer) key.attachment();
        
		c_channel.read(bfs);
		String msg = new String(bfs.array()).trim();
        if(!msg.isEmpty()) {
      
        /**
         * viene controllata la validità della funzione richiesta ed eventualmente
         * eseguita. Ogni operazione viene eseguita sulla struttura dati
         * worth, la quale provvede a restituire il risultato della funzione
		 * attraverso una stringa.
		 * Prima di effettuare un'operazione valida, viene controllata
		 * la correttezza del numero di parametri ricevuti per la determinata
		 * funzione
         */
        	
        System.out.println("Server: ricevuto "+msg);
    	String output = null;
    	//classe utilizzata per il parsing della richiesta ricevuta
    	StringTokenizer st = new StringTokenizer(msg);
    	switch(st.nextToken()) {
        	case "list_projects":
        	if(st.countTokens() == 1)
        		output = ADD_ANSWER + this.worth.listProjects(st.nextToken());
        	else
        		output = ERROR_ANSWER;
        		break;
    		case "create_project":
    			if(st.countTokens() == 2)
    				output = ADD_ANSWER + this.worth.createProject(st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "show_members":
    			if(st.countTokens() == 2)
    				output = ADD_ANSWER + this.worth.showMembers(st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "add_member":
    			if(st.countTokens() == 3)
    				output = ADD_ANSWER + this.worth.addMember(st.nextToken(), st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "show_cards":
    			if(st.countTokens() == 2)
    				output = ADD_ANSWER + this.worth.showCards(st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "show_card":
    			if(st.countTokens() == 3)
    				output = ADD_ANSWER + this.worth.showCard(st.nextToken(), st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "add_card":
    			if(st.countTokens() >= 2 ) {
    				String name_project = st.nextToken();
    				String name_card = st.nextToken();
    				String description = st.nextToken()+" ";
    				while(st.countTokens() > 1) {
    					description += st.nextToken()+" ";
    				}
    				String user = st.nextToken();
    				output = ADD_ANSWER + this.worth.addCard(name_project, name_card, description, user);
    			}
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "show_history":
    			if(st.countTokens() == 3)
    				output = ADD_ANSWER + this.worth.getHistory(st.nextToken(), st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "cancel_project":
    			if(st.countTokens() == 2)
    				output = ADD_ANSWER + this.worth.cancelProject(st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "move_card":
    			if(st.countTokens() == 4)
    				output = ADD_ANSWER + this.worth.moveCard(st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		case "join_chat":
    			if(st.countTokens() == 2)
    				output = this.worth.getAddressProject(st.nextToken(), st.nextToken());
    			else
    				output = ERROR_ANSWER;
    			break;
    		default:
    			output = ERROR_ANSWER;
    			break;
    	}
    	/**
    	 * aggiunge il canale del client al selector con l'operazione OP_WRITE
    	 * e con la risposta da inviare al client
    	 */
        c_channel.register(sel, SelectionKey.OP_WRITE, output);
      }
            
    }

    /**
     * scrive il buffer sul canale del client
     *
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void echoAnswer(Selector sel, SelectionKey key) throws IOException {
        SocketChannel c_channel = (SocketChannel) key.channel();
        String echoAnsw= (String) key.attachment();
        ByteBuffer bbEchoAnsw = ByteBuffer.wrap(echoAnsw.getBytes());
        c_channel.write(bbEchoAnsw);
        System.out.println("Server: " + echoAnsw + " inviato al client " + c_channel.getRemoteAddress());
        if (!bbEchoAnsw.hasRemaining()) {
            bbEchoAnsw.clear();
            this.registerRead(sel, c_channel);
        }
    }

}