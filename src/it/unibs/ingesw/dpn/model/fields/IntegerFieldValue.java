package it.unibs.ingesw.dpn.model.fields;

public class IntegerFieldValue implements FieldValue {

	private int value;
	
	public IntegerFieldValue(int value) {
		this.value = value;
	}
	
	public String toString() {
		return String.format("%d", this.value);
	}

}
