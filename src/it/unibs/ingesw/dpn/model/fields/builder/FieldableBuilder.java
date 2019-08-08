package it.unibs.ingesw.dpn.model.fields.builder;

import java.util.Map;

import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

/**
 * Interfaccia che rappresenta un oggetto in grado di costruire -secondo un processo a più step -
 * un oggetto implementante l'interfaccia {@link Fieldable}.
 * Al momento, quest'interfaccia viene implementata da una classe astratta che raccoglie i funzionamento
 * di due Builder differenti: quello per gli User e quello per gli Event.
 * Tutto quanto segue il design pattern GoF <em>Builder</em>.
 * 
 * @author Michele Dusi
 *
 */
public interface FieldableBuilder {
	
	/**
	 * Comincia il processo di creazione di un oggetto Fieldable, dato l'oggetto vuoto.
	 * Poiché il Fieldable Builder si occupa solo della creazione dei valori dei campi, è opportuno che
	 * l'oggetto che viene passato abbia già gli attributi non campi inizializzati, e tutti i campi vuoti.
	 * 
	 * @param emptyFieldable L'oggetto Fieldable che si intende "riempire" di campi
	 */
	public void startCreation(Fieldable emptyFieldable);
	
	/**
	 * Comincia il processo di modifica dell'oggetto Fieldable passato come parametro.
	 * 
	 * @param fieldableSubject Il soggetto Fieldable che si intende modificare
	 */
	public void startEditing(Fieldable fieldableSubject);
	
	/**
	 * Acquisisce un singolo valore per lo specifico campo passato come parametro.
	 * 
	 * @param field Il campo di cui si intende acquisire il valore
	 */
	public void acquireFieldValue(Field field);
	
	/**
	 * Termina il processo di creazione o modifica restituendo un riferimento all'oggetto
	 * Fieldable finalizzato.
	 * 
	 * @return Il risultato del processo di creazione o modifica
	 */
	public Fieldable finalise();
	
	/**
	 * Annulla il processo di creazione o modifica in corso. 
	 */
	public void cancel();
	// TODO: Bisogna decidere se, nel caso del processo di modifica, l'annullamento comporti la cancellazione
	// di tutto quello fatto o ormai quel che è fatto è fatto.
	// AKA: Devo sciogliere la transazione??
	
	/**
	 * In qualunque momento durante il processo di creazione o modifica, restituisce la
	 * lista di coppie campo-valore inizializzate fino a quel momento.
	 * Per valori non inizializzati l'oggetto {@link FieldValue} sarà nullo.
	 * 
	 * La lista sarà definitiva solamente quando verrà chiamato il metodo finalise().
	 * 
	 * Nota: la lista che viene restituita è immutabile. Non ha pertanto alcun collegamento con la lista originale
	 * e non può essere modificata.
	 * 
	 * @return La lista di coppie campo-valore provvisori
	 */
	public Map<Field, FieldValue> getProvisionalFieldValues();
	
	/**
	 * Verifica che tutti i campi obbligatori siano compilati al momento della chiamata.
	 * 
	 * @return "true" se tutti i campi obbligatori sono compilati
	 */
	public boolean verifyMandatoryFields();

}
