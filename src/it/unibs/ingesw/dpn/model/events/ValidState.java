package it.unibs.ingesw.dpn.model.events;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato VALID.
 * 
 * @author Michele Dusi
 *
 */
public class ValidState implements EventState {

	@Override
	public String getStateName() {
		return EventState.VALID;
	}
	
	/**
	 * L'evento è stato pubblicato.
	 * La pubblicazione comporta il passaggio di stato da {@link ValidState} a {@link OpenState}.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onPublication(Event e) {
		e.setState(new OpenState());
	}

}
