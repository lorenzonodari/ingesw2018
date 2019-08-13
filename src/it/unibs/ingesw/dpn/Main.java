package it.unibs.ingesw.dpn;

import java.io.File;

import it.unibs.ingesw.dpn.ui.MenuManager;
import it.unibs.ingesw.dpn.ui.TextUI;
import it.unibs.ingesw.dpn.ui.UserInterface;
import it.unibs.ingesw.dpn.model.ModelManager;

public class Main {
	
	public static final int NO_ERROR_EXIT_CODE = 0;
	public static final int DB_LOAD_ERROR_EXIT_CODE = 1;
	public static final int DB_SAVE_ERROR_EXIT_CODE = 2;
	
	public static final File DEFAULT_DATABASE = new File(System.getProperty("user.home"), "socialnetwork.db");
	
	private static ModelManager modelManager = null;
	private static UserInterface userInterface = null;
	private static MenuManager menuManager = null;

	public static void main(String[] args) {
		
		if (DEFAULT_DATABASE.exists() && DEFAULT_DATABASE.canRead()) {
			
			modelManager = ModelManager.loadFromDisk(DEFAULT_DATABASE);
			
		}
		else {
			
			modelManager = new ModelManager();
		}
		
		userInterface = new TextUI();
		menuManager = new MenuManager(modelManager, userInterface);
		
		// Avvio del menu
		menuManager.getStartMenuAction().execute(userInterface);

	}
	
	public static void terminate(int status) {
		
		modelManager.saveToDisk(DEFAULT_DATABASE);
		System.exit(status);
		
	}

}
