package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.LocalDateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;;

public enum UserField implements Field {
	
	NICKNAME (
			"Nickname",
			"Il nickname univoco da associare ad un utente",
			true,
			false,
			StringFieldValue.class
			),
	
	DATA_DI_NASCITA (
			"Data di nascita",
			"La data di nascita (giorno, mese e anno) dell'utente",
			false,
			true,
			LocalDateFieldValue.class
			),
	
	CATEGORIE_DI_INTERESSE (
			"Categorie di interesse",
			"Le categorie di eventi a cui l'utente è interessato e di cui vuole ricevere gli aggiornamenti",
			false,
			true,
			CategoryListFieldValue.class
			)
	
	;

	private static final String TO_STRING =
			  "Nome:           %s\n"
			+ "Descrizione:    %s\n"
			+ "%s\n"
			+ "%s\n";	
	private static final String MANDATORY_TAG = "Obbligatorio";
	private static final String OPTIONAL_TAG = "Facoltativo";
	private static final String EDITABLE_TAG = "Modificabile";
	private static final String IMMUTABLE_TAG = "Non modificabile";

	private final String name;
	private final String description;
	private final boolean mandatory;
	private final boolean editable;
	private Class<? extends FieldValue> type;
	
	private UserField(String name, String description, boolean mandatory, boolean editable, Class<? extends FieldValue> type) {
		if (name == null || description == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.editable = editable;
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
	 * Restituisce la modificabilità del campo, ossia se un campo è modificabile
	 * o meno anche dopo la creazione dell'oggetto a cui tale campo è associato.
	 * 
	 * @return true se la modifica a posteriori del campo è permessa, false altrimenti
	 */
	@Override
	public boolean isEditable() {
		return this.editable;
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
						OPTIONAL_TAG,
				this.editable ?
						EDITABLE_TAG :
						IMMUTABLE_TAG
				);
		return str;
	}
	
	

}
