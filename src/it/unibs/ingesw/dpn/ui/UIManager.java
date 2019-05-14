package it.unibs.ingesw.dpn.ui;

import java.util.List;
import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.users.UsersManager;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.users.Mailbox;
import it.unibs.ingesw.dpn.model.users.Notification;

/**
 * Classe adibita alla gestione dell'interfaccia utente. In particolare, alle istanze
 * di questa classe e' delegata la gestione dell'input dell'utente e la creazione e l'aggiornamento
 * dell'interfaccia grafica
 */
public class UIManager {
	
	private static final String GENERIC_PROMPT = "Selezionare una voce";
	private static final String INVALID_CHOICE_PROMPT = "Scelta non valida, riprovare";
	private static final String LIST_ELEMENT_PREFIX = " * ";
	
	private UIRenderer renderer;
	private InputGetter inputManager;
	private ModelManager model;
	private UsersManager users;
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
		this.users = model.getUsersManager();
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
			action.execute();
			
		}
		
	}
	
	/**
	 * Crea il menu di login e lo rende il menu corrente
	 */
	public void loginMenu() {
		
		// Callback Esci
		MenuAction quitAction = () -> {System.exit(0);};
		
		// Callback Login
		MenuAction loginAction = () -> {
			this.renderer.renderPrompt("Username: ");
			
			String username = this.inputManager.getString();
			this.users.login(username);
			mainMenu();
		};
		
		Menu loginMenu = new Menu("SocialNetwork", "Benvenuto", "Esci", quitAction);
		loginMenu.addEntry("Login", loginAction);
		
		this.currentMenu = loginMenu;
	}
	
	/**
	 * Crea il menu associato allo spazio personale dell'utente correntemente connesso al sistema
	 * e lo rende il menu corrente
	 */
	public void personalSpace() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.mainMenu();};
		
		// Callback spazio notifiche
		MenuAction notificationsAction = () -> {this.notificationsMenu();};
		
		Menu personalSpace = new Menu("Spazio personale", backAction);
		personalSpace.addEntry("Spazio notifiche", notificationsAction);
		
		this.currentMenu = personalSpace;
	}
	
	/**
	 * Crea il menu delle notifiche dell'utente e lo rende il menu corrente
	 */
	public void notificationsMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.personalSpace();};
		
		// Callback Cancella notifiche
		MenuAction deleteAction = () -> {this.deleteNotificationMenu();};
		
		Mailbox mailbox = users.getCurrentUser().getMailbox();
		String menuContent = null;
		
		if (!mailbox.isEmpty()) {
			
			StringBuffer notifications = new StringBuffer();
			for (Notification n : mailbox.getEveryNotification()) {
				notifications.append(LIST_ELEMENT_PREFIX);
				notifications.append(n.toString());
				notifications.append("\n");
			}
			menuContent = notifications.toString();
			
		}
		else {
			
			menuContent = "Nessuna notifica";
			
		}
		
		Menu notificationsMenu = new Menu("Spazio notifiche", menuContent, Menu.BACK_ENTRY_TITLE, backAction);
		notificationsMenu.addEntry("Cancella notifiche", deleteAction);
		
		this.currentMenu = notificationsMenu;
		
	}
	
	/**
	 * Crea il menu di eliminazione delle notifiche e lo rende il menu corrente
	 */
	public void deleteNotificationMenu() {
		
		// Callback per tornare allo spazio dell notifiche
		MenuAction backAction = () -> {this.notificationsMenu();};
		
		Menu deleteMenu = new Menu("Elimina notifiche", "Seleziona la notifica da eliminare", Menu.BACK_ENTRY_TITLE, backAction);
		
		Mailbox mailbox = users.getCurrentUser().getMailbox();
		
		if (!mailbox.isEmpty()) {
			
			for (Notification n : mailbox.getEveryNotification()) {
				
				MenuAction deleteAction = () -> {
					mailbox.delete(n);
					this.deleteNotificationMenu();
				};
				
				deleteMenu.addEntry(n.toString(), deleteAction);
			}
			
		}
		
		this.currentMenu = deleteMenu;
		
	}
	
	/**
	 * Crea il menu principale del programma e lo rende il menu corrente
	 */
	public void mainMenu() {
		
		// Callback Logout
		MenuAction quitAction = () -> {
			this.users.logout();
			loginMenu();
		};
		
		// Callback Spazio personale
		MenuAction toPersonalSpaceAction = () -> {this.personalSpace();};
		
		// Callback Bacheca
		MenuAction boardAction = () -> {this.boardMenu();};
		
		Menu mainMenu = new Menu("Menu principale", null, "Logout", quitAction);
		mainMenu.addEntry("Bacheca", boardAction);
		mainMenu.addEntry("Spazio personale", toPersonalSpaceAction);
		
		this.currentMenu = mainMenu;
				
	}
	
	/**
	 * Crea il menu della bacheca e lo rende il menu corrente
	 */
	public void boardMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.mainMenu();};
		
		// Callback visualizza eventi
		MenuAction eventsAction = () -> {;};
		
		// Callback visualizza categorie
		MenuAction categoriesAction = () -> {this.categoriesMenu();};
		
		// Callback proponi evento
		MenuAction createAction = () -> {;};
		
		Menu boardMenu = new Menu("Bacheca", backAction);
		boardMenu.addEntry("Visualizza eventi", eventsAction);
		boardMenu.addEntry("Visualizza categorie", categoriesAction);
		boardMenu.addEntry("Proponi evento", createAction);
		
		this.currentMenu = boardMenu;
		
	}
	
	/**
	 * Crea il menu delle informazioni dettagliate di categoria e lo rende il menu corrente
	 * 
	 * @param category La categoria alla quale si riferisce il menu
	 */
	public void categoryInfoMenu(Category category) {
		
		// Callback indietro
		MenuAction backAction = () -> {this.categoryMenu(category);};
		
		String title = String.format("Categoria: %s", category.getName());
		Menu infoMenu = new Menu(title, category.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		
		this.currentMenu = infoMenu;
	}
	
	/**
	 * Crea il menu specifico per una categoria data e lo rende il menu corrente
	 * 
	 * @param category La categoria alla quale si riferisce il menu
	 */
	public void categoryMenu(Category category) {
		
		// Callback indietro
		MenuAction backAction = () -> {this.categoriesMenu();};
		
		// Visualizza informazioni dettagliate
		MenuAction infoAction = () -> {this.categoryInfoMenu(category);};
		
		Menu categoryMenu = new Menu("Menu di categoria", category.getName(), Menu.BACK_ENTRY_TITLE, backAction);
		categoryMenu.addEntry("Visualizza informazioni dettagliate", infoAction);
		
		this.currentMenu = categoryMenu;
		
	}
	
	/**
	 * Crea il menu delle categorie e lo rende il menu corrente
	 */
	public void categoriesMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.boardMenu();};
		
		Menu categoriesMenu = new Menu("Menu categorie", "Categorie di eventi disponibili:", Menu.BACK_ENTRY_TITLE, backAction);
		
		// Callback categorie
		for (Category c : model.getAllCategories()) {
			
			MenuAction categoryAction = () -> {this.categoryMenu(c);};
			categoriesMenu.addEntry(c.getName(), categoryAction);
			
		}
				
		this.currentMenu = categoriesMenu;
			
	}
	
}