package it.unibs.ingesw.dpn.model.events;

import java.util.HashMap;
import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.fields.Field;

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
public abstract class Event {
	
	private static final String ARRAY_SIZE_MISMATCH_EXCEPTION = "L'array di valori dei campi dell'evento non corrisponde all'array dei campi previsti.";
	private static final String FIELD_TYPE_MISMATCH_EXCEPTION= "Impossibile creare l'evento associando al campo %s il valore dell'oggetto %s poiché quest'ultimo non è del tipo previsto.";
	private static final String FIELD_NOT_PRESENT_EXCEPTION = "Il campo %s non appartiene alla categoria prevista dall'evento";
	
	private final CategoryEnum category;
	private final Map<Field, Object> valuesMap;
	private final int fieldsNumber;
	
	private EventState state;
	
	/**
	 * Crea un nuovo evento con la relativa categoria.
	 * Tale costruttore (o meglio, i costruttori delle classi figlie che fanno affidamento su
	 * questo costruttore di Event) dovrà essere chiamato da una classe apposita, la cui responsabilità 
	 * principale sarà creare gli eventi nella maniera prevista dal programma.
	 * 
	 * Precondizione: la categoria a cui si vuole associare l'evento deve essere già stata
	 * istanziata completamente. Deve cioè contenere tutti i campi previsti. Ogni altra aggiunta di nuovi campi runtime
	 * alla categoria non comporterà l'aggiunta di tali campi agli eventi di quella categoria già esistenti.
	 * Tuttavia questo non genera alcuna problematica, poiché le categorie non sono modificabili runtime.
	 * 
	 * Precondizione: i valori dei campi devono essere uguali come numero e come tipo ai campi
	 * previsti dalla categoria. Questo viene garantito dalla classe adibita alla creazione degli eventi.
	 * 
	 * @param category la categoria prescelta
	 * @param fieldValues i valori dei campi della categoria dell'evento
	 */
	protected Event(CategoryEnum category, Object [] fieldValues) {
		this.category = category;
		this.valuesMap = new HashMap<>();
		
		// Recupero la categoria e tutti i campi relativi
		CategoryProvider catProv = CategoryProvider.getProvider();
		Category chosenCategory = catProv.getCategory(category);
		Field [] categoryFields = chosenCategory.getFieldsArray();
		
		// Controllo che i due array abbiano la stessa dimensione
		if (categoryFields.length != fieldValues.length) {
			throw new IllegalArgumentException(ARRAY_SIZE_MISMATCH_EXCEPTION);
		} else {
			this.fieldsNumber = categoryFields.length;
		}
		
		// Controllo che i valori dei campi corrispondano ai campi previsiti
		for (int i = 0; i < this.fieldsNumber; i++) {
			
			// Verifico che il valore sia del tipo previsto
			if (categoryFields[i].getType().isInstance(fieldValues[i])) {
				// Se è dello stesso tipo, aggiungo la coppia (campo, valore) all'hashmap dell'evento
				this.valuesMap.put(categoryFields[i], fieldValues[i]);
			} else {
				// In caso contrario genero un'eccezione
				throw new IllegalArgumentException(String.format(
						FIELD_TYPE_MISMATCH_EXCEPTION,
						categoryFields[i].getName(),
						fieldValues[i].toString()));
			}
			
		}
		
		// A questo punto posso settare lo stato come "valido".
		this.state = new ValidState();
	}
	
	/**
	 * Restituisce il valore caratterizzante l'evento del campo richiesto.
	 * 
	 * Precondizione: l'oggetto {@link Field} passato come parametro deve essere un campo previsto e contenuto
	 * nella categoria a cui appartiene l'evento.
	 * 
	 * @param chosenField il campo di cui si vuole conoscere il valore
	 * @return Il valore del campo
	 */
	public Object getFieldValue(Field chosenField) {
		if (this.valuesMap.containsKey(chosenField)) {
			return this.valuesMap.get(chosenField);
		} else {
			throw new IllegalArgumentException(String.format(
					FIELD_NOT_PRESENT_EXCEPTION, 
					chosenField.getName()));
		}
	}

