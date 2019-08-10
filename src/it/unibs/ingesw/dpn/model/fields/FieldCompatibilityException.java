package it.unibs.ingesw.dpn.model.fields;

/**
 * Eccezione che viene lanciata in caso in cui un valore {@link FieldValue} non sia compatibile
 * con il {@link Field} associato o con l'oggetto {@link Fieldable} in cui è contenuto.
 * 
 * @author Michele Dusi
 *
 */
public class FieldCompatibilityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -36987654202344976L;

	/**
	 * Costruttore con messaggio.
	 * 
	 * @param message Il messaggio che indica la causa dell'eccezione
	 */
	public FieldCompatibilityException(String message) {
		super(message);
	}

}
