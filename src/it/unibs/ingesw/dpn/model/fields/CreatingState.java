package it.unibs.ingesw.dpn.model.fields;

public class CreatingState implements BuilderState {

	@Override
	public String getStateName() {
		return BuilderState.CREATING;
	}
	
	/**
	 * Metodo per l'acquisizione di un valore di un campo.
	 * Non provoca il cambio di stato, ma può modificare il comportamento 
	 * a seconda dello stato in cui ci si trova.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 * @param field Il field di cui acquisire il valore
	 */
	@Override
	public void onFieldValueAcquisition(AbstractBuilder b, Field field) {
		// Non ci sono controlli, non ci sono transizioni.
	}
	
	/**
	 * Metodo per la transizione di stato da "Creating" a "Ready" nel caso in cui
	 * venga finalizzata la creazione.
	 * La transizione si verifica solo se tutti i campi obbligatori sono stati inizializzati,
	 * altrimenti viene generata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	@Override
	public void onFinalisation(AbstractBuilder b) {
		if (b.verifyMandatoryFields()) {
			b.setState(new ReadyState());
		} else {
			throw new IllegalStateException("Impossibile finalizzare la creazione: non tutti i campi obligatori sono stati compilati");
		}
	}

	/**
	 * Metodo per la transizione di stato da "Creating" a "Ready" nel caso in cui
	 * venga annullata la procedura di creazione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	@Override
	public void onCancelation(AbstractBuilder b) {
		b.setState(new ReadyState());
	}

	/**
	 * Metodo che viene chiamato ogni volta che il Builder viene interrogato per informazioni
	 * relative agli oggetti coinvolti nel processo di creazione o modifica.
	 * Questo metodo genera un'eccezione nello stato READY, mentre è permesso normalmente in CREATING e EDITING.
	 */
	@Override
	public void onQuerying(AbstractBuilder b) {
		// Al momento qui non è presente nulla.
		// Tuttavia, in futuro potrebbe servire attivare un'azione solo quando viene effettuata un'interrogazione al Builder.
	}
	
}
