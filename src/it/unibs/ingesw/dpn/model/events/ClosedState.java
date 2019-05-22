package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

import it.unibs.ingesw.dpn.model.fields.CommonField;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato CLOSED.
 * 
 * @author Michele Dusi
 *
 */
public class ClosedState implements EventState, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1343313668189070063L;
	
	private static final String TIMER_NAME = "OngoingTimer_";

	private transient Timer ongoingTimer;
	
	@Override
	public String getStateName() {
		return EventState.CLOSED;
	}
	
	/**
	 * All'entrata nel nuovo stato, viene schedulato un timer programmato per scadere a conclusione dell'evento.
	 * Se l'evento prevede un'orario e una data conclusiva, la scadenza del timer scatena il passaggio da CLOSED a ONGOING,
	 * nel momento in cui l'evento inizia.
	 * Se invece l'evento è "istantaneo" e non presenta una data di conclusione, la scadenza del timer scatena
	 * il passaggio da CLOSED a ENDED in maniera diretta.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onEntry(Event e) {	
		// Preparo il timer di scadenza della conclusione dell'evento
		// Lo configuro in modo che venga eseguito come daemon (grazie al parametro con valore true).
		this.ongoingTimer = new Timer(TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data di inizio dell'evento
		Date ongoingDate = (Date) e.getFieldValue(CommonField.DATA_E_ORA);
		
		// Verifico se effettuare il passaggio a ONGOING o a ENDED
		if (e.getFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE) != null) {

			// Schedulo il cambiamento di stato da CLOSED a ONGOING
			EventState.scheduleStateChange(e, EventState.ONGOING, ongoingTimer, ongoingDate);
			
		} else {
		
			// Schedulo il cambiamento di stato da CLOSED a ENDED
			EventState.scheduleStateChange(e, EventState.ENDED, ongoingTimer, ongoingDate);
			
		}
		
	}

}
