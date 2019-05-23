package it.unibs.ingesw.dpn.model.fields;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import it.unibs.ingesw.dpn.model.fields.Field.FieldValueAcquirer;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public enum CommonField implements Field, Serializable {
	
	TITOLO (
			"Titolo",
			"Nome di fantasia attribuito all'evento",
			false,
			StringFieldValue.class,
			(renderer, getter, partialValues) -> {
				return StringFieldValue.acquireValue(renderer, getter);
			}
			),
	
	LUOGO (
			"Luogo",
			"Il luogo di svolgimento o di ritrovo dell'evento",
			true,
			StringFieldValue.class,
			(renderer, getter, partialValues) -> {
				return StringFieldValue.acquireValue(renderer, getter);
			}
			),
	
	DATA_E_ORA (
			"Data e ora",
			"Il giorno e l'orario in cui si svolgerà o avrà inizio l'evento",
			true,
			DateFieldValue.class,
			(renderer, getter, partialValues) -> {
				boolean okFlag = false;
				DateFieldValue acquiredDate = null;
				do {
					acquiredDate = DateFieldValue.acquireValue(renderer, getter);
					// Verifico che la data dell'evento sia posteriore alla creazione
					if (acquiredDate.before(new Date())) {
						renderer.renderError("Inserire una data futura");
					} else if (isAfterEndingDate(acquiredDate, partialValues)) {
						renderer.renderError("Data d'inizio posteriore alla data conclusiva dell'evento");
					} else if (isBeforeSubscriptionTimeoutDate(acquiredDate, partialValues)) {
						renderer.renderError("Data d'inizio precedente al termine ultimo di iscrizione");
					} else if (isBeforeUnsubscriptionTimeoutDate(acquiredDate, partialValues)) {
						renderer.renderError("Data d'inizio precedente al termine ultimo di ritiro iscrizione");
					} else {
						okFlag = true;
					}
				} while (!okFlag);
				return acquiredDate;
			}
			),
	
	DATA_E_ORA_CONCLUSIVE (
			"Data e ora conclusive",
			"Il giorno e l'orario di conclusione dell'evento",
			false,
			DateFieldValue.class,
			(renderer, getter, partialValues) -> {
				boolean okFlag = false;
				DateFieldValue acquiredDate = null;
				do {
					acquiredDate = DateFieldValue.acquireValue(renderer, getter);
					// Verifico che la data dell'evento sia posteriore alla creazione
					if (acquiredDate.before(new Date())) {
						renderer.renderError("Inserire una data futura");
					} else if (isBeforeStartingDate(acquiredDate, partialValues)) {
						renderer.renderError("Data di conclusione precedente alla data d'inizio dell'evento");
					} else if (isBeforeSubscriptionTimeoutDate(acquiredDate, partialValues)) {
						renderer.renderError("Data di conclusione precedente al termine ultimo di iscrizione");
					} else if (isBeforeUnsubscriptionTimeoutDate(acquiredDate, partialValues)) {
						renderer.renderError("Data di conclusione precedente al termine ultimo di ritiro iscrizione");
					} else {
						okFlag = true;
					}
				} while (!okFlag);
				return acquiredDate;
			}
			),
	
	DURATA (
			"Durata",
			"La durata approssimata, in ore e minuti o in giorni, dell'evento",
			false,
			IntegerFieldValue.class,
			(renderer, getter, partialValues) -> {
				renderer.renderText("Inserisci il valore numerico della durata");
				return new IntegerFieldValue(getter.getInteger(0, Integer.MAX_VALUE));
			}
			),

	TERMINE_ULTIMO_DI_ISCRIZIONE (
			"Termine ultimo di iscrizione",
			"Ultimo giorno utile per iscriversi all'evento",
			true,
			DateFieldValue.class,
			(renderer, getter, partialValues) -> {
				boolean okFlag = false;
				DateFieldValue acquiredDate = null;
				do {
					acquiredDate = DateFieldValue.acquireValue(renderer, getter);
					// Verifico che la data dell'evento sia posteriore alla creazione
					if (acquiredDate.before(new Date())) {
						renderer.renderError("Inserire una data futura");
					} else if (CommonField.isAfterStartingDate(acquiredDate, partialValues)) {
						renderer.renderError("Termine ultimo d'iscrizione posteriore alla data d'inizio dell'evento");
					} else if (CommonField.isAfterEndingDate(acquiredDate, partialValues)) {
						renderer.renderError("Termine ultimo d'iscrizione posteriore alla data di conclusione dell'evento");
					} else if (isBeforeUnsubscriptionTimeoutDate(acquiredDate, partialValues)) {
						renderer.renderError("Termine ultimo d'iscrizione precedente al termine ultimo di ritiro iscrizione");
					} else {
						okFlag = true;
					}
				} while (!okFlag);
				return acquiredDate;
			}
			),
	
	TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE (
			"Termine ultimo di ritiro dell'iscrizione",
			"L'ultima data utile entro cui è concesso ritirare la propria iscrizione all'evento",
			false,
			DateFieldValue.class,
			(renderer, getter, partialValues) -> {
				boolean okFlag = false;
				DateFieldValue acquiredDate = null;
				do {
					acquiredDate = DateFieldValue.acquireValue(renderer, getter);
					// Verifico che la data dell'evento sia posteriore alla creazione
					if (acquiredDate.before(new Date())) {
						renderer.renderError("Inserire una data futura");
					} else if (CommonField.isAfterStartingDate(acquiredDate, partialValues)) {
						renderer.renderError("Termine ultimo di ritiro iscrizione posteriore alla data d'inizio dell'evento");
					} else if (CommonField.isAfterEndingDate(acquiredDate, partialValues)) {
						renderer.renderError("Termine ultimo di ritiro iscrizione posteriore alla data di conclusione dell'evento");
					} else if (CommonField.isAfterSubscriptionTimeoutDate(acquiredDate, partialValues)) {
						renderer.renderError("Termine ultimo di ritiro iscrizione posteriore al termine ultimo d'iscrizione");
					} else {
						okFlag = true;
					}
				} while (!okFlag);
				return acquiredDate;
			}
			),
	
	NUMERO_DI_PARTECIPANTI (
			"Numero di partecipanti",
			"Numero di persone da coinvolgere nell'evento",
			true,
			IntegerFieldValue.class,
			(renderer, getter, partialValues) -> {
				renderer.renderText("Inserisci il numero di partecipanti (almeno 2)");
				return new IntegerFieldValue(getter.getInteger(2, Integer.MAX_VALUE));
			}
			),
	
	TOLLERANZA_NUMERO_DI_PARTECIPANTI (
			"Tolleranza sul numero di partecipanti",
			"Numero di persone eventualmente accettabili in esubero rispetto al \"numero di partecipanti\"",
			false,
			IntegerFieldValue.class,
			(renderer, getter, partialValues) -> {
				renderer.renderText("Inserisci la tolleranza massima sul numero di partecipanti");
				return new IntegerFieldValue(getter.getInteger(0, Integer.MAX_VALUE));
			}
			),
	
	QUOTA_INDIVIDUALE (
			"Quota individuale",
			"La spesa che ogni partecipante dovrà sostenere per l'evento",
			true,
			MoneyAmountFieldValue.class,
			(renderer, getter, partialValues) -> {
				renderer.renderText("Inserisci il costo di partecipazione");
				return new MoneyAmountFieldValue(getter.getFloat(0, Float.MAX_VALUE));
			}
			),
	
	COMPRESO_NELLA_QUOTA (
			"Compreso nella quota",
			"Lista delle voci di spesa comprese nella quota di partecipazione",
			false,
			StringFieldValue.class,
			(renderer, getter, partialValues) -> {
				return StringFieldValue.acquireValue(renderer, getter);
			}
			),
	
	NOTE (
			"Note",
			"Note aggiuntive sull'evento",
			false,
			StringFieldValue.class,
			(renderer, getter, partialValues) -> {
				return StringFieldValue.acquireValue(renderer, getter);
			}
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
	private final FieldValueAcquirer valueAcquirer;

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
	 * @param acquirer Il metodo di acquisizione del dato
	 */
	private CommonField(String name, String description, boolean mandatory, Class<? extends FieldValue> type, FieldValueAcquirer acquirer) {
		if (name == null || description == null || type == null || acquirer == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.type = type;
		this.valueAcquirer = acquirer;
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
	 * Passa il controllo all'istanza di {@link FieldValueAcquirer} che si occupa di acquisire
	 * un valore per questo specifico campo.
	 * 
	 * @param renderer Il renderer da chiamare per visualizzare eventuali messaggi d'errore
	 * @param getter L'oggetto che si occupa di acquisire i dati in maniera primitiva
	 * @return L'oggetto che rappresenta il valore del campo
	 */
	@Override
	public FieldValue acquireFieldValue(UIRenderer renderer, InputGetter getter, Map<Field, FieldValue> partialValues) {
		renderer.renderLineSpace();
		renderer.renderText(String.format(
				" ### %-35s",
				this.name.toUpperCase()));
		renderer.renderText(String.format(
				" ### %s",
				this.description));
		renderer.renderLineSpace();
		return this.valueAcquirer.acquireFieldValue(renderer, getter, partialValues);
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
	
	private static boolean isAfterEndingDate(Date date, Map<Field, FieldValue> partialValues) {
		return (
				partialValues.get(DATA_E_ORA_CONCLUSIVE) != null && 
				date.after((Date) partialValues.get(DATA_E_ORA_CONCLUSIVE))
				);
	}

	private static boolean isAfterStartingDate(Date date, Map<Field, FieldValue> partialValues) {
		return (
				partialValues.get(DATA_E_ORA) != null && 
				date.after((Date) partialValues.get(DATA_E_ORA))
				);
	}
	
	private static boolean isBeforeStartingDate(Date date, Map<Field, FieldValue> partialValues) {
		return (
				partialValues.get(DATA_E_ORA) != null && 
				date.before((Date) partialValues.get(DATA_E_ORA))
				);
	}

	private static boolean isBeforeSubscriptionTimeoutDate(Date date, Map<Field, FieldValue> partialValues) {
		return (
				partialValues.get(TERMINE_ULTIMO_DI_ISCRIZIONE) != null && 
				date.before((Date) partialValues.get(TERMINE_ULTIMO_DI_ISCRIZIONE))
				);
	}

	private static boolean isAfterSubscriptionTimeoutDate(Date date, Map<Field, FieldValue> partialValues) {
		return (
				partialValues.get(TERMINE_ULTIMO_DI_ISCRIZIONE) != null && 
				date.after((Date) partialValues.get(TERMINE_ULTIMO_DI_ISCRIZIONE))
				);
	}
	
	private static boolean isBeforeUnsubscriptionTimeoutDate(Date date, Map<Field, FieldValue> partialValues) {
		return (
				partialValues.get(TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE) != null && 
				date.before((Date) partialValues.get(TERMINE_ULTIMO_DI_RITIRO_ISCRIZIONE))
				);
	}

}
