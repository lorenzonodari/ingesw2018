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

}
