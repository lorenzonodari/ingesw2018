package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;

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
	
	private static final String TIMER_NAME = "EndingTimer_";
	private static final String MEMO_NOTIFICATION_MESSAGE = "PROMEMORIA: L'evento %s si terra' in data %s";
	private static final String MEMO_NOTIFICATION_MONEY = "; Importo dovuto: %s";

	private transient Timer endingTimer;
	
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
		
		// Invia promemoria agli iscritti
		StringBuilder message = new StringBuilder(String.format(MEMO_NOTIFICATION_MESSAGE, 
									   							e.getFieldValue(CommonField.TITOLO),
									   							e.getFieldValue(CommonField.DATA_E_ORA)));
		
		MoneyAmountFieldValue quota = (MoneyAmountFieldValue) e.getFieldValue(CommonField.QUOTA_INDIVIDUALE);
		if (quota.getValue() > 0.0f) {
			message.append(String.format(MEMO_NOTIFICATION_MONEY, quota));
		}
		
		e.notifySubscribers(message.toString());
		
		
		// Preparo il timer di scadenza della conclusione dell'evento
		// Lo configuro in modo che venga eseguito come daemon (grazie al parametro con valore true).
		this.endingTimer = new Timer(TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data della conclusione dell'evento
		// La data deve essere quella del giorno successivo, allo scoccare della mezzanotte
		Date endingDate = (Date) e.getFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE);
		Calendar cal = Calendar.getInstance();
        cal.setTime(endingDate);
        cal.add(Calendar.DATE, 1); 		// Aggiungo un giorno alla data
        cal.set(Calendar.HOUR, 0); 		// Imposto la data a mezzanotte
        cal.set(Calendar.MINUTE, 0); 	// Imposto la data a mezzanotte
        Date timerDate = cal.getTime();
		
		// Schedulo il cambiamento di stato da CLOSED a ENDED
		EventState.scheduleStateChange(e, EventState.ENDED, endingTimer, timerDate);
		
	}

}
