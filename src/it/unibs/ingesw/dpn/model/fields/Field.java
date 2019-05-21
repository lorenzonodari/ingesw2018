package it.unibs.ingesw.dpn.model.fields;

import java.util.Map;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

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
	 * Interfaccia funzionale per acquisire un valore del tipo specifico.
	 */
	@FunctionalInterface
	public interface FieldValueAcquirer {
		public FieldValue acquireFieldValue(UIRenderer renderer, InputGetter getter, Map<Field, FieldValue> partialValues);
	};

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
	 * Restituisce la classe le cui istanze sono i possibili valori di questo Field.
	 * 
	 * @return il "tipo" del campo
	 */
	public Class<? extends FieldValue> getType();
	
	/**
	 * Metodo "controller" che si occupa, attraverso l'interazione con un Renderer ed un Getter,
	 * di acquisire dall'utente un valore valido per il campo.
	 * Ad esempio, se il campo avesse come tipo "String", questo metodo acquisisce una stringa dall'utente.
	 * 
	 * @param renderer Un'istanza di UIRenderer per visualizzare le informazioni
	 * @param getter Un'istanza di InputGetter per catturare le informazioni
	 * @return un valore (formalmente corretto) per il campo
	 */
	public FieldValue acquireFieldValue(UIRenderer renderer, InputGetter getter, Map<Field, FieldValue> partialValues);
	
}
