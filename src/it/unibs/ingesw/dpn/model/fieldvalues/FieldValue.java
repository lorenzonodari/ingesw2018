package it.unibs.ingesw.dpn.model.fieldvalues;

import it.unibs.ingesw.dpn.ui.UserInterface;

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
	
	/**
	 * Inizializza il valore incapsulato dal FieldValue.
	 * 
	 * @param userInterface L'interfaccia utente a cui fare affidamento
	 */
	public void initializeValue(UserInterface userInterface);

}
