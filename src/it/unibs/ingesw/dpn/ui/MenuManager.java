package it.unibs.ingesw.dpn.ui;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unibs.ingesw.dpn.Main;
import it.unibs.ingesw.dpn.model.persistence.Model;
import it.unibs.ingesw.dpn.model.users.UsersRepository;
import it.unibs.ingesw.dpn.ui.actions.Action;
import it.unibs.ingesw.dpn.ui.actions.CheckboxListMenuAction;
import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.ConfirmAction.OptionStrings;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;
import it.unibs.ingesw.dpn.ui.actions.SimpleAction;
import it.unibs.ingesw.dpn.model.users.Invite;
import it.unibs.ingesw.dpn.model.users.LoginManager;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventState;
import it.unibs.ingesw.dpn.model.events.Inviter;
import it.unibs.ingesw.dpn.model.events.NewEventNotifier;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.builder.UserBuilder;
import it.unibs.ingesw.dpn.model.fieldvalues.UserDependantFieldValue;

/**
 * Classe adibita alla gestione e alla creazione del sistema dei menu.
 * Richiede un riferimento all'interfaccia utente utilizzata, più un riferimento
 * agli oggetti del model.
 */
public class MenuManager {
	
	private static final String LIST_ELEMENT_PREFIX = " * ";
	
	/** Riferimento all'interfaccia utente */
	private UserInterface userInterface;
	
	/** Riferimento agli oggetti del Model */
	private Model model;
	
	/** Riferimento al DB di utenti */
	private UsersRepository users;
	
	/** Classe per la gestione dei login */
	private LoginManager loginManager;
	
	/** Classe per la gestione dei processi di creazione/modifica di User e Event */
	private BuilderUIAssistant builderAssistant;
	
	/** Classe per la gestione a livello UI di un evento */
	private EventManagementUIAssistant eventManagementAssistant;
		
	/**
	 * Crea un nuovo UIManager utilizzando il renderer dato per la creazione
	 * dell'interfaccia utente, il gestore di input utente e il gestorel del model dati.
	 * 
	 * Precondizione: model != null
	 * Precondizione: userInterface != null
	 * 
	 * @param model Il gestore dei dati di dominio da utilizzare
	 * @param userInterface L'interfaccia utente da utilizzare per i menu
	 */
	public MenuManager(Model model, UserInterface userInterface, LoginManager loginManager) {
		
		// Verifica della precondizione
		if (model == null || userInterface == null || loginManager == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo MenuManager con parametri nulli");
		}
		
		this.userInterface = userInterface;
		this.model = model;
		this.users = model.getUsersRepository();
		this.loginManager = loginManager;
		
		// Assistenti alla UI
		this.builderAssistant = new BuilderUIAssistant(this.userInterface);
		this.eventManagementAssistant = new EventManagementUIAssistant(this.userInterface, this.model.getEventBoard());
		
	}
	
	/**
	 * Restituisce il menu d'avvio del programma.
	 * 
	 * @return Il menu d'avvio del programma.
	 */
	public Action getStartMenuAction() {
		// Menu d'inizio
		MenuAction startMenuAction = new MenuAction("Social Network per Eventi", "Benvenuto/a!");
		
		// Callback Esci
		SimpleAction quitAction = (userInterface) -> {
			userInterface.renderer().renderLineSpace();
			userInterface.renderer().renderText("Programma terminato.");
			Main.terminate(Main.NO_ERROR_EXIT_CODE);
			};
		
		startMenuAction.addEntry("Login", getLoginAction());
		startMenuAction.addEntry("Registrati", getRegisterAction());
		startMenuAction.setBackEntry("Esci", quitAction);
		
		return startMenuAction;
	}
	
	/**
	 * Azione di login.<br>
	 * Richiede il nickname all'utente che intende effettuare il login.
	 * Se il nickname è presente nel database, allora il login termina con successo.
	 */
	private Action getLoginAction() {
		// Callback di login di un utente
		SimpleAction loginAction = (userInterface) -> {
			// Leggo il nuovo nickname da input
			userInterface.renderer().renderText("Nickname: ");
			String username = userInterface.getter().getString();
			// Provo a loggare
			if (loginManager.login(users, username)) {
				userInterface.renderer().renderTextInFrame("Login effettuato con successo!");
				getHomeMenuAction().execute(userInterface);
			} else {
				userInterface.renderer().renderError("Login fallito");
			}
		};
		
		return loginAction;
	}
	
