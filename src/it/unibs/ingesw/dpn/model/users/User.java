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
	private static final String FIELD_NOT_EDITABLE_EXCEPTION = "Il campo \"%s\" non appartiene alla categoria prevista dall'evento";
	private static final String FIELDVALUE_TYPE_NOT_VALID_EXCEPTION = "Il valore \"%s\" di tipo \"%s\" non è assegnabile al campo \"%s\" che richiede un valore di tipo \"%s\"";
	private static final String FIELD_NULL_EXCEPTION = "Non è possibile eseguire il metodo con un campo o un valore nullo";

	/** La casella di posta a cui recapitare i messaggi dell'utente */
	private Mailbox mailbox;

	/** Mappa dei campi che definiscono e caratterizzano l'utente */
	private final Map<Field, FieldValue> valuesMap;
	
	/**
	 * Crea un nuovo utente con il nome dato. La relativa mailbox e' automaticamente creata, vuota.
	 * 
	 * Precondizione: la lista di coppie (campo, valore) devono essere istanziate correttamente.
	 * Questo significa che tutti i campi obbligatori devono già essere stati inizializzati. 
	 * L'unica classe abilitata a fare ciò è la classe {@link UserFactory}.
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
	 * Restituisce il valore associato al campo dell'utente richiesto.
	 * 
	 * Precondizione: l'oggetto {@link Field} passato come parametro non deve essere null.
	 * 
	 * Precondizione: l'oggetto {@link Field} passato come parametro deve essere un campo previsto per un
	 * utente, ossia deve appartenere all'enumerazione {@link UserField}.
	 * 
	 * @param chosenField il campo di cui si vuole conoscere il valore
	 * @return Il valore del campo
	 */
	public FieldValue getFieldValue(Field chosenField) {
		// Verifico le precondizioni
		if (chosenField == null) {
			throw new IllegalArgumentException(FIELD_NULL_EXCEPTION);
		} else if (!this.valuesMap.containsKey(chosenField)) {
			throw new IllegalArgumentException(String.format(
					FIELD_NOT_PRESENT_EXCEPTION, 
					chosenField.getName()));
		}

		return this.valuesMap.get(chosenField);
	}
	
	/**
	 * Restituisce l'intera lista di valori dei campi, come coppie (Field-FieldValue).
	 * 
	 * @return La mappa di valori Field/FieldValue.
	 */
	public Map<Field, FieldValue> getAllFieldValues() {
		return this.valuesMap;
	}
	
	/**
	 * Metodo che permette la modifica runtime di un valore associato ad un campo dell'utente.
	 * 
	 * <ul>
	 * 
	 * <li>Precondizione: gli oggetti {@link Field} e {@link FieldValue} passati come parametro
	 * non devono essere nulli.
	 * 
	 * <li>Precondizione: l'oggetto {@link Field} passato come parametro deve essere un campo previsto per un
	 * utente, ossia deve appartenere all'enumerazione {@link UserField}.
	 * 
	 * <li>Precondizione: l'oggetto {@link Field} passato come parametro deve essere modificabile, ossia
	 * deve essere impostato per accettare modifiche al valore anche dopo la creazione dell'utente stesso.
	 * 
	 * <li>Precondizione: l'oggetto {@link FieldValue} passato come parametro deve essere un'istanza
	 * del tipo previsto dall'oggetto {@link Field}, recuperabile dal metodo "getType()".
	 * 
	 * <li>Precondizione: l'oggetto {@link FieldValue} deve soddisfare tutte le condizioni di coerenza previste
	 * per un valore del campo indicato da {@link Field}. Poiché eseguire questi controlli all'interno della
	 * classe User risulterebbe troppo oneroso e -per l'impostazione del programma- lievemente fuori luogo, si 
	 * suppone con ragionevole certezza che tale condizione sia verificata a priori nel metodo chiamante 
	 * dell'unica classe autorizzata a gestire la creazione e la modifica di User: {@link it.unibs.ingesw.dpn.ui.UserFactory}.
	 * 
	 * </ul>
	 * 
	 * @param chosenField Il campo che si vuole modificare
	 * @param newValue Il nuovo valore da associare al campo 
	 */
	public void setFieldValue(Field chosenField, FieldValue newValue) {
		// Verifico le precondizioni
		if (chosenField == null || newValue == null) {
			throw new IllegalArgumentException(FIELD_NULL_EXCEPTION);
		} else if (!this.valuesMap.containsKey(chosenField)) {
			throw new IllegalArgumentException(String.format(
					FIELD_NOT_PRESENT_EXCEPTION, 
					chosenField.getName()));
		} else if (!chosenField.isEditable()) {
			throw new IllegalArgumentException(String.format(
					FIELD_NOT_EDITABLE_EXCEPTION,
					chosenField.getName()));
		} else if (!chosenField.getType().isInstance(newValue)) {
			throw new IllegalArgumentException(String.format(
					FIELDVALUE_TYPE_NOT_VALID_EXCEPTION,
					newValue.toString(),
					newValue.getClass().getSimpleName(),
					chosenField.getName(),
					chosenField.getType().getSimpleName()));
		}
		
		// Assegno il nuovo valore 
		this.valuesMap.put(chosenField, newValue);
	}
	
	/**
	 * Metodo che assegna a più campi i rispettivi valori, passati come parametro tramite
	 * una mappa di coppie (campo, valore) = (Field, FieldValue).
	 * 
	 * Le seguenti precondizioni devono valere per ciascuna coppia:
	 * <ul>
	 * 
	 * <li>Precondizione: gli oggetti {@link Field} e {@link FieldValue} passati come parametro
	 * non devono essere nulli.
	 * 
	 * <li>Precondizione: l'oggetto {@link Field} passato come parametro deve essere un campo previsto per un
	 * utente, ossia deve appartenere all'enumerazione {@link UserField}.
	 * 
	 * <li>Precondizione: l'oggetto {@link Field} passato come parametro deve essere modificabile, ossia
	 * deve essere impostato per accettare modifiche al valore anche dopo la creazione dell'utente stesso.
	 * 
	 * <li>Precondizione: l'oggetto {@link FieldValue} passato come parametro deve essere un'istanza
	 * del tipo previsto dall'oggetto {@link Field}, recuperabile dal metodo "getType()".
	 * 
	 * <li>Precondizione: l'oggetto {@link FieldValue} deve soddisfare tutte le condizioni di coerenza previste
	 * per un valore del campo indicato da {@link Field}. Poiché eseguire questi controlli all'interno della
	 * classe User risulterebbe troppo oneroso e -per l'impostazione del programma- lievemente fuori luogo, si 
	 * suppone con ragionevole certezza che tale condizione sia verificata a priori nel metodo chiamante 
	 * dell'unica classe autorizzata a gestire la creazione e la modifica di User: {@link it.unibs.ingesw.dpn.ui.UserFactory}.
	 * 
	 * </ul>
	 * 
	 * @param newValuesMap Le nuove coppie (campo-valore) da associare all'utente
	 */
	public void setAllFieldValues(Map<Field, FieldValue> newValuesMap) {
		// Per tutti i campi che voglio impostare, chiamo il metodo apposta
		for (Field f : newValuesMap.keySet()) {
			// Questo metodo opera tutti i controlli necessari
			this.setFieldValue(f, newValuesMap.get(f));
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
