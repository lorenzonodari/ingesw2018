package it.unibs.ingesw.dpn.model.fieldvalues;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class PeriodFieldValue implements FieldValue {
	
	/**
	 * Classe enum interna che aggrega solamente le unità temporali utilizzate nel software,
	 * mappando un sottoinsieme dell'enum ChronoUnit.
	 * 
	 * @author MicheleDusi
	 *
	 */
	public static enum TimeUnit {
		MESI(ChronoUnit.MONTHS, "Mesi", 3000),
		SETTIMANE(ChronoUnit.WEEKS, "Settimane", 15_000),
		GIORNI(ChronoUnit.DAYS, "Giorni", 75_000),
		ORE(ChronoUnit.HOURS, "Ore", 1_500_000),
		MINUTI(ChronoUnit.MINUTES, "Minuti", 50_000_000);
		
		private final ChronoUnit correspondingUnit;
		private final String name;
		private final int max;
		
		private TimeUnit(ChronoUnit unit, String name, int max) {
			this.correspondingUnit = unit;
			this.name = name;
			this.max = max;
		}
		
		public ChronoUnit getUnit() {
			return this.correspondingUnit;
		}
		
		public String getName() {
			return this.name;
		}

		public int getMaxUnitAmount() {
			return this.max;
		}
		
	}
	
	private TemporalAmount value;
	
	/**
	 * Costruttore che costruisce un periodo come intervallo temporale tra due date.
	 * 
	 * @param start L'istante di partenza, come oggetto {@link Date}
	 * @param end L'istante di fine, come oggetto {@link Date}
	 */
	public PeriodFieldValue(Date start, Date end) { 
		if (start == null || end == null) {
			throw new IllegalArgumentException("Parametri nulli: impossibile creare un oggetto PeriodFieldValue");
		}
		this.value = Duration.between(convertToLocalDate(start), convertToLocalDate(end));
	}
	
	/**
	 * Costruttore che costruisce un oggetto {@link PeriodFieldValue} come multiplo di una certa unità temporale.
	 * 
	 * @param times Il numero di ripetizioni dell'unità
	 * @param unit L'unità temporale
	 */
	public PeriodFieldValue(long times, TimeUnit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("Parametri nulli: impossibile creare un oggetto PeriodFieldValue");
		} else if (times < 0) {
			throw new IllegalArgumentException("Parametri invalidi: impossibile creare un oggetto PeriodFieldValue con tempi negativi");
		}
		this.value = Duration.of(times, unit.getUnit());
	}

	/**
	 * Restituisce una descrizione testuale del valore del campo.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		// Clono il valore, perché devo modificarlo
		Duration temporaryTemporalAmount = Duration.from(this.value);
		
		for (TimeUnit unit : TimeUnit.values()) {
			long unitAmount = temporaryTemporalAmount.get(unit.getUnit());
			temporaryTemporalAmount = temporaryTemporalAmount.minus(Duration.of(unitAmount, unit.getUnit()));
			if (unitAmount > 0) {
				s.append(String.format("%l %s, ", unitAmount, unit.getName().toLowerCase()));
			}
		}
		
		if (s.length() != 0) {
			return s.toString().substring(0, s.length() - 2);
		} else {
			return "Evento istantaneo";
		}
	}
	
	public static PeriodFieldValue acquireValue(UIRenderer renderer, InputGetter getter) {
		// Seleziono unità di misura
		renderer.renderText("Seleziona l'unità di misura temporale:");
		renderer.renderLineSpace();
		int counter = 1;
		for (TimeUnit unit : TimeUnit.values()) {
			renderer.renderText(String.format("%3d) %s", counter++, unit.getName()));
		}
		renderer.renderLineSpace();
		int unitIndex = getter.getInteger(1, TimeUnit.values().length) - 1;
		TimeUnit chosenUnit = TimeUnit.values()[unitIndex];
		
		// Acquisisco la quantità
		renderer.renderText(String.format(
				"Inserisci la durata dell'evento come numero di %s:", 
				chosenUnit.getName().toLowerCase()));
		long unitAmount = getter.getInteger(0, chosenUnit.getMaxUnitAmount());
		
		// Restituisco il nuovo PeriodFieldValue
		return new PeriodFieldValue(unitAmount, chosenUnit);
	}
	
	/**
	 * Converte da "Date" a "LocalDate".
	 * 
	 * @param dateToConvert la data da convertire, come oggetto {@link Date}
	 * @return Il corrispondente oggetto {@link LocalDate}
	 */
	private LocalDate convertToLocalDate(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	
}
