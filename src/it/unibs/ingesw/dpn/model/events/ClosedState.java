package it.unibs.ingesw.dpn.model.events;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato CLOSED.
 * 
 * @author Michele Dusi
 *
 */
public class ClosedState implements EventState {
	
	private Timer endingTimer;
	
	private static final String TIMER_NAME = "EndingTimer_";

	@Override
	public String getStateName() {
		return EventState.CLOSED;
	}
	
	/**
	 * All'entrata nel nuovo stato, viene schedulato un timer programmato per scadere a conclusione dell'evento.
	 * Il timer scatenerà il passaggio di stato da CLOSED a ENDED.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onEntry(Event e) {	
		// Preparo il timer di scadenza della conclusione dell'evento
		// Lo configuro in modo che venga eseguito come daemon (grazie al parametro con valore true).
		this.endingTimer = new Timer(TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data del termine ultimo di iscrizione
		Date endingDate = (Date) e.getFieldValueByName("Data e ora conclusive");
		
		// Schedulo il cambiamento di stato da CLOSED a ENDED
		this.endingTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				e.setState(new EndedState());
			}
			
		}, endingDate);
		
	}

}
