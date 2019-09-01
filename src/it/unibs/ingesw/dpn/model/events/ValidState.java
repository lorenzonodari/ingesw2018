package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato VALID.
 * 
 * @author Michele Dusi
 *
 */
public class ValidState implements EventState, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2717330959234252375L;

	@Override
	public String getStateName() {
		return EventState.VALID;
	}

	/**
	 * Informa il metodo chiamante se lo stato corrente {@link ValidState} accetta o meno l'azione 
	 * di pubblicazione dell'evento.<br>
	 * È opportuno chiamare questo metodo prima di "onPublication", poiché in caso 
	 * l'azione non fosse possibile verrebbe generata un'eccezione.<br>
	 * Nello stato {@link ValidState}, si considera l'operazione di pubblicazione come possibile in 
	 * qualunque momento.
	 * <br>
	 * 
	 * @return "TRUE" se è possibile completare senza eccezioni l'azione prevista.
	 */
	public boolean canDoPublication() {
		return true;
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
