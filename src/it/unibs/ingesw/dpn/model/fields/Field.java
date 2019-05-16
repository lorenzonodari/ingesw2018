package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

/**
 * La classe "Field" rappresenta un campo di una categoria all'interno del modello concettuale del progetto.
 * Un campo è caratterizzato da un nome, da una descrizione e dall'essere o meno obbligatorio.
 * Inoltre è parametrizzato su una classe, che è la classe le cui istanze rappresentano il valore del campo.
 * 
 * Nota: ogni attributo viene presentato come "final" poichè non vi è mai la necessità che il suo valore cambi runtime.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 * 
 */
public class Field<T extends FieldValue> implements Serializable {
	
	private static final long serialVersionUID = -7411339597195983805L;

	private static final String TO_STRING =
			  "Nome:           %s\n"
			+ "Descrizione:    %s\n"
			+ "%s\n";
	
	private static final String MANDATORY_TAG = "Obbligatorio";
	private static final String OPTIONAL_TAG = "Facoltativo";

	private final String name;
	private final String description;
	private final boolean mandatory;
	private final FieldValueAcquirer<T> valueAcquirer;
	
	
	public interface FieldValueAcquirer<T> {
		public T acquireFieldValue(UIRenderer renderer, InputGetter getter);
	}
	
	/**
	 * Costruttore.
	 * 
	 * Precondizione: name deve essere un nome valido, non nullo.
	 * 
	 * Precondizione: description deve essere una stringa non nulla.
	 * 
	 * Precondizione: mandatory deve essere un valore non nullo.
	 * 
	 * Precondizione: type deve essere un valore non nullo, relativo ad una classe che implementa
	 * l'interfaccia {@link FieldValue}. Quest'ultima condizione dovrebbe essere garantita in automatico
	 * dall'IDE utilizzato per programmare.
	 * 
	 * @param name Il nome del campo
	 * @param description La descrizione del campo
	 * @param mandatory L'obbligatorietà del campo
	 * @param type Il tipo del valore del campo
	 */
	public Field(String name, String description, boolean mandatory, FieldValueAcquirer<T> valueAcquirer) {
		if (name == null || description == null || valueAcquirer == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.valueAcquirer = valueAcquirer;
	}

	/**
	 * @return Il nome del campo
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return La descrizione del campo
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return L'obbligatorietà del campo
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	/**
	 * Verifica se questo campo è uguale ad un altro.
	 * E' possibile passare all'interno del metodo un qualunque oggetto. Tale metodo 
	 * restituisce TRUE se e solo se due campi hanno lo stesso nome.
	 * Per questo motivo, questo metodo può essere utilizzato nella classe {@link Category} per capire quando
	 * due campi hanno lo stesso nome (e quindi, a livello di categoria, sono uguali).
	 * 
	 * @param otherField un oggetto con cui operare il confronto.
	 * @return l'uguaglianza fra i due campi.
	 */
	@Override
	public boolean equals(Object otherField) {
		if (otherField != null && Field.class.isInstance(otherField)) {
			@SuppressWarnings("unchecked")
			Field<T> castOtherField = (Field<T>) otherField;
			return this.name.equals(castOtherField.name);
		} else {
			return false;
		}
	}
	
	/**
	 * Restituisce la stringa per la rappresentazione testuale dell'intero campo.
	 * 
	 * Nota: il tipo del campo non viene visualizzato, poiché l'utente riceve tutte 
	 * le informazioni di cui ha bisogno dal campo descrizione.
	 * 
	 * @return la rappresentazione testuale dell'intero campo.
	 */
	@Override
	public String toString() {
		String str = String.format(TO_STRING, 
				this.name,
				this.description,
				this.mandatory ? 
						MANDATORY_TAG :
						OPTIONAL_TAG
				);
		return str;
	}

	/**
	 * Passa il controllo all'istanza di {@link FieldValueAcquirer} che si occupa di acquisire
	 * un valore per questo specifico campo.
	 * 
	 * @param renderer Il renderer da chiamare per visualizzare eventuali messaggi d'errore
	 * @param getter L'oggetto che si occupa di acquisire i dati in maniera primitiva
	 * @return L'oggetto <T> che rappresenta il valore del campo
	 */
	public T acquireFieldValue(UIRenderer renderer, InputGetter getter) {
		renderer.renderLineSpace();
		renderer.renderText(String.format(
				" ### %-35s",
				this.name.toUpperCase()));
		renderer.renderText(String.format(
				" ### %s",
				this.description));
		renderer.renderLineSpace();
		return this.valueAcquirer.acquireFieldValue(renderer, getter);
	}
	
	
}
