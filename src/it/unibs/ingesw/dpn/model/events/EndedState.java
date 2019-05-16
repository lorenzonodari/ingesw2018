package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato ENDED.
 * 
 * @author Michele Dusi
 *
 */
public class EndedState implements EventState, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3031726619770153087L;

	@Override
	public String getStateName() {
		return EventState.ENDED;
	}

}
