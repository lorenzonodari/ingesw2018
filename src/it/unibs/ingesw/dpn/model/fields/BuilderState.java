package it.unibs.ingesw.dpn.model.fields;

/**
 * Interfaccia che permette l'implementazione del pattern <em>State</em> all'interno del
 * Builder degli oggetti Fieldable.
 * 
 * @author MicheleDusi
 *
 */
public interface BuilderState {
	
	String READY = "Pronto";
	String CREATING = "Creazione";
	String EDITING = "Modifica";
	
	String STATE_EXCEPTION = "Impossibile eseguire questo metodo del Builder mentre è in modalità \"%s\"";
	
	/**
	 * Restituisce il nome comune dello stato.
	 * 
	 * @return il nome comune dello stato
	 */
	public String getStateName();

	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	public default void onStartingCreating(AbstractBuilder b) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}
	
	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	public default void onStartingEditing(AbstractBuilder b) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}

	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	public default void onFieldValueAcquisition(AbstractBuilder b, Field field) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}
	
	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	public default void onFinalisation(AbstractBuilder b) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}

	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	public default void onCancelation(AbstractBuilder b) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}

	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * In particolare, questo metodo viene chiamato tutte le volte che si cerca di ottenere dal Builder
	 * delle informazioni che dovrebbero essere disponibili unicamente durante il processo di creazione o di modifica.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	public default void onQuerying(AbstractBuilder b) {
		throw new IllegalStateException(String.format(STATE_EXCEPTION, this.getStateName().toUpperCase()));
	}
}
