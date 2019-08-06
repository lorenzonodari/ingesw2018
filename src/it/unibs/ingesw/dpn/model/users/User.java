package it.unibs.ingesw.dpn.model.users;

import java.util.List;

import it.unibs.ingesw.dpn.model.fields.AbstractFieldable;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.UserField;

/**
 * Classe utilizzata per contenere i dati relativi ad un singolo utente
 */
public class User extends AbstractFieldable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1333193895476185438L;

	/** La casella di posta a cui recapitare i messaggi dell'utente */
	private Mailbox mailbox;
	
	/**
	 * Crea un nuovo utente con il nome dato. La relativa mailbox e' automaticamente creata, vuota.
	 * 
	 * Precondizione: la lista di coppie (campo, valore) devono essere istanziate correttamente.
	 * Questo significa che tutti i campi obbligatori devono già essere stati inizializzati. 
	 * L'unica classe abilitata a fare ciò è la classe {@link UserBuilder}.
	 * 
	 * @param fieldValues La lista di campi previsti per un oggetto User
	 */
	public User(List<Field> fieldsList) {
		super(fieldsList);
		
		// Inizializzo una nuova mailbox
		this.mailbox = new Mailbox();
	}

	/**
	 * Imposta il valore id default di alcuni campi.
	 * Questo metodo viene chiamato dal costruttore e racchiude tutte le procedure che impostano
	 * i valori dei campi facoltativi utilizzati nel programma.
	 */
	@Override
	public void setDefaultFieldValues() {
		// Al momento non esistono campi da impostare in maniera automatica con valori di default
	}
	
	/**
	 * Restituisce la mailbox dell'utente
	 * 
	 * @return La mailbox dell'utente
	 */
	public Mailbox getMailbox() {
		return this.mailbox;
	}
	
	/**
	 * Restituisce una stringa contenente la descrizione completa ma compatta delle caratteristiche
	 * dell'utente.
	 *  
	 * @return Una descrizione testuale dell'utente
	 */
	public String toString() {
		StringBuffer description = new StringBuffer();
		// Valori dei campi
		description.append("Descrizione dell'utente\n");
		for (Field f : UserField.values()) {
			if(!(this.getFieldValue(f) == null)) {
			description.append(String.format(" | %-50s : %s\n",
					f.getName(),
					this.getFieldValue(f).toString()));
			}
		}
		return description.toString();
	}

}
