package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.GenderEnumFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;

public enum SoccerMatchField implements Field {
	
	GENERE (
			"Genere",
			"Il genere dei giocatori che partecipano alla partita",
			true,
			GenderEnumFieldValue.class
			),
	
	FASCIA_DI_ETA (
			"Fascia di età",
			"L'intervallo in cui sono comprese le età accettate dei giocatori",
			true,
			IntegerFieldValue.class
			)
	
	;

	private final String name;
	private final String description;
	private final boolean mandatory;
	private Class<? extends FieldValue> type;
	
	private SoccerMatchField(String name, String description, boolean mandatory, Class<? extends FieldValue> type) {
		if (name == null || description == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.type = type;
	}

	/**
	 * Restituisce il nome dell'oggetto Field.
	 * 
	 * @return il nome dell'oggetto Field.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Restituisce la descrizione del campo.
	 * 
	 * @return la descrizione del campo, come oggetto {@link String}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * Restituisce l'obbligatorietà del campo.
	 * 
	 * @return true se la compilazione del campo è obbligatoria, false altrimenti
	 */
	@Override
	public boolean isMandatory() {
		return this.mandatory;
	}

	/**
	 * Metodo che restituisce automaticamente "false".
	 * Per default, tutti i campi comuni alle categoria NON sono modificabili.
	 */
	@Override
	public boolean isEditable() {
		return false;
	}

	/**
	 * Restituisce la classe le cui istanze sono i possibili valori di questo Field.
	 * 
	 * @return il "tipo" del campo
	 */
	@Override
	public Class<? extends FieldValue> getType() {
		return this.type;
	}
	
};
