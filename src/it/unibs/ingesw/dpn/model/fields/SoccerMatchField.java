package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.model.fields.Field.FieldValueAcquirer;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.GenderEnumFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerIntervalFieldValue;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public enum SoccerMatchField implements Field {
	
	GENERE (
			"Genere",
			"Il genere dei giocatori che partecipano alla partita",
			true,
			GenderEnumFieldValue.class,
			(renderer, getter) -> {
				GenderEnumFieldValue [] values = GenderEnumFieldValue.values();
				int i = 1;
				for (GenderEnumFieldValue gender : values) {
					renderer.renderText(String.format("%3d)\t%s", 
							i++, gender.toString()));
				}
				int input = getter.getInteger(1, values.length);
				return values[input - 1];
			}
			),
	
	FASCIA_DI_ETA (
			"Fascia di età",
			"L'intervallo in cui sono comprese le età accettate dei giocatori",
			true,
			IntegerFieldValue.class,
			(renderer, getter) -> {

				IntegerIntervalFieldValue value = null;
				boolean check = false;
				do {
					renderer.renderText("Inserisci il valore minimo");
					int min = getter.getInteger(0, Integer.MAX_VALUE);
					renderer.renderText("Inserisci il valore massimo");
					int max = getter.getInteger(0, Integer.MAX_VALUE);
					
					if (min <= max) {
						value = new IntegerIntervalFieldValue(min, max);
						check = true;
					} else {
						renderer.renderError("Inserire un valore minimo inferiore al valore massimo");
					}
				} while (!check);
				return value;
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
	private Class<? extends FieldValue> type;
	private final FieldValueAcquirer valueAcquirer;
	
	private SoccerMatchField(String name, String description, boolean mandatory, Class<? extends FieldValue> type, FieldValueAcquirer acquirer) {
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
	public FieldValue acquireFieldValue(UIRenderer renderer, InputGetter getter) {
		renderer.renderLineSpace();
		renderer.renderText(String.format(
				" ### %-35s",
				this.name.toUpperCase()));
		renderer.renderText(String.format(
				" ### %s",
				this.description));
		renderer.renderLineSpace();
		return this.valueAcquirer.acquireFieldValue(renderer, getter);
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
	
};