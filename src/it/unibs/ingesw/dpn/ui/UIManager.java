package it.unibs.ingesw.dpn.ui;

import java.util.Date;
import java.util.List;
import it.unibs.ingesw.dpn.Main;
import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.users.UsersManager;
import it.unibs.ingesw.dpn.model.users.Invite;
import it.unibs.ingesw.dpn.model.users.Mailbox;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventState;
import it.unibs.ingesw.dpn.model.events.Inviter;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;

/**
 * Classe adibita alla gestione dell'interfaccia utente. In particolare, alle istanze
 * di questa classe e' delegata la gestione dell'input dell'utente e la creazione e l'aggiornamento
 * dell'interfaccia grafica
 */
public class UIManager {
	
	private static final String GENERIC_PROMPT = "Selezionare una voce";
	private static final String INVALID_CHOICE_PROMPT = "Scelta non valida, riprovare";
	private static final String LIST_ELEMENT_PREFIX = " * ";
	private static final String CREATION_ENTRY_FORMAT = "%-50s : %s";
	
	private UIRenderer renderer;
	private InputGetter inputManager;
	private ModelManager model;
	private UsersManager users;
	private Menu currentMenu;
	private EventFactory eventFactory;
	private UserFactory userFactory;
		
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
		this.eventFactory = new EventFactory(renderer, inputManager);
		this.userFactory = new UserFactory(renderer, inputManager, users);
		
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
		MenuAction quitAction = () -> {
			this.renderer.renderLineSpace();
			this.renderer.renderText("Programma terminato.");
			Main.terminate(Main.NO_ERROR_EXIT_CODE);
			};
		
		// Callback Login
		MenuAction loginAction = () -> {
			// Leggo il nuovo nickname da input
			this.renderer.renderText("Nickname: ");
			String username = this.inputManager.getString();
			// Provo a loggare
			if (this.users.login(username)) {
				mainMenu();
			} else {
				this.userFactory.startCreation(username);
				createUserMenu();
			}
		};
		
		Menu loginMenu = new Menu("SocialNetwork", "Benvenuto/a", "Esci", quitAction);
		loginMenu.addEntry("Login", loginAction);
		
