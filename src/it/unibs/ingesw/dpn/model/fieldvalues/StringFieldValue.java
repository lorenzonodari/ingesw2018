package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public class StringFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5576058610748280117L;
	
	private String value;
	
	/**
	 * Costruttore che crea una istanza "vuota". Tale istanza dovra' quindi, prima di poter essere utilizzata, inizializzata
	 * mediante la chiamata al metodo initializeValue().
	 * 
	 */
	public StringFieldValue() {
		this.value = null;
	}
	
	public StringFieldValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	public void initializeValue(UIRenderer renderer, InputGetter getter) {
		this.value = getter.getString();
	}

}
