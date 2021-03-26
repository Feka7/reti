

public class Help {
	
	public static final String getHelp() {
		String s = "list_users : restituisce l’elenco degli utenti registrati e il loro stato online/offline\n"
				+ "list_users_on : restituisce l’elenco degli utenti registrati online\n"
				+ "send [nome_progetto] [msg] : invia alla chat di gruppo msg\n"
				+ "read_chat [nome_progetto] : legge i messaggi sulla chat di gruppo\n"
				+ "add_member [nome_progetto] [nome_membro] : aggiunge un nuovo membro al progetto\n"
				+ "logout : effettua il logout dall’applicazione\n"
				+ "create_project [nome_progetto] : crea un nuovo progetto\n"
				+ "list_projects : restituisce l’elenco dei progetti di cui l’utente connesso è membro\n"
				+ "join_chat [nome_progetto] : l’utente connesso si unisce alla chat del progetto. NOTA: prima di effettuare le operazioni di invio e lettura sulla"
				+ " chat bisogna aver effettuato l’operazione di join alla chat del progetto, altrimenti verrà restituito errore\n"
				+ "show_members [nome_progetto] : restituisce l’elenco di tutti i membri del progetto\n"
				+ "show_cards [nome_progetto] : restituisce tutte le card contenute nel progetto\n"
				+ "show_card [nome_progetto] [nome_card] : restituisce tutti i "
				+ "dettagli della card appartenente al progetto\n"
				+ "add_card [nome_progetto] [nome_card] [descrizione] : aggiunge una nuova card al progetto\n"
				+ "move_card [nome_progetto] [nome_card] [destinazione] :"
				+ "muove la card nel progetto verso un nuovo stato del flusso di lavoro\n"
				+ "show_history [nome_progetto] [nome_card] : restituisce la storia del flusso di lavoro della card\n"
				+ "cancel_project [nome_progetto] : elimina il progetto se tutti i suoi task sono terminati\n";
				return s;
	}
}
