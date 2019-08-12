package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class MoneyAmountFieldValue implements FieldValue, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5887722467075194461L;
	
	private float value;
	
	@Override
	public String toString() {
		return String.format("%.2f €", this.value);
	}
	
	public float getValue() {
		return this.value;
	}

	@Override
	public void initializeValue(UIRenderer renderer, InputGetter input) {
		
		this.value = input.getFloat();
		
	}

}
