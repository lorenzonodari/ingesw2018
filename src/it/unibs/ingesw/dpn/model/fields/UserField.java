package it.unibs.ingesw.dpn.model.fields;

import java.time.LocalDate;

import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.LocalDateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;

public enum UserField implements Field {
	
	NICKNAME (
			"Nickname",
			"Il nickname univoco da associare ad un utente",
			true,
			false,
			StringFieldValue.class
			)
	{
		@Override
		public FieldValue createBlankFieldValue() {
			return new StringFieldValue();
		}
	},
	
	DATA_DI_NASCITA (
			"Data di nascita",
			"La data di nascita (giorno, mese e anno) dell'utente",
			false,
			true,
			LocalDateFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			LocalDateFieldValue localDateValue = (LocalDateFieldValue) value;
			
			if (localDateValue.getLocalDate().isAfter(LocalDate.now())) {
				throw new FieldCompatibilityException("Impossibile accettare una data futura");
				
			} else if (localDateValue.getLocalDate().isAfter(LocalDate.now().minusYears(AGE_LIMIT))) {
				throw new FieldCompatibilityException(String.format(
						"Per utilizzare questo programma devi avere almeno %d anni.\nInserisci la tua vera data di nascita o termina l'esecuzione del programma.",
						AGE_LIMIT));
			}
		}
		
		@Override
		public FieldValue createBlankFieldValue() {
			return new LocalDateFieldValue();
		}
	},
	
	CATEGORIE_DI_INTERESSE (
			"Categorie di interesse",
			"Le categorie di eventi a cui l'utente è interessato e di cui vuole ricevere gli aggiornamenti",
			false,
			true,
			CategoryListFieldValue.class
			)
	{
		@Override
		public FieldValue createBlankFieldValue() {
			return new CategoryListFieldValue();
		}
	},
	
	;
	
	/** Parametri */
	private static final int AGE_LIMIT = 12;

	/** Attributi d'istanza */
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
	

}
