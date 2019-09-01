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
 * necessariamente il riferimento all'evento in questione.<br>
 * <br>
 * Il menu di gestione (compresi tutti i menu figli, secondo la struttura ad albero)
 * è creato in modo da essere aggiornato ad ogni visualizzazione, grazie all'utilizzo dell'interfaccia
 * funzionale {@link UpdatingMenuAction}.
 *
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

		// Verifico che l'evento si possa ritirare
		if (targetEvent.canBeWithdrawn() && targetEvent.getCreator().equals(currentUser)) {
			// Aggiungo l'opzione di ritiro
			eventMenuAction.addEntry("Ritira proposta", prepareWithdrawnAction(targetEvent));
		}

		// Verifico che l'utente si possa iscrivere all'evento
		if (targetEvent.canSubscribe(currentUser)) {
			// Aggiungo l'opzione di iscrizione
			eventMenuAction.addEntry("Iscriviti", prepareSubscriptionAction(targetEvent, currentUser));
		}
		
		// Verifico che l'utente si possa disiscrivere dall'evento
		if (targetEvent.canUnsubscribe(currentUser)) {
			// Aggiungo l'opzione di disiscrizione
			eventMenuAction.addEntry("Disiscriviti", prepareUnsubscriptionAction(targetEvent, currentUser));
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
			
			// Verifico che l'iscrizione sia ancora possibile
			if (targetEvent.canSubscribe(currentUser)) {

				targetEvent.subscribe(currentUser);
				// Mostro all'utente il risultato dell'iscrizione
				(new DialogAction("Iscrizione effettuata correttamente", null)).execute(userInterface);
			
				// Se l'iscrizione ha avuto successo, imposto i valori dipendenti dall'utente
				for (Field f : targetEvent.getUserDependantFields()) {
					UserDependantFieldValue fieldValue = (UserDependantFieldValue) targetEvent.getFieldValue(f);
					fieldValue.userCustomization(currentUser, userInterface);
				}
			}
			else {
				(new DialogAction("Non è stato possibile completare l'iscrizione.\n"
						+ "Controllare il termine ultimo di iscrizione o il numero massimo di partecipanti.", null)).execute(userInterface);
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
			
			// Verifico che la disiscrizione sia ancora possibile
			if (targetEvent.canUnsubscribe(currentUser)) {

				targetEvent.unsubscribe(currentUser);
				// Mostro all'utente il risultato della disiscrizione
				(new DialogAction("Iscrizione rimossa correttamente.\n"
						+ "Puoi iscriverti nuovamente entro il \"Termine ultimo di iscrizione\".", null)).execute(userInterface);
			
			}
			else {
				(new DialogAction("Non è stato possibile annullare correttamente l'iscrizione.\n"
						+ "È possibile disiscriversi solamente entro il \"Termine ultimo di ritiro iscrizione\".", null)).execute(userInterface);
			}
				
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

			// Verifico che il ritiro dell'evento sia ancora possibile
			if (targetEvent.canBeWithdrawn()) {

				this.eventBoard.removeEvent(targetEvent);
				// Mostro all'utente il risultato della rimozione
				(new DialogAction("L'evento è stato annullato correttamente.", null)).execute(userInterface);
			
			}
			else {
				(new DialogAction("Non è stato possibile ritirare l'evento.\n"
						+ "Verificare che non sia già stato ritirato o che non sia fallito.", null)).execute(userInterface);
			}
			
		};
		return withdrawAction;
	}

}
