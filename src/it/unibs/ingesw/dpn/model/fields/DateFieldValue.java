package it.unibs.ingesw.dpn.model.fields;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFieldValue extends Date implements FieldValue {

	private static final long serialVersionUID = 123L;
	private static final String DATE_FORMAT_STRING = "dd/MM/yyyy - HH:mm";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

	/**
	 * Genera un oggetto con la data attuale.
	 */
	public DateFieldValue() {
		super();
	}

	public DateFieldValue(long dateLongValue) {
		super(dateLongValue);
	}
	
	@Override
	public String toString() {
		return DATE_FORMAT.format(this);
	}

}
