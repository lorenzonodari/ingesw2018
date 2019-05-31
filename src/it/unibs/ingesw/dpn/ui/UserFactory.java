package it.unibs.ingesw.dpn.ui;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.LocalDateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersManager;

/**
 * Classe che permette la creazione di utenti in maniera "controllata", secondo il pattern "Factory" e
 * secondo un preciso processo.
 * 
 * Per la creazione di un utente è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - metodi di acquisizione dati (acquireNickname, acquireBirthday, ...)
 * - finalizeCreation(..);
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class UserFactory {

	/** Input/Output */
	private final UIRenderer renderer;
	private final InputGetter getter;
	
	/** Classi per recuperare dati ausiliari */
	private final UsersManager usersManager;
	
	/** Flag per mantenere lo stato della factory */
	private boolean creationOn = false;

	/** Attributi che aiutano la creazione di un evento */
	private Map<Field, FieldValue> provisionalFieldValues = null;

	/** Stringhe */
	private static final String EMPTY_FIELDVALUE_STRING = "- - - - -";
	private static final String CREATION_MODE_OFF_EXCEPTION = "Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo utente";
	
	/** Parametri */
	private static final int AGE_LIMIT = 12;

	/**
	 * Costruttore pubblico.
	 * 
	 * Nota: l'oggetto "usersManager" richiesto come parametro è necessario perché la factory
	 * ha bisgono di sapere quali utenti sono presenti nel sistema e quali nickname sono già utilizzati.
	 * 
	 * Precondizione: i parametri non possono ovviamente essere nulli.
	 * 
	 * @param renderer Il renderizzatore dei prompt e dei messaggi d'errore
	 * @param getter L'acquisitore di dati primitivi
	 * @param usersManager La classe che si occupa della gestione degli utenti.
	 */
	public UserFactory(UIRenderer renderer, InputGetter getter, UsersManager usersManager) {
		// verifico la precondizione
		if (renderer == null || getter == null || usersManager == null) {
			throw new IllegalArgumentException("impossiile creare un oggetto UserFactory con parametri nulli");
		}
		
		this.renderer = renderer;
		this.getter = getter;
		this.usersManager = usersManager;
	}
	

	/**
	 * Comincia la creazione di un utente.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni in corso. Una factory puà costruire un solo
	 * evento alla volta, secondo il processo descritto nell'introduzione alla classe.
	 * 
	 * @param defaultNickname Il nickname di default con cui cominciare l'iscrizione
	 */
	public void startCreation(String defaultNickname) {		
		// Verifico che i parametri non siano null
		if (defaultNickname == null) {
			throw new IllegalArgumentException("Impossibile creare un evento con creatore o categoria nulli");
		}
		
		if (this.creationOn) {
			// E' già in corso la creazione di un evento, non è possibile cominciarne una nuova
			throw new IllegalStateException("Impossibile cominciare la creazione di un nuovo evento: una creazione di un evento è già in corso");
		} else {
			// Comincio la creazione di un nuovo evento
			this.creationOn = true;
		}
		
		// Preparo i campi vuoti, pronti per essere inizializzati
		this.provisionalFieldValues = new HashMap<Field, FieldValue>();

		for (Field f : UserField.values()) {
			this.provisionalFieldValues.put(f, null);
		};
		
		// Inizializzo già il nickname
		this.provisionalFieldValues.put(UserField.NICKNAME, new StringFieldValue(defaultNickname));
	}

	/**
	 * Gestisce l'acquisizione del dato {@link FieldValue} relativo al campo {@link Field} passato come parametro.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Precondizione: il campo {@link Field} deve essere un campo previsto per un utente, ossia deve
	 * essere un campo della classe {@link UserField}. Questa condizione è verificata in automatico poiché il metodo chiamante
	 * presente in {@link UIManager} ha come valori possibili per i campi soltanto oggetti {@link UserField}.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 */
	public void acquireFieldValue(Field field) {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Verifico che il parametro non sia null
		if (field == null) {
			throw new IllegalArgumentException("Parametro nullo: impossibile acquisire un dato senza specificare il campo");
		}
		
		// Prompt e interazione con l'utente
		renderer.renderLineSpace();
		renderer.renderText(String.format(
				" ### %-50s",
				field.getName().toUpperCase()));
		renderer.renderText(String.format(
				" ### %s",
				field.getDescription()));
		renderer.renderLineSpace();
		
		// Acquisizione del dato, basata su Field
		FieldValue value = null;
		
		// Campi previsti per un utente
		if (field instanceof UserField) {
			value = acquireUserFieldValue((UserField) field);
		
		// Campi estranei
		} else {
			throw new IllegalArgumentException("Campo \"Field\" non riconosciuto: impossibile acquisire un valore");
		}
		
		// Aggiungo il dato acquisito alla mappa di valori provvisori
		this.provisionalFieldValues.put(field, value);
	}

	/**
	 * Metodo che verifica se sono stati compilati TUTTI i campi obbligatori previsti.
	 * In tal caso restituisce true, altrimenti false.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return "true" se tutti i campi obbligatori sono stati compilati
	 */
	public boolean verifyMandatoryFields() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Verifico tutti i campi
		boolean checkMandatoryFieldsFlag = true;
		for (Field f : this.provisionalFieldValues.keySet()) {
			if (f.isMandatory() && provisionalFieldValues.get(f) == null) {
				checkMandatoryFieldsFlag = false;
				break;
			}
		}
		return checkMandatoryFieldsFlag;
	
	}

	/**
	 * Restituisce i valori parzialmente compilati dell'utente che si sta creando.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return la mappa <Field, FieldValue> non completa di valori
	 */
	public Map<Field, FieldValue> getProvisionalFieldValues() {
		// Verifico di essere in modalità "creazione di un nuovo utente"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		return this.provisionalFieldValues;
	}
	
	/**
	 * Restituisce un valore testuale per la visualizzazione del valore del campo.
	 * Nel caso in cui il campo non sia ancora stato inizializzato, viene restituita una stringa di 
	 * default "- - - - -".
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @param f Il campo di cui si vuole ottenere il valore come stringa
	 * @return Un testo rappresentante il valore del campo richiesto
	 */
	public String getProvisionalFieldValueString(Field f) {
		// Verifico di essere in modalità "creazione di un nuovo utente"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		FieldValue value = this.provisionalFieldValues.get(f);
		if (value != null) {
			return value.toString();
		} else {
			return EMPTY_FIELDVALUE_STRING;
		}
	}

	/**
	 * Metodo che annulla la creazione dell'utente e provoca il reset della fabbrica.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Postcondizione: al termine della chiamata la factory non è più in modalità creazione.
	 * Tutti i valori immessi fino a questo momento vengono cancellati.
	 * 
	 */
	public void cancelCreation() {
		// Verifico di essere in modalità "creazione di un nuovo utente"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		this.creationOn = false;
		
		// Reset di tutte gli attributi
		this.provisionalFieldValues = null;
	}

	/**
	 * Termina la creazione di un utente con tutti i campi acquisiti finora.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Precondizione: tutti i campi obbligatori devono essere stati inizializzati. Per verificare questa condizione
	 * è possibile invocare (cosa che viene fatta anche in questo metodo) il metodo "verifyMandatoryFields".
	 * 
	 * @return L'utente creato correttamente
	 */
	public User finalizeCreation() {
		// Verifico di essere in modalità "creazione di un nuovo utente"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Creo un nuovo oggetto User
		User newUser = new User(this.provisionalFieldValues);
		
		// Azzero i campi provvisori
		this.provisionalFieldValues = null;
		
		// Termino la modalità creazione
		this.creationOn = false;
		
		// Restituisco l'evento
		return newUser;
	}

	/* METODI PRIVATI DI UTILITA' */

	/**
	 * Metodo che acquisisce e restituisce il valore di un campo "UserField".
	 * 
	 * Precondizione: Il campo che si vuole acquisire deve essere contenuto nell'enum {@link UserField},
	 * ossia deve essere uno dei campi previsti per la caratterizzazione di un utente.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 * @return Il valore acquisito
	 */
	private FieldValue acquireUserFieldValue(UserField field) {
		switch (field) {
		
		case NICKNAME :
		{
			boolean okFlag = false;
			StringFieldValue nick = null;
			// Prompt e interazione con l'utente
			do {
				nick = StringFieldValue.acquireValue(renderer, getter);

				// Verifiche
				if (this.usersManager.isNicknameExisting(nick.toString())) {
					renderer.renderError("Questo nickname è già utilizzato, per favore scegli un altro nickname personale.");
				} else {
					okFlag = true;
				}
			} while (!okFlag);

			// Restituzione del valore acquisito
			return nick;
			
		}
			
		case DATA_DI_NASCITA :
		{
			boolean okFlag = false;
			LocalDateFieldValue date = null;
			// Prompt e interazione con l'utente
			do {
				date = LocalDateFieldValue.acquireValue(renderer, getter);
				
				// Verifiche
				if (date.getLocalDate().isAfter(LocalDate.now())) {
					renderer.renderError("Impossibile accettare una data futura");
				} else if (date.getLocalDate().isAfter(LocalDate.now().minusYears(AGE_LIMIT))) {
					renderer.renderError(String.format(
							"Per utilizzare questo programma devi avere almeno %d anni.\nInserisci la tua vera data di nascita o termina l'esecuzione del programma.",
							AGE_LIMIT));
				} else {
					okFlag = true;
				}
				
			} while (!okFlag);
			
			// Restituzione del valore acquisito
			return date;
		}
		
		}
		
		// Se non matcho nulla
		throw new IllegalArgumentException("Il campo non corrisponde ad alcun campo della categoria \"Partita di calcio\"");
	}
	
}
