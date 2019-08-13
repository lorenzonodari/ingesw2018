package it.unibs.ingesw.dpn.ui;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Stack;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.FieldCompatibilityException;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fields.SoccerMatchField;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.GenderEnumFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerIntervalFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.LocalDateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;

public class FieldValueUIAcquirer {
	
	/** Parametri */
	private static final int AGE_LIMIT = 12;
	
	/** Input / Output */
	private final UserInterface userInterface;
	
	/** Temporary reference */
	private Fieldable provisionalFieldable = null;

	/**
	 * Costruttore. 
	 * 
	 * @param userInterface Un'istanza di {@link UserInterface} che fa riferimento all'interfaccia utente da utilizzare.
	 */
	public FieldValueUIAcquirer(UserInterface userInterface) {
		if (userInterface == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo FieldValueAcquirer con parametri nulli");
		}
		// Inizializzo I/O
		this.userInterface = userInterface;
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
		
		boolean repeatFlag = true;
		FieldValue value = null;

		do {
			// Tento di acquisire un valore
			try {
				// Il primo livello di verifica che faccio è sul valore stesso, deve essere accettabile in quanto tale
				
	//#########################################################//
				value = null; // TODO Qui andrebbe la chiamata per acquisire un valore FieldValue "crudo"
	//#########################################################//
				
				// Il secondo livello di verifica coinvolge anche il Field e l'oggetto Fieldable interessato,
				// e si compone di tre fasi:
				// 1) Controllo sul tipo del Field
				// 2) Controllo della compatibilità con gli altri campi
				// 3) Propagazione del valore ad altri valori "legati" dello stesso oggetto Fieldable
				field.checkTypeAndCompatibilityAndPropagateValueAcquisition(provisionalFieldable, value);
				
				// Se termino i controlli, termino anche la ripetizione
				repeatFlag = false;
			}
			catch (FieldCompatibilityException e) {
				// Se durante il processo si verifica un'eccezione, mostro l'eccezione e ripeto l'acquisizione
				userInterface.renderer().renderError(e.getMessage());
				
				// Ripeto l'acquisizione
				repeatFlag = true;
			}
			
		} while (repeatFlag);
		
		return value;
		
	}

	/* METODI PRIVATI DI UTILITA' */
	
	/**
	 * Metodo che stampa sul renderer una breve introduzione al campo che si intende acquisire.
	 * 
	 * @param field Il campo di cui si vogliono visualizzare le informazioni
	 */
	private void printFieldIntro(Field field) {
		userInterface.renderer().renderLineSpace();
		userInterface.renderer().renderText(String.format(
				" ### %-50s",
				field.getName().toUpperCase()));
		userInterface.renderer().renderText(String.format(
				" ### %s",
				field.getDescription()));
		userInterface.renderer().renderLineSpace();
	}
	
	/* 
	 * TODO TODO TODO TODO TODO
	 * I METODI SUCCESSIVI DOVRANNO ESSERE ELIMINATI.
	 * AL MOMENTO VENGONO MANTENUTI COME RIFERIMENTO PER LA 
	 * SCRITTURA DEL CODICE PER L'ACQUISIZIONE DEI FIELDVALUE.
	 * TODO TODO TODO TODO TODO
	 */

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
			// TODO:
			// Quando questo pezzo di codice verrà tradotto e messo in "SoccerMatchField", sarà importante
			// creare un metodo apposito solo con renderer e getter
			GenderEnumFieldValue [] values = GenderEnumFieldValue.values();
			int i = 1;
			for (GenderEnumFieldValue gender : values) {
				userInterface.renderer().renderText(String.format("%3d)\t%s", 
						i++, gender.toString()));
			}
			int input = userInterface.getter().getInteger(1, values.length);
			return values[input - 1];
		}
			
		case FASCIA_DI_ETA :
		{

			IntegerIntervalFieldValue value = null;
			boolean check = false;
			do {
				userInterface.renderer().renderText("Inserisci il valore minimo");
				int min = userInterface.getter().getInteger(0, 150);
				userInterface.renderer().renderText("Inserisci il valore massimo");
				int max = userInterface.getter().getInteger(0, 150);
				
				if (min > max) {
					userInterface.renderer().renderError("Inserire un valore minimo inferiore al valore massimo");
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
			return StringFieldValue.acquireValue(userInterface);
		}
			
		case ARGOMENTO :
		{
			return StringFieldValue.acquireValue(userInterface);
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
				userInterface.renderer().renderText("Voci di spesa opzionali:");
				for (String s : entriesBuffer.keySet()) {
					userInterface.renderer().renderText(String.format(" - %s : %.2f €", s, entriesBuffer.get(s)));
				}
				userInterface.renderer().renderText("");
				
				// Stampa le opzioni disponibili all'utente
				userInterface.renderer().renderText(" 1 - Aggiungi una voce");
				
				// Visualizzo l'opzione solo se ho gia' aggiunto almeno una voce
				if (!entriesOrder.isEmpty()) {
					userInterface.renderer().renderText(" 2 - Rimuovi l'ultima voce");
				}
				
				userInterface.renderer().renderText(" 0 - Conferma");
				
				option = userInterface.getter().getInteger(0, 2);
				
				// Aggiunta di una nuova voce
				if (option == 1) {
					
					userInterface.renderer().renderText("Inserisci la ragione della spesa");
					String reason = userInterface.getter().getString();
					userInterface.renderer().renderText("Inserisci l'ammontare della spesa");
					float amount = userInterface.getter().getFloat();
					
					if (entriesBuffer.containsKey(reason)) {
						userInterface.renderer().renderError("La voce di spesa inserita risulta gia' definita");
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
				
				userInterface.renderer().renderText("");
				
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
			return StringFieldValue.acquireValue(userInterface);
			
		}
			
		case DATA_DI_NASCITA :
		{
			boolean okFlag = false;
			LocalDateFieldValue date = null;
			// Prompt e interazione con l'utente
			do {
				try {
					date = LocalDateFieldValue.acquireValue(userInterface);
				}
				catch (DateTimeException e) {
					userInterface.renderer().renderError("Data non accettabile");
					continue;
				}
				
				// Verifiche
				if (date.getLocalDate().isAfter(LocalDate.now())) {
					userInterface.renderer().renderError("Impossibile accettare una data futura");
				} else if (date.getLocalDate().isAfter(LocalDate.now().minusYears(AGE_LIMIT))) {
					userInterface.renderer().renderError(String.format(
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
				userInterface.renderer().renderText("Seleziona le categorie a cui sei interessato/a:");
				// Per ciascuna categoria creo e visualizzo l'opzione relativa
				for (int i = 0; i < categories.length; i++) {
					userInterface.renderer().renderText(String.format(
							"%3d) %-50s [%s]",
							(i + 1),
							categories[i].getName(),
							(checksArray[i] ? "X" : " ")
							));
				}
				userInterface.renderer().renderText(String.format("%3d) %-50s", 0, "Esci e conferma"));
				userInterface.renderer().renderLineSpace();
				option = userInterface.getter().getInteger(0, categories.length);
				
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

}
