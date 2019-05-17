package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class DateFieldValue extends Date implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 715073002238005354L;
	
	private static final String DATE_FORMAT_STRING = "dd/MM/yyyy - HH:mm";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

	/**
	 * Genera un oggetto con la data attuale.
	 */
	public DateFieldValue() {
		super();
	}

	public DateFieldValue(long dateLongValue) {
		super(dateLongValue);
	}
	
	@Override
	public String toString() {
		return DATE_FORMAT.format(this);
	}
	
	public static DateFieldValue acquireValue(UIRenderer renderer, InputGetter getter) {
		// Anno, mese, giorno
		renderer.renderText("Inserisci il giorno in formato (GG/MM/AAAA)");
		String data = getter.getMatchingString(
				  "(0([1-9])|[1-2][0-9]|3([0-1]))" // Giorno
				+ "(/|-)" // Divisore
				+ "(0([1-9])|1([0-2]))" // Mese
				+ "(/|-)" // Divisore
				+ "(19|20|21)([0-9][0-9])"); // Anno
		int giorno = Integer.parseInt(data.substring(0, 2));
		int mese = Integer.parseInt(data.substring(3, 5)) - 1;
		int anno = Integer.parseInt(data.substring(6, 10));							
				
		// Ora e minuti
		renderer.renderText("Inserisci l'orario in formato (HH:MM)");
		String ora = getter.getMatchingString("([0-1][0-9]|2[0-3])(:|\\.)([0-5][0-9])");
		int ore = Integer.parseInt(ora.substring(0, 2));
		int minuti = Integer.parseInt(ora.substring(3, 5));
		
		// Creo la data
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(anno, mese, giorno, ore, minuti, 0);
		DateFieldValue date = new DateFieldValue(cal.getTimeInMillis());
		return date;
	}

}
