package it.unibs.ingesw.dpn.model.fields;

import java.util.Map;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

/**
 * Interfaccia che rappresenta un qualunque oggetto composto da Field.
 * Al momento nel software sono presenti due classi principali che implementano questa interfaccia:
 * - User
 * - Event
 * 
 * E' stata creata principalmente per agevolare e uniformare il processo di creazione di
 * oggetti Fieldable tramite i Builder.
 * 
 * Nota: un oggetto Fieldable NON può aggiungere o rimuovere Field a runtime, ossia la lista di Field
 * prevista per tale oggetto è fissa. Tuttavia, esso può -in alcuni casi- modificare il valore
 * associato a ciascun Field.
 * 
 * @author Michele Dusi
 *
 */
public interface Fieldable {

	/**
	 * Metodo che restituisce "true" se il campo passato come parametro è
	 * presente all'interno dell'oggetto Fieldable.
	 * 
	 * @param searchedField il campo cercato
	 * @return "true" se il campo è previsto per tale oggetto, "false" altrimenti.
	 */
	public boolean hasField(Field searchedField);

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
	public boolean hasFieldValue(Field searchedField);
	
	/**
	 * Restituisce il valore che caratterizza l'oggetto Fieldable nel campo richiesto.
	 * 
	 * @param chosenField il campo di cui si vuole conoscere il valore
	 * @return Il valore del campo
	 */
	public FieldValue getFieldValue(Field chosenField);
	
	/**
	 * Restituisce la lista di Field e FieldValue attualmente presente nell'oggetto.
	 * 
	 * @return la mappa dei campi e dei rispettivi valori
	 */
	public Map<Field, FieldValue> getAllFieldValues();
	
	/**
	 * Metodo che permette la modifica runtime di un valore associato ad un campo.
	 * 
	 * Precondizione: il campo deve essere uno dei campi previsti dal Fieldable.
	 * 
	 * @param chosenField Il campo che si vuole settare
	 * @param newValue Il nuovo valore da associare al campo 
	 */
	public boolean setFieldValue(Field chosenField, FieldValue newValue);
	
	/**
	 * Metodo che causa l'assegnamento a specifici campi dei valori di default previsti.
	 * 
	 * Nota: è un metodo astratto, poiché i valori di default dipendono dalla 
	 * classe che estende AbstractFieldable.
	 */
	public void setDefaultFieldValues();
	
	/**
	 * Metodo che restituisce "true" se e solo se tutti i campi marcati come "obbligatori"
	 * hanno già un valore valido associato.
	 * 
	 * @return "true" se tutti i campi obbligatori sono già stati inizializzati
	 */
	public boolean hasAllMandatoryField();

}
