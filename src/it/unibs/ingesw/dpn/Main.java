package it.unibs.ingesw.dpn;

import java.io.File;

import it.unibs.ingesw.dpn.ui.UIManager;
import it.unibs.ingesw.dpn.model.ModelManager;

public class Main {
	
	public static final int NO_ERROR_EXIT_CODE = 0;
	public static final int DB_LOAD_ERROR_EXIT_CODE = 1;
	public static final int DB_SAVE_ERROR_EXIT_CODE = 2;
	
	public static final File DEFAULT_DATABASE = new File(System.getProperty("user.home"), "socialnetwork.db");
	
	private static ModelManager modelManager = null;
	private static UIManager uiManager = null;

	public static void main(String[] args) {
		
		if (DEFAULT_DATABASE.exists() && DEFAULT_DATABASE.canRead()) {
			
			modelManager = ModelManager.loadFromDisk(DEFAULT_DATABASE);
			
		}
		else {
			
			modelManager = new ModelManager();
		}
		
		uiManager = new UIManager(modelManager);
		uiManager.uiLoop();

	}
	
	public static void terminate(int status) {
		
		modelManager.saveToDisk(DEFAULT_DATABASE);
		System.exit(status);
		
	}

}
