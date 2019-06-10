package it.unibs.ingesw.dpn.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.events.ConferenceEvent;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.SoccerMatchEvent;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.SoccerMatchField;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.GenderEnumFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerIntervalFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.TimeAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe che permette la creazione di eventi in maniera "controllata", secondo il pattern "Factory" e
 * secondo un preciso processo.
 * 
 * Per la creazione di un evento è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - acquireFieldValue(..);		<- Quante volte si vuole
 * - finalise(..);
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
	private static final String CREATION_MODE_OFF_EXCEPTION = "Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento";
	

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
		
		/*
		 * Nota: a differenza della versione 2, nella quale viene utilizzato il polimorfismo per
		 * l'acquisizione dei valori FieldValue, dalla versione 3 si è deciso di rinunciare ad 
		 * esso in favore di una più netta separazione fra le classi che rappresentano dei "dati" 
		 * e le classi che si occupano della loro acquisizione o stampa.
		 */
		
		// Campi comuni a tutte le categorie
		if (field instanceof CommonField) {
			value = acquireCommonFieldValue((CommonField) field);
			
		// Campi esclusivi della categoria "SoccerMatchField"
		} else if (field instanceof SoccerMatchField) {
			value = acquireSoccerMatchFieldValue((SoccerMatchField) field);
			
		// Campi esclusivi della categoria "ConferenceField"
		} else if (field instanceof ConferenceField) {
			value = acquireConferenceFieldValue((ConferenceField) field);
			
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
		// Verifico di essere in modalità "creazione di un nuovo evento"
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
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
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
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		return this.provisionalCategoryFields;
	}
	
	/**
	 * Metodo che annulla la creazione dell'evento e provoca il reset della fabbrica.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Postcondizione: al termine della chiamata la factory non è più in modalità creazione.
	 * Tutti i valori immessi fino a questo momento vengono cancellati.
	 * 
	 */
	public void cancel() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		this.creationOn = false;
		
		// Reset di tutte gli attributi
		this.provisionalCreator = null;
		this.provisionalCategory = null;
		this.provisionalFieldValues = null;
		this.provisionalCategoryName = null;
		this.provisionalCategoryFields = null;
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
	public Event finalise() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Creo un nuovo oggetto Event sulla base della categoria
		Event newEvent = null;
		switch (this.provisionalCategory) {
				
		case PARTITA_DI_CALCIO :
			newEvent = new SoccerMatchEvent(this.provisionalCreator, this.provisionalFieldValues);
			break;
			
		case CONFERENZA :
			newEvent = new ConferenceEvent(this.provisionalCreator, this.provisionalFieldValues);
			break;
				
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

	/* METODI PRIVATI DI UTILITA' */

	/**
	 * Metodo che acquisisce e restituisce il valore di un campo "CommonField".
	 * 
	 * Precondizione: Il campo che si vuole acquisire deve essere contenuto nell'enum {@link CommonField},
	 * ossia deve essere comune a tutte le categorie.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 * @return Il valore acquisito
	 */
	private FieldValue acquireCommonFieldValue(CommonField field) {
		switch (field) {
		
		case TITOLO :
		{
			return StringFieldValue.acquireValue(renderer, getter);
		}
			
		case LUOGO :
		{
			return StringFieldValue.acquireValue(renderer, getter);
		}
			
		case DATA_E_ORA : 
		{
			// Acquisizione del dato
			boolean okFlag = false;
			DateFieldValue acquiredDate = null;
			do {
				acquiredDate = DateFieldValue.acquireValue(renderer, getter);
				// Verifico che la data dell'evento sia posteriore alla creazione
				if (acquiredDate.before(new Date())) {
					renderer.renderError("Inserire una data futura");
				} else if (isAfterOrEqualToDateField(acquiredDate, CommonField.DATA_E_ORA_CONCLUSIVE)) {
					renderer.renderError("Data d'inizio posteriore alla data conclusiva dell'evento");
				} else if (isBeforeOrEqualToDateField(acquiredDate, CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)) {
					renderer.renderError("Data d'inizio precedente al termine ultimo di iscrizione");
				} else if (isBeforeOrEqualToDateField(acquiredDate, CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE)) {
					renderer.renderError("Data d'inizio precedente al termine ultimo di ritiro iscrizione");
				} else {
					okFlag = true;
				}
			} while (!okFlag);

			// Setup aggiuntivi, derivati da dati già presenti nell'evento
			if (this.isInitialized(CommonField.DATA_E_ORA_CONCLUSIVE)) {
				
				// Setup di "Durata"
				TimeAmountFieldValue duration = new TimeAmountFieldValue(
						acquiredDate,
						((DateFieldValue) this.provisionalFieldValues.get(CommonField.DATA_E_ORA_CONCLUSIVE)));
				this.provisionalFieldValues.put(CommonField.DURATA, duration);
				
			} else if (this.isInitialized(CommonField.DURATA)) {
				
				// Setup di "Data e ora conclusive"
				DateFieldValue dataEOraConclusive = new DateFieldValue(
						acquiredDate.getTime() +
						1000 * ((TimeAmountFieldValue) this.provisionalFieldValues.get(CommonField.DURATA)).getSeconds()
						);
				this.provisionalFieldValues.put(CommonField.DATA_E_ORA_CONCLUSIVE, dataEOraConclusive);
			}
			
			// Restituzione del valore acquisito
			return acquiredDate;
		}
			
		case DATA_E_ORA_CONCLUSIVE : 
		{
			// Acquisizione del dato
			boolean okFlag = false;
			DateFieldValue acquiredDate = null;
			do {
				acquiredDate = DateFieldValue.acquireValue(renderer, getter);
				// Verifico che la data dell'evento sia posteriore alla creazione
				if (acquiredDate.before(new Date())) {
					renderer.renderError("Inserire una data futura");
				} else if (isBeforeOrEqualToDateField(acquiredDate, CommonField.DATA_E_ORA)) {
					renderer.renderError("Data di conclusione precedente alla data d'inizio dell'evento");
				} else if (isBeforeOrEqualToDateField(acquiredDate, CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)) {
					renderer.renderError("Data di conclusione precedente al termine ultimo di iscrizione");
				} else if (isBeforeOrEqualToDateField(acquiredDate, CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE)) {
					renderer.renderError("Data di conclusione precedente al termine ultimo di ritiro iscrizione");
				} else {
					okFlag = true;
				}
			} while (!okFlag);

			// Setup aggiuntivi, derivati da dati già presenti nell'evento
			if (isInitialized(CommonField.DATA_E_ORA)) {
				// Setup di "Durata"
				TimeAmountFieldValue duration = new TimeAmountFieldValue(
						((DateFieldValue) this.provisionalFieldValues.get(CommonField.DATA_E_ORA)),
						acquiredDate);
				this.provisionalFieldValues.put(CommonField.DURATA, duration);
			}
			
			// Restituzione del valore acquisito
			return acquiredDate;
		}
		
		case DURATA : 
		{
			// Acquisizione del dato
			TimeAmountFieldValue acquiredValue = TimeAmountFieldValue.acquireValue(renderer, getter);
			
			// Setup aggiuntivi, derivati da dati già presenti nell'evento
			if (isInitialized(CommonField.DATA_E_ORA)) {
				// Setup di "Data e ora conclusive"
				DateFieldValue dataEOraConclusive = new DateFieldValue(
						((DateFieldValue) this.provisionalFieldValues.get(CommonField.DATA_E_ORA)).getTime() +
						1000 * acquiredValue.getSeconds()
						);
				this.provisionalFieldValues.put(CommonField.DATA_E_ORA_CONCLUSIVE, dataEOraConclusive);
			}
			
			// Restituzione del valore acquisito
			return acquiredValue;
		}
		
		case TERMINE_ULTIMO_DI_ISCRIZIONE : 
		{
			boolean okFlag = false;
			DateFieldValue acquiredDate = null;
			do {
				acquiredDate = DateFieldValue.acquireValue(renderer, getter);
				// Verifico che la data dell'evento sia posteriore alla creazione
				if (acquiredDate.before(new Date())) {
					renderer.renderError("Inserire una data futura");
				} else if (isAfterOrEqualToDateField(acquiredDate, CommonField.DATA_E_ORA)) {
					renderer.renderError("Termine ultimo d'iscrizione posteriore alla data d'inizio dell'evento");
				} else if (isAfterOrEqualToDateField(acquiredDate, CommonField.DATA_E_ORA_CONCLUSIVE)) {
					renderer.renderError("Termine ultimo d'iscrizione posteriore alla data di conclusione dell'evento");
				} else if (isBeforeOrEqualToDateField(acquiredDate, CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE)) {
					renderer.renderError("Termine ultimo d'iscrizione precedente al termine ultimo di ritiro iscrizione");
				} else {
					okFlag = true;
				}
			} while (!okFlag);
			return acquiredDate;
		}
		
		case TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE : 
		{
			boolean okFlag = false;
			DateFieldValue acquiredDate = null;
			do {
				acquiredDate = DateFieldValue.acquireValue(renderer, getter);
				// Verifico che la data dell'evento sia posteriore alla creazione
				if (acquiredDate.before(new Date())) {
					renderer.renderError("Inserire una data futura");
				} else if (isAfterOrEqualToDateField(acquiredDate, CommonField.DATA_E_ORA)) {
					renderer.renderError("Termine ultimo di ritiro iscrizione posteriore alla data d'inizio dell'evento");
				} else if (isAfterOrEqualToDateField(acquiredDate, CommonField.DATA_E_ORA_CONCLUSIVE)) {
					renderer.renderError("Termine ultimo di ritiro iscrizione posteriore alla data di conclusione dell'evento");
				} else if (isAfterOrEqualToDateField(acquiredDate, CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)) {
					renderer.renderError("Termine ultimo di ritiro iscrizione posteriore al termine ultimo d'iscrizione");
				} else {
					okFlag = true;
				}
			} while (!okFlag);
			return acquiredDate;
		}
		
		case NUMERO_DI_PARTECIPANTI : 
		{
			renderer.renderText("Inserisci il numero di partecipanti (almeno 2)");
			return new IntegerFieldValue(getter.getInteger(2, Integer.MAX_VALUE));
		}
			
		case TOLLERANZA_NUMERO_DI_PARTECIPANTI : 
		{
			renderer.renderText("Inserisci la tolleranza massima sul numero di partecipanti");
			return new IntegerFieldValue(getter.getInteger(0, Integer.MAX_VALUE));
		}
			
		case QUOTA_INDIVIDUALE : 
		{
			renderer.renderText("Inserisci il costo di partecipazione");
			return new MoneyAmountFieldValue(getter.getFloat(0, Float.MAX_VALUE));
		}
			
		case COMPRESO_NELLA_QUOTA : 
		{
			return StringFieldValue.acquireValue(renderer, getter);
		}
			
		case NOTE : 
		{
			return StringFieldValue.acquireValue(renderer, getter);
		}

		}
		
		// Se non matcho nulla
		throw new IllegalArgumentException("Il campo non corrisponde ad alcun campo comune a tutte le categorie");
	}
	

	/**
	 * Metodo che acquisisce e restituisce il valore di un campo "SoccerMatch".
	 * 
	 * Precondizione: Il campo che si vuole acquisire deve essere contenuto nell'enum {@link SoccerMatchField},
	 * ossia deve essere esclusivo della categoria "Partita di calcio".
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 * @return Il valore acquisito
	 */
	private FieldValue acquireSoccerMatchFieldValue(SoccerMatchField field) {
		switch (field) {
		
		case GENERE :
		{
			GenderEnumFieldValue [] values = GenderEnumFieldValue.values();
			int i = 1;
			for (GenderEnumFieldValue gender : values) {
				renderer.renderText(String.format("%3d)\t%s", 
						i++, gender.toString()));
			}
			int input = getter.getInteger(1, values.length);
			return values[input - 1];
		}
			
		case FASCIA_DI_ETA :
		{

			IntegerIntervalFieldValue value = null;
			boolean check = false;
			do {
				renderer.renderText("Inserisci il valore minimo");
				int min = getter.getInteger(0, 150);
				renderer.renderText("Inserisci il valore massimo");
				int max = getter.getInteger(0, 150);
				
				if (min > max) {
					renderer.renderError("Inserire un valore minimo inferiore al valore massimo");
				} else {
					value = new IntegerIntervalFieldValue(min, max);
					check = true;
				}
			} while (!check);
			return value;
		}
		
		}
		
		// Se non matcho nulla
		throw new IllegalArgumentException("Il campo non corrisponde ad alcun campo della categoria \"Partita di calcio\"");
	}
	
	/**
	 * Metodo che acquisisce e restituisce il valore di un campo "ConferenceField".
	 * 
	 * Precondizione: Il campo che si vuole acquisire deve essere contenuto nell'enum {@link ConferenceField},
	 * ossia deve essere un campo esclusivo della categoria "Conferenza".
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 * @return Il valore acquisito
	 */
	private FieldValue acquireConferenceFieldValue(ConferenceField field) {
		switch (field) {
		
		case RELATORI :
		{
			return StringFieldValue.acquireValue(renderer, getter);
		}
			
		case ARGOMENTO :
		{
			return StringFieldValue.acquireValue(renderer, getter);
		}
		
		case SPESE_OPZIONALI :
		{
			// Variabili ausiliarie
			int option = 0;
			LinkedHashMap<String, Float> entriesBuffer = new LinkedHashMap<>();
			Stack<String> entriesOrder = new Stack<String>();
			
			
			// Ciclo di acquisizione
			do {
				
				// Stampa il riepilogo delle voci aggiunte
				renderer.renderText("Voci di spesa opzionali:");
				for (String s : entriesBuffer.keySet()) {
					renderer.renderText(String.format(" - %s : %.2f €", s, entriesBuffer.get(s)));
				}
				renderer.renderText("");
				
				// Stampa le opzioni disponibili all'utente
				renderer.renderText(" 1 - Aggiungi una voce");
				
				// Visualizzo l'opzione solo se ho gia' aggiunto almeno una voce
				if (!entriesOrder.isEmpty()) {
					renderer.renderText(" 2 - Rimuovi l'ultima voce");
				}
				
				renderer.renderText(" 0 - Conferma");
				
				option = getter.getInteger(0, 2);
				
				// Aggiunta di una nuova voce
				if (option == 1) {
					
					renderer.renderText("Inserisci la ragione della spesa");
					String reason = getter.getString();
					renderer.renderText("Inserisci l'ammontare della spesa");
					float amount = getter.getFloat();
					
					if (entriesBuffer.containsKey(reason)) {
						renderer.renderError("La voce di spesa inserita risulta gia' definita");
					}
					else {
						entriesBuffer.put(reason, amount);
						entriesOrder.push(reason);
					}
							
				}
				// Rimozione dell'ultima voce aggiunta
				else if (option == 2) {
					
					entriesBuffer.remove(entriesOrder.pop());
				}
				
				renderer.renderText("");
				
			} while (option != 0);
			
			// Preparo l'effettivo valore del campo se l'utente ha definito almeno una voce di spesa
			if (entriesOrder.size() > 0) {
				
				OptionalCostsFieldValue value = new OptionalCostsFieldValue();
				for (String s : entriesBuffer.keySet()) {
				
					value.addEntry(s, entriesBuffer.get(s));
					
				}
				return value;

			}
			// In caso contrario restituisco null
			else {
				return null;
			}
			
		}
		
		}
		
		// Se non matcho nulla
		throw new IllegalArgumentException("Il campo non corrisponde ad alcun campo della categoria \"Conferenza\"");
	}

	/**
	 * Verifica che una certa data sia successiva o al più uguale al valore temporale contenuto in un campo specificato.
	 * Se il campo non contiene valore, restituisce false.
	 * Se il campo non ha valore di tipo {@link DateFieldValue}, restituisce un'eccezione.
	 * (Essendo un metodo privato, è compito della classe stessa assicurarsi che l'eccezione non si verifichi).
	 * 
	 * @param date La data da analizzare
	 * @param comparingDateField Il campo da cui estrarre il valore (di tipo "data") con cui effettuare il confronto
	 * @return true se il valore del campo ESISTE && se la data è SUCCESSIVA o CONTEMPORANEA.
	 */
	private boolean isAfterOrEqualToDateField(DateFieldValue date, Field comparingDateField) {
		// Verifico che il campo abbia un tipo "data"
		if (comparingDateField.getType() != DateFieldValue.class) {
			throw new IllegalArgumentException("Impossibile interpretare il campo come data o istante temporale");
		}
		
		return (
				// Verifico che il valore del campo di comparazione sia presente
				this.provisionalFieldValues.get(comparingDateField) != null && 
				// Verifico che la data da controllare sia successiva o contemporanea al valore del campo
				date.compareTo((Date) provisionalFieldValues.get(comparingDateField)) >= 0
				);
	}

	/**
	 * Verifica che una certa data sia precendte o al più uguale al valore temporale contenuto in un campo specificato.
	 * Se il campo non contiene valore, restituisce false.
	 * Se il campo non ha valore di tipo {@link DateFieldValue}, restituisce un'eccezione.
	 * (Essendo un metodo privato, è compito della classe stessa assicurarsi che l'eccezione non si verifichi).
	 * 
	 * @param date La data da analizzare
	 * @param comparingDateField Il campo da cui estrarre il valore (di tipo "data") con cui effettuare il confronto
	 * @return true se il valore del campo ESISTE && se la data è PRECENDENTE o CONTEMPORANEA.
	 */
	private boolean isBeforeOrEqualToDateField(DateFieldValue date, Field comparingDateField) {
		// Verifico che il campo abbia un tipo "data"
		if (comparingDateField.getType() != DateFieldValue.class) {
			throw new IllegalArgumentException("Impossibile interpretare il campo come data o istante temporale");
		}
		
		return (
				// Verifico che il valore del campo di comparazione sia presente
				this.provisionalFieldValues.get(comparingDateField) != null && 
				// Verifico che la data da controllare sia successiva o contemporanea al valore del campo
				date.compareTo((Date) provisionalFieldValues.get(comparingDateField)) <= 0
				);
	}
	
	/**
	 * Restituisce "true" se il campo ha già un valore assegnato.
	 * 
	 * Precondizione: il campo deve appartenere alla categoria dell'evento.
	 * Se ciò non succede, il metood restituisce false.
	 * 
	 * @param field Il campo da verificare
	 * @return La conferma di inizializzazione
	 */
	private boolean isInitialized(Field field) {
		return (this.provisionalFieldValues.containsKey(field) && this.provisionalFieldValues.get(field) != null);
	}
	
}
