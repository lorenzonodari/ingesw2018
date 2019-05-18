package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;

public class MoneyAmountFieldValue implements FieldValue, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5887722467075194461L;
	
	private float value;

	public MoneyAmountFieldValue(float value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("%.2f €", this.value);
	}

}
