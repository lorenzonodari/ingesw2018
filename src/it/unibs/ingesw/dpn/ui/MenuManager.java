package it.unibs.ingesw.dpn.ui;

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
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;

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
	private ModelManager model;
	
	/** Riferimento al DB di utenti */
	private UsersRepository users;
	
	/** Classe per la gestione dei login */
	private LoginManager loginManager;
	
	/** Classe per la gestione dei processi di creazione/modifica di User e Event */
	private BuilderUIAssistant builderAssistant;
		
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
	public MenuManager(ModelManager model, UserInterface userInterface) {
		
		// Verifica della precondizione
		if (model == null || userInterface == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo MenuManager con parametri nulli");
		}
		
		this.userInterface = userInterface;
		this.model = model;
		this.users = model.getUsersManager();
		this.loginManager = new LoginManager(); // TODO Non dovrebbe essere il sistema di menu a crearlo
		this.builderAssistant = new BuilderUIAssistant(this.userInterface);
		
	}
	
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
	
	private Action getHomeMenuAction() {
		// Menu di home
		MenuAction homeMenuAction = new MenuAction("Menu principale", null);

		homeMenuAction.addEntry("Bacheca", getBoardMenuAction());
		homeMenuAction.addEntry("Spazio personale", getPersonalSpaceMenuAction());
		homeMenuAction.setBackEntry("Logout", getLogoutAction());
		
		return homeMenuAction;
	}
	
	private Action getLogoutAction() {
		// Callback Logout effettiva
		SimpleAction logoutAction = (userInterface) -> {
			loginManager.logout();
			userInterface.renderer().renderTextInFrame("Logout effettuato");
		};
		
		return new ConfirmAction("Procedere con il logout?", logoutAction);
	}
	
	private Action getBoardMenuAction() {
		// Menu per la bacheca
		MenuAction boardMenuAction = new MenuAction("Bacheca", null);
		
		boardMenuAction.addEntry("Visualizza eventi", getEventsViewMenuAction());
		boardMenuAction.addEntry("Visualizza categorie", getCategoriesViewMenuAction());
		boardMenuAction.addEntry("Proponi evento", getEventCreationAction());
		
		return boardMenuAction;
	}
	
	private Action getPersonalSpaceMenuAction() {
		// Menu dello Spazio Personale
		MenuAction personalSpaceMenuAction = new MenuAction("Spazio personale", null);
		
		personalSpaceMenuAction.addEntry("Notifiche", getNotificationsMenuAction());
		personalSpaceMenuAction.addEntry("Inviti", getInvitationsMenuAction());
		personalSpaceMenuAction.addEntry("Le mie iscrizioni", getSubscriptionMenuAction());
		personalSpaceMenuAction.addEntry("Le mie proposte", getProposalsMenuAction());
		personalSpaceMenuAction.addEntry("Modifica profilo", getUserEditingAction());
		
		return personalSpaceMenuAction;
	}
	
	private Action getEventsViewMenuAction() {
		MenuAction eventsViewMenuAction = new MenuAction("Lista eventi aperti", null);
		
		// Callback per gli eventi
		for (Event open : model.getEventBoard().getEventsByState(EventState.OPEN)) {
			// Associo al titolo dell'evento l'azione del menu relativo ad esso
			eventsViewMenuAction.addEntry(open.getFieldValue(CommonField.TITOLO).toString(), getEventMenuAction(open));
		}
		
		return eventsViewMenuAction;
	}
	
	private Action getCategoriesViewMenuAction() {
		// Menu per la selezione delle categorie
		MenuAction categoriesViewMenuAction = new MenuAction("Menu categorie", "Categorie di eventi disponibili:");
		
		// Callback categorie
		for (Category category : model.getAllCategories()) {
			// Associo al nome della categoria l'azione del menu relativo ad essa
			categoriesViewMenuAction.addEntry(category.getName(), getCategoryMenuAction(category));
		}
		
		return categoriesViewMenuAction;
	}
	
	private Action getEventCreationAction() {
		// Callback per la proposta di un evento
		SimpleAction eventCreationAction = (userInterface) -> {
			Event newEvent = this.builderAssistant.createEvent(loginManager);
			
			// Se l'acquisizione è stata annullata
			if (newEvent == null) {
				return; // Termino immediatamente
			}

			// Aggiungo l'evento alla bacheca
			this.model.getEventBoard().addEvent(newEvent, loginManager.getCurrentUser());
			
			//TODO Da qui in poi sarebbe da fare meglio con un InviterUIAssistant
			
			// Preparo il menu degli inviti
			Inviter inviter = new Inviter(newEvent, model);
			
			Action invitationsAction = null;
			StringBuffer dialogMessage = new StringBuffer(
					"L'evento è stato creato e pubblicato correttamente.\n"
					+ "Sei stato iscritto/a in automatico al tuo evento.");
			String dialogOption = null;
			
			// Se sono presenti utenti invitabili
			if (inviter.getCandidates().size() > 0) {
				invitationsAction = getUserInvitationsMenuAction(newEvent, inviter);
				dialogOption = "Vai al menu degli inviti";
				
			} // Se non ci sono candidati all'invito
			else {
				invitationsAction = SimpleAction.EMPTY_ACTION;
				dialogMessage.append("\nNon sono presenti utenti invitabili all'evento.");
				dialogOption = "Torna al menu principale";
				
			}
			
			// Costruisco ed eseguo la finestra di dialogo
			DialogAction dialogAction = new DialogAction(dialogMessage.toString(), dialogOption);
			dialogAction.execute(userInterface);
			
			// Eseguo l'azione degli inviti (se possibile)
			invitationsAction.execute(userInterface);
			
			// Alla fine, notifico gli utenti che sono interessati alla categoria dell'evento
			inviter.sendNotifications();
		};
		
		return eventCreationAction;		
	}
	
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
		
		return notificationsMenu;
	}
	
	private Action getInvitationsMenuAction() {
		
		String menuContent = null;
		List<Invite> userInvites = loginManager.getCurrentUser().getInvites();
		if (userInvites.size() == 0) {
			menuContent = "Nessun invito da visualizzare";
		}
		MenuAction invitationsMenuAction = new MenuAction("Inviti ricevuti", menuContent);
		
		for (Invite invite : userInvites) {
			// Aggiunge l'opzione per il menu di gestione dell'invito
			invitationsMenuAction.addEntry(invite.toString(), getInviteMenuAction(invite));
		}
		
		return invitationsMenuAction;
	}
	
	private Action getSubscriptionMenuAction() {
		// Menu di gestione delle iscrizioni
		MenuAction subscriptionsMenuAction = new MenuAction("Le mie iscrizioni", null);
		
		// Callback per ogni evento al quale l'utente e' iscritto ma del quale non e' creatore
		
		// TODO Creare nella EventBoard un metodo che filtri già gli eventi a cui si è iscritti ma senza essere creatori.
		List<Event> subscriptions = model.getEventBoard().getUserSubscriptions(loginManager.getCurrentUser());
		for (Event event : subscriptions) {
			// Per ciascuna iscrizione aggiungo un'opzione al menu
			if (event.getCreator() != loginManager.getCurrentUser()) {
				subscriptionsMenuAction.addEntry(event.getFieldValue(CommonField.TITOLO).toString(), getEventMenuAction(event));
			}
		}
		
		return subscriptionsMenuAction;
	}
	
	private Action getProposalsMenuAction() {
		// Menu di gestione degli eventi proposti
		MenuAction proposalsMenuAction = new MenuAction("Le mie proposte", null);

		// Callback per ogni evento creato dall'utente
		List<Event> proposals = model.getEventBoard().getEventsByAuthor(loginManager.getCurrentUser());
		for (Event event : proposals) {
			// Per ciascuna proposta aggiungo un'opzione al menu
			proposalsMenuAction.addEntry(event.getFieldValue(CommonField.TITOLO).toString(), getEventMenuAction(event));
		}
		
		return proposalsMenuAction;
	}
	
	private Action getUserEditingAction() {
		// Callback per la modifica dell'utente
		SimpleAction userEditingAction = (userInterface) -> {
			this.builderAssistant.editUser(this.loginManager.getCurrentUser());
			};
		
		return userEditingAction;
	}
	
	private Action getEventMenuAction(Event event) {	
		// Menu di visualizzazione di un evento (dal punto di vista dell'utente corrente)
		MenuAction eventMenuAction = new MenuAction("Visualizzazione evento", event.toString(loginManager.getCurrentUser()));
		
		// Se l'evento è creato dall'utente corrente
		if (loginManager.getCurrentUser().equals(event.getCreator())) {
			eventMenuAction.addEntry("Ritira proposta", getWithdrawnAction(event));
			
		} // Altrimenti, se l'evento NON è creato dall'utente corrente
		else {
			// Se l'utente corrente è iscritto
			if (event.hasSubscriber(loginManager.getCurrentUser())) {
				eventMenuAction.addEntry("Disiscriviti", getUnsubscriptionAction(event));
			
			} // Altrimenti, se l'utente corrente NON è iscritto
			else {
				eventMenuAction.addEntry("Iscriviti", getSubscriptionAction(event));
			}
		}

		// Aggiungo le opzioni relative alle spese
		// TODO TODO SISTEMARE IN MANIERA PIU' SENSATA TUTTO IL DISCORSO DEI FIELD USER-DEPENDANT
		OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
		Map<String, Float> costs = costsFieldValue.getValue();
		
		for (String cost : costs.keySet()) {
			
			// Callback seleziona spesa aggiuntiva
			SimpleAction selectAction = (userInterface) -> {
				costsFieldValue.registerUserToCost(loginManager.getCurrentUser(), cost);
			};
			
			// Callback rimuovi spesa aggiuntiva
			SimpleAction removeAction = (userInterface) -> {
				costsFieldValue.removeUserFromCost(loginManager.getCurrentUser(), cost);
			};
			
			if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
				String entryName = String.format("Desidero sostenere la spesa: \"%s\"", cost);
				eventMenuAction.addEntry(entryName, selectAction);
			}
			else {
				String entryName = String.format("Non desidero sostenere la spesa: \"%s\"", cost);
				eventMenuAction.addEntry(entryName, removeAction);
			}
		}
		
		return eventMenuAction;
	}
	
	private Action getCategoryMenuAction(Category category) {
		// Menu relativo ad una precisa categoria
		MenuAction categoryMenuAction = new MenuAction("Menu di categoria", category.getName());
		
		categoryMenuAction.addEntry("Visualizza informazioni dettagliate", getCategoryInfoMenuAction(category));
		
		return categoryMenuAction;
	}
	
	private Action getUserInvitationsMenuAction(Event event, Inviter inviter) {
		// Menu di selezione degli utenti da invitare
		MenuAction userInvitationsMenuAction = new MenuAction("Menu di selezione degli invitati", "Seleziona gli utenti che desideri invitare:");

		// Creo l'azione di invio degli inviti
		SimpleAction sendInvitationsAction = (userInterface) -> {
			// Invia gli inviti
			inviter.sendInvites();
			// Presenta una finestra di dialogo per confermare
			DialogAction resultDialog = new DialogAction(
					"Procedura di invito terminata correttamente.\n" + 
				    String.format("Hai inviato %d inviti", inviter.getInvited().size()),
				    "Torna al menu principale [????? DEBUG: CONTROLLARE]");
			resultDialog.execute(userInterface);
		};
		// Incapsulo l'azione di invio all'interno di un'azione di conferma
		ConfirmAction confirmInvitationsAction = new ConfirmAction("Invitare gli utenti selezionati?", sendInvitationsAction);
		confirmInvitationsAction.setOptionStrings(ConfirmAction.OptionStrings.YES_NO_OPTIONS);
		
		userInvitationsMenuAction.setBackEntry("Conferma ed invia gli inviti", confirmInvitationsAction);

		// Per ciascun utente creo un'opzione di invito
		for (User u : inviter.getCandidates()) {
			// Ad eccezione del creatore
			if (u == event.getCreator()) {
				continue;
			}
			
			StringBuffer entryTitle = new StringBuffer(u.getFieldValue(UserField.NICKNAME).toString());
			SimpleAction userAction = null;
			
			if (inviter.isInvited(u)) {
				entryTitle.append(" [X]");
				userAction = (userInterface) -> {
					inviter.removeInvitation(u);
				};
			}
			else {
				entryTitle.append(" [ ]");
				userAction = (userInterface) -> {
					inviter.addInvitation(u);
				};
			}
			
			userInvitationsMenuAction.addEntry(entryTitle.toString(), userAction);
		}
		
		return userInvitationsMenuAction;
	}
	
	private Action getDeleteNotificationsMenu() {
		// Menu di eliminazione delle notifiche
		MenuAction deleteNotificationsMenu = new MenuAction("Elimina notifiche", "Seleziona la notifica da eliminare");
		
		User currentUser = loginManager.getCurrentUser();
		
		// Se l'utente ha delle notifiche
		if (currentUser.hasNotifications()) {
			// Per ciascuna notifica
			for (Notification n : currentUser.getNotifications()) {
				// Aggiungo l'opzione di cancellazione di tale notifica
				SimpleAction deleteAction = (userInterface) -> {
					currentUser.delete(n);
				};
				
				deleteNotificationsMenu.addEntry(n.toString(), deleteAction);
			}	
		}
		
		return deleteNotificationsMenu;
	}
	
	private Action getSubscriptionAction(Event event) {
		// Azione di iscrizione ad un evento
		SimpleAction subscriptionAction = (userInterface) -> {
			boolean success = event.subscribe(loginManager.getCurrentUser());
			// Mostro all'utente il risultato dell'iscrizione
			DialogAction dialogResult = new DialogAction(
					success ?
					"Iscrizione effettuata correttamente." :
					"Non e' stato possibile registrare correttamente l'iscrizione.\nE' possibile iscriversi solamente entro il \"Termine ultimo di iscrizione\".", 
					null);
			dialogResult.execute(userInterface);
		};
		return subscriptionAction;
	}
	
	private Action getUnsubscriptionAction(Event event) {
		// Azione di disiscrizione da un evento
		SimpleAction unsubscribeAction = (userInterface) -> {
			boolean success = event.unsubscribe(loginManager.getCurrentUser());
			// Mostro all'utente il risultato della disiscrizione
			DialogAction dialogResult = new DialogAction(
					success ?
					"Iscrizione rimossa correttamente.\nPuoi iscriverti nuovamente entro il \"Termine ultimo di iscrizione\"." :
					"Non è stato possibile annullare correttamente l'iscrizione.\nE' possibile disiscriversi solamente entro il \"Termine ultimo di ritiro iscrizione\".", 
					null);
			dialogResult.execute(userInterface);		
		};
		
		return unsubscribeAction;
	}
	
	private Action getWithdrawnAction(Event event) {
		// Azione di ritiro di una proposta di evento
		SimpleAction withdrawAction = (userInterface) -> {
			boolean success = model.getEventBoard().removeEvent(event);
			// Mostro all'utente il risultato del ritiro
			DialogAction dialogResult = new DialogAction(
					success ?
					"L'evento è stato annullato correttamente." :
					"Non è stato possibile ritirare l'evento.\nRiprovare dopo la data \"Termine ultimo di ritiro iscrizione\".",
					null);
			dialogResult.execute(userInterface);
		};
		
		return withdrawAction;
	}
	
	private Action getInviteMenuAction(Invite invite) {
		/*
		StringBuffer menuContent = new StringBuffer(event.toString());
		
		if (invite.getEvent().hasUserDependantFields() && loginManager.getCurrentUser() != invite.getEvent().getCreator()) { 
			// NElla riga sopra c'è un errore:
			// Non deve succedere che un utente riceva inviti per un evento che ha creato lui stesso.
			menuContent.append("\n");
			menuContent.append("Spese opzionali scelte: \n");
			
			OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) invite.getEvent().getFieldValue(ConferenceField.SPESE_OPZIONALI);
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
		
		if (event.hasUserDependantFields() && loginManager.getCurrentUser() != event.getCreator()) {
			
			OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
			Map<String, Float> costs = costsFieldValue.getValue();
			
			for (String cost : costs.keySet()) {
				
				// Callback seleziona spesa aggiuntiva
				SimpleAction selectAction = () -> {
					costsFieldValue.registerUserToCost(loginManager.getCurrentUser(), cost);
					this.inviteMenu(i);
				};
				
				// Callback rimuovi spesa aggiuntiva
				SimpleAction removeAction = () -> {
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
		*/
		
		MenuAction inviteMenu = new MenuAction("Sei stato invitato/a al seguente evento:", invite.getEvent().toString());
		inviteMenu.addEntry("Accetta invito ed iscriviti all'evento", getInviteAcceptationAction(invite));
		inviteMenu.addEntry("Rifiuta ed elimina invito", getInviteDeclinationAction(invite));
		
		// TODO Qui c'è un errore. Alla selezione di una delle due opzioni il menu dovrebbe TERMINARE, come se fosse stata selezionata un'opzione di uscita
		
		return inviteMenu;
	}

	private Action getInviteAcceptationAction(Invite invite) {
		// Azione per l'accettazione dell'invito
		SimpleAction inviteAcceptationAction = (userInterface) -> {
			// Se la procedura di iscrizione termina correttamente
			if (invite.getEvent().subscribe(loginManager.getCurrentUser())) {
				(new DialogAction(
						"Invito accettato correttamente!\nIscrizione all'evento effettuata.", 
						"Prosegui"))
				.execute(userInterface);	
				// In questo caso elimino l'invito
				loginManager.getCurrentUser().delete(invite);	
				
			} // Altrimenti, l'invito rimane finché non viene eliminato
			else {
				(new DialogAction(
						"Non è stato possibile accettare l'invito.\nControllare la data \"Termine ultimo di iscrizione\" o il numero massimo di partecipanti.", 
						"Torna all'elenco degli inviti"))
				.execute(userInterface);	
			}			
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
	
	private Action getCategoryInfoMenuAction(Category category) {
		// Azione di visualizzazione di tutte le info della categoria
		DialogAction categoryInfoDialogAction = new DialogAction(category.toString(), "Indietro");
		
		return categoryInfoDialogAction;
	}
}