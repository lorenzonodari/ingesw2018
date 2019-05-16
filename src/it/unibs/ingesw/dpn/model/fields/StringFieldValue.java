package it.unibs.ingesw.dpn.model.fields;

public class StringFieldValue implements FieldValue {

	private String value; 
	
	public StringFieldValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}

}