	/**
	 * Azione di registrazione.<br>
	 * Permette ad un utente di iscriversi al social network,
	 * impostando le proprie informazioni personali tramite i metodi
	 * della classe {@link BuilderUIAssistant} che si intefracciano con il pattern
	 * Builder {@link UserBuilder} per la classe {@link User}.
	 */
	private Action getRegisterAction() {
		// Callback Registrazione di un nuovo User
		SimpleAction registerAction = (userInterface) -> {
			// Creo il nuovo utente
			User newUser = this.builderAssistant.createUser(users);
			// Aggiungo l'utente alla lista di utenti
			this.users.addUser(newUser);			
			// Breve messaggio di conferma
			this.userInterface.renderer().renderTextInFrame("Registrazione completata!");
		};
		
		return registerAction;
	}
	
	/**
	 * Menu home di un utente.<br>
	 * Permette l'accesso alla bacheca o al proprio spazio personale.<br>
	 * Uscendo viene effettuato il logout.
	 */
	private Action getHomeMenuAction() {
		// Menu di home
		MenuAction homeMenuAction = new MenuAction("Menu principale", null);

		homeMenuAction.addEntry("Bacheca", getBoardMenuAction());
		homeMenuAction.addEntry("Spazio personale", getPersonalSpaceMenuAction());
		homeMenuAction.setBackEntry("Logout", getLogoutAction());
		
		return homeMenuAction;
	}
	
	/**
	 * Azione di logout.<br>
	 * Effettua il logout dell'utente dal programma.
	 */
	private Action getLogoutAction() {
		// Callback Logout effettiva
		SimpleAction logoutAction = (userInterface) -> {
			loginManager.logout();
			userInterface.renderer().renderTextInFrame("Logout effettuato");
		};
		
		return logoutAction;
	}
	
	/**
	 * Menu Bacheca degli eventi.<br>
	 * Permette l'accesso ai sotto-menu per la visualizzazione degli eventi aperti
	 * o della lista di categorie. Permette inoltre l'aggiunta di un nuovo evento.
	 */
	private Action getBoardMenuAction() {
		// Menu per la bacheca
		MenuAction boardMenuAction = new MenuAction("Bacheca", null);
		
		boardMenuAction.addEntry("Visualizza eventi aperti", getEventsViewMenuAction());
		boardMenuAction.addEntry("Visualizza categorie", getCategoriesViewMenuAction());
		boardMenuAction.addEntry("Proponi evento", getEventCreationAction());
		
		return boardMenuAction;
	}
	
	/**
	 * Menu "Spazio personale".<br>
	 * Permette l'accesso ai seguenti sottomenu:
	 * <ul>
	 * 	<li> Notifiche </li>
	 * 	<li> Inviti </li>
	 * 	<li> Le mie iscrizioni </li>
	 * 	<li> Le mie proposte </li>
	 * 	<li> Modifica profilo </li>
	 * </ul>
	 */
	private Action getPersonalSpaceMenuAction() {
		// Menu dello Spazio Personale
		MenuAction personalSpaceMenuAction = new MenuAction("Spazio personale", null);
		
		personalSpaceMenuAction.addEntry("Notifiche", getNotificationsMenuAction());
		personalSpaceMenuAction.addEntry("Inviti", getInvitationsMenuAction());
		personalSpaceMenuAction.addEntry("Le mie iscrizioni", getSubscriptionsMenuAction());
		personalSpaceMenuAction.addEntry("Le mie proposte", getProposalsMenuAction());
		personalSpaceMenuAction.addEntry("Modifica profilo", getUserEditingAction());
		
		return personalSpaceMenuAction;
	}
	
	/**
	 * Azione associata all'entry "Visualizza eventi aperti".<br>
	 * Presenta un'opzione per ciascun evento aperto presente in bacheca.
	 */
	private Action getEventsViewMenuAction() {
		MenuAction eventsViewMenuAction = new MenuAction("Lista eventi aperti", null);
		
		// Callback per gli eventi
		for (Event open : model.getEventBoard().getEventsByState(EventState.OPEN)) {
			// Associo al titolo dell'evento l'azione del menu relativo ad esso
			eventsViewMenuAction.addEntry(open.getTitle(), getEventManagementMenuAction(open));
		}
		
		return eventsViewMenuAction;
	}
	
