package it.unibs.ingesw.dpn.model.events;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato OPEN.
 * 
 * @author Michele Dusi
 *
 */
public class OpenState implements EventState {

	private int currentSubscribers;
	private Timer timeoutTimer;
	
	private static final String TIMER_NAME = "TimeoutTimer_";

	@Override
	public String getStateName() {
		return EventState.OPEN;
	}
	
	/**
	 * All'entrata nel nuovo stato, il numero di iscritti viene inizializzato a 0.
	 * Inoltre, viene schedulato il timer di timeout per verificare quando scade il termine ultimo 
	 * di iscrizione dell'evento.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onEntry(Event e) {
		// Inizializzo il numero di iscritti a 0.
		this.currentSubscribers = 0;
		
		// Preparo il timer di scadenza del termine ultimo di iscrizioni
		// Lo configuro in modo che venga eseguito come daemon (grazie al parametro con valore true).
		this.timeoutTimer = new Timer(TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data del termine ultimo di iscrizione
		Date timeoutDate = (Date) e.getFieldValueByName("Termine ultimo di iscrizione");
		
		// Schedulo il cambiamento di stato da OPEN a FAILED
		this.timeoutTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				e.setState(new FailedState());
			}
			
		}, timeoutDate);
		
	}
	
	/**
	 * Alla sottoscrizione di un nuovo partecipante all'evento, viene incrementato il contatore dei parteicpanti.
	 * Se il contatore raggiunge il numero previsto dall'evento, viene effettauto il passaggio da OPEN a CLOSED.
	 * Inoltre, viene fermato il timer relativo al termine ultimo di iscrizione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onNewParticipant(Event e) {
		this.currentSubscribers++;
		
		// Verifico se ho raggiunto il numero massimo
		int numMax = (Integer) e.getFieldValueByName("Numero di partecipanti");
		
		if (this.currentSubscribers >= numMax) {
			
			// Cancello il timer
			this.timeoutTimer.cancel();
			// Effettuo il cambiamento di stato
			e.setState(new ClosedState());
		}
	}

}
