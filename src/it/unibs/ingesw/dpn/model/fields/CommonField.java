package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;

import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.PeriodFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;

public enum CommonField implements Field, Serializable {
	
	TITOLO (
			"Titolo",
			"Nome di fantasia attribuito all'evento",
			false,
			StringFieldValue.class
			),
	
	LUOGO (
			"Luogo",
			"Il luogo di svolgimento o di ritrovo dell'evento",
			true,
			StringFieldValue.class
			),
	
	DATA_E_ORA (
			"Data e ora",
			"Il giorno e l'orario in cui si svolgerà o avrà inizio l'evento",
			true,
			DateFieldValue.class
			),
	
	DATA_E_ORA_CONCLUSIVE (
			"Data e ora conclusive",
			"Il giorno e l'orario di conclusione dell'evento",
			false,
			DateFieldValue.class
			),
	
	DURATA (
			"Durata",
			"La durata dell'evento",
			false,
			PeriodFieldValue.class
			),

	TERMINE_ULTIMO_DI_ISCRIZIONE (
			"Termine ultimo di iscrizione",
			"Ultimo giorno utile per iscriversi all'evento",
			true,
			DateFieldValue.class
			),
	
	TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE (
			"Termine ultimo di ritiro dell'iscrizione",
			"L'ultima data utile entro cui è concesso ritirare la propria iscrizione all'evento",
			false,
			DateFieldValue.class
			),
	
	NUMERO_DI_PARTECIPANTI (
			"Numero di partecipanti",
			"Numero di persone da coinvolgere nell'evento",
			true,
			IntegerFieldValue.class
			),
	
	TOLLERANZA_NUMERO_DI_PARTECIPANTI (
			"Tolleranza sul numero di partecipanti",
			"Numero di persone eventualmente accettabili in esubero rispetto al \"numero di partecipanti\"",
			false,
			IntegerFieldValue.class
			),
	
	QUOTA_INDIVIDUALE (
			"Quota individuale",
			"La spesa che ogni partecipante dovrà sostenere per l'evento",
			true,
			MoneyAmountFieldValue.class
			),
	
	COMPRESO_NELLA_QUOTA (
			"Compreso nella quota",
			"Lista delle voci di spesa comprese nella quota di partecipazione",
			false,
			StringFieldValue.class
			),
	
	NOTE (
			"Note",
			"Note aggiuntive sull'evento",
			false,
			StringFieldValue.class
			)
	
	;

	private static final String TO_STRING =
			  "Nome:           %s\n"
			+ "Descrizione:    %s\n"
			+ "%s\n";	
	private static final String MANDATORY_TAG = "Obbligatorio";
	private static final String OPTIONAL_TAG = "Facoltativo";

	private final String name;
	private final String description;
	private final boolean mandatory;
	private final Class<? extends FieldValue> type;

	/**
	 * Costruttore privato.
	 * Segnalo di seguito le condizioni da rispettare per inserire un nuovo campo nell'enumerazione:
	 * 
	 * Precondizione: name deve essere un nome valido, non nullo.
	 * 
	 * Precondizione: description deve essere una stringa non nulla.
	 * 
	 * Precondizione: mandatory deve essere un valore non nullo.
	 * 
	 * Precondizione: type deve essere un valore non nullo, relativo ad una classe che implementa
	 * l'interfaccia {@link FieldValue}. Quest'ultima condizione dovrebbe essere garantita in automatico
	 * dall'IDE utilizzato per programmare.
	 * 
	 * @param name Il nome del campo
	 * @param description La descrizione del campo
	 * @param mandatory L'obbligatorietà del campo
	 * @param type Il tipo del valore del campo
	 */
	private CommonField(String name, String description, boolean mandatory, Class<? extends FieldValue> type) {
		if (name == null || description == null || type == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.type = type;
	}

	/**
	 * Restituisce il nome dell'oggetto Field.
	 * 
	 * @return il nome dell'oggetto Field.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Restituisce la descrizione del campo.
	 * 
	 * @return la descrizione del campo, come oggetto {@link String}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * Restituisce l'obbligatorietà del campo.
	 * 
	 * @return true se la compilazione del campo è obbligatoria, false altrimenti
	 */
	@Override
	public boolean isMandatory() {
		return this.mandatory;
	}

	/**
	 * Restituisce la classe le cui istanze sono i possibili valori di questo Field.
	 * 
	 * @return il "tipo" del campo
	 */
	@Override
	public Class<? extends FieldValue> getType() {
		return this.type;
	}

	/**
	 * Restituisce la stringa per la rappresentazione testuale dell'intero campo.
	 * 
	 * Nota: il tipo del campo non viene visualizzato, poiché l'utente riceve tutte 
	 * le informazioni di cui ha bisogno dal campo descrizione.
	 * 
	 * @return la rappresentazione testuale dell'intero campo.
	 */
	@Override
	public String toString() {
		String str = String.format(TO_STRING, 
				this.name,
				this.description,
				this.mandatory ? 
						MANDATORY_TAG :
						OPTIONAL_TAG
				);
		return str;
	}

}