	/**
	 * Menu di visualizzazione della lista di categorie<br>
	 * Per ciascuna categoria presenta un'opzione per la visualizzazione delle sue informazioni
	 * specifiche.
	 */
	private Action getCategoriesViewMenuAction() {
		// Menu per la selezione delle categorie
		MenuAction categoriesViewMenuAction = new MenuAction("Menu categorie", "Categorie di eventi disponibili:");
		
		// Callback categorie
		for (Category category : Category.values()) {
			// Associo al nome della categoria l'azione del menu relativo ad essa
			categoriesViewMenuAction.addEntry(category.getName(), getCategoryMenuAction(category));
		}
		
		return categoriesViewMenuAction;
	}
	
	/**
	 * Azione di creazione di un evento.<br>
	 * Si avvale dei metodi di {@link BuilderUIAssistantAssistant} che fornisce
	 * un'interfaccia utente comoda per utilizzare la classe {@link Builder} per creare
	 * oggetti {@link Event}.
	 */
	private Action getEventCreationAction() {
		// Callback per la proposta di un evento
		SimpleAction eventCreationAction = (userInterface) -> {
			Event newEvent = this.builderAssistant.createEvent(loginManager);
			
			// Se l'acquisizione è stata annullata
			if (newEvent == null) {
				return; // Termino immediatamente
			}

			// Aggiungo l'evento alla bacheca
			this.model.getEventBoard().addEvent(newEvent);
			// Messaggio di conferma
			(new DialogAction("L'evento è stato creato e pubblicato correttamente.\n"
					+ "Sei stato iscritto/a in automatico al tuo evento.", 
					"Avanti")).execute(userInterface);
			
			// Preparo il menu degli inviti
			Inviter inviter = new Inviter(newEvent, model.getEventBoard());
			NewEventNotifier notifier = new NewEventNotifier(newEvent, model.getUsersRepository());
			
			// Se sono presenti utenti invitabili
			if (inviter.getCandidates().size() > 0) {
				// Preparo l'azione di invito
				Action invitationsAction = getUserInvitationsMenuAction(inviter);
				// Inglobo l'azione in un'azione di conferma
				ConfirmAction confirmInvitationsAction = new ConfirmAction(
						"Vuoi procedere con l'invio di inviti all'evento?", 
						invitationsAction);
				confirmInvitationsAction.setOptionStrings(OptionStrings.YES_NO_OPTIONS);
				confirmInvitationsAction.execute(userInterface);
				
			} // Se non ci sono candidati all'invito
			else {
				(new DialogAction(
						"Non sono presenti utenti invitabili all'evento.", 
						"Torna al menu principale"))
				.execute(userInterface);
			}
			
			// Alla fine, notifico gli utenti che sono interessati alla categoria dell'evento
			notifier.sendNotifications();
		};
		
		return eventCreationAction;		
	}
	
	/**
	 * Menu delle notifiche.<br>
	 * Per ciascuna notifica visualizza la entry per la visualizzazione della notifica.
	 */
	private Action getNotificationsMenuAction() {
		// Recupero l'utente corrente
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
			
		} else {
			menuContent = "Nessuna notifica";
		}
		
		MenuAction notificationsMenu = new MenuAction("Notifiche Personali", menuContent);
		notificationsMenu.addEntry("Cancella notifiche", getDeleteNotificationsMenu());
		// TODO AGGIORNAMENTO DEL MENU
		
