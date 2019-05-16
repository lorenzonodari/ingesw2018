package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

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
	
	public static StringFieldValue acquireValue(UIRenderer renderer, InputGetter getter) {
		renderer.renderText("Inserisci il valore testuale del campo");
		return new StringFieldValue(getter.getString());
	}

}
