package it.unibs.ingesw.dpn.model.fieldvalues;

/**
 * Interfaccia che unforma il comportamento di un oggetto rappresentante il valore di un campo.
 * 
 * @author Michele Dusi
 *
 */
public interface FieldValue {

	/**
	 * Restituisce una descrizione testuale completa del valore del campo.
	 * 
	 * @return La descrizione testuale completa del valore
	 */
	@Override
	public String toString();

}