		return notificationsMenu;
	}
	
	/**
	 * Menu per la visualizzazione degli inviti.<br>
	 * Se non sono presenti inviti, il menu presenta la scritta "Nessun invito da visualizzare".
	 * 
	 * TODO SOLITO PROBLEMA, SE ACCETTO O RIFIUTO UN INVITO IL MENU NON SI AGGIORNA
	 * 
	 */
	private Action getInvitationsMenuAction() {
		
		String menuContent = null;
		List<Invite> userInvites = loginManager.getCurrentUser().getInvites();
		if (userInvites.size() == 0) {
			menuContent = "Nessun invito da visualizzare";
		}
		MenuAction invitationsMenuAction = new MenuAction("Inviti ricevuti", menuContent);
		
		for (Invite invite : userInvites) {
			// Aggiunge l'opzione per il menu di gestione dell'invito
			invitationsMenuAction.addEntry(invite.toString(), getInviteConfirmAction(invite));
		}
		
		return invitationsMenuAction;
	}
	
	/**
	 * Presenta la lista di eventi a cui un utente è iscritto.<br>
	 */
	private Action getSubscriptionsMenuAction() {
		// Menu di gestione delle iscrizioni
		MenuAction subscriptionsMenuAction = new MenuAction("Le mie iscrizioni", null);
		
		// Callback per ogni evento al quale l'utente e' iscritto ma del quale non e' creatore
		List<Event> subscriptions = model.getEventBoard().getOpenSubscriptionsNotProposedByUser(loginManager.getCurrentUser());
		for (Event event : subscriptions) {
			// Per ciascuna iscrizione aggiungo un'opzione al menu
			subscriptionsMenuAction.addEntry(
					event.getTitle(), 
					getEventManagementMenuAction(event));
		}
		
		return subscriptionsMenuAction;
	}
	
	/**
	 * Restituisce il menu con la lista di eventi creati e pubblicati dall'utente.<br>
	 * Si avvale dei metodi contenuti nella classe {@link EventManagementUIAssistant}.
	 */
	private Action getProposalsMenuAction() {
		// Menu di gestione degli eventi proposti
		MenuAction proposalsMenuAction = new MenuAction("Le mie proposte", null);

		// Callback per ogni evento creato dall'utente
		List<Event> proposals = model.getEventBoard().getEventsByAuthor(loginManager.getCurrentUser());
		for (Event event : proposals) {			
			// Per ciascuna proposta aggiungo un'opzione al menu
			proposalsMenuAction.addEntry(event.getTitle(), getEventManagementMenuAction(event));
		}
		
		return proposalsMenuAction;
	}
	
	/**
	 * Restituisce l'azione per la modifica dei dati dell'utente.<br>
	 * Si avvale dei metodi della classe {@link BuilderUIAssistant}.
	 */
	private Action getUserEditingAction() {
		// Callback per la modifica dell'utente
		SimpleAction userEditingAction = (userInterface) -> {
			this.builderAssistant.editUser(this.loginManager.getCurrentUser());
			};
		
		return userEditingAction;
	}
	
	/**
	 * Restituisce il menu relativo alla gestione di una categoria.<br>
	 * Al momento, le azioni previste per una categoria sono:
	 * <ul>
	 * 	<li> Visualizza informazioni dettagliate </li>
	 * </ul>
	 * 
	 * @param category La categoria che si vuole gestire
	 * @return Il menu di una categoria.
	 */
	private Action getCategoryMenuAction(Category category) {
		// Menu relativo ad una precisa categoria
		MenuAction categoryMenuAction = new MenuAction("Menu di categoria", category.getName());
		
		categoryMenuAction.addEntry("Visualizza informazioni dettagliate", getCategoryInfoMenuAction(category));
		
		return categoryMenuAction;
	}
	
	/**
	 * Restituisce il menu per selezionare gli utenti da inviare.
	 * 
	 * @param inviter L'inviter
	 */
	private Action getUserInvitationsMenuAction(Inviter inviter) {
		// Lista di candidati
		Set<User> candidates = inviter.getCandidates();
		Map<User, String> candidatesNicknames = new LinkedHashMap<>();
		for (User candidate : candidates) {
			candidatesNicknames.put(candidate, candidate.getNickname());
		}
		// Menu di selezione
		CheckboxListMenuAction<User> userInvitationsMenuAction = new CheckboxListMenuAction<User>(
				"Menu di selezione degli invitati",
				"Seleziona gli utenti che desideri invitare:",
				candidatesNicknames);
		
		// Creo l'azione di conferma degli inviti
		SimpleAction finishInvitationsAction = (userInterface) -> {
			// Aggiungo gli inviti
			inviter.addAllInvitations(new HashSet<>(userInvitationsMenuAction.getSelectedObjects()));
			// Invia gli inviti
			inviter.sendInvites();
			// Presenta una finestra di dialogo per confermare
			(new DialogAction(
					"Procedura di invito terminata correttamente.\n" + 
				    String.format("Hai inviato %d inviti", inviter.getInvited().size()),
				    "Torna al menu principale"))
			.execute(userInterface);
		};
		userInvitationsMenuAction.setBackEntry("Conferma ed invia gli inviti", finishInvitationsAction);
		
		return userInvitationsMenuAction;
	}
	
	/**
	 * Menu di eliminazione delle notifiche.
	 * L'eliminazione delle notifiche avviene selezionando quelle da cancellare e cliccando sul tasto per confermare.
	 */
	private Action getDeleteNotificationsMenu() {
		// Preparo la lista di notifiche e delle relative descrizioni da visualizzare
		Map<Notification, String> notificationsDescriptions = new LinkedHashMap<>();
		for (Notification notif : loginManager.getCurrentUser().getNotifications()) {
			notificationsDescriptions.put(notif, notif.toString());
		}
		
		// Menu di eliminazione delle notifiche
		CheckboxListMenuAction<Notification> deleteNotificationsMenu = new CheckboxListMenuAction<>(
				"Elimina notifiche",
				"Selezione le notifiche da eliminare, quindi seleziona \"Conferma\":",
				notificationsDescriptions);
		
		// Preparo l'azione di eliminazione delle notifiche
		SimpleAction deleteNotificationsAction = (userInterface) -> {
			for (Notification notif : deleteNotificationsMenu.getSelectedObjects()) {
				loginManager.getCurrentUser().delete(notif);
			}
		};
		
		deleteNotificationsMenu.setBackEntry("Conferma ed elimina le notifiche selezionate", deleteNotificationsAction);
		
		return deleteNotificationsMenu;
	}
	
	/**
	 * 
	 * @param invite
	 * @return
	 */
	private Action getInviteConfirmAction(Invite invite) {
		// Menu di accettazione		
		ConfirmAction inviteMenuAction = new ConfirmAction(
				"Sei stato/a invitato/a al seguente evento:\n\n" 
						+ invite.getEvent().toString(loginManager.getCurrentUser())
						+ "\n\nVuoi accettare l'invito ed iscriverti all'evento?",
				getInviteAcceptationAction(invite));
		// In caso di rifiuto
		inviteMenuAction.setCancelAction(getInviteDeclinationAction(invite));
		// Personalizzazione delle opzioni
		inviteMenuAction.setOptionStrings(OptionStrings.YES_NO_OPTIONS);
		
		return inviteMenuAction;
	}

	private Action getInviteAcceptationAction(Invite invite) {
		// Azione per l'accettazione dell'invito
		SimpleAction inviteAcceptationAction = (userInterface) -> {
			// Eseguo la procedura di iscrizione
			getSubscriptionAction(invite.getEvent()).execute(userInterface);
			// Elimino l'invito
			loginManager.getCurrentUser().delete(invite);		
		};
		
		return inviteAcceptationAction;
	}
	
	private Action getInviteDeclinationAction(Invite invite) {
		// Azione di cancellazione e declinazione dell'invito
		SimpleAction inviteDeclinationAction = (userInterface) -> {
			loginManager.getCurrentUser().delete(invite); // Cancella l'invito dalla MailBox dell'utente
			(new DialogAction(
					"Invito cancellato correttamente.", 
					"Torna all'elenco degli inviti"))
			.execute(userInterface);
		};
		
		return inviteDeclinationAction;
	}
	
	/**
	 * Azione di iscrizione dell'utente corrente all'evento.
	 * 
	 * @param targetEvent L'evento in questione
	 * @return L'azione di iscrizione all'evento come oggetto {@link Action}
	 */
	private Action getSubscriptionAction(Event targetEvent) {
		// Azione di iscrizione ad un evento
		SimpleAction subscriptionAction = (userInterface) -> {
			boolean success = targetEvent.subscribe(loginManager.getCurrentUser());
			// Mostro all'utente il risultato dell'iscrizione
			DialogAction dialogResult = new DialogAction(
					success ?
					"Iscrizione effettuata correttamente." :
					"Non e' stato possibile registrare correttamente l'iscrizione.\nE' possibile iscriversi solamente entro il \"Termine ultimo di iscrizione\".", 
					null);
			dialogResult.execute(userInterface);
			
			// Se l'iscrizione non ha avuto successo, termino qui l'azione
			if (!success) {
				return;
			}
			
			// Se l'iscrizione ha avuto successo, imposto i valori dipendenti dall'utente

			for (Field f : targetEvent.getUserDependantFields()) {
				UserDependantFieldValue fieldValue = (UserDependantFieldValue) targetEvent.getFieldValue(f);
				fieldValue.userCustomization(loginManager.getCurrentUser(), userInterface);
			}

		};
		return subscriptionAction;
	}
	
	/**
	 * Restituisce l'azione di gestione evento.
	 * 
	 * @param event L'evento in questione
	 */
	private Action getEventManagementMenuAction(Event event) {
		// Creo l'azione
		SimpleAction eventManagementMenuAction = (userInterface) -> {
			eventManagementAssistant.manageEvent(event, this.loginManager.getCurrentUser());
		};	
		
		return eventManagementMenuAction;
	}
	
	/**
	 * Azione di visualizzazione delle info di una categoria.
	 */
	private Action getCategoryInfoMenuAction(Category category) {
		// Azione di visualizzazione di tutte le info della categoria
		DialogAction categoryInfoDialogAction = new DialogAction(category.toString(), "Indietro");
		
		return categoryInfoDialogAction;
	}
}