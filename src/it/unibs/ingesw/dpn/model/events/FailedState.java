package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato FAILED.
 * 
 * @author Michele Dusi
 *
 */
public class FailedState implements EventState, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7364665044961312608L;

	@Override
	public String getStateName() {
		return EventState.FAILED;
	}
	
	@Override
	public void onEntry(Event event) {
		
		// Avviso gli iscritti che la proposta e' fallita
		event.notifyEveryone(String.format(
				"L'evento %s, al quale eri iscritto/a, è fallito per mancanza di partecipanti", 
				event.getTitle()));
		
	}

}
