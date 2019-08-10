package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;
import java.util.Date;

import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.TimeAmountFieldValue;
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
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			DateFieldValue dateValue = (DateFieldValue) value;
			if (dateValue.before(new Date())) {
				throw new FieldCompatibilityException("Inserire una data futura");
				
			} else if (isAfterOrEqualToDateField(dateValue, fieldableTarget, CommonField.DATA_E_ORA_CONCLUSIVE)) {
				throw new FieldCompatibilityException("Data d'inizio posteriore alla data conclusiva dell'evento");
				
			} else if (isBeforeOrEqualToDateField(dateValue, fieldableTarget, CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)) {
				throw new FieldCompatibilityException("Data d'inizio precedente al termine ultimo di iscrizione");
				
			} else if (isBeforeOrEqualToDateField(dateValue, fieldableTarget, CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE)) {
				throw new FieldCompatibilityException("Data d'inizio precedente al termine ultimo di ritiro iscrizione");
			}
		}
		
		@Override
		public void propagateAcquisition(Fieldable fieldableTarget, FieldValue value) {
			DateFieldValue dateValue = (DateFieldValue) value;
			
			if (fieldableTarget.hasFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE)) {
				
				// Setup di "Durata"
				TimeAmountFieldValue duration = new TimeAmountFieldValue(
						dateValue,
						((DateFieldValue) fieldableTarget.getFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE)));
				fieldableTarget.setFieldValue(CommonField.DURATA, duration);
				
			} else if (fieldableTarget.hasFieldValue(CommonField.DURATA)) {
				
				// Setup di "Data e ora conclusive"
				DateFieldValue dataEOraConclusive = new DateFieldValue(
						dateValue.getTime() +
						1000 * ((TimeAmountFieldValue) fieldableTarget.getFieldValue(CommonField.DURATA)).getSeconds()
						);
				fieldableTarget.setFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE, dataEOraConclusive);
			}
		}
	},
	
	DATA_E_ORA_CONCLUSIVE (
			"Data e ora conclusive",
			"Il giorno e l'orario di conclusione dell'evento",
			false,
			DateFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			DateFieldValue dateValue = (DateFieldValue) value;
			// Verifico che la data dell'evento sia posteriore alla creazione
			if (dateValue.before(new Date())) {
				throw new FieldCompatibilityException("Inserire una data futura");
				
			} else if (isBeforeOrEqualToDateField(dateValue, fieldableTarget, CommonField.DATA_E_ORA)) {
				throw new FieldCompatibilityException("Data di conclusione precedente alla data d'inizio dell'evento");
			
			} else if (isBeforeOrEqualToDateField(dateValue, fieldableTarget, CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)) {
				throw new FieldCompatibilityException("Data di conclusione precedente al termine ultimo di iscrizione");
			
			} else if (isBeforeOrEqualToDateField(dateValue, fieldableTarget, CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE)) {
				throw new FieldCompatibilityException("Data di conclusione precedente al termine ultimo di ritiro iscrizione");
			}
		}
		
		@Override
		public void propagateAcquisition(Fieldable fieldableTarget, FieldValue value) {
			DateFieldValue dateValue = (DateFieldValue) value;
			
			if (fieldableTarget.hasFieldValue(CommonField.DATA_E_ORA)) {
				// Setup di "Durata"
				TimeAmountFieldValue duration = new TimeAmountFieldValue(
						((DateFieldValue) fieldableTarget.getFieldValue(CommonField.DATA_E_ORA)),
						dateValue);
				fieldableTarget.setFieldValue(CommonField.DURATA, duration);
			}
		}
	},
	
	DURATA (
			"Durata",
			"La durata dell'evento",
			false,
			TimeAmountFieldValue.class
			)
	{
		@Override
		public void propagateAcquisition(Fieldable fieldableTarget, FieldValue value) {
			TimeAmountFieldValue dateValue = (TimeAmountFieldValue) value;
			
			if (fieldableTarget.hasFieldValue(CommonField.DATA_E_ORA)) {
				// Setup di "Data e ora conclusive"
				DateFieldValue dataEOraConclusive = new DateFieldValue(
						((DateFieldValue) fieldableTarget.getFieldValue(CommonField.DATA_E_ORA)).getTime() +
						1000 * dateValue.getSeconds()
						);
				fieldableTarget.setFieldValue(CommonField.DATA_E_ORA_CONCLUSIVE, dataEOraConclusive);
			}
		}
	},

	TERMINE_ULTIMO_DI_ISCRIZIONE (
			"Termine ultimo di iscrizione",
			"Ultimo giorno utile per iscriversi all'evento",
			true,
			DateFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			DateFieldValue dateValue = (DateFieldValue) value;
			// Verifico che la data dell'evento sia posteriore alla creazione
			if (dateValue.before(new Date())) {
				throw new FieldCompatibilityException("Inserire una data futura");
			
			} else if (isAfterOrEqualToDateField(dateValue, fieldableTarget, CommonField.DATA_E_ORA)) {
				throw new FieldCompatibilityException("Termine ultimo d'iscrizione posteriore alla data d'inizio dell'evento");
			
			} else if (isAfterOrEqualToDateField(dateValue, fieldableTarget, CommonField.DATA_E_ORA_CONCLUSIVE)) {
				throw new FieldCompatibilityException("Termine ultimo d'iscrizione posteriore alla data di conclusione dell'evento");
			
			} else if (isBeforeOrEqualToDateField(dateValue, fieldableTarget, CommonField.TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE)) {
				throw new FieldCompatibilityException("Termine ultimo d'iscrizione precedente al termine ultimo di ritiro iscrizione");
			}
		}
	},
	
	TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE (
			"Termine ultimo di ritiro dell'iscrizione",
			"L'ultima data utile entro cui è concesso ritirare la propria iscrizione all'evento",
			false,
			DateFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			DateFieldValue dateValue = (DateFieldValue) value;
			// Verifico che la data dell'evento sia posteriore alla creazione
			if (dateValue.before(new Date())) {
				throw new FieldCompatibilityException("Inserire una data futura");
			
			} else if (isAfterOrEqualToDateField(dateValue, fieldableTarget, CommonField.DATA_E_ORA)) {
				throw new FieldCompatibilityException("Termine ultimo di ritiro iscrizione posteriore alla data d'inizio dell'evento");
			
			} else if (isAfterOrEqualToDateField(dateValue, fieldableTarget, CommonField.DATA_E_ORA_CONCLUSIVE)) {
				throw new FieldCompatibilityException("Termine ultimo di ritiro iscrizione posteriore alla data di conclusione dell'evento");
			
			} else if (isAfterOrEqualToDateField(dateValue, fieldableTarget, CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE)) {
				throw new FieldCompatibilityException("Termine ultimo di ritiro iscrizione posteriore al termine ultimo d'iscrizione");
			}
		}
	},
	
	NUMERO_DI_PARTECIPANTI (
			"Numero di partecipanti",
			"Numero di persone da coinvolgere nell'evento",
			true,
			IntegerFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			IntegerFieldValue integerValue = (IntegerFieldValue) value;
			
			// Controllo che il numero di partecipanti sia almeno 2
			if (integerValue.getValue() < 2) {
				throw new FieldCompatibilityException("Il numero di partecipanti previsti deve essere almeno 2");
			}
		}
		
	},
	
	TOLLERANZA_NUMERO_DI_PARTECIPANTI (
			"Tolleranza sul numero di partecipanti",
			"Numero di persone eventualmente accettabili in esubero rispetto al \"numero di partecipanti\"",
			false,
			IntegerFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			IntegerFieldValue integerValue = (IntegerFieldValue) value;
			
			// Controllo che la tolleranza sia almeno 0
			if (integerValue.getValue() < 0) {
				throw new FieldCompatibilityException("Non è possibile inserire un valore di tolleranza negativo");
			}
		}
		
	},
	
	QUOTA_INDIVIDUALE (
			"Quota individuale",
			"La spesa che ogni partecipante dovrà sostenere per l'evento",
			true,
			MoneyAmountFieldValue.class
			)
	{
		@Override
		public void checkValueCompatibility(Fieldable fieldableTarget, FieldValue value) throws FieldCompatibilityException {
			MoneyAmountFieldValue moneyAmountValue = (MoneyAmountFieldValue) value;
			
			// Controllo che il valore di spesa non sia negativo
			if (moneyAmountValue.getValue() < 0) {
				throw new FieldCompatibilityException("Non è possibile inserire un valore di spesa negativo");
			}
		}
		
	},
	
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
	 * Metodo che restituisce automaticamente "false".
	 * Per default, tutti i campi comuni alle categoria NON sono modificabili.
	 */
	@Override
	public boolean isEditable() {
		return false;
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
	 * Verifica che una certa data sia successiva o al più uguale al valore temporale contenuto in un campo specificato.
	 * Se il campo non contiene valore, restituisce false.
	 * Se il campo non ha valore di tipo {@link DateFieldValue}, restituisce un'eccezione.
	 * (Essendo un metodo privato, è compito della classe stessa assicurarsi che l'eccezione non si verifichi).
	 * 
	 * @param date La data da analizzare
	 * @param fieldable L'oggetto Fieldable su cui effettuare il confronto
	 * @param comparingDateField Il campo da cui estrarre il valore (di tipo "data") con cui effettuare il confronto
	 * @return true se il valore del campo ESISTE && se la data è SUCCESSIVA o CONTEMPORANEA.
	 */
	private static boolean isAfterOrEqualToDateField(DateFieldValue date, Fieldable fieldable, Field comparingDateField) {
		// Verifico che il campo abbia un tipo "data"
		if (comparingDateField.getType() != DateFieldValue.class) {
			throw new IllegalArgumentException("Impossibile interpretare il campo come data o istante temporale");
		}
		
		return (
				// Verifico che il valore del campo di comparazione sia presente
				fieldable.getFieldValue(comparingDateField) != null && 
				// Verifico che la data da controllare sia successiva o contemporanea al valore del campo
				date.compareTo((Date) fieldable.getFieldValue(comparingDateField)) >= 0
				);
	}

	/**
	 * Verifica che una certa data sia precendte o al più uguale al valore temporale contenuto in un campo specificato.
	 * Se il campo non contiene valore, restituisce false.
	 * Se il campo non ha valore di tipo {@link DateFieldValue}, restituisce un'eccezione.
	 * (Essendo un metodo privato, è compito della classe stessa assicurarsi che l'eccezione non si verifichi).
	 * 
	 * @param date La data da analizzare
	 * @param fieldable L'oggetto Fieldable su cui effettuare il confronto
	 * @param comparingDateField Il campo da cui estrarre il valore (di tipo "data") con cui effettuare il confronto
	 * @return true se il valore del campo ESISTE && se la data è SUCCESSIVA o CONTEMPORANEA.
	 */
	private static boolean isBeforeOrEqualToDateField(DateFieldValue date, Fieldable fieldable, Field comparingDateField) {
		// Verifico che il campo abbia un tipo "data"
		if (comparingDateField.getType() != DateFieldValue.class) {
			throw new IllegalArgumentException("Impossibile interpretare il campo come data o istante temporale");
		}
		
		return (
				// Verifico che il valore del campo di comparazione sia presente
				fieldable.getFieldValue(comparingDateField) != null && 
				// Verifico che la data da controllare sia successiva o contemporanea al valore del campo
				date.compareTo((Date) fieldable.getFieldValue(comparingDateField)) <= 0
				);
	}
	
	
}
