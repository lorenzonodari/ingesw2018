package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;

public class IntegerFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3185507168202478696L;
	
	private int value;
	
	public IntegerFieldValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String toString() {
		return String.format("%d", this.value);
	}
}
