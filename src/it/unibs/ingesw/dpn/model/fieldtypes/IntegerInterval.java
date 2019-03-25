package it.unibs.ingesw.dpn.model.fieldtypes;

/**
 * Classe che rappresenta un intervallo di valori compreso fra due interi, estremi inclusi.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class IntegerInterval {

	private static final String TO_STRING = "[%d; %d]";
	
	private int min, max;
	
	public IntegerInterval(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("Impossibile creare un intervallo con estremo inferiore maggiore dell'estremo superiore.");
		}
		this.min = min;
		this.max = max;
	}
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}
	
	public int getRange() {
		return this.max - this.min;
	}
	
	public String toString() {
		return String.format(TO_STRING, this.min, this.max);
	}
}
