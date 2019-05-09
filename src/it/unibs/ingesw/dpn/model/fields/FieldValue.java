package it.unibs.ingesw.dpn.model.fields;

/**
 * Interfaccia che unforma il comportamento di un oggetto rappresentante il valore di un campo.
 * 
 * @author Michele Dusi
 *
 */
public interface FieldValue {
	
	/**
	 * Restituisce il valore del campo come stringa, nel formato prestabilito.
	 * 
	 * @return Il valore del campo come stringa
	 */
	public String getStringValue();
	
	/**
	 * Imposta il valore del campo da una stringa
	 * 
	 * @param value Il valore del campo
	 */
	void setStringValue(String value);
	
	/**
	 * Restituisce una descrizione testuale del valore del campo.
	 * 
	 * @return La descrizione testuale del valore
	 */
	@Override
	public String toString();

}
