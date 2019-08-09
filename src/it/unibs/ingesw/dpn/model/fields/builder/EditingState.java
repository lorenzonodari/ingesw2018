package it.unibs.ingesw.dpn.model.fields.builder;

import it.unibs.ingesw.dpn.model.fields.Field;

public class EditingState implements BuilderState {

	@Override
	public String getStateName() {
		return BuilderState.EDITING;
	}

	/**
	 * Metodo per l'acquisizione di un valore di un campo.
	 * Non provoca il cambio di stato, ma può modificare il comportamento 
	 * a seconda dello stato in cui ci si trova.
	 * 
	 * Inoltre, a differenza dello stato "Creating", verifica anche che il campo 
	 * di cui acquisire il valore sia un campo "Editabile". In caso contrario
	 * genera un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 * @param field Il field di cui acquisire il valore
	 */
	@Override
	public void onFieldValueAcquisition(AbstractBuilder b, Field field) {
		if (!field.isEditable()) {
			throw new IllegalArgumentException(String.format(
					"Impossibile cambiare il valore del campo \"%s\" poiché non modificabile",
					field.getName()));
		}
	}

	/**
	 * Metodo per la transizione di stato da "Editing" a "Ready" nel caso in cui
	 * venga finalizzata la modifica.
	 * 
	 * Nota: a differenza dello stato "Creating", qui non devo verificare che tutti i campi obbligatori
	 * siano inizializzati, poiché è già stato verificato alla creazione e non c'è modo di assegnare
	 * un valore nullo ad un campo già inizializzato.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	@Override
	public void onFinalisation(AbstractBuilder b) {
		b.setState(new ReadyState());
	}

	/**
	 * Metodo per la transizione di stato da "Editing" a "Ready" nel caso in cui
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
