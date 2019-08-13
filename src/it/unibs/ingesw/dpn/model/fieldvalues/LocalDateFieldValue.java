package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import it.unibs.ingesw.dpn.ui.UserInterface;

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
	 * @param userInterface L'interfaccia utente
	 * @return Il valore acquisito
	 */
	public void initializeValue(UserInterface userInterface) {
		userInterface.renderer().renderText("Inserisci la data in formato (GG/MM/AAAA)");
		String data = userInterface.getter().getMatchingString(
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
		
		this.date = LocalDate.of(year, month, day);
	}

}
