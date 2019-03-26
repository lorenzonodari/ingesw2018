package it.unibs.ingesw.dpn.model.fieldtypes;

/**
 * Classe che rappresenta un intervallo di valori compreso fra due interi.
 * I due estremi possono essere inclusi o esclusi a piacere.
 * Di default, l'estremo iniziale è incluso e quello finale è escluso.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class IntegerInterval {

	private static final String INCLUDE_MIN_STRING = "[";
	private static final String EXCLUDE_MIN_STRING = "]";
	private static final String INCLUDE_MAX_STRING = "]";
	private static final String EXCLUDE_MAX_STRING = "[";

	private static final boolean DEFAULT_MIN_INCLUSION = true;
	private static final boolean DEFAULT_MAX_INCLUSION = false;
	
	private int min, max;
	private boolean includeMin, includeMax;

	public IntegerInterval(int min, int max) {
		this(min, DEFAULT_MIN_INCLUSION, max, DEFAULT_MAX_INCLUSION);
	}
	
	public IntegerInterval(int min, boolean includeMin, int max, boolean includeMax) {
		if (min > max) {
			throw new IllegalArgumentException("Impossibile creare un intervallo con estremo inferiore maggiore dell'estremo superiore.");
		}
		this.min = min;
		this.max = max;
		this.includeMin = includeMin;
		this.includeMax = includeMax;
	}
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}

	public boolean isMinIncluded() {
		return this.includeMin;
	}
	
	public boolean isMaxIncluded() {
		return this.includeMax;
	}
	
	/**
	 * Restituisce la differenza fra il massimo valore e il minimo valore dell'intervallo,
	 * prestando attenzione alla presenza o meno degli estremi.
	 * Se sono inclusi, verranno conteggiati come valori possibili; altrimenti il loro valore non 
	 * verrà conteggiato nelle possibilità.
	 * Il risultato restituito corrisponde al numero di valori interi possibili dell'intervallo.
	 * 
	 * @return il numero di valori interi compresi nell'intervallo
	 */
	public int getRange() {
		int range = this.max - this.min;
		range += (this.includeMin) ?  0 : -1;
		range += (this.includeMax) ? +1 :  0;
		return range;
	}
	
	public String toString() {
		return String.format("%s %%s; %%s %s", 
				this.includeMin ?
						INCLUDE_MIN_STRING :
						EXCLUDE_MIN_STRING,
				this.min,
				this.max,
				this.includeMax ?
						INCLUDE_MAX_STRING :
						EXCLUDE_MAX_STRING);
	}
}
