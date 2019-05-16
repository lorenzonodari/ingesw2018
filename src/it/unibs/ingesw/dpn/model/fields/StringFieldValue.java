package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class StringFieldValue implements FieldValue {

	private String value; 
	
	public StringFieldValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	public static StringFieldValue acquireValue(UIRenderer renderer, InputGetter getter) {
		renderer.renderText("Inserisci il valore testuale del campo");
		return new StringFieldValue(getter.getString());
	}

}
