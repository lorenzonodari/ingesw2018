package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

/**
 * Classe astratta che implementa più nello specifico il concetto di "Fieldable", ossia
 * di un oggetto composto da specifici campi (la cui lista è immutabile) a ciascuno dei quali
 * può o deve essere associato un valore coerente con certi vincoli.
 * 
 * @author Michele Dusi
 *
 */
public abstract class AbstractFieldable implements Fieldable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4709738112160049362L;
	
	/** Eccezioni **/
	private static final String FIELD_NOT_PRESENT_EXCEPTION = "Il campo \"%s\" non appartiene alla lista dei campi previsti per questo oggetto AbstractFieldable";
	private static final String FIELD_NULL_EXCEPTION = "Non è possibile eseguire il metodo con un campo nullo";
	private static final String FIELDVALUE_NULL_EXCEPTION = "Non è possibile eseguire il metodo con un valore di campo nullo";
	private static final String FIELDVALUE_TYPE_NOT_VALID_EXCEPTION = "Il valore \"%s\" di tipo \"%s\" non è assegnabile al campo \"%s\" che richiede un valore di tipo \"%s\"";
	
	/** Mappa dei campi che definiscono e caratterizzano l'utente */
	private final Map<Field, FieldValue> valuesMap;
	
	/**
	 * Costruttore.
	 * Richiede come parametro la lista di campi previsti per l'oggetto.
	 * 
	 * Nota: tale lista NON può essere modificata runtime, ossia un oggetto AbstractFieldable
	 * non potrà mai avere un valore di un campo NON previsto dalla sua lista iniziale.
	 * Potrà tuttavia modificare, rimuovere o aggiungere valori di campi previsti.
	 * 
	 * @param fieldsList La lista di campi previsti per tale oggetto
	 */
	public AbstractFieldable(final List<Field> fieldsList) {
		// Verifico che i parametri non siano nulli o vuoti
		if (fieldsList == null || fieldsList.isEmpty()) {
			throw new IllegalArgumentException("Impossibile creare un nuovo AbstractFieldable: lista di campi nulla o vuota");
		}
		
		this.valuesMap = new HashMap<>();
		// Per ciascun campo, lo aggiungo come chiave alla HashMap
		for (Field f : fieldsList) {
			this.valuesMap.put(f, null);
		}
	}

	/**
	 * Metodo che restituisce "true" se il campo passato come parametro è
	 * presente all'interno dell'oggetto Fieldable.
	 * 
	 * Precondizione: il campo non deve essere nullo.
	 * 
	 * @param searchedField il campo cercato
	 * @return "true" se il campo è previsto per tale oggetto, "false" altrimenti.
	 */
	@Override
	public boolean hasField(Field searchedField) {
		// Verifica della precondizione
		if (searchedField == null) {
			throw new IllegalArgumentException(FIELD_NULL_EXCEPTION);
		}
		return this.valuesMap.containsKey(searchedField);
	}

	/**
	 * Metodo che restituisce "true" se il campo passato come parametro è
	 * stato inizializzato nell'oggetto, ossia se ha associato un oggetto {@link FieldValue} valido.
	 * 
	 * Nota: Se il campo NON è presente all'interno dell'oggetto, viene lanciata un'eccezione (e quindi
	 * non viene restituito alcun valore).
	 * 
	 * @param searchedField il campo di cui si vuole sapere se esiste già un valore
	 * @return "true" se il campo è previsto per tale oggetto, "false" altrimenti.
	 */
	public boolean hasFieldValue(Field searchedField) {
		if (!this.hasField(searchedField)) {
			throw new IllegalArgumentException(
					String.format(FIELD_NOT_PRESENT_EXCEPTION, searchedField.getName()));
		} else {
			return (this.valuesMap.get(searchedField) != null);
		}
	}

	/**
	 * Restituisce il valore associato ad uno specifico campo.
	 * 
	 * Precondizione: il campo deve essere previsto per questo oggetto.
	 * 
	 * @param chosenField Il campo di cui si richiede il valore
	 * @return Il valore del campo
	 */
	@Override
	public FieldValue getFieldValue(Field chosenField) {
		// Verifico se il campo è previsto per questo oggetto
		if (this.hasField(chosenField)) {
			// In caso affermativo, restituisco il valore associato
			return this.valuesMap.get(chosenField);
			
		} else {
			// In caso negativo, lancio un'eccezione
			throw new IllegalArgumentException(
					String.format(FIELD_NOT_PRESENT_EXCEPTION, chosenField.getName()));
		}
	}

	/**
	 * Restituisce l'intera lista attuale di campi e relativi valori.
	 * 
	 * @return La mappa attuale di coppie "Field-FieldValue"
	 */
	@Override
	public Map<Field, FieldValue> getAllFieldValues() {
		return this.valuesMap;
	}
	
	/**
	 * Metodo che permette la modifica runtime di un valore associato ad un campo dell'utente.
	 * 
	 * <ul>
	 * 
	 * <li>Precondizione: gli oggetti {@link Field} e {@link FieldValue} passati come parametro
	 * non devono essere nulli.
	 * 
	 * <li>Precondizione: l'oggetto {@link Field} passato come parametro deve essere un campo previsto per
	 * questo oggetto AbstractFieldable.
	 * 
	 * <li>Precondizione: l'oggetto {@link FieldValue} passato come parametro deve essere un'istanza
	 * del tipo previsto dall'oggetto {@link Field}, recuperabile dal metodo "getType()".
	 * 
	 * <li>Precondizione: l'oggetto {@link FieldValue} deve soddisfare tutte le condizioni di coerenza previste
	 * per un valore del campo indicato da {@link Field}. Poiché eseguire questi controlli all'interno della
	 * classe AbstractFieldable risulterebbe troppo oneroso e -per l'impostazione del programma- lievemente fuori luogo, si 
	 * suppone con ragionevole certezza che tale condizione sia verificata a priori nel metodo chiamante 
	 * dell'unica classe autorizzata a gestire la creazione e la modifica di AbstractFieldable: le classi Builder.
	 * 
	 * </ul>
	 * 
	 * @param chosenField Il campo che si vuole modificare
	 * @param newValue Il nuovo valore da associare al campo 
	 */
	@Override
	public boolean setFieldValue(Field chosenField, FieldValue newValue) {
		// Controllo la precondizione, verificando se il campo è fra quelli previsti
		if (this.hasField(chosenField)) {
			// Poi controllo che il valore sia del tipo corretto
			if (!chosenField.getType().isInstance(newValue)) {
				throw new IllegalArgumentException(String.format(
						FIELDVALUE_TYPE_NOT_VALID_EXCEPTION,
						newValue.toString(),
						newValue.getClass().getSimpleName(),
						chosenField.getName(),
						chosenField.getType().getSimpleName()));
			} else if (newValue == null) {
				throw new IllegalArgumentException(FIELDVALUE_NULL_EXCEPTION);
			} else {
				// In caso affermativo, associo il nuovo valore alla mappa
				this.valuesMap.put(chosenField, newValue);
				return true;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo che causa l'assegnamento a specifici campi dei valori di default previsti.
	 * 
	 * Nota: è un metodo astratto, poiché i valori di default dipendono dalla 
	 * classe che estende AbstractFieldable.
	 */
	public abstract void setDefaultFieldValues();

	/**
	 * Restituisce "true" se e solo se tutti i campi obbligatori sono già stati inizializzati.
	 * 
	 * @return "true" se tutti i campi obbligatori sono stati inizializzati.
	 */
	@Override
	public boolean hasAllMandatoryField() {
		// Scorro su tutti i campi previsti
		for (Field f : this.valuesMap.keySet()) {
			// Se trovo un campo obbligatorio che NON è stato inizializzato
			if (f.isMandatory() && this.valuesMap.get(f) == null) {
				return false;
			}
		}
		return true;
	}

}
