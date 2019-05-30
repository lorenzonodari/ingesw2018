package it.unibs.ingesw.dpn.model.fieldvalues;

/**
 * Classe che rappresenta un valore di un campo contenente il genere.
 * Questa classe è utilizzata, più nello specifico, nella categoria "Partita di Calcio".
 * Per questo motivo le opzioni previste sono:
 * - Uomo
 * - Donna
 * - Misto (se le squadre sono indipendentemente formate da uomini e donne)
 * - Altro
 * Implementa l'interfaccia {@link FieldValue}.
 * 
 * @author Michele Dusi
 *
 */
public enum GenderEnumFieldValue implements FieldValue {
	
	MALE ("Uomini"),
	FEMALE ("Donne"),
	MIXED ("Misti"),
	OTHER ("Altro");
	
	private final String properName;
	
	private GenderEnumFieldValue(String properName) {
		this.properName = properName;
	}
	
	@Override
	public String toString() {
		return this.properName;
	}

}
