package it.unibs.ingesw.dpn.model.events;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato FAILED.
 * 
 * @author Michele Dusi
 *
 */
public class FailedState implements EventState {

	@Override
	public String getStateName() {
		return EventState.FAILED;
	}

}
