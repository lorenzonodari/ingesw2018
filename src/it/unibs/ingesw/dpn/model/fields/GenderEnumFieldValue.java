package it.unibs.ingesw.dpn.model.fields;

/**
 * Classe che rappresenta un valore di un campo contenente il genere.
 * Implementa l'interfaccia {@link FieldValue}.
 * 
 * @author Michele Dusi
 *
 */
public enum GenderEnumFieldValue implements FieldValue {
	
	MALE ("Uomo"),
	FEMALE ("Donna"),
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
