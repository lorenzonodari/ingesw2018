package it.unibs.ingesw.dpn.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe astratta che rappresenta in maniera concettuale un evento generico gestito dal programma.
 * Questa classe viene poi specificata in differenti classi a seconda delle categorie previste.
 * (Si veda, ad esempio, la classe {@link SoccerMatch}.)
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
	
	/**
	 * Crea un nuovo evento con la relativa categoria.
	 * Tale costruttore verrà comunque chiamato da una classe apposita, 
	 * la cui responsabilità principale sarà creare gli eventi nella maniera prevista dal programma.
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
	public Event(CategoryEnum category, Object [] fieldValues) {
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
	 * Restituisce la categoria di appartenenza dell'evento come istanza di {@link CategoryEnum}.
	 * 
	 * @return la categoria a cui appartiene l'evento.
	 */
	public CategoryEnum getCategory() {
		return category;
	}

}
