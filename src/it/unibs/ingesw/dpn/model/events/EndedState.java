package it.unibs.ingesw.dpn.model.events;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato ENDED.
 * 
 * @author Michele Dusi
 *
 */
public class EndedState implements EventState {

	@Override
	public String getStateName() {
		return EventState.ENDED;
	}

}
