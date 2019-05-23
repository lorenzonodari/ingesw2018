package it.unibs.ingesw.dpn.model.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

/**
 * Classe che permette la creazione di eventi in maniera "controllata", secondo il pattern "Factory" e
 * secondo un preciso processo.
 * 
 * Per la creazione di un evento è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - acquireFieldValue(..);		<- Quante volte si vuole
 * - finalizeCreation(..);
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class EventFactory {
	
	/** Input/Output */
	private final UIRenderer renderer;
	private final InputGetter getter;
	
	/** Flag per mantenere lo stato della factory */
	private boolean creationOn = false;

	/** Attributi che aiutano la creazione di un evento */
	private User provisionalCreator = null;
	private CategoryEnum provisionalCategory = null;
	private Map<Field, FieldValue> provisionalFieldValues = null;
	
	/** Attributi che evitano l'invocazione continua di CategoryProvider */
	private String provisionalCategoryName = null;
	private List<Field> provisionalCategoryFields = null;
	
	/** Stringhe */
	private static final String EMPTY_FIELDVALUE_STRING = "- - - - -";
	

	/**
	 * Costruttore pubblico.
	 * 
	 * @param renderer Il renderizzatore dei prompt e dei messaggi d'errore
	 * @param getter L'acquisitore di dati primitivi
	 */
	public EventFactory(UIRenderer renderer, InputGetter getter) {
		this.renderer = renderer;
		this.getter = getter;
	}
	
	/**
	 * Comincia la creazione di un evento, data una precisa categoria e un utente creatore.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni in corso. Una factory puà costruire un solo
	 * evento alla volta, secondo il processo descritto nell'introduzione alla classe.
	 * 
	 * @param creator L'utente che ha creato l'evento
	 * @param category La categoria dell'evento
	 */
	public void startCreation(User creator, CategoryEnum category) {
		// Verifico che i parametri non siano null
		if (creator == null || category == null) {
			throw new IllegalArgumentException("Impossibile creare un evento con creatore o categoria nulli");
		}
		
		if (this.creationOn) {
			// E' già in corso la creazione di un evento, non è possibile cominciarne una nuova
			throw new IllegalStateException("Impossibile cominciare la creazione di un nuovo evento: una creazione di un evento è già in corso");
		} else {
			// Comincio la creazione di un nuovo evento
			this.creationOn = true;
		}
		
		// Memorizzo il creatore e la categoria
		this.provisionalCreator = creator;
		this.provisionalCategory = category;
		
		// Preparo i campi da completare man mano
		this.provisionalFieldValues = new HashMap<Field, FieldValue>();
		Category completeCategory = CategoryProvider.getProvider().getCategory(category);
		
		for (Field f : completeCategory.getFields()) {
			this.provisionalFieldValues.put(f, null);
		};
		
		this.provisionalCategoryName = completeCategory.getName();
		this.provisionalCategoryFields = completeCategory.getFields();
		
	}
	
	/**
	 * Gestisce l'acquisizione del dato {@link FieldValue} relativo al campo {@link Field} passato come parametro.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Precondizione: il campo {@link Field} deve essere un campo previsto per la categoria
	 * a cui appartiene l'evento. Questa condizione è verificata in automatico poiché il metodo chiamante
	 * presente in {@link UIManager} ha come valori possibili per i campi solo i campi appartenenti alla categoria
	 * dell'evento.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 */
	public void acquireFieldValue(Field field) {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
		}
		
		// Verifico che il parametro non sia null
		if (field == null) {
			throw new IllegalArgumentException("Parametro nullo: impossibile acquisire un dato senza specificare il campo");
		}
		
		FieldValue value = field.acquireFieldValue(this.renderer, this.getter, this.provisionalFieldValues);
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
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
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
	 * Restituisce i valori parzialmente compilati dell'evento che si sta creando.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return la mappa <Field, FieldValue> non completa di valori
	 */
	public Map<Field, FieldValue> getProvisionalFieldValues() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
		}
		
		return this.provisionalFieldValues;
	}
	
	public String getProvisionalFieldValueString(Field f) {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
		}
		
		FieldValue value = this.provisionalFieldValues.get(f);
		if (value != null) {
			return value.toString();
		} else {
			return EMPTY_FIELDVALUE_STRING;
		}
	}

	/**
	 * Restituisce il nome della categoria a cui appartiene l'evento che si sta creando.
	 * Questo metodo si appoggia ad un attributo memorizzato internamente alla factory, evitando
	 * l'invocazione continua di {@link CategoryProvider} che deve cercare ogni volta la categoria giusta.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return la stringa contenente il nome della categoria dell'evento
	 */
	public String getProvisionalCategoryName() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
		}
		
		return this.provisionalCategoryName;
	}
	
	/**
	 * Restituisce i campi della categoria a cui appartiene l'evento che si sta creando.
	 * Questo metodo si appoggia ad un attributo memorizzato internamente alla factory, evitando
	 * l'invocazione continua di {@link CategoryProvider} che deve cercare ogni volta la categoria giusta.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return la stringa contenente il nome della categoria dell'evento
	 */
	public List<Field> getProvisionalCategoryFields() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
		}
		
		return this.provisionalCategoryFields;
	}
	
	/**
	 * Termina la creazione di un evento con tutti i campi acquisiti finora.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Precondizione: tutti i campi obbligatori devono essere stati inizializzati. Per verificare questa condizione
	 * è possibile invocare (cosa che viene fatta anche in questo metodo) il metodo "verifyMandatoryFields".
	 * 
	 * @return L'evento creato correttamente
	 */
	public Event finalizeCreation() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException("Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento");
		}
		
		// Creo un nuovo oggetto Event sulla base della categoria
		Event newEvent = null;
		switch (this.provisionalCategory) {
				
		case PARTITA_DI_CALCIO:
			newEvent = new SoccerMatchEvent(this.provisionalCreator, this.provisionalFieldValues);
				
		}
		
		// Azzero i campi provvisori
		this.provisionalCreator = null;
		this.provisionalCategory = null;
		this.provisionalFieldValues = null;
		this.provisionalCategoryName = null;
		
		// Termino la modalità creazione
		this.creationOn = false;
		
		// Restituisco l'evento
		return newEvent;
	}
	
	/**
	 * Metodo che si occupa della creazione di un evento di una precisa categoria indicata.
	 * 
	 * @param creator L'utente che ha creato l'evento
	 * @param category La categoria dell'evento
	 * @param fieldValues I campi dell'evento
	 * @return l'istanza di {@link Event} creata
	 */
	@Deprecated
	public Event createEvent(User creator, CategoryEnum category, Map<Field, FieldValue> fieldValues) {
		
		if (category == null) {
			throw new IllegalArgumentException("Impossibile creare un Evento con categoria \"null\"");
		}
		
		// Creo l'evento secondo l'apposita categoria
		switch (category) {
		
		case PARTITA_DI_CALCIO:
			return new SoccerMatchEvent(creator, fieldValues);
				
		}
		
		return null;
	}
	
}
