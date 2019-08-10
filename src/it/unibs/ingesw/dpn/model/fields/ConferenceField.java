package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;

/**
 * Classe che presenta i dati per tutti i campi esclusivi previsti dalla tipologia di eventi "Conferenza".
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public enum ConferenceField implements Field {
	
	RELATORI (
			"Relatori",
			"Nomi delle persone che parleranno alla conferenza in qualità di relatori",
			true,
			true,
			false,
			StringFieldValue.class
			),
	
	ARGOMENTO (
			"Argomento",
			"Breve descrizione delle tematiche che saranno trattate nella conferenza",
			true,
			false,
			false,
			StringFieldValue.class			
			),
	SPESE_OPZIONALI (
			"Spese opzionali",
			"Spese aggiuntive a scelta del partecipante",
			false,
			false,
			true,
			OptionalCostsFieldValue.class
			)
	
	;

	private final String name;
	private final String description;
	private final boolean mandatory;
	private final boolean editable;
	private final boolean userDependant;
	private Class<? extends FieldValue> type;
	
	private ConferenceField(String name, String description, boolean mandatory, boolean editable, boolean userDependant, Class<? extends FieldValue> type) {
		if (name == null || description == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.editable = editable;
		this.userDependant = userDependant;
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
	 * Restituisce la modificabilità del campo.
	 * 
	 * @return "true" se il campo può essere modificato dopo la creazione dell'oggetto a cui è associato, "false" altrimenti
	 */
	@Override
	public boolean isEditable() {
		return this.editable;
	}
	
	/**
	 * Restituisce true se il campo da la possibilita' all'utente di interagire con questo
	 * 
	 * @return true se il campo da la possibilita' all'utente di interagire con questo
	 */
	@Override
	public boolean isUserDependant() {
		return this.userDependant;
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

}
