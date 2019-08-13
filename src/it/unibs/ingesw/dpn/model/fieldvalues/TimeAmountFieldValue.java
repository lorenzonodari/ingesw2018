package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Classe che rappresenta il valore di un campo di tipo "quantità di tempo".
 * Ad esempio, il campo "Durata" di {@link CommonField} richiede questo tipo di dato.
 * 
 * Esistono due modi per costruire un oggetto {@link TimeAmountFieldValue}:
 * - come differenza di due istanti temporali precisi.
 * - come multiplo di un'unità temporale base.
 * 
 * Nota: la precisione minima consentita da questa classe è di 1 secondo. Per la gestione di eventi, infatti,
 * non si è ritenuto necessario implementare una precisione ulteriore.
 * 
 * @author Michele Dusi
 *
 */
public class TimeAmountFieldValue implements FieldValue, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3259859482858796325L;

	/**
	 * Classe enum interna che aggrega solamente le unità temporali utilizzate nel software,
	 * mappando un sottoinsieme dell'enum ChronoUnit.
	 * 
	 * @author MicheleDusi
	 *
	 */
	public static enum TimeUnit {
		SETTIMANE(ChronoUnit.WEEKS, "Settimane", 1_000),
		GIORNI(ChronoUnit.DAYS, "Giorni", 7_000),
		ORE(ChronoUnit.HOURS, "Ore", 200_000),
		MINUTI(ChronoUnit.MINUTES, "Minuti", 12_000_000);
		
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
	
	private long seconds;
	
	/**
	 * Costruttore che crea una istanza "vuota". Tale istanza dovra' quindi, prima di poter essere utilizzata, inizializzata
	 * mediante la chiamata al metodo initializeValue().
	 * 
	 */
	public TimeAmountFieldValue() {
		
	}
	
	/**
	 * Costruttore che costruisce un periodo come intervallo temporale tra due date.
	 * 
	 * @param start L'istante di partenza, come oggetto {@link Date}
	 * @param end L'istante di fine, come oggetto {@link Date}
	 */
	public TimeAmountFieldValue(Date start, Date end) { 
		if (start == null || end == null) {
			throw new IllegalArgumentException("Parametri nulli: impossibile creare un oggetto PeriodFieldValue");
		}
		this.seconds = (end.getTime() - start.getTime()) / 1000;
	}
	
	/**
	 * Costruttore che costruisce un oggetto {@link TimeAmountFieldValue} come multiplo di una certa unità temporale.
	 * 
	 * @param times Il numero di ripetizioni dell'unità
	 * @param unit L'unità temporale
	 */
	public TimeAmountFieldValue(long times, TimeUnit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("Parametri nulli: impossibile creare un oggetto PeriodFieldValue");
		} else if (times < 0) {
			throw new IllegalArgumentException("Parametri invalidi: impossibile creare un oggetto PeriodFieldValue con tempi negativi");
		}
		this.seconds = times * unit.getUnit().getDuration().getSeconds();
	}
	
	/**
	 * Restituisce il numero di secondi totali della durata.
	 * 
	 * @return La durata rappresentata dall'oggetto, espressa in secondi
	 */
	public long getSeconds() {
		return this.seconds;
	}

	/**
	 * Restituisce una descrizione testuale del valore del campo.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		// Clono il valore, perché devo modificarlo
		long amount = this.seconds;
		
		for (TimeUnit unit : TimeUnit.values()) {
			
			long unitSecs = unit.getUnit().getDuration().getSeconds();
			long result = amount / unitSecs;
			amount %= unitSecs;
			
			if (result > 0) {
				s.append(String.format("%d %s, ", result, unit.getName().toLowerCase()));
			}
		}
		
		if (s.length() != 0) {
			return s.toString().substring(0, s.length() - 2);
		} else {
			return "Evento istantaneo";
		}
	}
	
	/**
	 * Acquisisce un valore di tipo "TimeAmountFieldValue".
	 * Per fare ciò, viene chiesto di selezionare l'unità di tempo che più si adatta a rappresentare il periodo
	 * di tempo, e dopo viene chiesto di selezionare il multiplo di tale unità.
	 * 
	 * @param userInterface L'interfaccia utente da usare
	 * @return Un valore di tipo "TimeAmountFieldValue"
	 */
	public void initializeValue(UserInterface userInterface) {
		// Seleziono unità di misura
		userInterface.renderer().renderText("Seleziona l'unità di misura temporale:");
		userInterface.renderer().renderLineSpace();
		int counter = 1;
		for (TimeUnit unit : TimeUnit.values()) {
			userInterface.renderer().renderText(String.format("%3d) %s", counter++, unit.getName()));
		}
		userInterface.renderer().renderLineSpace();
		int unitIndex = userInterface.getter().getInteger(1, TimeUnit.values().length) - 1;
		TimeUnit chosenUnit = TimeUnit.values()[unitIndex];
		
		// Acquisisco la quantità
		userInterface.renderer().renderText(String.format(
				"Inserisci il numero di %s:",
				chosenUnit.getName().toLowerCase()));
		long unitAmount = userInterface.getter().getInteger(0, chosenUnit.getMaxUnitAmount());
		
		this.seconds = unitAmount * chosenUnit.getUnit().getDuration().getSeconds();
		
	}
	
}
