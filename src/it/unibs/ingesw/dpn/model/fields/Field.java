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

	public static final String FIELD_FORMAT_STRING =
			  "Nome:           %s\n"
			+ "Descrizione:    %s\n"
			+ "%s\n"
			+ "%s\n";	
	public static final String MANDATORY_TAG = "Obbligatorio";
	public static final String OPTIONAL_TAG = "Facoltativo";
	public static final String EDITABLE_TAG = "Modificabile";
	public static final String IMMUTABLE_TAG = "Non modificabile";

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
	 * 
	 * @return true se la modifica del campo è permessa dopo che l'oggetto a cui il campo fa riferimento è già stato creato, false altrimenti
	 */
	public boolean isEditable();
	
	/**
	 * Restituisce la classe le cui istanze sono i possibili valori di questo Field.
	 * 
	 * @return il "tipo" del campo
	 */
	public Class<? extends FieldValue> getType();
	

	/**
	 * Restituisce la stringa per la rappresentazione testuale dell'intero campo.
	 * 
	 * Nota: il tipo del campo non viene visualizzato, poiché l'utente riceve tutte 
	 * le informazioni di cui ha bisogno dal campo descrizione.
	 * 
	 * @return la rappresentazione testuale dell'intero campo.
	 */
	public default String fieldToString() {
		String str = String.format(FIELD_FORMAT_STRING, 
				this.getName(),
				this.getDescription(),
				this.isMandatory() ? 
						MANDATORY_TAG :
						OPTIONAL_TAG,
				this.isEditable() ?
						EDITABLE_TAG :
						IMMUTABLE_TAG
				);
		return str;
	}
	
}
