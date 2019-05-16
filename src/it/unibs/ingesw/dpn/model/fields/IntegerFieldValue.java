package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class IntegerFieldValue implements FieldValue {

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
