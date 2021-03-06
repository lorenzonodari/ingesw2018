package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato VALID.
 * 
 * @author Michele Dusi
 *
 */
public class WithdrawnState implements EventState, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5324549079551219879L;

	@Override
	public String getStateName() {
		return EventState.WITHDRAWN;
	}
	
	public void onEntry(Event e) {
		
		String eventName = e.getTitle();
		
		String partecipantsMessage = String.format("L'evento \"%s\", al quale eri iscritto, e' stato ritirato", eventName);
		e.notifyPartecipants(partecipantsMessage);
		
		String creatorMessage = String.format("Hai ritirato l'evento \"%s\"", eventName);
		e.notifyCreator(creatorMessage);
		
	}

}
