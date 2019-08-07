package it.unibs.ingesw.dpn.ui;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Stack;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fields.SoccerMatchField;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.GenderEnumFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerIntervalFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.LocalDateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.TimeAmountFieldValue;

public class FieldValueAcquirer {
	
	/** Parametri */
	private static final int AGE_LIMIT = 12;
	
	/** Input / Output */
	private final UIRenderer renderer;
	private final InputGetter getter;
	
	/** Temporary reference */
	private Fieldable provisionalFieldable = null;

	/**
	 * Costruttore. 
	 * 
	 * @param renderer
	 * @param getter
	 */
	public FieldValueAcquirer(UIRenderer renderer, InputGetter getter) {
		if (renderer == null || getter == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo FieldValueAcquirer con parametri nulli");
		}
		// Inizializzo I/O
		this.renderer = renderer;
		this.getter = getter;
	}
	
	/**
	 * Metodo che permette l'acquisizione di un valore per un dato campo {@link Field}, su un dato
	 * oggetto {@link Fieldable}.
	 * Il primo parametro serve ovviamente per capire come procedere con l'acquisizione, mentre il secondo
	 * permette di effettuare controlli comparati con altri campi dello stesso oggetto che possono portare ad una
	 * migliore costruzione dello stesso (mantenendo più di una volta un'importante coerenza interna fra i campi).
	 * 
	 * Invariante di metodo: il parametro "provisionalFieldable" non viene modificato in alcun modo.
	 * Sarà quindi compito del metodo chiamante assegnare il valore corretto al campo, valore che viene ritornato
	 * da questo metodo.
	 * Questa scelta è stata fatta per evitare effetti collaterali sui parametri.
	 * 
	 * @param provisionalFieldable L'oggetto per cui si vuole acquisire un campo, utilizzato per comparazioni con altri campi.
	 * @param field Il campo di cui si vuole acquisire il valore.
	 * @return Il valore acquisito dal campo.
	 */
	public FieldValue acquireFieldValue(Fieldable provisionalFieldable, Field field) {
		// Verifico le precondizioni
		if (field == null || provisionalFieldable == null) {
			throw new IllegalArgumentException("Impossibile acquisire un campo nullo o su un oggetto nullo");
		}
		
		// Inizializzo il riferimento in modo che i metodi di questo oggetto possano utilizzarlo in maniera comoda.
		// La scelta più corretta sarebbe stato ovviamente passarlo per parametro ogni volta, ma visto che questo 
		// è l'unico metodo esterno posso inizializzarlo all'inizio e terminarlo alla fine.
		this.provisionalFieldable = provisionalFieldable;
		
		// Stampo sul renderer una breve introduzione
		this.printFieldIntro(field);

		// Campi comuni a tutte le categorie di Eventi
		if (field instanceof CommonField) {
			return acquireCommonFieldValue((CommonField) field);
			
		// Campi esclusivi della categoria "SoccerMatchField"
		} else if (field instanceof SoccerMatchField) {
			return acquireSoccerMatchFieldValue((SoccerMatchField) field);
			
		// Campi esclusivi della categoria "ConferenceField"
		} else if (field instanceof ConferenceField) {
			return acquireConferenceFieldValue((ConferenceField) field);
			
		} else if (field instanceof UserField) {
			return acquireUserFieldValue((UserField) field);
			
		// Campi estranei
		} else {
			throw new IllegalArgumentException("Campo \"Field\" non riconosciuto: impossibile acquisire un valore");
		}
		
	}

	/* METODI PRIVATI DI UTILITA' */
	
