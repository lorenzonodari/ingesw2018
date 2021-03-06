package it.unibs.ingesw.dpn.model.events;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Interfaccia che implementa il pattern "State" per la classe context {@link Event}.
 * - Ogni evento, al momento della sua creazione, è nello stato {@link ValidState}.
 * - A seguito della pubblicazione, transita nello stato {@link OpenState}, 
 * in cui può accettare proposte di iscrizione.
 * - Se scade il termine ultimo di iscrizione, l'evento fallisce e si passa allo stato finale {@link FailedState}.
 * - Se invece si raggiunge il numero sufficiente di partecipanti, si transita in {@link ClosedState}.
 * - A conclusione dell'evento, si transita nello stato finale {@link EndedState}.
 * 
 * Per ulteriori informazioni e diagrammi si veda la documentazione.
 * 
 * @author Michele Dusi
 *
 */
public interface EventState {
	
	String VALID = "Valido";
	String OPEN = "Aperto";
	String CLOSED = "Chiuso";
	String ONGOING = "In corso";
	String ENDED = "Concluso";
	String WITHDRAWN = "Ritirato";
	String FAILED = "Fallito";
	
	String STATE_EXCEPTION = "Impossibile eseguire questo metodo nello stato \"%s\"";
	
	/**
	 * Restituisce il nome comune dello stato.
	 * 
	 * @return il nome comune dello stato
	 */
	public String getStateName();
	
	/**
	 * Metodo di default.
	 * Racchiude le azioni da compiere appena l'evento entra nel nuovo stato.
	 * Di default non viene compiuta nessuna azione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	public default void onEntry(Event e) {};

	/**
	 * Informa il metodo chiamante se lo stato corrente accetta o meno l'azione 
	 * di pubblicazione dell'evento.<br>
	 * È opportuno chiamare questo metodo prima di "onPublication", poiché in caso 
	 * l'azione non fosse possibile verrebbe generata un'eccezione.<br>
	 * <br>
	 * Di default, questo metodo restituisce "false".
	 * 
	 * @return "TRUE" se è possibile completare senza eccezioni l'azione prevista.
	 */
	public default boolean canDoPublication() {
		return false;
	}

	/**
	 * Informa il metodo chiamante se lo stato corrente accetta o meno l'azione 
	 * di ritiro dell'evento.<br>
	 * È opportuno chiamare questo metodo prima di "onWithdrawal", poiché in caso 
	 * l'azione non fosse possibile verrebbe generata un'eccezione.<br>
	 * <br>
	 * Di default, questo metodo restituisce "false".
	 * 
	 * @return "TRUE" se è possibile completare senza eccezioni l'azione prevista.
	 */
	public default boolean canDoWithdrawal() {
		return false;
	}

	/**
	 * Informa il metodo chiamante se lo stato corrente accetta o meno l'azione 
	 * di iscrizione all'evento.<br>
	 * È opportuno chiamare questo metodo prima di "onSubscription", poiché in caso 
	 * l'azione non fosse possibile verrebbe generata un'eccezione.<br>
	 * <br>
	 * Di default, questo metodo restituisce "false".
	 * 
	 * @return "TRUE" se è possibile completare senza eccezioni l'azione prevista.
	 */
	public default boolean canDoSubscription() {
		return false;
	}

	/**
	 * Informa il metodo chiamante se lo stato corrente accetta o meno l'azione 
	 * di disiscrizione dall'evento.<br>
	 * È opportuno chiamare questo metodo prima di "onUnsubscription", poiché in caso 
	 * l'azione non fosse possibile verrebbe generata un'eccezione.<br>
	 * <br>
	 * Di default, questo metodo restituisce "false".
	 * 
	 * @return "TRUE" se è possibile completare senza eccezioni l'azione prevista.
	 */
	public default boolean canDoUnsubscription() {
		return false;
	}
	
	/**
	 * Metodo di default.
	 * Nel caso si tenti di pubblicare un evento già pubblicato, quindi nel caso in cui il metodo venga 
	 * chiamato per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	public default void onPublication(Event e) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}
	
	/**
	 * Metodo di default.
	 * Nel caso si tenti di ritirare un evento in uno stato in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	public default void onWithdrawal(Event e) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}

	/**
	 * Metodo di default.
	 * Nel caso si tenti di aggiungere un partecipante in uno stato in cui non è permesso,
	 * viene lanciata un'eccezione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	public default void onSubscription(Event e) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}

	/**
	 * Metodo di default.
	 * Nel caso si tenti di rimuovere un partecipante in uno stato in cui non è permesso,
	 * viene lanciata un'eccezione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	public default void onUnsubscription(Event e) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}
	
	
	/**
	 * Il fine di questo metodo e' quello di resettare correttamente lo stato di un evento
	 * in seguito alla sua deserializzazione. Poiche' non tutti gli stati di un evento necessitano
	 * di particolari azioni per il corretto ripristino, l'implementazione di default del metodo non fa nulla.
	 * 
	 * @param e L'evento al quale si riferisce lo stato
	 */
	public default void resetState(Event e) {
		// DO NOTHING BY DEFAULT
	}

	/**
	 * Metodo di utilita' utilizzato per avviare il timer di cambio stato di un evento
	 * 
	 * @param event L'evento di riferimento
	 * @param newState Il nome del nuovo stato, come restituito da {@link EventState.getStateName()}
	 * @param timer Il timer da avviare
	 * @param timeout Il timeout da impostare al timer
	 */
	static void scheduleStateChange(Event event, String state, Timer timer, Date timeout) {
		
		EventState newState;
		
		switch (state) {
		
			case EventState.VALID:
				newState = new ValidState();
				break;
				
			case EventState.OPEN:
				newState = new OpenState();
				break;

			case EventState.CLOSED:
				newState = new ClosedState();
				break;
				
			case EventState.ONGOING:
				newState = new OngoingState();
				break;
				
			case EventState.FAILED:
				newState = new FailedState();
				break;

			case EventState.WITHDRAWN:
				newState = new WithdrawnState();
				break;
				
			case EventState.ENDED:
				newState = new EndedState();
				break;
				
			default:
				newState = null;
				throw new IllegalArgumentException();
			
		}
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				event.setState(newState);
			};
			
		}, timeout);
	}
	

}
