package it.unibs.ingesw.dpn;

import java.io.File;

import it.unibs.ingesw.dpn.model.persistence.DiskSerializationStrategy;
import it.unibs.ingesw.dpn.model.persistence.PersistenceException;
import it.unibs.ingesw.dpn.model.persistence.PersistenceManager;
import it.unibs.ingesw.dpn.model.users.LoginManager;
import it.unibs.ingesw.dpn.ui.MenuManager;
import it.unibs.ingesw.dpn.ui.TextUI;
import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Classe di avvio dell'intero programma.<br>
 * Si occupa di istanziare le classi principali e di avviare l'esecuzione dell'albero di menu.<br>
 * Inoltre, utilizza le classi che si occupano della persistenza per caricare i dati all'avvio
 * e salvarli alla chiusura del programma.<br>
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class Main {
	
	public static final int NO_ERROR_EXIT_CODE = 0;
	public static final int DB_LOAD_ERROR_EXIT_CODE = 1;
	public static final int DB_SAVE_ERROR_EXIT_CODE = 2;
	
	public static final File DEFAULT_DATABASE = new File(System.getProperty("user.home"), "socialnetwork.db");

	private static PersistenceManager persistenceManager = null;
	private static MenuManager menuManager = null;
	private static UserInterface userInterface = null;
	/**
	 * Metodo di avvio del programma, è il primo ad essere chiamato.
	 * 
	 * @param args Parametri esterni, al momento non vengono usati.
	 */
	public static void main(String[] args) {
		

		try {
			persistenceManager = new PersistenceManager(new DiskSerializationStrategy(DEFAULT_DATABASE));
			persistenceManager.load();
		}
		catch (PersistenceException ex) {
			System.err.println("Errore durante la lettura del database di dominio: impossibile avviare l'applicazione");
			ex.printStackTrace();
			System.exit(DB_LOAD_ERROR_EXIT_CODE);
		}

		LoginManager loginManager = new LoginManager();
		menuManager = new MenuManager(persistenceManager.getModel(), loginManager);
		
		// Avvio del menu e dell'interfaccia utente
		userInterface = new TextUI();
		menuManager.getStartMenuAction().execute(userInterface);

	}
	
	/**
	 * Metodo che termina l'esecuzione del programma ed esegue il salvataggio dei dati secondo
	 * la strategia prevista dalla classe {@link PersistenceManager}.
	 * 
	 * @param status Il numero di errori con cui il programma termina.
	 */
	public static void terminate(int status) {
		
		try {
			persistenceManager.save();
		}
		catch (PersistenceException ex) {
			System.err.println("Errore durante la scrittura del database di dominio: impossibile salvare i dati");
			status = DB_SAVE_ERROR_EXIT_CODE;
		}
		
		System.exit(status);
		
	}

}