	/**
	 * Metodo che stampa sul renderer una breve introduzione al campo che si intende acquisire.
	 * 
	 * @param field Il campo di cui si vogliono visualizzare le informazioni
	 */
	private void printFieldIntro(Field field) {
		renderer.renderLineSpace();
		renderer.renderText(String.format(
				" ### %-50s",
				field.getName().toUpperCase()));
		renderer.renderText(String.format(
				" ### %s",
				field.getDescription()));
		renderer.renderLineSpace();
	}

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
			if (provisionalFieldable.hasFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE)) {
				
				// Setup di "Durata"
				TimeAmountFieldValue duration = new TimeAmountFieldValue(
						acquiredDate,
						((DateFieldValue) provisionalFieldable.getFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE)));
				provisionalFieldable.setFieldValue(CommonField.DURATA, duration);
				
			} else if (provisionalFieldable.hasFieldValue(CommonField.DURATA)) {
				
				// Setup di "Data e ora conclusive"
				DateFieldValue dataEOraConclusive = new DateFieldValue(
						acquiredDate.getTime() +
						1000 * ((TimeAmountFieldValue) provisionalFieldable.getFieldValue(CommonField.DURATA)).getSeconds()
						);
				provisionalFieldable.setFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE, dataEOraConclusive);
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
			if (provisionalFieldable.hasFieldValue(CommonField.DATA_E_ORA)) {
				// Setup di "Durata"
				TimeAmountFieldValue duration = new TimeAmountFieldValue(
						((DateFieldValue) provisionalFieldable.getFieldValue(CommonField.DATA_E_ORA)),
						acquiredDate);
				provisionalFieldable.setFieldValue(CommonField.DURATA, duration);
			}
			
			// Restituzione del valore acquisito
			return acquiredDate;
		}
		
		case DURATA : 
		{
			// Acquisizione del dato
			TimeAmountFieldValue acquiredValue = TimeAmountFieldValue.acquireValue(renderer, getter);
			
			// Setup aggiuntivi, derivati da dati già presenti nell'evento
			if (provisionalFieldable.hasFieldValue(CommonField.DATA_E_ORA)) {
				// Setup di "Data e ora conclusive"
				DateFieldValue dataEOraConclusive = new DateFieldValue(
						((DateFieldValue) provisionalFieldable.getFieldValue(CommonField.DATA_E_ORA)).getTime() +
						1000 * acquiredValue.getSeconds()
						);
				provisionalFieldable.setFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE, dataEOraConclusive);
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
			// Nota: qui all'interno NON viene effettuato il controllo sull'esistenza precedente del Nickname
			return StringFieldValue.acquireValue(renderer, getter);
			
		}
			
		case DATA_DI_NASCITA :
		{
			boolean okFlag = false;
			LocalDateFieldValue date = null;
			// Prompt e interazione con l'utente
			do {
				try {
					date = LocalDateFieldValue.acquireValue(renderer, getter);
				}
				catch (DateTimeException e) {
					renderer.renderError("Data non accettabile");
					continue;
				}
				
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
		
		case CATEGORIE_DI_INTERESSE : 
		{
			// Inizializzo le variabili ausiliarie
			int option = 0;
			Category [] categories = Category.values();
			boolean [] checksArray = new boolean[categories.length];
			
			// Creo la variabile che conterrà il valore finale del campo
			CategoryListFieldValue list = (CategoryListFieldValue) provisionalFieldable.getFieldValue(UserField.CATEGORIE_DI_INTERESSE);
			
			// Verifico se ho già un valore inizializzato
			if (list != null) {
				for (int i = 0; i < categories.length; i++) {
					if (list.contains(categories[i])) {
						checksArray[i] = true;
					}
				}
			} else {
				list = new CategoryListFieldValue();
			}
			
			// Ciclo di interazione con l'utente
			do {
				renderer.renderText("Seleziona le categorie a cui sei interessato/a:");
				// Per ciascuna categoria creo e visualizzo l'opzione relativa
				for (int i = 0; i < categories.length; i++) {
					renderer.renderText(String.format(
							"%3d) %-50s [%s]",
							(i + 1),
							categories[i].getName(),
							(checksArray[i] ? "X" : " ")
							));
				}
				renderer.renderText(String.format("%3d) %-50s", 0, "Esci e conferma"));
				renderer.renderLineSpace();
				option = getter.getInteger(0, categories.length);
				
				// Inverto il check dell'opzione selezionata
				if (option != 0) {
					checksArray[option - 1] ^= true;
				}
				// Continuo finché l'utente non decide di uscire
			} while (option != 0);

			for (int i = 0; i < categories.length; i++) {
				if (checksArray[i]) {
					list.addCategory(categories[i]);
				} else {
					list.removeCategory(categories[i]);
				}
			}
			// Restituzione del dato acquisito
			return list;
		}
		
		}
		
		// Se non matcho nulla
		throw new IllegalArgumentException("Il campo non corrisponde ad alcun campo Utente");
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
				provisionalFieldable.getFieldValue(comparingDateField) != null && 
				// Verifico che la data da controllare sia successiva o contemporanea al valore del campo
				date.compareTo((Date) provisionalFieldable.getFieldValue(comparingDateField)) >= 0
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
				provisionalFieldable.getFieldValue(comparingDateField) != null && 
				// Verifico che la data da controllare sia successiva o contemporanea al valore del campo
				date.compareTo((Date) provisionalFieldable.getFieldValue(comparingDateField)) <= 0
				);
	}

}
