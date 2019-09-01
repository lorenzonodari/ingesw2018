package it.unibs.ingesw.dpn.model.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.AbstractFieldable;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.UserDependantFieldValue;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe astratta che rappresenta in maniera concettuale un evento generico gestito dal programma.
 * Questa classe viene poi specificata in differenti classi a seconda delle categorie previste.
 * (Si veda, ad esempio, la classe {@link SoccerMatchEvent}.)
 * 
 * Nota: Le classi figlie non contengono direttamente i dati, bensì è la classe padre che mantiene in memoria 
 * tutti i valori dei campi. Le classi figlie, tuttavia, sono necessarie per l'implementazione dei differenti
 * comportamenti basati sui differenti campi.
 * 
 * Invariante di classe: la categoria dell'evento.
 * 
 * Invariante di classe: il numero di campi dell'evento (pari a quello della categoria).
 * 
 * Invariante di classe: il fatto che l'evento, una volta creato con successo, contenga esattamente tutti 
 * i campi previsti dalla categoria e già correttamente inizializzati (eventualmente a "null" se facoltativi).
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public abstract class Event extends AbstractFieldable implements Comparable<Event> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1575965680034326082L;
	
	/** Eccezioni */
	private static final String NULL_ARGUMENT_EXCEPTION = "Impossibile creare un evento con parametri nulli";
	private static final String ILLEGAL_COMPARING_METHOD_EXCEPTION = "Metodologia di ordinamento non riconosciuta";
	
	/** Messaggi di Log o di notifica */
	private static final String STATE_CHANGE_LOG = "Cambio di stato in: %s";
	private static final String EVENT_SUBSCRIPTION_MESSAGE = "Ti sei iscritto/a correttamente all'evento \"%s\"";
	private static final String EVENT_UNSUBSCRIPTION_MESSAGE = "Ti sei disiscritto/a correttamente dall'evento \"%s\"";
	private static final String EVENT_CREATION_AND_PUBLICATION_MESSAGE = "L'evento \"%s\" da te creato è stato pubblicato correttamente";
	
	/** Stringhe di formattazione */
	private static final String FIELD_DESCRIPTION_STRING = " | %-50s : %s\n";
	
	/** Strategie per il confronto di eventi */
	public enum ComparingMethod {
		BY_DATE,
		BY_TITLE
	};
	
	/** Strategia di default, comune a tutti gli Event */
	private static ComparingMethod comparingMethod = ComparingMethod.BY_DATE;
	
	/** Attributi d'istanza */
		
	private final User creator;
	
	private final Category category;
	
	private EventState state;
	
	private final EventHistory history;
	
	private final List<User> partecipants;
	
	/**
	 * Crea un nuovo evento con la relativa categoria. Tutti i campi definiti in CommonField sono automaticamente
	 * aggiunti all'evento. Eventuali sottoclassi che desiderassero aggiungere dei propri field a questi dovranno
	 * farlo manualmente mediante invocazioni del metodo addField() all'interno del loro costruttore. Si veda la classe
	 * {@link SoccerMatchEvent} per un esempio.
	 * 
	 * Precondizione: il creatore dell'evento non deve essere un valore nullo. In questo caso verrebbe lanciata un'eccezione.
	 * 
	 * Postcondizione: il creatore dell'evento NON è iscritto automaticamente all'evento.
	 * E' necessario chiamare il metodo "subscribe" per confermare l'iscrizione, ma solamente DOPO aver pubblicato l'evento.
	 * 
	 * @param creator L'utente {@link User} creatore dell'evento
	 * @param category La categoria prescelta
	 */
	public Event(User creator, Category category) {
		super(category.getFields());
		
		// Verifico che i parametri non siano nulli
		if (creator == null || category == null) {
			throw new IllegalArgumentException(NULL_ARGUMENT_EXCEPTION);
		}
		
		// Inizializzo gli attributi della classe
		this.creator = creator;
		this.category = category;
		
		// Preparo l'oggetto EventHistory che terrà traccia dei cambiamenti di stato
		this.history = new EventHistory();
		
		// Inizializzo la lista di sottoscrittori della mailing list
		this.partecipants = new LinkedList<>();
		
		// A questo punto posso settare lo stato come "valido".
		this.setState(new ValidState());	
		
	}
	
	/**
	 * Imposta il valore id default di alcuni campi.
	 * Questo metodo viene chiamato dal costruttore e racchiude tutte le procedure che impostano
	 * i valori dei campi facoltativi utilizzati nel programma.
	 */
	@Override
	public void setDefaultFieldValues() {

		// TITOLO
		// Valore di default = "<nomeCategoria> del <dataEvento>"
		if (this.getFieldValue(CommonField.TITOLO) == null) {
			this.setFieldValue(CommonField.TITOLO, new StringFieldValue(String.format(
					"%s del %s",
					this.category.getName(),
					this.getFieldValue(CommonField.DATA_E_ORA))));
		}

		// TOLLERANZA NUMERO DI PARTECIPANTI
		// Valore di default = 0
		if (this.getFieldValue(CommonField.TOLLERANZA_NUMERO_DI_PARTECIPANTI) == null) {
			this.setFieldValue(CommonField.TOLLERANZA_NUMERO_DI_PARTECIPANTI, new IntegerFieldValue(0));
		}
		
		// TERMINE ULTIMO DI RITIRO ISCRIZIONE
		// Valore di default = Termine ultimo di iscrizione
		if (this.getFieldValue(CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE) == null) {
			this.setFieldValue(
					CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE, 
					this.getFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)
					);
		}
		
	}
	
	/**
	 * Restituisce l'utente che ha creato l'evento, impostato all'atto della creazione.
	 * 
	 * @return il creatore dell'evento come oggetto {@link User}
	 */
	public User getCreator() {
		return this.creator;
	}

	/**
	 * Restituisce la categoria di appartenenza dell'evento come istanza di {@link Category}.
	 * 
	 * @return la categoria a cui appartiene l'evento.
	 */
	public Category getCategory() {
		return this.category;
	}
	
	/**
	 * Restituisce il titolo dell'evento.<br>
	 * Questo metodo maschera la struttura interna fatta di Field evitanto una troppo
	 * frequente chiamata al metodo "getFieldValue".<br>
	 * Poiché il titolo è un valore che sicuramente ogni evento possiede, visto che è impostato
	 * di default, questo metodo restituisce sempre un valore attendibile.
	 * 
	 * @return Il titolo di questo evento
	 */
	public String getTitle() {
		return this.getFieldValue(CommonField.TITOLO).toString();
	}
	
	/**
	 * Si occupa di notificare tutti gli iscritti all'evento, ovvero sia il creatore che i partecipanti, 
	 * dei vari cambiamenti che avvengono al suo interno.
	 * Più nello specifico, invia ad ogni Mailbox una notifica di cambiamento di stato, che potrà essere
	 * visualizzata dal relativo utente su richiesta nella sua area personale.
	 * 
	 * @param message Il messaggio da inviare agli iscritti
	 */
	 void notifyEveryone(String message) {
		notifyCreator(message);
		notifyPartecipants(message);
	 }
	 
	 /**
	  * Invia una notifica al creatore dell'evento.
	  * 
	  * @param message Il testo della notifica
	  */
	 void notifyCreator(String message) {
		 creator.receive(new Notification(message));
	 }
	 
	 /**
	  * Invia una notifica a tutti i partecipanti all'evento ma NON al creatore.
	  * 
	  * @param message Il testo della notifica
	  */
	 void notifyPartecipants(String message) {
		 for (User u : partecipants) {

			 if (u == creator) {
				 continue;
			 }
			 
			 // Invio la notifica
			 u.receive(new Notification(message));
		 }
	 }
	 
	 /**
	  * Restituisce i campi dell'evento dipendenti dall'utente al momento della sua iscrizione
	  * 
	  * @return La lista dei campi dipendenti dall'utente
	  */
	 public List<Field> getUserDependantFields() {
		 
		 return this.getAllFieldValues().keySet().stream()
		 							   .filter(field -> field.isUserDependant() && this.getFieldValue(field) != null)
		 							   .collect(Collectors.toCollection(ArrayList::new));
		 
	 }
	 
	 /**
	  * Restituisce true se l'evento contiene campi dipendenti dall'utente al momento della sua iscrizione
	  * 
	  * @return true se l'evento contiene campi dipendenti dall'utente al momento della sua iscrizione
	  */
	 public boolean hasUserDependantFields() {
		 
		 return this.getAllFieldValues().keySet().stream()
				   .filter(field -> field.isUserDependant() && this.getFieldValue(field) != null)
				   .count() > 0;
	 }
	
	/**
	 * Modifica lo stato dell'evento, secondo il pattern "State".
	 * Necessita di un'implementazione concreta dell'interfaccia {@link EventState} come parametro.
	 * 
	 * Precondizione: lo stato passato come parametro deve essere un oggetto EventState valido, coerente
	 * con il funzionamento del programma e correttamente inizializzato.
	 * Ciò viene garantito in parte dal fatto che solo le classi di questo package possono utilizzare questo metodo.
	 * Infatti, questo metodo sarà chiamato prevalentemente dagli EventState stessi.
	 * 
	 * @param newState il nuovo stato dell'Evento come oggetto {@link EventState}
	 */
	synchronized void setState(EventState newState) {
		// Modifico lo stato
		this.state = newState;
		
		// Effettuo le attività d'entrata nello stato
		this.state.onEntry(this);
		
		// Aggiorno la storia
		String message_log = String.format(
				STATE_CHANGE_LOG, 
				this.state.getStateName().toUpperCase());
		this.history.addLog(message_log);
		
	}
	
	/**
	 * Reimposta lo stato corretto dell'evento. Questo metodo DEVE essere invocato su ogni evento
	 * quando questi sono caricati da disco mediante serializzazione.
	 */
	void resetState() {
		this.state.resetState(this);
	}
	
	/**
	 * Restituisce una stringa corrispondente allo stato dell'oggetto {@link Event}.
	 * 
	 * @return La stringa corrispondente allo stato in cui si trova l'evento
	 */
	public String getState() {
		return state.getStateName();
	}
	
	/**
	 * Indica se l'evento può essere pubblicato nell'istante in cui tale
	 * metodo viene chiamato ed eseguito.<br>
	 * Un evento pubblicato può accettare iscrizioni e può poi essere ritirato in futuro.
	 * 
	 * @return "True" se l'evento è pronto per essere pubblicato
	 */
	public boolean canBePublished() {
		return this.state.canDoPublication();
	}
	
	/**
	 * Su decisione dell'utente, questo metodo permette di "pubblicare l'evento".
	 * La bacheca si occuperà di mostrare l'evento agli altri utenti, mentre l'esecuzione di questo metodo
	 * comporta il passaggio di stato da VALID a OPEN, se ci si trova nello stato corretto.<br>
	 * <br>
	 * Precondizione: l'evento deve essere nello stato VALID. Se questa non è soddisfatta, il metodo 
	 * non esegue il passaggio di stato e restituisce un'eccezione. La verifica della condizione è effettuata
	 * mediante il pattern "state".<br>
	 * Per essere certi che questo metodo non generi un'eccezione, è importante verificare che l'evento può subire
	 * l'operazione di pubblicazione mediante la chiamata di "canBePublished".<br>
	 * <br>
	 * Postcondizione: l'evento sarà nello stato OPEN. Questa postcondizione non può essere garantita se l'evento,
	 * al momento della chiamata del metodo, non si trova nello stato VALID.
	 * Si noti che, in caso venga chiamato il metodo quando si è già nello stato OPEN, lo stato non verrà modificato
	 * e sarà restituito il valore false. Questo metodo è pertanto idempotente per eventi nello stato OPEN.
	 * 
	 * @return true se l'evento viene pubblicato, false altrimenti.
	 */
	public void publish() {
		// Verifico che sia possibile effettuare la pubblicazione dell'evento
		if (this.canBePublished()) {
			this.state.onPublication(this);
			// Una volta che è stato pubblicato, iscrivo direttamente il creatore all'evento
			this.subscribe(this.creator);
		} else {
			throw new IllegalStateException("Impossibile pubblicare questo evento");
		}
		
		// Comunico all'utente che ha creato l'evento
		notifyCreator(String.format(EVENT_CREATION_AND_PUBLICATION_MESSAGE, this.getTitle()));
				
	}
	
	/**
	 * Indica se l'evento può essere ritirato ("withdrawn") nell'istante in cui tale
	 * metodo viene chiamato ed eseguito.<br>
	 * Un evento ritirato non può più accettare iscrizioni e non verrà svolto.<br>
	 * Un evento può essere ritirato solamente se è stato già pubblicato e non è ancora
	 * scaduta la data di svolgimento dello stesso.<br>
	 * 
	 * @return "True" se l'evento può essere ritirato
	 */
	public boolean canBeWithdrawn() {
		return this.state.canDoWithdrawal();
	}

	/**
	 * Su decisione dell'utente, questo metodo permette di "ritirare l'evento".<br>
	 * La bacheca si occuperà di revocare agli utenti la possibilità di visionare l'evento,
	 * mentre l'esecuzione di questo metodo comporta il passaggio di stato da OPEN a WITHDRAWN,
	 * se ci si trova nello stato corretto.<br>
	 * <br>
	 * Precondizione: l'evento deve essere nello stato OPEN. Se questa non è soddisfatta, il metodo 
	 * non esegue il passaggio di stato e restituisce un'eccezione. La verifica della condizione è effettuata
	 * mediante il pattern "state".<br>
	 * <strong>NOTA:</strong> Per essere certi che questo metodo non generi un'eccezione, è importante verificare che l'evento può subire
	 * l'operazione di ritiro mediante la chiamata di "canBeWithdrawn".<br>
	 * <br>
	 * Postcondizione: l'evento sarà nello stato WITHDRAWN. Questa postcondizione non può essere garantita se l'evento,
	 * al momento della chiamata del metodo, non si trova nello stato OPEN.<br>
	 * Si noti che, in caso venga chiamato il metodo quando si è già nello stato WITHDRAWN, lo stato non verrà modificato
	 * e sarà restituita un'eccezione.<br>
	 */
	public void withdraw() {
		if (this.canBeWithdrawn()) {
			this.state.onWithdrawal(this);
		} else {
			throw new IllegalStateException("Impossibile ritirare questo evento");
		}
	}

	/**
	 * Indica se l'evento può accettare l'iscrizione di uno specifico {@link User} nell'istante in cui tale
	 * metodo viene chiamato ed eseguito.<br>
	 * Un utente può iscriversi a questo evento solamente se:
	 * <ul>
	 * 	<li> Non è già iscritto </li>
	 * 	<li> L'evento è aperto, cioè se accetta iscrizioni, e non si è ancora svolto </li>
	 * </ul>
	 * 
	 * @param potentialSubscriber L'utente che si vuole verificare se sia iscrivibile
	 * @return "True" se l'utente può essere iscritto all'evento
	 */
	public boolean canSubscribe(User potentialSubscriber) {
		// Verifico che l'utente NON sia già iscritto/a
		if (this.partecipants.contains(potentialSubscriber)) {
			return false;
		} 
		// Verifico che l'evento accetti iscrizioni
		else if (!this.state.canDoSubscription()) {
			return false;
		}
		// Se tutto va bene, comunico che l'utente può iscriversi
		else {
			return true;
		}
	}

	/**
	 * Metodo che aggiunge la partecipazione di un utente all'evento.<br>
	 * Questo metodo può scatenare il cambiamento di stato da OPEN a CLOSED se viene raggiunto il numero massimo di
	 * partecipanti consentito all'evento entro il tempo ultimo di iscrizione.<br>
	 * <br>
	 * Precondizione: l'evento deve essere nello stato OPEN. Non è possibile accettare iscrizioni se non si è in
	 * tale stato. In questo caso il metodo restituirà un'eccezione. <br>
	 * <br>
	 * Precondizione: l'utente non è ancora iscritto all'evento. In caso si cerchi di iscrivere un utente
	 * già iscritto, il metodo restituirò un'eccezione.<br>
	 * <br>
	 * <strong>NOTA:</strong> Per essere certi che questo metodo non generi un'eccezione, è importante verificare
	 * che l'utente può essere iscritto mediante la chiamata di "canSubscribe(User)".<br>
	 * <br>
	 * Postcondizione: l'evento sarà nello stato CLOSED in due casi:<br>
	 * <ul>
	 * 	<li> l'aggiunta del partecipante permette di raggiungere il numero massimo di partecipanti (dato dal numero
	 *   minimo di partecipanti + la tolleranza). </li>
	 * 	<li> l'aggiunta del partecipante permette di raggiungere il numero minimo di partecipanti e SUCCESSIVAMENTE
	 *   scade la data "Termine ultimo di iscrizione", anche se non si è raggiunto il numero massimo. </li>
	 * </ul>
	 * Questa postcondizione non può essere garantita se l'evento, al momento della chiamata del metodo, 
	 * non si trova nello stato OPEN.<br>
	 */
	public synchronized void subscribe(User subscriber) {
		// Verifica che l'utente possa iscriversi
		if (!this.canSubscribe(subscriber)) {
			throw new IllegalStateException("Impossibile iscriversi a questo evento");
		}
		
		// Aggiungo l'iscritto
		this.partecipants.add(subscriber);

		// Se l'utente non e' il creatore, notifica l'utente che l'iscrizione è andata a buon fine
		if (subscriber != this.creator) {
			// Notifico l'iscrizione
			StringBuffer message = new StringBuffer(
					String.format(
							EVENT_SUBSCRIPTION_MESSAGE, 
							this.getTitle()));
			// Notifico il costo
			message.append(String.format("; Importo dovuto: %.2f €", this.getExpensesForUser(subscriber)));
			// Invio il messaggio
			subscriber.receive(new Notification(message.toString()));
		}
		
		// Notifico lo stato che c'è stata un'iscrizione
		/*
		 * NOTA: Quest'istruzione potrebbe generare a sua volta
		 * un cambio di stato (e quindi un'invio di notifiche).
		 */
		this.state.onSubscription(this);
	}

	/**
	 * Indica se l'evento può accettare la disiscrizione di uno specifico {@link User} nell'istante in cui tale
	 * metodo viene chiamato ed eseguito.<br>
	 * Un utente può disiscriversi a questo evento solamente se:
	 * <ul>
	 * 	<li> E' già iscritto </li>
	 * 	<li> L'evento è aperto, cioè accetta disiscrizioni </li>
	 * 	<li> Non è scaduto il termine ultimo di disiscrizione </li>
	 * </ul>
	 * 
	 * @param potentialSubscriber L'utente che si vuole verificare se sia disiscrivibile
	 * @return "True" se l'utente può essere disiscritto dall'evento
	 */
	public boolean canUnsubscribe(User potentialUnsubscriber) {
		// Verifico che l'utente sia già iscritto
		if (!this.partecipants.contains(potentialUnsubscriber)) {
			return false;
		}
		// Verifico che l'utente NON sia il creatore
		else if (potentialUnsubscriber.equals(this.creator)) {
			return false;
		}
		// Verifico che lo stato accetti disiscrizioni
		else if (!this.state.canDoUnsubscription()) {
			return false;
		}
		// Altrimenti, la disiscrizione può essere effettuata
		else {
			return true;
		}
	}
	
	/**
	 * Metodo che revoca la partecipazione di un utente all'evento.<br>
	 * L'utente disiscritto non riceverà più notifiche di aggiornamento sull'evento.<br>
	 * La disiscrizione di un utente non scatena alcun cambiamento di stato all'interno dell'evento. Tuttavia, se
	 * si dovesse retrocedere dalla soglia del numero minimo di partecipanti e SUCCESSIVAMENTE scadesse la data del
	 * "Termine ultimo di iscrizione", allora l'evento passerà nello stato FAILED.<br>
	 * <br>
	 * Precondizione: l'evento deve essere nello stato OPEN. Non è possibile revocare iscrizioni se non si è in
	 * tale stato. In questo caso il metodo restituirà un'eccezione.<br>
	 * <br>
	 * Precondizione: l'utente deve essere già iscritto all'evento. In caso si cerchi di revocare l'iscrizione
	 * di un utente NON iscritto, il metodo restituisce un'eccezione.<br>
	 * <br>
	 * <strong>NOTA:</strong> Per essere certi che questo metodo non generi un'eccezione, è importante verificare
	 * che l'utente può essere disiscritto mediante la chiamata di "canUnsubscribe(User)".<br>
	 * <br>
	 * 
	 * @return true se il partecipante viene rimosso dalle iscrizioni, false altrimenti.
	 */
	public void unsubscribe(User unsubscriber) {
		// Verifico che l'utente sia già iscritto
		if (!this.canUnsubscribe(unsubscriber)) {
			throw new IllegalStateException("Impossibile disiscriversi da questo evento");
		}

		// Rimuove l'iscritto dalla mailing list
		this.partecipants.remove(unsubscriber);

		// Rimuovo le tracce dell'utente dai campi passibili di personalizzazione
		for (Field field : this.getUserDependantFields()) {
			// Estraggo da ciascuno il valore FieldValue
			UserDependantFieldValue fieldValue = (UserDependantFieldValue) this.getFieldValue(field);
			fieldValue.forgetUserCustomization(unsubscriber);
		}

		// Notifica l'utente che la disiscrizione è andata a buon fine
		unsubscriber.receive(new Notification(
				String.format(EVENT_UNSUBSCRIPTION_MESSAGE, this.getTitle())
				));
		
		// Comunica allo stato che c'è stata una disiscrizione
		this.state.onUnsubscription(this);
	}
	
	/**
	 * Metodo di classe che imposta (per tutti gli oggetti {@link Event}) il metodo di comparazione
	 * degli stessi, utilizzato per ordinamenti basilari.
	 * Poiché il metodo ha effetto su tutte le istanze della classe (poiché modifica il comportamento
	 * del metodo "compareTo"), NON è possibile utilizzare questo metodo per ordinare una sottolista di {@link Event}.
	 * Si consiglia, per fare ciò, di creare un comparatore apposito avvalendosi dei metodi "compareBy-Strategy-To(Event e)".
	 * 
	 * Nota: questo metodo NON garantisce un ri-ordino automatico degli oggetti {@link Event} a seguito della
	 * sua chiamata. E' necessario occuparsi direttamente di tale ordinamento secondo altri modi.
	 * 
	 * @param comparingMethod Il modo per confrontare e ordinare due eventi.
	 */
	public static void setComparingMethod(ComparingMethod method) {
		Event.comparingMethod = method;
	}
	
	/**
	 * Restituisce, come oggetto enun {@link ComparingMethod}, il metodo di confronto degli eventi.
	 * 
	 * @return Il metodo di comparazione utilizzato per gli eventi
	 */
	public static ComparingMethod getComparingMethod() {
		return Event.comparingMethod;
	}

	/**
	 * Confronta due eventi sulla base del criterio specificato dal metodo "setComparingMethod".
	 * Al momento è possibile ordinare due eventi:
	 * - Per ID crescenti.
	 * - Per date crescenti.
	 * - Per titolo, in ordina alfabetico.
	 * 
	 * @param e L'evento con cui effettuare il confronto
	 * @return Un valore numerico per capire l'ordinamento dei due eventi
	 */
	@Override
	public int compareTo(Event e) {
		switch (Event.comparingMethod) {
		case BY_DATE: 
			return this.compareByEventDateTo(e);
		case BY_TITLE:
			return this.compareByTitleTo(e);
		default:
			throw new IllegalStateException(ILLEGAL_COMPARING_METHOD_EXCEPTION);
		}
	}
	
	/**
	 * Confronta due eventi in base alla loro data di inizio.
	 * Un evento è "maggiore" di un altro se la sua data è posteriore alla data dell'altro.
	 * 
	 * @param e L'evento con cui effettuare il confronto
	 * @return Un valore numerico per capire l'ordinamento dei due eventi
	 */
	public int compareByEventDateTo(Event e) {
		Date thisDate = ((DateFieldValue) this.getFieldValue(CommonField.DATA_E_ORA)).getValue();
		Date otherDate = ((DateFieldValue) e.getFieldValue(CommonField.DATA_E_ORA)).getValue();
		// Se l'evento corrente è più recente come data di creazione dell'evento passato come parametro
		if (thisDate.after(otherDate)) {
			return +1;
		// Se l'evento corrente è meno recente dell'evento passato come parametro
		} else if (thisDate.before(otherDate)) {
			return -1;
		// Se i due eventi sono lo stesso evento
		// (L'unico caso in cui i due ID sono uguali)
		} else {
			return 0;
		}
	}

	/**
	 * Confronta due eventi in base al loro titolo, in ordine alfabetico.
	 * Un evento è "maggiore" di un altro il suo titolo è successivo (in ordine alfabetico)
	 * al titolo dell'altro.
	 * 
	 * @param e L'evento con cui effettuare il confronto
	 * @return Un valore numerico per capire l'ordinamento dei due eventi
	 */
	public int compareByTitleTo(Event e) {
		return this.getTitle().compareTo(e.getTitle());
	}
	
	/**
	 * Restituisce le spese che il dato utente dovra' sostenere per l'evento
	 * 
	 * @param user L'utente per il quale si vogliono conoscere i costi
	 * @return Il costo che il dato utente dovra' sostenere
	 */
	public float getExpensesForUser(User user) {
		
		return ((MoneyAmountFieldValue) this.getFieldValue(CommonField.QUOTA_INDIVIDUALE)).getValue();
		
	}
	
	/**
	 * Restituisce una stringa contenente la descrizione completa ma compatta delle caratteristiche
	 * dell'evento.
	 * 
	 * @return Una descrizione testuale dell'evento
	 */
	@Override
	public String toString() {
		StringBuffer description = new StringBuffer();
		// Categoria
		String categoryName = this.category.getName();
		description.append(String.format("Categoria   : %s\n", categoryName));
		// Creatore
		description.append(String.format("Creatore    : %s\n", this.getCreator().getNickname()));
		// Valori dei campi
		description.append("Campi       :\n");
		for (Field f : this.category.getFields()) {
			if(!(this.getFieldValue(f) == null)) {
			description.append(String.format(FIELD_DESCRIPTION_STRING,
					f.getName(),
					this.getFieldValue(f).toString()));
			}
		}
		// Cronologia/Storia
		description.append("Cronologia  :\n");
		description.append(this.history.toString());
		return description.toString();
	}
	
	/**
	 * Restituisce una descrizione testuale dell'evento.<br>
	 * In particolare, adatta la descrizione a seconda dell'utente che richiede la
	 * visualizzazione, in modo da presentare valori diversi in caso di campi {@link Field}
	 * dipendenti dall'utente stesso.<br>
	 * 
	 * Nota: Nel caso in cui l'utente passato come parametro sia il creatore, viene visualizzata
	 * la lista di campi in maniera standard.
	 * 
	 * @param pointOfViewUser L'utente in riferimento al quale si vuole ottenere una visualizzazione dell'evento
	 * @return Una descrizione compatta dell'oggetto {@link Event} come oggetto {@link String}
	 */
	public String toString(User pointOfViewUser) {
		// Se l'utente è il creatore, restituisco semplicemente il toString "base"
		if (this.getCreator().equals(pointOfViewUser)) {
			return this.toString();
		}
		
		// L'inizio è identico
		StringBuffer s = new StringBuffer(this.toString());
		
		// Aggiungo la visualizzazione dei campi dipendenti dall'utente, ma SOLO SE l'utente non è il creatore
		s.append("Selezioni dell'utente:\n");
		for (Field f : this.getUserDependantFields()) {
			s.append(String.format(FIELD_DESCRIPTION_STRING, f.getName(), this.getFieldValue(f).toString()));
			// TODO Credo che non funzioni...
			// Attendo la visualizzazione personalizzata anche dei FieldValue
		}
	
		/*
		if (this.hasUserDependantFields() && pointOfViewUser != this.getCreator()) {
			s.append("\n");
			s.append("Spese opzionali scelte: \n");
			
			OptionalCostsFieldValue costsFieldValue = (OptionalCostsFieldValue) event.getFieldValue(ConferenceField.SPESE_OPZIONALI);
			Map<String, Float> costs = costsFieldValue.getValue();
			
			for (String cost : costs.keySet()) {
				
				menuContent.append(cost);
				menuContent.append(String.format(" : %.2f € ", costs.get(cost)));
				
				if (!costsFieldValue.userHasCost(loginManager.getCurrentUser(), cost)) {
					menuContent.append("[ ]");
				}
				else {
					menuContent.append("[X]");
				}
				
				menuContent.append("\n");
			}
			menuContent.append("\n");
			
			menuContent.append(String.format("Costo complessivo di partecipazione: %.2f €", event.getExpensesForUser(loginManager.getCurrentUser())));
		}
		*/
		
		return s.toString();
	}
	
	/**
	 * Restituisce true se l'utente dato e' iscritto all'evento
	 * 
	 * Precondizione: user != null
	 * 
	 * @param user L'utente del quale verificare l'iscrizione all'evento
	 * @return true se l'utente risulta iscritto all'evento
	 */
	public boolean hasSubscriber(User user) {
		
		// Verifica precondizione
		if (user == null) {
			throw new IllegalArgumentException();
		}
		
		return this.partecipants.contains(user);
	}
	
	/**
	 * Restituisce la lista non modificabile degli utenti iscritti all'evento
	 * 
	 * @return La lista degli iscritti all'evento
	 */
	public List<User> getSubscribers() {
		return Collections.unmodifiableList(this.partecipants);
	}
}
