package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;

/**
 * Classe che modellizza il comportamento di un evento {@link Event} nello stato OPEN.
 * 
 * @author Michele Dusi
 *
 */
public class OpenState implements EventState, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8082128824977480600L;
	
	/** Numero di iscritti attuale */
	private int currentSubscribers = 0;
	/** Numero minimo di iscritti, pari al valore del campo "Numero di partecipanti" */
	private int minSubscribers;
	/** Numero massimo di iscritti, pari alla somma dei valori dei campi "Numero di partecipanti" e "tolleranza" */
	private int maxSubscribers;
	/** Flag che considera quando è scaduto il primo timer "unsubscriptionTimeoutTimer" */
	private boolean acceptUnsubscription = true;
	
	private transient Timer unsubscriptionTimeoutTimer;
	private transient Timer subscriptionTimeoutTimer;
	
	private transient Semaphore timerSemaphore;

	private static final String UNSUBS_TIMER_NAME = "unsubscriptionTimeoutTimer_";
	private static final String SUBS_TIMER_NAME = "subscriptionTimeoutTimer_";

	@Override
	public String getStateName() {
		return EventState.OPEN;
	}
	
	/**
	 * All'entrata nel nuovo stato, vengono schedulati i due timer.
	 * Inoltre, vengono impostati alcuni valori di parametri utilizzati in questo stato.
	 * 
	 * Precondizione: questo metodo deve essere chiamato prima degli altri metodi di questo stato,
	 * poiché altrimenti alcuni parametri sono senza valore.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onEntry(Event e) {
		
		// Imposto i due valori di riferimento, che non verranno più modificati
		this.minSubscribers = ((IntegerFieldValue) e.getFieldValue(CommonField.NUMERO_DI_PARTECIPANTI)).getValue();
		this.maxSubscribers = this.minSubscribers + ((IntegerFieldValue) e.getFieldValue(CommonField.TOLLERANZA_NUMERO_DI_PARTECIPANTI)).getValue();
		
		// Preparo i due timer:
		// - quello del termine ultimo di ritiro delle iscrizioni
		// - quello del termine ultimo di iscrizioni
		
		// Configuro i timer in modo che vengano eseguiti come daemon (grazie al parametro con valore true).
		this.unsubscriptionTimeoutTimer = new Timer(UNSUBS_TIMER_NAME + e.hashCode(), true);
		this.subscriptionTimeoutTimer = new Timer(SUBS_TIMER_NAME + e.hashCode(), true);
		
		// Ricavo la data del termine ultimo di iscrizione
		Date unsubscriptionTimeoutDate = (Date) e.getFieldValue(CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE);
		Date subscriptionTimeoutDate = (Date) e.getFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE);
		
		// Semaforo per le precedenze
		// Mi assicuro che "unsubscriptionTimeoutTimer" venga eseguito prima di "subscriptionTimeoutTimer".
		this.timerSemaphore = new Semaphore(0);
		
		// Schedulo l'azione da effettuare al "Termine ultimo di ritiro iscrizione"
		this.unsubscriptionTimeoutTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// Tutte le azioni da effettuare sono contenute nel seguente metodo:
				onUnsubscriptionTimeout(e);
			}
		}, unsubscriptionTimeoutDate);
		
		// Schedulo l'azione da effettuare al "Termine ultimo di iscrizione"
		this.subscriptionTimeoutTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// Tutte le azioni da effettuare sono contenute nel seguente metodo:
				onSubscriptionTimeout(e);
			}
		}, subscriptionTimeoutDate);
		
	}
	
	/**
	 * Questo metodo modifica lo stato dell'evento in "ritirato".
	 * Da quel momento l'evento non può più accettare iscrizioni o disiscrizioni, non genererà ulteriori
	 * notifiche per gli iscritti e non verrà più modificato il suo stato.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onWithdrawal(Event e) {
		e.setState(new WithdrawnState());
	}
	
	/**
	 * Alla sottoscrizione di un nuovo partecipante all'evento, viene incrementato il contatore dei parteicpanti.
	 * Se il contatore raggiunge il numero previsto dall'evento, viene effettauto il passaggio da OPEN a CLOSED.
	 * Inoltre, viene fermato il timer relativo al termine ultimo di iscrizione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onSubscription(Event e) {
		// Incremento immediatamente il numero di iscritti
		this.currentSubscribers++;
		
		// Verifico se il numero di partecipanti ha raggiunto il massimo
		if (this.currentSubscribers > this.maxSubscribers) {
			if (this.acceptUnsubscription) {
				// In caso il numero di iscritti sia maggiore, è impossibile accettare una nuova iscrizione
				this.currentSubscribers--;
				throw new IllegalStateException("Impossibile iscriversi all'evento: numero di partecipanti massimo raggiunto");
			} else {
				// ERRORE DI SISTEMA
				// Non dovrebbe verificarsi mai, tuttavia questo caso viene lasciato per segnalare l'eventuale presenza di bug
				(new Exception("Errore nella logica del programma")).printStackTrace();
			}
			
		} else if (this.currentSubscribers == this.maxSubscribers && ! this.acceptUnsubscription) {
			// Siamo nel caso in cui vale:
			/* 
			 * 			this.currentSubscribers <= this.maxSubscribers
			 */
			// In questo caso l'iscrizione va a buon fine.
			// Inoltre effettuo un controllo per verificare se devo transizionare allo stato CLOSED
			e.setState(new ClosedState());
		}
		
	}
	
	/**
	 * Quando si verifica una disiscrizione in un evento OPEN, viene invocato questo metodo.
	 * In caso non sia ancora scaduto il timer "unsubscriptionTimeoutTimer", viene decrementato il valore
	 * corrente di iscrizioni.
	 * Non è possibile che tale valore assuma un valore negativo poiché nella classe {@link Event} viene effettuato
	 * un controllo sull'utente che intende disiscriversi, e questo metodo viene chiamato solo se l'utente era
	 * iscritto in precedenza. Non viene mai rimossa, perciò, alcuna iscrizione che non sia stata già effettuata correttamente.
	 * 
	 * In caso il timer "unsubscriptionTimeoutTimer" sia già scaduto, viene lanciata un'eccezione.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	@Override
	public void onUnsubscription(Event e) {
		// Verifico di poter accettare disiscrizioni
		if (this.acceptUnsubscription) {
			this.currentSubscribers--;
		} else {
			throw new IllegalStateException("Non è possibile ritirare un'iscrizione in una data successiva a quella del \"Termine ultimo di ritiro iscrizione\"");
		}
	}

	/**
	 * Metodo privato che viene richiamato da una callback allo scadere del "Termine ultimo di iscrizione".
	 * A seconda del numero di iscritti raggiunto alla scadenza, viene effettuata una transizione:
	 * - verso FAILED se gli iscritti non sono sufficienti.
	 * - verso CLOSED se gli iscritti hanno raggiunto il numero minimo.
	 *
	 * Nota: questo metodo è impostato per essere eseguito solo dopo la scadenza (e la relativa esecuzione) del
	 * timer del "Termine ultimo di ritiro iscrizione". Per fare ciò viene utilizzato un semaforo.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	private void onSubscriptionTimeout(Event e) {
		// Verifico le precedenze fra thread dei timer:
		try {
			// Attendo il completamento dell'altro timer
			this.timerSemaphore.acquire();
		} catch (InterruptedException exc) {
			// In caso d'errore, stampo l'errore su console.
			exc.printStackTrace();
		}
		
		// Verifico se ho raggiunto il numero minimo di iscritti
		// Se non è stato raggiunto
		if (this.currentSubscribers < this.minSubscribers) {
			// Passo nello stato FAILED
			e.setState(new FailedState());
			
		// Se invece ho raggiunto o eventualmente superato il numero minimo
		} else if (this.currentSubscribers >= this.minSubscribers) {
			// Passo allo stato CLOSED
			e.setState(new ClosedState());
		}
	}
	
	/**
	 * Metodo che racchiude tutte le azioni da eseguire allo scadere del "Termine ultimo di ritiro iscrizione".
	 * Imposta il flag booleano per accettare disiscrizioni a "false": da questo momento in poi non sarà
	 * più possibile disiscriversi all'evento.
	 * Inoltre verifica se il numero di partecipanti ha raggiunto il numero massimo; in tal caso scatena
	 * una transizione da OPEN allo stato CLOSED.
	 * 
	 * @param e L'evento a cui si fa riferimento
	 */
	private void onUnsubscriptionTimeout(Event e) {
		this.acceptUnsubscription = false;
		
		// Verifico se il numero di iscritti era già al massimo
		if (this.currentSubscribers < this.maxSubscribers) {
			// Rimango nello stato OPEN
			// Lascio che venga eseguito il task dell'altro timer
			this.timerSemaphore.release();
			
		} else if (this.currentSubscribers == this.maxSubscribers) {
			// Annullo l'altro timer, poiché non mi serve più
			this.subscriptionTimeoutTimer.cancel();
			// Transiziono allo stato CLOSED
			e.setState(new ClosedState());
			
		} else if (this.currentSubscribers > this.maxSubscribers) {
			// ERRORE DI SISTEMA
			// Non dovrebbe verificarsi mai, tuttavia questo caso viene lasciato per segnalare l'eventuale presenza di bug
			(new Exception("Errore nella logica del programma")).printStackTrace();
		}
		
	}
	
	
}
