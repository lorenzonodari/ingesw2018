package it.unibs.ingesw.dpn.model.fieldvalues;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

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
public class GenderFieldValue implements FieldValue {
	
	 private enum Gender {
		
		MALE("Uomo"),
		FEMALE("Donna"),
		MIXED("Misto"),
		OTHER("Altro");
		
		private final String properName;
		
		Gender(String properName) {
			this.properName = properName;
		}
		
		@Override
		public String toString() {
			return this.properName;
		}
		
	}
	
	private Gender gender;
	
	@Override
	public String toString() {
		return this.gender.toString();
	}

	@Override
	public void initializeValue(UIRenderer renderer, InputGetter getter) {
		
		Gender [] values = Gender.values();
		int i = 1;
		for (Gender gender : values) {
			renderer.renderText(String.format("%3d)\t%s", 
					i++, gender.toString()));
		}
		int input = getter.getInteger(1, values.length);
		this.gender = values[input - 1];
		
	}

}
