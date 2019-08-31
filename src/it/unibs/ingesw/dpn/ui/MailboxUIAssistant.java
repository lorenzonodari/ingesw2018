package it.unibs.ingesw.dpn.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.UserDependantFieldValue;
import it.unibs.ingesw.dpn.model.users.Invite;
import it.unibs.ingesw.dpn.model.users.LoginManager;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.ui.actions.Action;
import it.unibs.ingesw.dpn.ui.actions.CheckboxListMenuAction;
import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;
import it.unibs.ingesw.dpn.ui.actions.SimpleAction;
import it.unibs.ingesw.dpn.ui.actions.UpdatingMenuAction;
import it.unibs.ingesw.dpn.ui.actions.ConfirmAction.OptionStrings;

public class MailboxUIAssistant {

	private static final String NOTIFICATION_STRING_FORMAT = " * %s\n";
	
	private LoginManager loginManager;
	private User currentUser;
	
	/**
	 * Costruttore.<br>
	 * Richiede un oggetto "loginManager" {@link LoginManager}, il quale conterrà il riferimento
	 * corretto all'utente corrente.<br>
	 * Questo permette all'oggetto {@link MailboxUIAssistant} di gestire la mailbox dell'utente corretto.
	 * 
	 * @param loginManager L'oggetto contenente il riferimento all'utente loggato.
	 */
	public MailboxUIAssistant(LoginManager loginManager) {
		// Verifica delle precondizioni
		if (loginManager == null) {
			throw new IllegalArgumentException("Impossibile istanziare la classe \"MailboxUIAssistant\" senza un riferimento valido ad un oggetto \"LoginManager\"");
		}
		this.loginManager = loginManager;
	}
	
	/**
	 * Imposta correttamente il riferimento all'utente corrente.
	 * Questo metodo viene chiamato ogni volta che è necessario ricreare i menu di gestione delle
	 * notifiche e degli inviti.
	 */
	private void updateCurrentUser() {
		this.currentUser = loginManager.getCurrentUser();
	}
	
	public Action getNotificationsManagementMenuAction() {
		// Aggiorno l'utente corrente
		this.updateCurrentUser();
		
		// Creo l'azione/menu che si aggiorna in automatico
		UpdatingMenuAction notificationsManagementMenuAction = () -> {
			return prepareNotificationsMenuAction();
		};
		
		return notificationsManagementMenuAction;
	}
	
	public Action getInvitationsManagementMenuAction() {
		// Aggiorno l'utente corrente
		this.updateCurrentUser();
		
		// Creo l'azione/menu che si aggiorna in automatico
		UpdatingMenuAction invitationsManagementMenuAction = () -> {
			return prepareInvitationsManagementMenuAction();
		};
		
		return invitationsManagementMenuAction;
	}
	
	/**
	 * Costruisce il menu di gestione delle notifiche.<br>
	 * Se non sono presenti notifiche, visualizza la scritta "nessuna notifica presente".<br>
	 * Se sono presenti notifiche, presenta l'opzione per cancellarle.<br>
	 * 
	 * @return Il menu di gestione delle notifiche.
	 */
	private MenuAction prepareNotificationsMenuAction() {
		String menuContent = null;
		
		// Se esistono notifiche, costruisco il testo del menu
		if (currentUser.hasNotifications()) {
			
			StringBuffer notifications = new StringBuffer();
			for (Notification n : currentUser.getNotifications()) {
				notifications.append(String.format(NOTIFICATION_STRING_FORMAT, n.toString()));
			}
			menuContent = notifications.toString();
			
		} else {
			// Altrimenti segnalo che non sono presenti notifiche
			menuContent = "Nessuna notifica";
		}
		
		MenuAction notificationsMenu = new MenuAction("Notifiche Personali", menuContent);
		
		// Solo se ho notifiche aggiungo l'opzione per la cancellazione
		if (currentUser.hasNotifications()) {
			notificationsMenu.addEntry("Cancella notifiche", getDeleteNotificationsMenu());
		}
		
		return notificationsMenu;
	}

	
	/**
	 * Menu di eliminazione delle notifiche.<br>
	 * L'eliminazione delle notifiche avviene selezionando quelle da cancellare e cliccando sul tasto per confermare.
	 */
	private Action getDeleteNotificationsMenu() {
		// Preparo la lista di notifiche e delle relative descrizioni da visualizzare
		Map<Notification, String> notificationsDescriptions = new LinkedHashMap<>();
		for (Notification notif : currentUser.getNotifications()) {
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
				currentUser.delete(notif);
			}
		};
		
		deleteNotificationsMenu.setBackEntry("Conferma ed elimina le notifiche selezionate", deleteNotificationsAction);
		
		return deleteNotificationsMenu;
	}
	

	/**
	 * Menu per la visualizzazione degli inviti.<br>
	 * Se non sono presenti inviti, il menu presenta la scritta "Nessun invito da visualizzare". 
	 */
	private MenuAction prepareInvitationsManagementMenuAction() {
		
		String menuContent = null;
		List<Invite> userInvites = currentUser.getInvites();
		
		// Se non sono presenti inviti, lo segnalo all'utente
		if (userInvites.isEmpty()) {
			menuContent = "Nessun invito da visualizzare";
			
		} else {
			// Altrimenti, mostro le istruzioni da seguire
			menuContent = "Seleziona l'invito di tuo interesse per accettare o rifiutare:";
		}
		
		MenuAction invitationsMenuAction = new MenuAction("Inviti ricevuti", menuContent);
		
		for (Invite invite : userInvites) {
			// Aggiunge l'opzione per il menu di gestione dell'invito
			invitationsMenuAction.addEntry(invite.toString(), getInviteConfirmAction(invite));
		}
		
		return invitationsMenuAction;
	}

	/**
	 * Restituisce il menu di conferma per un invito.
	 * 
	 * @param invite L'invito in questione
	 */
	private Action getInviteConfirmAction(Invite invite) {
		// Menu di accettazione		
		ConfirmAction inviteMenuAction = new ConfirmAction(
				"Sei stato/a invitato/a al seguente evento:\n\n" 
						+ invite.getEvent().toString(currentUser)
						+ "\n\nVuoi accettare l'invito ed iscriverti all'evento?",
				getInviteAcceptationAction(invite));
		// In caso di rifiuto
		inviteMenuAction.setCancelAction(getInviteDeclinationAction(invite));
		// Personalizzazione delle opzioni
		inviteMenuAction.setOptionStrings(OptionStrings.YES_NO_OPTIONS);
		
		return inviteMenuAction;
	}

	/**
	 * Azione di accettazione dell'invito.
	 * 
	 * @param invite L'invito che viene accettato
	 */
	private Action getInviteAcceptationAction(Invite invite) {
		// Azione per l'accettazione dell'invito
		SimpleAction inviteAcceptationAction = (userInterface) -> {
			// Eseguo la procedura di iscrizione
			getSubscriptionAction(invite.getEvent()).execute(userInterface);
			// Elimino l'invito
			currentUser.delete(invite);		
		};
		
		return inviteAcceptationAction;
	}

	/**
	 * Azione di rifiuto dell'invito.
	 * 
	 * @param invite L'invito che viene declinato
	 */
	private Action getInviteDeclinationAction(Invite invite) {
		// Azione di cancellazione e declinazione dell'invito
		SimpleAction inviteDeclinationAction = (userInterface) -> {
			currentUser.delete(invite); // Cancella l'invito dalla MailBox dell'utente
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
			boolean success = targetEvent.subscribe(currentUser);
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
				fieldValue.userCustomization(currentUser, userInterface);
			}

		};
		return subscriptionAction;
	}

}
