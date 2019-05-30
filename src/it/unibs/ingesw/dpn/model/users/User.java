package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.util.Map;

import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

/**
 * Classe utilizzata per contenere i dati relativi ad un singolo utente
 */
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1333193895476185438L;
	
	/** Eccezioni */
	private static final String FIELD_NOT_PRESENT_EXCEPTION = "Il campo \"%s\" non appartiene alla categoria prevista dall'evento";

	/** La casella di posta a cui recapitare i messaggi dell'utente */
	private Mailbox mailbox;

	/** Mappa dei campi che definiscono e caratterizzano l'utente */
	private final Map<Field, FieldValue> valuesMap;
	
	/**
	 * Crea un nuovo utente con il nome dato. La relativa mailbox e' automaticamente creata, vuota.
	 * 
	 * Precondizione: la lista di coppie (campo, valore) devono essere istanziate correttamente e devono 
	 * rispettare i campi previsti dalla categoria. Questo significa anche che tutti i campi obbligatori
	 * devono già essere stati inizializzati. L'unica classe abilitata a fare ciò è la classe {@link UserFactory}.
	 * 
	 * @param fieldValues Le coppie (campo-valore) dell'utente
	 */
	public User(Map<Field, FieldValue> fieldValues) {
		// Verifico che i parametri non siano nulli
		if (fieldValues == null) {
			throw new IllegalArgumentException("Parametri nulli: impossibile creare un nuovo utente");
		}
		
		// Inizializzo gli attributi della classe
		this.valuesMap = fieldValues;
		
		// Imposto i valori di default per alcuni campi che non sono stati inizializzati
		this.setDefaultFieldValues();
		
		// Inizializzo una nuova mailbox
		this.mailbox = new Mailbox();
	}

	/**
	 * Imposta il valore id default di alcuni campi.
	 * Questo metodo viene chiamato dal costruttore e racchiude tutte le procedure che impostano
	 * i valori dei campi facoltativi utilizzati nel programma.
	 */
	private void setDefaultFieldValues() {
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
	 * Restituisce il valore caratterizzante l'utente del campo richiesto.
	 * 
	 * Precondizione: l'oggetto {@link Field} passato come parametro deve essere un campo previsto per un
	 * utente, ossia deve appartenere all'enumerazione {@link UserField}.
	 * 
	 * @param chosenField il campo di cui si vuole conoscere il valore
	 * @return Il valore del campo
	 */
	public FieldValue getFieldValue(Field chosenField) {
		if (this.valuesMap.containsKey(chosenField)) {
			return this.valuesMap.get(chosenField);
		} else {
			throw new IllegalArgumentException(String.format(
					FIELD_NOT_PRESENT_EXCEPTION, 
					chosenField.getName()));
		}
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
