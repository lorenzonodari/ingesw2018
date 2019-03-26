package it.unibs.ingesw.dpn.ui;

/**
 * Classe adibita alla gestione dell'interfaccia utente. In particolare, alle istanze
 * di questa classe e' delegata la gestione dell'input dell'utente e la creazione e l'aggiornamento
 * dell'interfaccia grafica
 */
public class UIManager {

	private UIRenderer renderer;
	private Menu currentMenu;
	private InputGetter inputManager;
	
	
	/**
	 * Crea un nuovo UIManager utilizzando il renderer dato per la creazione
	 * dell'interfaccia utente e il gestore di input utente dato.
	 * 
	 * Precondizione: renderer != null
	 * Precondizione: inputManager != null
	 * 
	 * @param renderer Il renderer {@link UIRenderer} da utilizzare
	 * @param inputManager Il gestore dell'input utente da utilizzare
	 */
	public UIManager(UIRenderer renderer, InputGetter inputManager) {
		
		// Verifica della precondizione
		if (renderer == null || inputManager == null) {
			throw new NullPointerException();
		}
		
		this.renderer = renderer;
		this.inputManager = inputManager;
		this.currentMenu = null;
	}
	
	/**
	public void toMainMenu() {
		Menu mainMenu = new Menu("a", "b");
		MenuEntry toCategories = new MenuEntry("To categories", )
	}*/
	
}