	/**
	 * Restituisce il valore caratterizzante l'evento del campo richiesto.
	 * Se il nome del campo non corrisponde ad alcun campo dell'evento, verrà restituito il valore "null".
	 * 
	 * Precondizione: l'oggetto {@link String} passato come parametro deve corrispondere ad un campo 
	 * previsto e contenuto nella categoria a cui appartiene l'evento.
	 * 
	 * @param chosenFieldName il nome del campo di cui si vuole conoscere il valore
	 * @return Il valore del campo
	 */
	public Object getFieldValueByName(String chosenFieldName) {
		// Recupero l'oggetto Category con tutti i campi
		Category cat = CategoryProvider.getProvider().getCategory(this.category);
		Field field = cat.getFieldByName(chosenFieldName);
		// Verifico se esiste il campo
		if (field == null || !this.valuesMap.containsKey(field)) {
			// Nota: il secondo controllo dovrebbe essere inutile, poiché i campi di un evento e quelli della sua categoria DEVONO coincidere.
			return null;
		} else {
			return this.valuesMap.get(field);
		}
		
	}

	/**
	 * Restituisce la categoria di appartenenza dell'evento come istanza di {@link CategoryEnum}.
	 * 
	 * @return la categoria a cui appartiene l'evento.
	 */
	public CategoryEnum getCategory() {
		return category;
	}
	
	/**
	 * Si occupa di notificare tutti gli iscritti all'evento dei vari cambiamenti che avvengono al
	 * suo interno.
	 * Più nello specifico, invia ad ogni Mailbox una notifica di cambiamento di stato, che potrà essere
	 * visualizzata dal relativo utente su richiesta.
	 */
	private void notifySubscribers() {
		
		// TODO
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
		// TODO
		
		// Avviso tutti gli iscritti tramite le relative Mailbox
		this.notifySubscribers();
	}
	
	/**
	 * Su decisione dell'utente, questo metodo permette di "pubblicare l'evento".
	 * La bacheca si occuperà di mostrare l'evento agli altri utenti, mentre l'esecuzione di questo metodo
	 * comporta il passaggio di stato da VALID a OPEN, se ci si trova nello stato corretto.
	 * 
	 * Precondizione: l'evento deve essere nello stato VALID. Se questa non è soddisfatta, il metodo 
	 * non esegue il passaggio di stato e restituisce false.
	 * 
	 * Postcondizione: l'evento sarà nello stato OPEN. Questa postcondizione non può essere garantita se l'evento,
	 * al momento della chiamata del metodo, non si trova nello stato VALID.
	 * Si noti che, in caso venga chiamato il metodo quando si è già nello stato OPEN, lo stato non verrà modificato
	 * e sarà restituito il valore false.
	 * 
	 * @return true se l'evento viene pubblicato, false altrimenti.
	 */
	public boolean publish() {
		// TODO Eventuali azioni aggiuntive alla pubblicazione
		try {
			this.state.onPublication(this);
			return true;
		}
		catch (IllegalStateException e) {
			return false;
		}
	}
	
	/**
	 * Metodo che aggiunge la partecipazione di un utente all'evento.
	 * La sua funzione non è memorizzare gli utenti iscritti (compito della bacheca), ma tenere traccia
	 * delle {@link Mailbox} a cui inviare i messaggi di cambiamento di stato dell'evento stesso.
	 * Questo metodo comporta il cambiamento di stato da OPEN a CLOSED se viene raggiunto il numero massimo di
	 * partecipanti consentito all'evento.
	 * 
	 * Precondizione: l'evento deve essere nello stato OPEN. Non è possibile accettare iscrizioni se non si è in
	 * tale stato. In questo caso il metodo restituirà false.
	 * 
	 * Postcondizione: l'evento sarà nello stato CLOSED se e solo se l'aggiunta del partecipante permette
	 * di raggiungere il numero massimo di partecipanti. 
	 * Questa postcondizione non può essere garantita se l'evento, al momento della chiamata del metodo, 
	 * non si trova nello stato OPEN.
	 * 
	 * @return true se il partecipante viene aggiunto, false altrimenti.
	 */
	public boolean subscribe(/* User newSubscriber */) {
		// TODO Memorizzazione della mailbox nella mailing list
		try {
			this.state.onNewParticipant(this);
			return true;
		}
		catch (IllegalStateException e) {
			return false;
		}
	}

}
