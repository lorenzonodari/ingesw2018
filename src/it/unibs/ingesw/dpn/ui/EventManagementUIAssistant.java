package it.unibs.ingesw.dpn.ui;

import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventBoard;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.UserDependantFieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.ui.actions.Action;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;
import it.unibs.ingesw.dpn.ui.actions.SimpleAction;
import it.unibs.ingesw.dpn.ui.actions.UpdatingMenuAction;

/**
 * Affianca la classe {@link MenuManager} nella gestione di uno specifico menu
 * per la gestione di un evento.
 * Necessita di un riferimento all'interfaccia utente attualmente in uso, più
 * necessariamente il riferimento all'evento in questione.
 * 
 * @author Michele Dusi
 *
 */
public class EventManagementUIAssistant {
	
	private EventBoard eventBoard;
	
	/**
	 * Costruttore data l'interfaccia utente e la bacheca.
	 * 
	 * Precondizione: userInterface != null
	 * Precondizione: eventBoard != null
	 * 
	 * @param userInterface L'interfaccia utente attualmente in uso
	 * @param eventBoard La bacheca attualmente in uso nel programma
	 */
	public EventManagementUIAssistant(EventBoard eventBoard) {
		// Verifica delle precondizioni
		if (eventBoard == null) {
			throw new IllegalArgumentException("Impossibile istanziare la classe EventManagementUIAssistant con parametri nulli");
		}
		
		this.eventBoard = eventBoard;
	}
	
	/**
	 * Presenta il menu di gestione dell'evento.<br>
	 * Ogni volta che viene selezionata un'opzione, il menu si aggiorna di conseguenza.<br>
	 * <br>
	 * Tale menu può comprendere (a seconda di specifiche condizioni) le seguenti opzioni:
	 * <ul>
	 * 	<li> L'opzione di iscrizione, se l'utente corrente NON è il creatore e NON è iscritto. </li>
	 * 	<li> L'opzione di disiscrizione, se l'utente corrente NON è il creatore ed è iscritto. </li>
	 * 	<li> L'opzione di ritiro, se l'utente corrente È il creatore. </li>
	 * </ul>
	 * 
	 * Precondizione: targetEvent != null
	 * Precondizione: currentUser != null
	 * 
	 * @param targetEvent L'evento target da gestire
	 * @param currentUser L'utente corrente
	 */
	public Action getEventManagementMenuAction(Event targetEvent, User currentUser) {
		// Verifica delle precondizioni
		if (targetEvent == null || currentUser == null) {
			throw new IllegalArgumentException("Impossibile procedere senza un riferimento valido all'utente corrente o all'evento target");
		}
		
		// Preparo l'azione
		UpdatingMenuAction eventManagementMenuAction = () -> {
			return prepareEventManagementMenuAction(targetEvent, currentUser);
		};
		
		return eventManagementMenuAction;
	}
	
	/**
	 * Restituisce (costruendolo ogni volta che il metodo viene chiamato) il menu 
	 * di gestione dell'evento.<br>
	 * <br>
	 * Tale menu può comprendere (a seconda di specifiche condizioni) le seguenti opzioni:
	 * <ul>
	 * 	<li> L'opzione di iscrizione, se l'utente corrente NON è il creatore e NON è iscritto. </li>
	 * 	<li> L'opzione di disiscrizione, se l'utente corrente NON è il creatore ed è iscritto. </li>
	 * 	<li> L'opzione di ritiro, se l'utente corrente È il creatore. </li>
	 * </ul>
	 * 
	 * Precondizione: targetEvent != null
	 * Precondizione: currentUser != null
	 * 
	 * @param targetEvent L'evento in questione
	 * @param currentUser L'utente corrente
	 */
	private MenuAction prepareEventManagementMenuAction(Event targetEvent, User currentUser) {
		// Verifica delle precondizioni
		if (targetEvent == null || currentUser == null) {
			throw new IllegalArgumentException("Impossibile procedere senza un riferimento valido all'utente corrente o all'evento target");
		}
		
		// Menu di visualizzazione di un evento 
		// (dal punto di vista dell'utente corrente, quindi visualizzando anche i campi dipendenti dall'utente)
		MenuAction eventMenuAction = new MenuAction(
				"Visualizzazione evento", 
				targetEvent.toString(currentUser));
		
		// Se l'evento è creato dall'utente corrente
		if (currentUser.equals(targetEvent.getCreator())) {
			// Aggiungo l'opzione di ritiro
			eventMenuAction.addEntry("Ritira proposta", prepareWithdrawnAction(targetEvent));
			
		} // Altrimenti, se l'evento NON è creato dall'utente corrente
		else {
			// Se l'utente corrente è iscritto
			if (targetEvent.hasSubscriber(currentUser)) {
				eventMenuAction.addEntry("Disiscriviti", prepareUnsubscriptionAction(targetEvent, currentUser));
			
			} // Altrimenti, se l'utente corrente NON è iscritto
			else {
				eventMenuAction.addEntry("Iscriviti", prepareSubscriptionAction(targetEvent, currentUser));
			}
		}
		
		return eventMenuAction;
	}

	/**
	 * Azione di iscrizione dell'utente corrente all'evento.
	 * 
	 * @param targetEvent L'evento in questione
	 * @param currentUser L'utente corrente
	 * @return L'azione di iscrizione all'evento come oggetto {@link Action}
	 */
	private Action prepareSubscriptionAction(Event targetEvent, User currentUser) {
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

	/**
	 * Azione di disiscrizione di un utente ad un evento.
	 * 
	 * @param targetEvent L'evento in questione
	 * @param currentUser l'utente corrente che verrà disiscritto
	 * @return L'azione di disiscrizione come oggetto {@link Action}
	 */
	private Action prepareUnsubscriptionAction(Event targetEvent, User currentUser) {
		// Azione di disiscrizione da un evento
		SimpleAction unsubscribeAction = (userInterface) -> {
			boolean success = targetEvent.unsubscribe(currentUser);
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
	
	/**
	 * Azione di ritiro di un evento.<br>
	 * Poiché questo metodo è privato, è già stato verificato che
	 * l'utente corrente è il creatore dell'evento.
	 * 
	 * @param targetEvent L'evento in questione
	 * @return L'azione di ritiro dell'evento come oggetto {@link Action}
	 */
	private Action prepareWithdrawnAction(Event targetEvent) {
		// Azione di ritiro di una proposta di evento
		SimpleAction withdrawAction = (userInterface) -> {
			boolean success = this.eventBoard.removeEvent(targetEvent);
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

}
