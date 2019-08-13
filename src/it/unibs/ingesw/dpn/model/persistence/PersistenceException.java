package it.unibs.ingesw.dpn.model.persistence;

/**
 * Eccezione generata da errori durante le operazioni di persistenza dei dati
 */
public class PersistenceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6164840718332259993L;

	public PersistenceException(String string, Exception ex) {
		super(string, ex);
	}

}
