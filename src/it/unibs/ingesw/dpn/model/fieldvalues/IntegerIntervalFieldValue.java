package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;
/**
 * Classe che rappresenta un intervallo di valori compreso fra due interi.
 * Entrambi gli estremi sono inclusi nell'intervallo.
 * Implementa l'interfaccia {@link FieldValue}.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class IntegerIntervalFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9171350901257423215L;
	
	private static final String INCLUDE_MIN_STRING = "[";
	private static final String INCLUDE_MAX_STRING = "]";
	
	private int min, max;
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}
	
	/**
	 * Restituisce il numero di valori compresi nell'intervallo
	 * 
	 * @return il numero di valori interi compresi nell'intervallo
	 */
	public int getRange() {
		int range = this.max - this.min + 1;
		return range;
	}
	
	@Override
	public String toString() {
		return String.format("%s%d; %d%s", 
				INCLUDE_MIN_STRING,
				this.min,
				this.max,
				INCLUDE_MAX_STRING);
	}

	@Override
	public void initializeValue(UIRenderer renderer, InputGetter input) {
		
		boolean check = false;
		int tmpMin, tmpMax;
		
		do {
			renderer.renderText("Inserisci il valore minimo");
			tmpMin = input.getInteger();
			renderer.renderText("Inserisci il valore massimo");
			tmpMax = input.getInteger();
			
			if (min > max) {
				renderer.renderError("Inserire un valore minimo inferiore al valore massimo");
			} else {
				check = true;
			}
			
		} while (!check);

		this.min = tmpMin;
		this.max = tmpMax;
		
	}
	

}
