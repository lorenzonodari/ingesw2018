package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

/**
 * Classe che rappresenta un valore di un campo di tipo "data". Utilizza la classe {@link LocalDate} 
 * della libreria "time" di Java, che rappresenta una data senza un riferimento geografico e senza 
 * un indicazione oraria.
 * 
 * @author Michele Dusi
 *
 */
public class LocalDateFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 715073002238005354L;

	private static final String DATE_FORMAT_STRING = "dd/MM/yyyy";
	private static final String DATE_DELIMITER = "(/|-|,| )";
	
	private LocalDate date = null;
	
	/**
	 * Costruttore di un oggetto {@link LocalDateFieldValue} tramite i numeri del giorno, del mese
	 * e dell'anno della data.
	 * 
	 * Precondizione: i numeri devono essere validi come data.
	 * 
	 * @param day Il giorno del mese
	 * @param month Il mese dell'anno
	 * @param year Il numero dell'anno
	 */
	public LocalDateFieldValue(int day, int month, int year) {
		if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2200) {
			throw new IllegalArgumentException(String.format(
					"Impossibile creare un oggetto LocalDateFieldValue con valori [giorno = %d, mese = %d, anno = %d]",
					day,
					month,
					year));
		}
		// Creo l'oggetto LocalDate
		this.date = LocalDate.of(year, month, day);
	}
	
	/**
	 * Restituisce la data contenuta come valore del campo all'interno di questo oggetto.
	 * 
	 * @return La data come oggetto {@link LocalDate}
	 */
	public LocalDate getLocalDate() {
		return this.date;
	}

	/**
	 * Restituisce una descrizione testuale del valore temporale.
	 * 
	 * @return una descrizione testuale
	 */
	@Override
	public String toString() {
		return this.date.format(DateTimeFormatter.ofPattern(DATE_FORMAT_STRING));
	}

	/**
	 * Acquisisce un valore, utilizzando i metodi di I/O delle interfacce UIRenderer e InputGetter
	 * ed effettuando le opportune convalide.
	 * L'acquisizione richiede l'inserimento di un giorno, un mese e un anno in un formato preciso.
	 * 
	 * @param renderer Il renderer
	 * @param getter Il getter
	 * @return Il valore acquisito
	 */
	public static LocalDateFieldValue acquireValue(UIRenderer renderer, InputGetter getter) {
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
		int day = scanDate.nextInt();
		int month = scanDate.nextInt();
		int year = scanDate.nextInt();
		scanDate.close();
		
		return new LocalDateFieldValue(day, month, year);
	}

}
