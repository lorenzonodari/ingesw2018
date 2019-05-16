package it.unibs.ingesw.dpn.model.fields;

public class MoneyAmountFieldValue implements FieldValue {
	
	private float value;

	public MoneyAmountFieldValue(float value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("%.2f €", this.value);
	}

}
