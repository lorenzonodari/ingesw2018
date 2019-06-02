package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

/**
 * L'interfaccia "Field" rappresenta un campo di una categoria all'interno del modello concettuale del progetto.
 * Un oggetto "Field" deve essere in grado di fornire un nome, una descrizione e l'obbligatorietà.
 * Inoltre un oggetto "Field" mantiene un riferimento ad una classe, che è la classe le cui istanze rappresentano il valore del campo.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 * 
 */
public interface Field {

	/**
	 * Restituisce il nome dell'oggetto Field.
	 * 
	 * @return il nome dell'oggetto Field.
	 */
	public String getName();
	
	/**
	 * Restituisce la descrizione del campo.
	 * 
	 * @return la descrizione del campo, come oggetto {@link String}
	 */
	public String getDescription();
	
	/**
	 * Restituisce l'obbligatorietà del campo.
	 * 
	 * @return true se la compilazione del campo è obbligatoria, false altrimenti
	 */
	public boolean isMandatory();
	
	/**
	 * Restituisce la modificabilità del campo.
	 * Di default, questo metodo restituisce "false", poiché si è deciso che -a meno di ulteriori specifiche-
	 * il valore di un campo non possa essere modificato.
	 * 
	 * @return true se la modifica del campo è permessa dopo che l'oggetto a cui il campo fa riferimento è già stato creato, false altrimenti
	 */
	public default boolean isEditable() {
		return false;
	}
	
	/**
	 * Restituisce la classe le cui istanze sono i possibili valori di questo Field.
	 * 
	 * @return il "tipo" del campo
	 */
	public Class<? extends FieldValue> getType();
	
}
