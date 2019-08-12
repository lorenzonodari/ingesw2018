package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

/**
 * Classe che rappresenta un valore di un campo di tipo "Data e Ora".
 * In pratica, fissa un istante temporale definendo anno, mese, giorno, ore e minuti.
 * La precisione massima consentita che si è deciso di utilizzare sono i minuti, poiché non si 
 * ritiene necessaria una precisione ulteriore per la gestione di eventi.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class DateFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 715073002238005354L;
	
	private static final String DATE_FORMAT_STRING = "dd/MM/yyyy - HH:mm";
	private static final String DATE_DELIMITER = "(/|-|,| )";
	private static final String HOURS_DELIMITER = "(:|\\.| )";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	
	private Date date;
	
	/**
	 * Costruttore che crea una istanza "vuota". Tale istanza dovra' quindi, prima di poter essere utilizzata, inizializzata
	 * mediante la chiamata al metodo initializeValue().
	 * 
	 */
	public DateFieldValue() {
		this.date = null;
	}
	
	/**
	 * Genera un oggetto, dato il numero di millisecondi.
	 * 
	 * @param dateLongValue Il numero di millisecondi trascorsi dal 1 gennaio 1970.
	 */
	public DateFieldValue(long dateLongValue) {
		this.date = new Date(dateLongValue);
	}
	
	/**
	 * Restituisce una descrizione testuale del valore temporale.
	 * 
	 * @return una descrizione testuale
	 */
	@Override
	public String toString() {
		return DATE_FORMAT.format(this.date);
	}
	
	public Date getValue() {
		return this.date;
	}
	
	/**
	 * Acquisisce un valore, utilizzando i metodi di I/O delle interfacce UIRenderer e InputGetter
	 * ed effettuando le opportune convalide.
	 * 
	 * @param renderer Il renderer
	 * @param getter Il getter
	 * @return Il valore acquisito
	 */
	public void initializeValue(UIRenderer renderer, InputGetter getter) {
		// Anno, mese, giorno
		renderer.renderText("Inserisci il giorno in formato (GG/MM/AAAA)");
		String data = getter.getMatchingString(
				"(0?([1-9])|[1-2][0-9]|3([0-1]))" // Giorno
				+ DATE_DELIMITER // Divisore
				+ "(0?([1-9])|1([0-2]))" // Mese
				+ DATE_DELIMITER // Divisore
				+ "(19|20|21)([0-9][0-9])"); // Anno
		// Estraggo i dati
		Scanner scanDate = new Scanner(data);
		scanDate.useDelimiter(DATE_DELIMITER);		
		int giorno = scanDate.nextInt();
		int mese = scanDate.nextInt() - 1;
		int anno = scanDate.nextInt();
		scanDate.close();
				
		// Ora e minuti
		renderer.renderText("Inserisci l'orario in formato (HH:MM)");
		String ora = getter.getMatchingString(
				"([0-1]?[0-9]|2[0-3])" // Ore
				+ HOURS_DELIMITER // Divisore
				+ "([0-5][0-9])"); // Minuti
		// Estraggo i dati
		Scanner scanHours = new Scanner(ora);
		scanHours.useDelimiter(HOURS_DELIMITER);	
		int ore = scanHours.nextInt();
		int minuti = scanHours.nextInt();
		scanHours.close();
		
		// Creo la data
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(anno, mese, giorno, ore, minuti, 0);
		this.date = new Date(cal.getTimeInMillis());
	}

}
