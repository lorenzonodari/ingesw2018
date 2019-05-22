package it.unibs.ingesw.dpn.ui;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import it.unibs.ingesw.dpn.Main;
import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.users.UsersManager;
import it.unibs.ingesw.dpn.model.users.Mailbox;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventState;
import it.unibs.ingesw.dpn.model.events.EventFactory;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

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
	
	private FieldValue temporaryFieldValue = null;
		
	/**
	 * Crea un nuovo UIManager utilizzando il renderer dato per la creazione
	 * dell'interfaccia utente, il gestore di input utente e il gestorel del model dati.
	 * 
	 * Precondizione: model != null
	 * 
	 * @param model Il gestore dei dati di dominio da utilizzare
	 */
	public UIManager(ModelManager model) {
		
		// Verifica della precondizione
		if (model == null) {
			throw new NullPointerException();
		}
		
		this.renderer = new TextRenderer();
		this.inputManager = new ConsoleInputGetter(renderer);
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
				
				renderer.renderText(INVALID_CHOICE_PROMPT);
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
			renderer.renderText(GENERIC_PROMPT);
			
			MenuAction action = getUserChoice(currentMenu);
			action.execute();
			
		}
		
	}
	
	/**
	 * Crea il menu di login e lo rende il menu corrente
	 */
	public void loginMenu() {
		
		// Callback Esci
		MenuAction quitAction = () -> {Main.terminate(Main.NO_ERROR_EXIT_CODE);};
		
		// Callback Login
		MenuAction loginAction = () -> {
			this.renderer.renderText("Username: ");
			
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
		MenuAction eventsAction = () -> {this.eventView();;};
		
		// Callback visualizza categorie
		MenuAction categoriesAction = () -> {this.categoriesMenu();};
	
		
		// Callback proponi evento
		MenuAction createAction = () -> {this.categorySelectorMenu();};
		
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
	/**
	 * Crea un generico menu di dialogo e lo rende il menu corrente. Tali menu sono utilizzati
	 * per presentare semplici messaggi di conferma o avviso all'utente
	 * 
	 * @param dialog Il messaggio da presentare all'utente
	 * @param backTitle Il nome dell'opzione di uscita
	 * @param backAction L'azione da compiere all'uscita dal menu
	 */
	public void dialog(String dialog, String backTitle, MenuAction backAction) {
		
		Menu dialogMenu = new Menu (dialog, null, backTitle, backAction);
		this.currentMenu = dialogMenu;
			
	}
	
	/**
	 * Crea il menu dedicato all'evento
	 * 
	 * @param evento a cui punta il menu
	 */
	public void eventMenu(Event event) {
		
		// Callback indietro
		MenuAction backAction = () -> {this.eventView();};
		
		// Iscriviti azione
		MenuAction subscriptionAction = () -> {
			MenuAction dialogBackAction = () -> {this.eventMenu(event);};
			model.getEventBoard().addSubscription(event, model.getUsersManager().getCurrentUser());
			this.dialog("Iscrizione effettuata correttamente", Menu.BACK_ENTRY_TITLE, dialogBackAction);
			
		};
		
		Menu eventMenu = new Menu("Azioni su evento", event.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		if(model.getEventBoard().verifySubscription(event, model.getUsersManager().getCurrentUser()))
			eventMenu.addEntry("Iscriviti all'evento", subscriptionAction);
		
		this.currentMenu = eventMenu;
		
	}
	
	/**
	 * Crea il menu della bacheca degli eventi
	 */
	public void eventView() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.boardMenu();};
		
		Menu eventView = new Menu("Lista eventi aperti", null, Menu.BACK_ENTRY_TITLE, backAction);
		
		// Callback categorie
		for (Event open : model.getEventBoard().getEventsByState(EventState.OPEN)) {
			
			MenuAction eventAction = () -> {this.eventMenu(open);};
			eventView.addEntry(open.getFieldValue(CommonField.TITOLO).toString(), eventAction);
			
		}
				
		this.currentMenu = eventView;
			
	}
	
	/**
	 * Crea il menu per la creazione dell'evento.
	 * 
	 * @param category La categoria dell'evento
	 * @param fieldValues Le coppie <Campo, Valore> inizializzate finora.
	 */
	public void createEventMenu(CategoryEnum category, Map<Field, FieldValue> fieldValues) {
		
		// Callback per abortire la creazione dell'evento
		MenuAction abortAction = () -> {this.boardMenu();};
		
		String title = String.format("Creazione di un evento: %s", CategoryProvider.getProvider().getCategory(category).getName());
		Menu createEventMenu = new Menu(title, "Seleziona i campi dell'evento che vuoi impostare. \nI campi contrassegnati dall'asterisco (*) sono obbligatori.\nQuando avrai completato tutti i campi obbligatori seleziona \"Conferma\".", "Annulla la creazione e torna al menu principale", abortAction);
		
		// Verifico se tutti i campi obbligatori sono stati compilati
		boolean checkMandatoryFieldsFlag = true;
		
		List<Field> fields = CategoryProvider.getProvider().getCategory(category).getFields();
		
		for (Field f : fields) {
			
			/* Azione relativa ad un'opzione */
			MenuAction fieldAction = () -> {
				// Acquisisco il campo
				acquireFieldValueSubmenu(f, fieldValues);
				// Salvo il nuovo valore nella mappa
				fieldValues.put(f, this.temporaryFieldValue);
				// Creo il nuovo menu aggiornato
				this.createEventMenu(category, fieldValues);
				};
			
			/* Stringa relativa ad un'opzione */
			String fieldValueString;
			if (fieldValues.get(f) != null) {
				fieldValueString = fieldValues.get(f).toString();
			} else {
				fieldValueString = "- - - - -";
				
				// Inoltre, setto il controllo del completamento di tutti i campi obbligatori a "false"
				if (f.isMandatory()) {
					checkMandatoryFieldsFlag = false;
				}
			}
			
			// Creo la entry
			String entryTitle = String.format(
					"%-35s : %s",
					f.getName() + ((f.isMandatory()) ? " (*)" : ""),
					fieldValueString);
			createEventMenu.addEntry(entryTitle, fieldAction);
			
		}
		
		if (checkMandatoryFieldsFlag) {
			createEventMenu.addEntry("Conferma", () -> {
				EventFactory factory = EventFactory.getFactory();
				Event newEvent = factory.createEvent(this.users.getCurrentUser(), category, fieldValues);
				
				this.model.getEventBoard().addEvent(newEvent, users.getCurrentUser());
				
				MenuAction toHomeAction = () -> {this.mainMenu();};
				this.dialog("Evento creato correttamente", "Torna al menu principale", toHomeAction);
			});
		}
		
		this.currentMenu = createEventMenu;
	}
	
	/**
	 * Crea il sottomenu del menu di creazione di un evento utilizzato per acquisire il valore di 
	 * un dato Field di un evento, lo presenta all'utente e memorizza il dato acquisito all'interno 
	 * dell'attributo "temporaryFieldValue".
	 *
	 * @param field Il campo di cui si vuole acquisire il valore
	 */
	private void acquireFieldValueSubmenu(Field field, Map<Field, FieldValue> partialValues) {
		
		this.temporaryFieldValue = (FieldValue) field.acquireFieldValue(renderer, inputManager, partialValues);
		
	}
	
	/**
	 * Permette la selezione di una categoria per la creazione di un evento.
	 */
	public void categorySelectorMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.boardMenu();};
		
		Menu categorySelector = new Menu("Selezione della categoria", "Seleziona la categoria, fra quelle disponibili, in cui rientra l'evento che vuoi creare", Menu.BACK_ENTRY_TITLE, backAction);
		
		// Callback categorie
		for (CategoryEnum category : CategoryEnum.values()) {
			
			Category completeCategory = CategoryProvider.getProvider().getCategory(category);
			
			HashMap<Field, FieldValue> map = new HashMap<>();
			MenuAction categorySelectionAction = () -> {
				// Preparo una Map vuota per memorizzare i parametri di creazione dell'evento
				for (Field f : completeCategory.getFields()) {
					map.put(f, null);
				};
				this.createEventMenu(category, map);};
			categorySelector.addEntry(completeCategory.getName(), categorySelectionAction);
			
		}
				
		this.currentMenu = categorySelector;
		
	}
	
}