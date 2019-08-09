package it.unibs.ingesw.dpn.ui;

import java.util.Date;
import java.util.List;
import java.util.Map;

import it.unibs.ingesw.dpn.Main;
import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.users.UsersRepository;
import it.unibs.ingesw.dpn.model.users.Invite;
import it.unibs.ingesw.dpn.model.users.LoginManager;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventState;
import it.unibs.ingesw.dpn.model.events.Inviter;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;

/**
 * Classe adibita alla gestione dell'interfaccia utente. In particolare, alle istanze
 * di questa classe e' delegata la gestione dell'input dell'utente e la creazione e l'aggiornamento
 * dell'interfaccia grafica
 */
public class UIManager {
	
	private static final String LIST_ELEMENT_PREFIX = " * ";
	
	private UIRenderer renderer;
	private InputGetter inputManager;
	private ModelManager model;
	private UsersRepository users;
	private LoginManager loginManager;
	private Menu currentMenu;
	private BuilderUIAssistant builderAssistant;
		
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
		this.loginManager = new LoginManager();
		this.currentMenu = null;
		this.builderAssistant = new BuilderUIAssistant(renderer, inputManager);
		
	}
	
	/**
	 * Avvia il loop dell'interfaccia utente, all'interno del quale viene acquisita la scelta
	 * dell'utente e viene eseguita l'azione corrispondente
	 */
	public void uiLoop() {
		
		loginMenu();
		while (true) {
			
			MenuAction action = inputManager.getMenuChoice(currentMenu);
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
			if (this.loginManager.login(users, username)) {
				mainMenu();
			} else {
				this.renderer.renderError("Login fallito");
				this.loginMenu();
			}
		};
		
		// Callback Registrazione di un nuovo User
		MenuAction registerAction = () -> {
			// Creo il nuovo utente
			User newUser = this.builderAssistant.createUser(users);
			// Aggiungo l'utente alla lista di utenti
			this.users.addUser(newUser);			
			// Breve messaggio di conferma
			MenuAction loginMenuAction = () -> {this.loginMenu();};
			this.dialog("Registrazione completata", "Puoi ora procedere con il Login", "Torna al menu d'accesso", loginMenuAction);
			
		};
	
		
		Menu loginMenu = new Menu("SocialNetwork", "Benvenuto/a!", "Esci", quitAction);
		loginMenu.addEntry("Login", loginAction);
		loginMenu.addEntry("Register", registerAction);
		
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
		
		// Callback spazio inviti
		MenuAction invitationsAction = () -> {this.invitationsMenu();};
		
		// Callback spazio iscrizioni
		MenuAction subscriptionsAction = () -> {this.subscriptionsMenu();};
		
		// Callback spazio proposte
		MenuAction proposalsAction = () -> {this.proposalsMenu();};
		
		// Callback modifica utente
		MenuAction userEditingAction = () -> {
			this.builderAssistant.editUser(this.loginManager.getCurrentUser());
			this.personalSpace();
			};
		
		Menu personalSpace = new Menu("Spazio personale", backAction);
		personalSpace.addEntry("Notifiche", notificationsAction);
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
		
		User currentUser = loginManager.getCurrentUser();
		String menuContent = null;
		
		if (currentUser.hasNotifications()) {
			
			StringBuffer notifications = new StringBuffer();
			for (Notification n : currentUser.getNotifications()) {
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
		
		User currentUser = loginManager.getCurrentUser();
		
		if (currentUser.hasNotifications()) {
			
			for (Notification n : currentUser.getNotifications()) {
				
				MenuAction deleteAction = () -> {
					currentUser.delete(n);
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
			this.loginManager.logout();
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
		MenuAction eventsAction = () -> {this.eventView();};
		
		// Callback visualizza categorie
		MenuAction categoriesAction = () -> {this.categoriesMenu();};
	
		
		// Callback proponi evento
		MenuAction createAction = () -> {
			Event newEvent = this.builderAssistant.createEvent(loginManager);

			// Aggiungo l'evento alla bacheca
			this.model.getEventBoard().addEvent(newEvent, loginManager.getCurrentUser());
			
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
			
		};
		
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
			boolean success = event.subscribe(loginManager.getCurrentUser());
			
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
			boolean success = event.unsubscribe(loginManager.getCurrentUser());
			
			if (success) {
				this.dialog("L'iscrizione e' stata rimossa correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile annullare correttamente l'iscrizione", null, Menu.BACK_ENTRY_TITLE, dialogBackAction); 
			}
			
			if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
				OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
				
				for (String cost : costsFieldValue.getValue().keySet()) {
					costsFieldValue.removeUserFromCost(loginManager.getCurrentUser(), cost);
				}
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
		
		//Preparo il contenuto testuale del menu
		StringBuffer menuContent = new StringBuffer(event.toString());
		
		if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
			menuContent.append("\n");
			menuContent.append("Spese opzionali scelte: \n");
			
			OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
			Map<String, Float> costs = costsFieldValue.getValue();
			
			for (String cost : costs.keySet()) {
				
				menuContent.append(cost);
				menuContent.append(String.format(" : %.2f € ", costs.get(cost)));
				
				if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
					menuContent.append("[ ]");
				}
				else {
					menuContent.append("[X]");
				}
				
				menuContent.append("\n");
			}
			menuContent.append("\n");
			
			menuContent.append(String.format("Costo complessivo di partecipazione: %.2f €", event.getExpensesForUser(loginManager.getCurrentUser())));
		}
		
		Menu eventMenu = new Menu("Visualizzazione evento", menuContent.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		
		// Le iscrizioni e le proposte possono essere ritirate solamente in data precedente al "Termine ultimo di ritiro iscrizione"
		Date withdrawLimit = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE);
		Date subscriptionLimit = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE);
		Date now = new Date();
		
		if (loginManager.getCurrentUser() == event.getCreator() && now.before(withdrawLimit)) {
			eventMenu.addEntry("Ritira proposta", withdrawAction);
		}
		else if (event.hasSubscriber(loginManager.getCurrentUser()) && now.before(subscriptionLimit)) {
			
			eventMenu.addEntry("Iscriviti all'evento", subscriptionAction);
			
			if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
				
				OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
				Map<String, Float> costs = costsFieldValue.getValue();
				
				for (String cost : costs.keySet()) {
					
					// Callback seleziona spesa aggiuntiva
					MenuAction selectAction = () -> {
						costsFieldValue.registerUserToCost(loginManager.getCurrentUser(), cost);
						this.eventMenu(event);
					};
					
					// Callback rimuovi spesa aggiuntiva
					MenuAction removeAction = () -> {
						costsFieldValue.removeUserFromCost(loginManager.getCurrentUser(), cost);
						this.eventMenu(event);
					};
					
					if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
						String entryName = String.format("Desidero sostenere la spesa: \"%s\"", cost);
						eventMenu.addEntry(entryName, selectAction);
					}
					else {
						String entryName = String.format("Non desidero sostenere la spesa: \"%s\"", cost);
						eventMenu.addEntry(entryName, removeAction);
					}
				}
			}
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
	
	public void subscriptionsMenu() {
		
		// Callback indietro
		MenuAction backAction = () -> {this.personalSpace();};
		
		Menu subscriptionsMenu = new Menu("Le mie iscrizioni",
										  null,
										  Menu.BACK_ENTRY_TITLE,
										  backAction);
		
		// Callback per ogni evento al quale l'utente e' iscritto ma del quale non e' creatore
		List<Event> subscriptions = model.getEventBoard().getUserSubscriptions(loginManager.getCurrentUser());
		for (Event e : subscriptions) {
			
			if (e.getCreator() != loginManager.getCurrentUser()) {
				
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
		List<Event> proposals = model.getEventBoard().getEventsByAuthor(loginManager.getCurrentUser());
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
			boolean success = event.unsubscribe(loginManager.getCurrentUser());
			
			if (success) {
				this.dialog("L'iscrizione e' stata rimossa correttamente", null, Menu.BACK_ENTRY_TITLE, dialogBackAction);
			}
			else {
				this.dialog("Non e' stato possibile annullare correttamente l'iscrizione", null, Menu.BACK_ENTRY_TITLE, dialogBackAction); 
			}
			
			if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
				OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
				
				for (String cost : costsFieldValue.getValue().keySet()) {
					costsFieldValue.removeUserFromCost(loginManager.getCurrentUser(), cost);
				}
			}
			
		};
		
		StringBuffer menuContent = new StringBuffer(event.toString());
		if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
			menuContent.append("\n");
			menuContent.append("Spese opzionali scelte: \n");
			
			OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
			Map<String, Float> costs = costsFieldValue.getValue();
			
			for (String cost : costs.keySet()) {
				
				menuContent.append(cost);
				menuContent.append(String.format(" : %.2f € ", costs.get(cost)));
				
				if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
					menuContent.append("[ ]");
				}
				else {
					menuContent.append("[X]");
				}
				
				menuContent.append("\n");
			}
			menuContent.append("\n");
			
			menuContent.append(String.format("Costo complessivo di partecipazione: %.2f €", event.getExpensesForUser(loginManager.getCurrentUser())));
		}
		
		Menu eventMenu = new Menu("Dettagli evento", menuContent.toString(), Menu.BACK_ENTRY_TITLE, backAction);
		
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
		List<Invite> userInvites = loginManager.getCurrentUser().getInvites();
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
			
			if (event.subscribe(loginManager.getCurrentUser())) {
				
				this.dialog("Invito accettato correttamente", null, Menu.BACK_ENTRY_TITLE, backAction);
				
			}
			else {
				this.dialog("Errore durante l'accettazione dell'invito", null, Menu.BACK_ENTRY_TITLE, backAction);
			}
			
			loginManager.getCurrentUser().delete(i);
			
		};
		
		// Callback rifiuta invito
		MenuAction refuseAction = () -> {
			
			loginManager.getCurrentUser().delete(i); // Cancella l'invito dalla mailbox
			this.dialog("Invito rifiutato correttamente", null, Menu.BACK_ENTRY_TITLE, backAction);
			
		};
		
		Date now = new Date();
		Date subscriptionTerm = (DateFieldValue) event.getFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE);
		StringBuffer menuContent = new StringBuffer(event.toString());
		
		if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
			menuContent.append("\n");
			menuContent.append("Spese opzionali scelte: \n");
			
			OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
			Map<String, Float> costs = costsFieldValue.getValue();
			
			for (String cost : costs.keySet()) {
				
				menuContent.append(cost);
				menuContent.append(String.format(" : %.2f € ", costs.get(cost)));
				
				if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
					menuContent.append("[ ]");
				}
				else {
					menuContent.append("[X]");
				}
				
				menuContent.append("\n");
			}
			menuContent.append("\n");
			
			menuContent.append(String.format("Costo complessivo di partecipazione: %.2f €", event.getExpensesForUser(loginManager.getCurrentUser())));
		}
		
		Menu inviteMenu = null;
		
		// Verifico che le iscrizioni all'evento non siano chiuse
		if (now.after(subscriptionTerm)) {
			
			menuContent.append("\n\n Non e' piu' possibile accettare l'invito: le iscrizioni all'evento sono chiuse");
			inviteMenu = new Menu("Invito ad un evento", menuContent.toString(), Menu.BACK_ENTRY_TITLE, backAction);
			
			if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
				
				OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
				Map<String, Float> costs = costsFieldValue.getValue();
				
				for (String cost : costs.keySet()) {
					
					// Callback seleziona spesa aggiuntiva
					MenuAction selectAction = () -> {
						costsFieldValue.registerUserToCost(loginManager.getCurrentUser(), cost);
						this.inviteMenu(i);
					};
					
					// Callback rimuovi spesa aggiuntiva
					MenuAction removeAction = () -> {
						costsFieldValue.removeUserFromCost(loginManager.getCurrentUser(), cost);
						this.inviteMenu(i);
					};
					
					if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
						String entryName = String.format("Desidero sostenere la spesa: \"%s\"", cost);
						inviteMenu.addEntry(entryName, selectAction);
					}
					else {
						String entryName = String.format("Non desidero sostenere la spesa: \"%s\"", cost);
						inviteMenu.addEntry(entryName, removeAction);
					}
				}
			}
			
		}
		else {
			
			inviteMenu = new Menu("Invito ad un evento", menuContent.toString(), Menu.BACK_ENTRY_TITLE, backAction);
			inviteMenu.addEntry("Accetta invito", acceptAction);
			inviteMenu.addEntry("Rifiuta invito", refuseAction);

		}
		
		this.currentMenu = inviteMenu;
		
	}
	
}