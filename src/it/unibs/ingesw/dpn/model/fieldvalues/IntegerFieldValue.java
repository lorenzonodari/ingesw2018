package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class IntegerFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3185507168202478696L;
	
	private int value;
	
	/**
	 * Costruttore che crea una istanza "vuota". Tale istanza dovra' quindi, prima di poter essere utilizzata, inizializzata
	 * mediante la chiamata al metodo initializeValue().
	 * 
	 */
	public IntegerFieldValue() {
		
	}
	
	public IntegerFieldValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String toString() {
		return String.format("%d", this.value);
	}

	@Override
	public void initializeValue(UIRenderer renderer, InputGetter input) {
		
		this.value = input.getInteger();
		
	}
}