		this.currentMenu = loginMenu;
	}

	/**
	 * Crea il menu associato al processo di creazione di un utente, visualizzando i vari campi e permettendo
	 * al fruitore di inizializzarne (sotto opportune ipotesi) i valori.
	 * Questo metodo si occupa, in altre parole, di fornire un'interfaccia testuale interattiva al processo
	 * di creazione di un oggetto {@link User} gestito da {@link UserFactory}.
	 */
	public void createUserMenu() {

		// Callback per abortire la creazione dell'utente
		MenuAction abortAction = () -> {
			this.userFactory.cancel();
			this.loginMenu();
			};

		String title = String.format("Creazione di un nuovo utente");
		Menu createUserMenu = new Menu(title, 
				"Seleziona i campi dell'utente che vuoi impostare. \n"
				+ "I campi contrassegnati dall'asterisco (*) sono obbligatori.\n"
				+ "Quando avrai completato tutti i campi seleziona \"Conferma\".",
				"Annulla la creazione e torna al menu principale", abortAction);
			
		// Ciclo su tutti i campi previsti per l'utente
		for (Field f : UserField.values()) {
			
			/* Azione relativa ad un'opzione */
			MenuAction fieldAction = () -> {
				this.userFactory.acquireFieldValue(f);
				// Creo il nuovo menu aggiornato
				this.createUserMenu();
				};
			
			// Creo la entry
			String entryTitle = String.format(
					CREATION_ENTRY_FORMAT,
					f.getName() + ((f.isMandatory()) ? " (*)" : ""),
					this.userFactory.getProvisionalFieldValueString(f));
			createUserMenu.addEntry(entryTitle, fieldAction);
			
		}
		
		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (this.userFactory.verifyMandatoryFields()) {
			createUserMenu.addEntry("Conferma la creazione del nuovo utente", () -> {
				
				// Termino la creazione dell'utente
				User newUser = this.userFactory.finalise();
				
				// Aggiungo l'utente alla lista di utenti
				this.users.addUser(newUser);
				
				MenuAction toHomeAction = () -> {this.loginMenu();};
				this.dialog(
						"Creazione completata", 
						"Ora puoi provare ad effettuare il login con l'utente che hai appena creato.", 
						"Vai alla pagina iniziale", 
						toHomeAction);
			});
		}
		
		this.currentMenu = createUserMenu;
	}
	
	/**
	 * Crea il menu associato al processo di modifica di un utente, visualizzando i vari campi e permettendo
	 * al fruitore di modificarne (sotto opportune ipotesi) i valori.
	 * Questo metodo si occupa, in altre parole, di fornire un'interfaccia testuale interattiva al processo
	 * di editing di un oggetto {@link User} gestito da {@link UserFactory}.
	 */
	public void editUserMenu() {

		// Callback per abortire la modifica dell'utente
		MenuAction abortAction = () -> {
			this.userFactory.cancel();
			this.personalSpace();
			};

		String title = String.format("Modifica del proprio profilo");
		Menu editUserMenu = new Menu(title, 
				"Seleziona i campi del profilo che vuoi modificare. \n"
				+ "Soltanto i campi contrassegnati dal cancelletto (#) sono modificabili.\n"
				+ "Quando avrai terminato seleziona \"Conferma\".",
				"Annulla la modifica e torna allo spazio personale", abortAction);
			
		// Ciclo su tutti i campi previsti per l'utente
		for (Field f : UserField.values()) {
			
			/* Azione relativa ad un'opzione */
			MenuAction fieldAction = () -> {
				this.userFactory.acquireFieldValue(f);
				// Creo il nuovo menu aggiornato
				this.editUserMenu();
				};
			
			// Creo la entry
			String entryTitle = String.format(
					CREATION_ENTRY_FORMAT,
					f.getName() + ((f.isEditable()) ? " (#)" : ""),
					this.userFactory.getProvisionalFieldValueString(f));
			editUserMenu.addEntry(entryTitle, fieldAction);
			
		}
		
		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (this.userFactory.verifyMandatoryFields()) {
			editUserMenu.addEntry("Conferma la modifica del profilo", () -> {
				
				// Termino la creazione dell'utente
				this.userFactory.finalise();
				
				MenuAction toHomeAction = () -> {this.personalSpace();};
				this.dialog(
						"Modifica completata",
						null, 
						"Torna allo spazio personale", 
						toHomeAction);
			});
		}
		
		this.currentMenu = editUserMenu;
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
		
		// Callback spazio inviti
		MenuAction invitationsAction = () -> {this.invitationsMenu();};
		
		// Callback spazio iscrizioni
		MenuAction subscriptionsAction = () -> {this.subscriptionsMenu();};
		
		// Callback spazio proposte
		MenuAction proposalsAction = () -> {this.proposalsMenu();};
		
		// Callback modifica utente
		MenuAction userEditingAction = () -> {
			this.userFactory.startEditing(users.getCurrentUser());
			this.editUserMenu();
			};
		
		Menu personalSpace = new Menu("Spazio personale", backAction);
		personalSpace.addEntry("Spazio notifiche", notificationsAction);
		personalSpace.addEntry("Inviti", invitationsAction);
		personalSpace.addEntry("Le mie iscrizioni", subscriptionsAction);
		personalSpace.addEntry("Le mie proposte", proposalsAction);
		personalSpace.addEntry("Modifica profilo", userEditingAction);
		
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
		
		if (mailbox.containsNotifications()) {
			
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
		
		if (mailbox.containsNotifications()) {
			
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
	 * @param dialogTitle Il messaggio da presentare all'utente, o il titolo del messaggio se questo è più lungo
	 * @param dialogDescription La descrizione (facoltativa) del menu di dialogo
	 * @param backOption Il nome dell'opzione di uscita
	 * @param backAction L'azione da compiere all'uscita dal menu
	 */
	public void dialog(String dialogTitle, String dialogDescription, String backOption, MenuAction backAction) {
		
		Menu dialogMenu = new Menu (dialogTitle, dialogDescription, backOption, backAction);
		this.currentMenu = dialogMenu;
			
	}
	
	/**
	 * Crea il menu dedicato ad uno specifico evento all'intero della bacheca e lo
	 * rende il menu corrente.
	 * 
	 * @param event evento a cui punta il menu
	 */
	public void eventMenu(Event event) {
		
		// Callback indietro
		MenuAction backAction = () -> {this.eventView();};
		
		// Iscriviti azione
		MenuAction subscriptionAction = () -> {
			MenuAction dialogBackAction = () -> {this.eventMenu(event);};
			boolean success = model.getEventBoard().addSubscription(event, users.getCurrentUser());
			
			if (success) {
				this.dialog("Iscrizione effettuata correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile registrare correttamente l'iscrizione", null, Menu.BACK_ENTRY_TITLE, dialogBackAction); 
			}
			
		};
		
		// Callback rimuovi iscrizione
		MenuAction unsubscribeAction = () -> {
			
			MenuAction dialogBackAction = () -> {this.eventMenu(event);};
			boolean success = model.getEventBoard().removeSubscription(event, model.getUsersManager().getCurrentUser());
			
			if (success) {
				this.dialog("L'iscrizione e' stata rimossa correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile annullare correttamente l'iscrizione", null, Menu.BACK_ENTRY_TITLE, dialogBackAction); 
			}
			
		};
		
		// Callback ritira proposta
		MenuAction withdrawAction = () -> {
			
			MenuAction dialogBackAction = () -> {this.eventView();};
			boolean success = model.getEventBoard().removeEvent(event);
			
			if (success) {
				this.dialog("L'evento e' stato annullato correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile rimuovere l'evento dalla bacheca", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			
		};
		
		Menu eventMenu = new Menu("Visualizzazione evento", event.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		
		// Le iscrizioni e le proposte possono essere ritirate solamente in data precedente al "Termine ultimo di ritiro iscrizione"
		Date withdrawLimit = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE);
		Date subscriptionLimit = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE);
		Date now = new Date();
		
		if (users.getCurrentUser() == event.getCreator() && now.before(withdrawLimit)) {
			eventMenu.addEntry("Ritira proposta", withdrawAction);
		}
		else if (model.getEventBoard().verifySubscription(event, model.getUsersManager().getCurrentUser()) && now.before(subscriptionLimit)) {
			eventMenu.addEntry("Iscriviti all'evento", subscriptionAction);
		}
		else if (now.before(withdrawLimit)) {
			eventMenu.addEntry("Ritira iscrizione", unsubscribeAction);
		}
		
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
	 * Permette la selezione di una categoria per la creazione di un evento.
	 */
	public void categorySelectorMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.boardMenu();};
		
		Menu categorySelector = new Menu(
				"Selezione della categoria", 
				"Seleziona la categoria, fra quelle disponibili, in cui rientra l'evento che vuoi creare", 
				Menu.BACK_ENTRY_TITLE, 
				backAction);
		
		// Callback categorie
		for (CategoryEnum category : CategoryEnum.values()) {
			
			Category completeCategory = CategoryProvider.getProvider().getCategory(category);
			
			// Per ciascuna categoria, l'azione corrispondente consiste nell'attivazione della factory
			// (Con il creatore e la categoria opportuni) e nell'invocazione di "createEventMenu"
			MenuAction categorySelectionAction = () -> {
				this.eventFactory.startCreation(this.users.getCurrentUser(), category);
				this.createEventMenu();
				};
				
			categorySelector.addEntry(completeCategory.getName(), categorySelectionAction);
			
		}
				
		this.currentMenu = categorySelector;
		
	}
	
	/**
	 * Crea il menu per la creazione dell'evento.
	 * Il menu è composto da varie entries, ciascuna delle quali permette l'acquisizione di un campo 
	 * relativo all'evento che si intende creare.
	 */
	public void createEventMenu() {
		
		// Callback per abortire la creazione dell'evento
		MenuAction abortAction = () -> {
			this.eventFactory.cancel();
			this.boardMenu();
			};
		
		String title = String.format("Creazione di un evento: %s", this.eventFactory.getProvisionalCategoryName());
		Menu createEventMenu = new Menu(title, 
				"Seleziona i campi dell'evento che vuoi impostare. \n"
				+ "I campi contrassegnati dall'asterisco (*) sono obbligatori.\n"
				+ "Quando avrai completato tutti i campi obbligatori seleziona \"Conferma\".", 
				"Annulla la creazione e torna al menu principale", abortAction);
				
		// Ciclo su tutti i campi previsti per la categoria dell'evento che voglio creare
		for (Field f : this.eventFactory.getProvisionalCategoryFields()) {
			
			/* Azione relativa ad un'opzione */
			MenuAction fieldAction = () -> {
				this.eventFactory.acquireFieldValue(f);
				// Creo il nuovo menu aggiornato
				this.createEventMenu();
				};
			
			// Creo la entry
			String entryTitle = String.format(
					CREATION_ENTRY_FORMAT,
					f.getName() + ((f.isMandatory()) ? " (*)" : ""),
					this.eventFactory.getProvisionalFieldValueString(f));
			createEventMenu.addEntry(entryTitle, fieldAction);
			
		}
		
		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (this.eventFactory.verifyMandatoryFields()) {
			createEventMenu.addEntry("Crea e pubblica l'evento", () -> {
				
				// Termino la creazione dell'evento
				Event newEvent = this.eventFactory.finalise();
				
				// Aggiungo l'evento alla bacheca
				this.model.getEventBoard().addEvent(newEvent, users.getCurrentUser());
				
				
				// Preparo il menu degli inviti
				Inviter inviter = new Inviter(newEvent, model);
				
				MenuAction dialogAction = null;
				StringBuffer dialogDescription = new StringBuffer("L'evento è stato creato e pubblicato correttamente.\nSei stato iscritto/a in automatico al tuo evento.");
				String dialogBackEntryTitle = null;
				
				// Sono presenti utenti invitabili
				if (inviter.getCandidates().size() > 0) {
					
					dialogAction = () -> {this.inviteUsersMenu(newEvent, inviter);};
					dialogBackEntryTitle = "Vai al menu degli inviti";
					
				}
				// Non ci sono candidati all'invito
				else {
					
					dialogAction = () -> {this.mainMenu();};
					dialogDescription.append("\nNon sono presenti utenti invitabili all'evento.");
					dialogBackEntryTitle = "Torna al menu principale";
					
				}
				
				// Notifico gli utenti che sono interessati alla categoria dell'evento
				inviter.sendNotifications();
				
				this.dialog(
						"Pubblicazione completata", 
						dialogDescription.toString(), 
						dialogBackEntryTitle, 
						dialogAction);
				});
		}
		
		this.currentMenu = createEventMenu;
	}
	
	public void subscriptionsMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.personalSpace();};
		
		Menu subscriptionsMenu = new Menu("Le mie iscrizioni",
										  null,
										  Menu.BACK_ENTRY_TITLE,
										  backAction);
		
		// Callback per ogni evento al quale l'utente e' iscritto ma del quale non e' creatore
		List<Event> subscriptions = model.getEventBoard().getUserSubscriptions(users.getCurrentUser());
		for (Event e : subscriptions) {
			
			if (e.getCreator() != users.getCurrentUser()) {
				
				MenuAction eventAction = () -> {this.subscribedEventMenu(e);};
				subscriptionsMenu.addEntry(e.getFieldValue(CommonField.TITOLO).toString(), eventAction);
				
			}
		}
		
		this.currentMenu = subscriptionsMenu;
		
	}
	
	public void proposalsMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.personalSpace();};
		
		Menu proposalsMenu = new Menu("Le mie proposte",
										  null,
										  Menu.BACK_ENTRY_TITLE,
										  backAction);
		
		// Callback per ogni evento creato dall'utente
		List<Event> proposals = model.getEventBoard().getEventsByAuthor(users.getCurrentUser());
		for (Event e : proposals) {
				
			MenuAction eventAction = () -> {this.proposedEventMenu(e);};
			proposalsMenu.addEntry(e.getFieldValue(CommonField.TITOLO).toString(), eventAction);
				
		}
		
		this.currentMenu = proposalsMenu;		
		
	}
	
	/**
	 * Crea il menu dedicato ad uno specifico evento all'intero dell'area personale
	 * e lo rende il menu corrente. Tale menu permette unicamente la disiscrizione 
	 * dall'evento dato.
	 * 
	 * @param event evento a cui punta il menu
	 */
	public void subscribedEventMenu(Event event) {
		
		// Callback indietro
		MenuAction backAction = () -> {this.subscriptionsMenu();};
		
		// Callback rimuovi iscrizione
		MenuAction unsubscribeAction = () -> {
			
			MenuAction dialogBackAction = () -> {this.subscriptionsMenu();};
			boolean success = model.getEventBoard().removeSubscription(event, model.getUsersManager().getCurrentUser());
			
			if (success) {
				this.dialog("L'iscrizione e' stata rimossa correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile annullare correttamente l'iscrizione", null, Menu.BACK_ENTRY_TITLE, dialogBackAction); 
			}
			
		};
		
		Menu eventMenu = new Menu("Dettagli evento", event.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		
		// Le iscrizioni e le proposte possono essere ritirate solamente in data precedente al "Termine ultimo di ritiro iscrizione"
		Date withdrawLimit = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE);
		Date now = new Date();
		
		if (now.before(withdrawLimit)) {
			eventMenu.addEntry("Ritira iscrizione", unsubscribeAction);
		}
		
		this.currentMenu = eventMenu;
		
	}
	
	/**
	 * Crea il menu dedicato al ritiro di uno specifico evento all'intero dell'area
	 * personale e lo rende il menu corrente
	 * 
	 * @param event evento a cui punta il menu
	 */
	public void proposedEventMenu(Event event) {
		
		// Callback indietro
		MenuAction backAction = () -> {this.proposalsMenu();};
		
		// Callback ritira proposta
		MenuAction withdrawAction = () -> {
			
			MenuAction dialogBackAction = () -> {this.proposalsMenu();};
			boolean success = model.getEventBoard().removeEvent(event);
			
			if (success) {
				this.dialog("L'evento e' stato annullato correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile rimuovere l'evento dalla bacheca", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			
		};
		
		Menu eventMenu = new Menu("Dettagli evento", event.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		
		// Le iscrizioni e le proposte possono essere ritirate solamente in data precedente al "Termine ultimo di ritiro iscrizione"
		Date withdrawLimit = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE);
		Date now = new Date();
		
		if (now.before(withdrawLimit)) {
			eventMenu.addEntry("Ritira proposta", withdrawAction);
		}
		
		this.currentMenu = eventMenu;
		
	}
	
	/**
	 * Crea il menu che permette al creatore di un evento di invitare altri utenti e lo rende
	 * il menu corrente
	 * 
	 * @param e L'evento appena creato, al quale si desidera invitare gli altri utenti
	 * @param inviter L'istanza di Inviter utilizzata per inviare gli inviti
	 */
	public void inviteUsersMenu(Event e, Inviter inviter) {
		
		MenuAction confirmAction = () -> {
				
				inviter.sendInvites();
				MenuAction backAction = () -> {this.mainMenu();};
				this.dialog("Inviti inviati correttamente",
						    String.format("Hai inviato %d inviti", inviter.getInvited().size()),
						    "Torna al menu principale",
						    backAction);

		};
		
		Menu inviteMenu = new Menu("Seleziona gli utenti che desideri invitare",
								   null,
								   "Conferma e invia gli inviti",
								   confirmAction);
		
		for (User u : inviter.getCandidates()) {
			
			if (u == e.getCreator()) {
				continue;
			}
			
			StringBuffer entryTitle = new StringBuffer(u.getFieldValue(UserField.NICKNAME).toString());
			MenuAction userAction = null;
			
			if (inviter.isInvited(u)) {
				entryTitle.append(" [X]");
				userAction = () -> {
					inviter.removeInvitation(u);
					this.inviteUsersMenu(e, inviter);
				};
			}
			else {
				entryTitle.append(" [ ]");
				userAction = () -> {
					inviter.addInvitation(u);
					this.inviteUsersMenu(e, inviter);
				};
			}
			
			inviteMenu.addEntry(entryTitle.toString(), userAction);
		}
		
		this.currentMenu = inviteMenu;
	}
	
	/**
	 * Crea il menu di visualizzazione degli inviti ricevuti da un utente e lo rende il menu corrente
	 */
	public void invitationsMenu() {
		
		MenuAction backAction = () -> {this.personalSpace();};
		
		String menuContent = null;
		List<Invite> userInvites = users.getCurrentUser().getMailbox().getEveryInvite();
		if (userInvites.size() == 0) {
			menuContent = "Nessun invito da visualizzare";
		}
		Menu invitationsMenu = new Menu("Inviti ricevuti", menuContent, Menu.BACK_ENTRY_TITLE, backAction);
		
		for (Invite i : userInvites) {

			MenuAction inviteAction = () -> {this.inviteMenu(i);};
			invitationsMenu.addEntry(i.toString(), inviteAction);

		}
		
		this.currentMenu = invitationsMenu;
	}
	
	/**
	 * Crea il menu utilizzato da un utente per interagire con un dato invito ad un evento
	 * 
	 * @param i L'invito di interesse dell'utente
	 */
	public void inviteMenu(Invite i) {
		
		Event event = i.getEvent();
		
		// Callback indietro
		MenuAction backAction = () -> {this.invitationsMenu();};
		
		// Callback accetta invito
		MenuAction acceptAction = () -> {
			
			if (model.getEventBoard().addSubscription(event, users.getCurrentUser())) {
				
				this.dialog("Invito accettato correttamente", null, Menu.BACK_ENTRY_TITLE, backAction);
				
			}
			else {
				this.dialog("Errore durante l'accettazione dell'invito", null, Menu.BACK_ENTRY_TITLE, backAction);
			}
			
			users.getCurrentUser().getMailbox().delete(i);
			
		};
		
		// Callback rifiuta invito
		MenuAction refuseAction = () -> {
			
			users.getCurrentUser().getMailbox().delete(i); // Cancella l'invito dalla mailbox
			this.dialog("Invito rifiutato correttamente", null, Menu.BACK_ENTRY_TITLE, backAction);
			
		};
		
		Date now = new Date();
		Date subscriptionTerm = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE);
		StringBuffer menuContent = new StringBuffer(event.toString());
		Menu inviteMenu = null;
		
		// Verifico che le iscrizioni all'evento non siano chiuse
		if (now.after(subscriptionTerm)) {
			
			menuContent.append("\n\n Non e' piu' possibile accettare l'invito: le iscrizioni all'evento sono chiuse");
			inviteMenu = new Menu("Invito ad un evento", menuContent.toString(), Menu.BACK_ENTRY_TITLE, backAction);
			
		}
		else {
			
			inviteMenu = new Menu("Invito ad un evento", menuContent.toString(), Menu.BACK_ENTRY_TITLE, backAction);
			inviteMenu.addEntry("Accetta invito", acceptAction);
			inviteMenu.addEntry("Rifiuta invito", refuseAction);

		}
		
		this.currentMenu = inviteMenu;
		
	}
	
}