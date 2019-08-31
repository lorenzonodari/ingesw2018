package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

/**
 * L'interfaccia "Field" rappresenta un campo di una categoria all'interno del modello concettuale del progetto.
 * Un oggetto "Field" deve essere in grado di fornire un nome, una descrizione e l'obbligatorietà.
 * Inoltre un oggetto "Field" mantiene un riferimento ad una classe, che è la classe le cui istanze rappresentano il valore del campo.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 * 
 */
public interface Field {

	public static final String FIELD_FORMAT_STRING =
			  "Nome:           %s\n"
			+ "Descrizione:    %s\n"
			+ "%s\n"
			+ "%s\n";	
	public static final String MANDATORY_TAG = "Obbligatorio";
	public static final String OPTIONAL_TAG = "Facoltativo";
	public static final String EDITABLE_TAG = "Modificabile";
	public static final String IMMUTABLE_TAG = "Non modificabile";

	/**
	 * Restituisce il nome dell'oggetto Field.
	 * 
	 * @return il nome dell'oggetto Field.
	 */
	public String getName();
	
	/**
	 * Restituisce la descrizione del campo.
	 * 
	 * @return la descrizione del campo, come oggetto {@link String}
	 */
	public String getDescription();
	
	/**
	 * Restituisce l'obbligatorietà del campo.
	 * 
	 * @return true se la compilazione del campo è obbligatoria, false altrimenti
	 */
	public boolean isMandatory();
	
	/**
	 * Restituisce la modificabilità del campo.
	 * 
	 * @return true se la modifica del campo è permessa dopo che l'oggetto a cui il campo fa riferimento è già stato creato, false altrimenti
	 */
	public boolean isEditable();
	
	/**
	 * Restituisce true se il campo da la possibilita' all'utente di interagire con questo
	 * 
	 * @return true se il campo da la possibilita' all'utente di interagire con questo
	 */
	public default boolean isUserDependant() {
		return false;
	}
	
	/**
	 * Restituisce la classe le cui istanze sono i possibili valori di questo Field.
	 * 
	 * @return il "tipo" del campo
	 */
	public Class<? extends FieldValue> getType();
	
	/**
	 * Crea e restituisce un'istanza "vuota" di FieldValue. Con vuota si intende che tale 
	 * istanza non contiene ancora alcun valore. Affinché tale istanza rappresenti un valore
	 * effettivo, dovra' quindi essere inizializzata mediante il metodo initializeFieldValue().<br>
	 * <br>
	 * Di default, tale metodo richiama il costruttore vuoto del {@link FieldValue} associato.<br>
	 * Per come è stato implementato il software, infatti, ogni FieldValue offre un costruttore
	 * senza parametri.
	 */
	public default FieldValue createBlankFieldValue() {
		try {
			return this.getType().newInstance();
		} catch (InstantiationException e) {
			// Per come è implementato il SW, questo caso non si verifica
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// Per come è implementato il SW, questo caso non si verifica
			e.printStackTrace();
			return null;
		}
	}
	

	/**
	 * Restituisce la stringa per la rappresentazione testuale dell'intero campo.
	 * 
	 * Nota: il tipo del campo non viene visualizzato, poiché l'utente riceve tutte 
	 * le informazioni di cui ha bisogno dal campo descrizione.
	 * 
	 * @return la rappresentazione testuale dell'intero campo.
	 */
	public default String fieldToString() {
		String str = String.format(FIELD_FORMAT_STRING, 
				this.getName(),
				this.getDescription(),
				this.isMandatory() ? 
						MANDATORY_TAG :
						OPTIONAL_TAG,
				this.isEditable() ?
						EDITABLE_TAG :
						IMMUTABLE_TAG
				);
		return str;
	}
	
	/**
	 * Verifica se il valore passato come parametro è un'istanza della classe
	 * prevista come "tipo" dell'oggetto {@link Field}.
	 * 
	 * @param value Il valore {@link FieldValue} da verificare.
	 * @return "true" se il valore è un'istanza corretta, "false" altrimenti
	 */
	public default boolean checkValueType(FieldValue value) {
		return this.getType().isInstance(value);
	}
	
	/**
	 * Verifica la compatibilità del valore {@link FieldValue} con il {@link Field} relativo
	 * e con l'oggetto {@link Fieldable} in cui il valore è contenuto.
	 * In caso la compatibilità sia verificata (ovvero nel caso in cui il valore non genera conflitti con gli altri valori) il
	 * metodo termina senza errori. In caso contrario viene lanciata un'eccezione di tipo 
	 * {@link FieldCompatibilityException} contenente tutti i dettagli dell'errore.
	 * 
	 * Nota: questo metodo deve essere chiamato successivamente a "checkValueType". Esso infatti dà per 
	 * verificato che il valore sia già del tipo previsto.
	 * 
	 * @param fieldableTarget L'oggetto {@link Fieldable} contenente la coppia (campo, valore)
	 * @param value Il valore {@link FieldValue} da verificare
	 * @throws FieldCompatibilityException In caso in cui la compatibilità non risulti corretta
	 */
	public default void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) 
			throws FieldCompatibilityException {
				/* 
				 * Se questo metodo non viene sovrascritto 
				 * il controllo di compatibilità ha automaticamente successo.
				 */
			}
	
	/**
	 * Una volta verificata la compatibilità del valore {@link FieldValue} con questo campo e con 
	 * l'oggetto {@link Fieldable} in cui entrambi sono contenuti, questo metodo modifica alcuni valori
	 * dell'oggetto target in modo da mantenere la coerenza interna fra i vari campi-valori.
	 * 
	 * @param fieldableTarget L'oggetto {@link Fieldable} contenente la coppia (campo, valore)
	 * @param value Il valore i cui effetti possono essere propagati
	 */
	public default void propagateAcquisition(Fieldable fieldableTarget, FieldValue value) {
		/* 
		 * Se questo metodo non viene sovrascritto 
		 * l'acquisizione del valore non comporta la modifica di nessun altro FieldValue.
		 */
	}
	
	/**
	 * Metodo che accorpa i tre metodi:
	 * <ul>
	 * 	<li> checkValueType(FieldValue) : boolean </li>
	 * 	<li> checkValueCompatibility(Fieldable, FieldValue) : void </li>
	 * 	<li> propagateAcquisition(Fieldable, FieldValue) : void </li>
	 * </ul>
	 * 
	 * Nota: se il primo metodo genera un'eccezione, il secondo non viene mai chiamato.
	 * 
	 * @param fieldableTarget L'oggetto {@link Fieldable} contenente la coppia (campo, valore)
	 * @param value Il valore da verificare ed eventualmente propagare
	 * @throws FieldCompatibilityException In caso in cui la compatibilità non risulti corretta
	 */
	public default void checkTypeAndCompatibilityAndPropagateValueAcquisition(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
		if (!this.checkValueType(value)) {
			throw new FieldCompatibilityException("Il tipo del valore non è compatibile con quello previsto dal campo");
		}
		this.checkValueCompatibility(fieldableTarget, value);
		this.propagateAcquisition(fieldableTarget, value);
	}
	
}
