package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

import it.unibs.ingesw.dpn.model.fields.CommonField;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato ONGOING.
 * 
 * @author Michele Dusi
 *
 */
public class OngoingState implements EventState, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7078527717165157016L;

	private static final String TIMER_NAME = "EndingTimer_";
	
	private transient Timer endingTimer;

	@Override
	public String getStateName() {
		return EventState.ONGOING;
	}

	/**
	 * All'entrata nel nuovo stato, viene schedulato un timer programmato per scadere a conclusione dell'evento.
	 * Il timer scatenerà il passaggio di stato da ONGOING a ENDED.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onEntry(Event e) {	
		// Preparo il timer di scadenza della conclusione dell'evento
		// Lo configuro in modo che venga eseguito come daemon (grazie al parametro con valore true).
		this.endingTimer = new Timer(TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data della conclusione dell'evento
		Date endingDate = (Date) e.getFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE);
		
		// Schedulo il cambiamento di stato da ONGOING a ENDED
		EventState.scheduleStateChange(e, EventState.ENDED, endingTimer, endingDate);
		
	}
	
	/**
	 * In seguito alla deserializzazione di un OngoingState, e' necessario riavviare il timer
	 * per il passaggio allo stato "ENDED"
	 * 
	 * @param e L'evento di riferimento dello stato
	 */
	@Override
	public void resetState(Event e) {
		
		// Preparo il timer di scadenza della conclusione dell'evento
		// Lo configuro in modo che venga eseguito come daemon (grazie al parametro con valore true).
		this.endingTimer = new Timer(TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data della conclusione dell'evento
		Date endingDate = (Date) e.getFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE);
				
		// Schedulo il cambiamento di stato da ONGOING a ENDED
		EventState.scheduleStateChange(e, EventState.ENDED, endingTimer, endingDate);
		
	}
	
}
