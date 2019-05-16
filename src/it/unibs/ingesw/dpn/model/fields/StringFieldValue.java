package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;

public class StringFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5576058610748280117L;
	
	private String value; 
	
	public StringFieldValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}

}
