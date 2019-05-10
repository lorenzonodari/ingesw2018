package it.unibs.ingesw.dpn.ui;

import java.util.List;

import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.categories.Category;

/**
 * Classe adibita alla gestione dell'interfaccia utente. In particolare, alle istanze
 * di questa classe e' delegata la gestione dell'input dell'utente e la creazione e l'aggiornamento
 * dell'interfaccia grafica
 */
public class UIManager {
	
	private static final String GENERIC_PROMPT = "Selezionare una voce";
	private static final String INVALID_CHOICE_PROMPT = "Scelta non valida, riprovare";
	private static final String BACK_ENTRY_TITLE = "Indietro";
	
	private UIRenderer renderer;
	private InputGetter inputManager;
	private ModelManager model;
	private Menu currentMenu;
		
	/**
	 * Crea un nuovo UIManager utilizzando il renderer dato per la creazione
	 * dell'interfaccia utente, il gestore di input utente e il gestorel del model dati.
	 * 
	 * Precondizione: renderer != null
	 * Precondizione: inputManager != null
	 * Precondizione: model != null
	 * 
	 * @param renderer Il renderer {@link UIRenderer} da utilizzare
	 * @param inputManager Il gestore dell'input utente da utilizzare
	 * @param model Il gestore dei dati di dominio da utilizzare
	 */
	public UIManager(UIRenderer renderer, InputGetter inputManager, ModelManager model) {
		
		// Verifica della precondizione
		if (renderer == null || inputManager == null || model == null) {
			throw new NullPointerException();
		}
		
		this.renderer = renderer;
		this.inputManager = inputManager;
		this.model = model;
		this.currentMenu = null;
	}
	
	/**
	 * Acquisisce la scelta dell'utente relativa al menu dato
	 * 
	 * Precondizione: menu != null
	 * 
	 * @param menu Il menu relativo alla scelta da prendere
	 * @return L'azione corrispondente alla scelta dell'utente
	 */
	private MenuAction getUserChoice(Menu menu) {
		
		boolean done = false;
		int choice = 0;
		List<MenuEntry> entries = menu.getEntries();
		
		do {
			
			try {
				
				choice = inputManager.getInteger(0, entries.size());
				done = true;
				
			}
			catch (NumberFormatException ex) {
				
				renderer.renderPrompt(INVALID_CHOICE_PROMPT);
			}
			
		} while (!done);
		
		if (choice == 0) {
			return menu.getQuitEntry().getAction();
		}
		
		return entries.get(choice - 1).getAction(); // Le scelte son numerate partendo da 1
	}
	
	/**
	 * Avvia il loop dell'interfaccia utente, all'interno del quale viene acquisita la scelta
	 * dell'utente e viene eseguita l'azione corrispondente
	 */
	public void uiLoop() {
		
		loginMenu();
		while (true) {
			
			renderer.renderMenu(currentMenu);
			renderer.renderPrompt(GENERIC_PROMPT);
			
			MenuAction action = getUserChoice(currentMenu);
			action.execute(currentMenu);
			
		}
		
	}
	
	public void loginMenu() {
		
		// Esci
		MenuAction quitAction = (parent) -> {System.exit(0);};
		MenuEntry quitEntry = new MenuEntry("Esci", quitAction);
		
		// Login
		MenuAction loginAction = (parent) -> {
			this.renderer.renderPrompt("Username: ");
			
			String username = this.inputManager.getString();
			this.model.getUsersManager().login(username);
			mainMenu();
		};
		MenuEntry loginEntry = new MenuEntry("Login", loginAction);
		
		Menu loginMenu = new Menu("SocialNetwork", "Benvenuto", quitEntry);
		loginMenu.addEntry(loginEntry);
		
		this.currentMenu = loginMenu;
	}
	
	/**
	 * Crea il menu principale del programma e lo rende il menu corrente
	 */
	public void mainMenu() {
		
		// Logout
		MenuAction quitAction = (parent) -> {
			this.model.getUsersManager().logout();
			loginMenu();
		};
		MenuEntry quitEntry = new MenuEntry("Logout", quitAction);
		
		// Visualizza categorie
		MenuAction toCategoriesAction = (parent) -> {this.categoriesMenu();};
		MenuEntry toCategories = new MenuEntry("Visualizza categorie", toCategoriesAction);
		
		String username = this.model
						   .getUsersManager()
						   .getCurrentUser()
						   .getUsername();
		String title = String.format("User: %s", username);
		
		Menu mainMenu = new Menu(title, "Menu principale", quitEntry);
		mainMenu.addEntry(toCategories);
		
		this.currentMenu = mainMenu;
				
	}
	
	/**
	 * Crea il menu delle informazioni dettagliate di categoria e lo rende il menu corrente
	 * 
	 * @param category La categoria alla quale si riferisce il menu
	 */
	public void categoryInfoMenu(Category category) {
		
		// Indietro
		MenuAction backAction = (parent) -> {this.categoryMenu(category);};
		MenuEntry backEntry = new MenuEntry(BACK_ENTRY_TITLE, backAction);
		
		String title = String.format("Categoria: %s", category.getName());
		Menu infoMenu = new Menu(title, category.toString(), backEntry);
		
		this.currentMenu = infoMenu;
	}
	
	/**
	 * Crea il menu specifico per una categoria data e lo rende il menu corrente
	 * 
	 * @param category La categoria alla quale si riferisce il menu
	 */
	public void categoryMenu(Category category) {
		
		// Indietro
		MenuAction backAction = (parent) -> {this.categoriesMenu();};
		MenuEntry backEntry = new MenuEntry(BACK_ENTRY_TITLE, backAction);
		
		// Visualizza informazioni dettagliate
		MenuAction infoAction = (parent) -> {this.categoryInfoMenu(category);};
		MenuEntry infoEntry = new MenuEntry("Visualizza informazioni dettagliate", infoAction);
		
		Menu categoryMenu = new Menu("Menu di categoria", category.getName(), backEntry);
		categoryMenu.addEntry(infoEntry);
		
		this.currentMenu = categoryMenu;
		
	}
	
	/**
	 * Crea il menu delle categorie e lo rende il menu corrente
	 */
	public void categoriesMenu() {
		
		// Indietro
		MenuAction backAction = (parent) -> {this.mainMenu();};
		MenuEntry backEntry = new MenuEntry(BACK_ENTRY_TITLE, backAction);
		
		Menu categoriesMenu = new Menu("Menu categorie", "Categorie di eventi disponibili:", backEntry);
		
		// Categorie
		for (Category c : model.getAllCategories()) {
			
			MenuAction categoryAction = (parent) -> {this.categoryMenu(c);};
			MenuEntry categoryEntry = new MenuEntry(c.getName(), categoryAction);
			categoriesMenu.addEntry(categoryEntry);
			
		}
				
		this.currentMenu = categoriesMenu;
			
	}
	
}
