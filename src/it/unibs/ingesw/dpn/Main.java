package it.unibs.ingesw.dpn;

import java.io.File;

import it.unibs.ingesw.dpn.model.persistence.DiskSerializationStrategy;
import it.unibs.ingesw.dpn.model.persistence.PersistenceException;
import it.unibs.ingesw.dpn.model.persistence.PersistenceManager;
import it.unibs.ingesw.dpn.ui.UIManager;

public class Main {
	
	public static final int NO_ERROR_EXIT_CODE = 0;
	public static final int DB_LOAD_ERROR_EXIT_CODE = 1;
	public static final int DB_SAVE_ERROR_EXIT_CODE = 2;
	
	public static final File DEFAULT_DATABASE = new File(System.getProperty("user.home"), "socialnetwork.db");
	
	private static PersistenceManager persistenceManager = null;
	
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

		UIManager uiManager = new UIManager(persistenceManager.getModel());
		uiManager.uiLoop();

	}
	
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
