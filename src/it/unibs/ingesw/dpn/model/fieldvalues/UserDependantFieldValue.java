package it.unibs.ingesw.dpn.model.fieldvalues;

import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Interfaccia che astrae il comportamento legato al valore di un campo di un evento per il quale 
 * e' possibile, per ogni utente, esprimere un proprio valore desiderato
 */
public interface UserDependantFieldValue extends FieldValue {
	
	/**
	 * Inizializza il valore dipendente dall'utente per l'utente specificato
	 * 
	 * @param user L'utente per il quale si desidera definire la personalizzazione
	 * @param ui L'interfaccia utente da utilizzare per la personalizzazione
	 */
	public void userCustomization(User user, UserInterface ui);
	
	/**
	 * Rimuove le personalizzazioni dell'utente dato dal FieldValue
	 * 
	 * @param user L'utente per il quale vanno cancellate le personalizzazioni
	 */
	public void forgetUserCustomization(User user);

}
