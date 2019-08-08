package it.unibs.ingesw.dpn.model.fields.builder;

import java.util.Collections;
import java.util.Map;

import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.ui.FieldValueAcquirer;

public abstract class AbstractBuilder implements FieldableBuilder {
	
	/** Input/Output */
	private final FieldValueAcquirer acquirer;
	
	/** State */
	private BuilderState state;
	
	/** Riferimento al soggetto dei processi di creazione / modifica */
	private Fieldable provisionalFieldable;

	/**
	 * Costruttore pubblico.
	 * 
	 * @param renderer Il renderizzatore dei prompt e dei messaggi d'errore
	 * @param getter L'acquisitore di dati primitivi
	 */
	public AbstractBuilder(FieldValueAcquirer acquirer) {
		if (acquirer == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo AbstractFieldableBuilder con parametri nulli");
		}
		// Inizializzo I/O
		this.acquirer = acquirer;
		
		// Inizializzo state
		this.state = new ReadyState();
		
		// Null provisionalFieldable
		this.provisionalFieldable = null;
	}

	/**
	 * Modifica lo stato del Builder, secondo il pattern "State".
	 * Necessita di un'implementazione concreta dell'interfaccia {@link BuilderState} come parametro.
	 * 
	 * Precondizione: lo stato passato come parametro deve essere un oggetto {@link BuilderState} valido, coerente
	 * con il funzionamento del programma e correttamente inizializzato.
	 * Ciò viene garantito in parte dal fatto che solo le classi di questo package possono utilizzare questo metodo.
	 * Infatti, questo metodo sarà chiamato prevalentemente dai {@link BuilderState} stessi.
	 * 
	 * @param newState il nuovo stato del Builder come oggetto {@link BuilderState}
	 */
	void setState(BuilderState newState) {
		if (newState == null) {
			throw new IllegalArgumentException("Impossibile transizionare in uno stato nullo");
		}
		this.state = newState;
	}

	/**
	 * Inizia il processo di creazione di un oggetto Fieldable.
	 * Questo significa che l'oggetto NON viene creato da zero, ma viene fatto da zero solamente il processo
	 * di inizializzazione dei campi.
	 * 
	 * Precondizione: l'oggetto {@link Fieldable} NON deve essere nullo
	 * 
	 * @param emptyFieldable L'oggetto Fieldable "vuoto"
	 */
	@Override
	public void startCreation(Fieldable emptyFieldable) {
		// Gestione dello stato
		this.state.onStartingCreation(this);
		
		if (emptyFieldable == null) {
			throw new IllegalArgumentException("Impossibile proseguire con la costruzione di un oggetto null");
		}
		
		this.provisionalFieldable = emptyFieldable;
	}

	@Override
	public void startEditing(Fieldable fieldableSubject) {
		// Gestione dello stato
		this.state.onStartingEditing(this);
		
		if (fieldableSubject == null) {
			throw new IllegalArgumentException("Impossibile modificare un utente nullo");
		}
		this.provisionalFieldable = fieldableSubject;
	}
	
	/**
	 * Gestisce l'acquisizione del dato {@link FieldValue} relativo al campo {@link Field} passato come parametro.
	 * 
	 * Precondizione: il Builder deve essere in modalità "Creazione" o "Modifica", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreating" o "startEditing" (e quindi lo stato interno deve essere o "Creating" o "Editing").
	 * 
	 * Precondizione: il campo {@link Field} non deve essere nullo.
	 * 
	 * Precondizione: il campo {@link Field} deve essere un campo previsto per l'oggetto Fieldable che si intende creare.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 */
	@Override
	public void acquireFieldValue(Field field) {
		if (field == null) {
			throw new IllegalArgumentException("Impossibile acquisire il valore per un campo nullo");
		} else if (!this.provisionalFieldable.hasField(field)) {
			throw new IllegalArgumentException(String.format(
					"Il campo \"%s\" non è previsto per l'oggetto che si sta creando o modificando",
					field.getName()));
		}
			
		// Gestione dello stato
		// Permette di distinguere il comportamento in caso di creazione o di modifica
		this.state.onFieldValueAcquisition(this, field);
		
		// Acquisisco il valore del campo.
		// Avendo controllato tutto correttamente, questo metodo mi garantisce che terminerò l'acquisizione con un valore valido
		FieldValue value = this.acquirer.acquireFieldValue(this.provisionalFieldable, field);
		
		// Infine, setto il nuovo valore del campo
		this.provisionalFieldable.setFieldValue(field, value);
		
	}

	@Override
	public Fieldable finalise() {
		// Gestione dello stato
		this.state.onFinalisation(this);
		
		// Inizializzo i campi di default
		this.provisionalFieldable.setDefaultFieldValues();
		
		return this.provisionalFieldable;
	}

	@Override
	public void cancel() {
		// Gestione dello stato
		this.state.onFinalisation(this);
		
		this.provisionalFieldable = null;
	}
	
	/**
	 * Restituisce una lista di coppie campo-valore (immutabile) contenente tutti i Field
	 * inizializzati fino a questo momento dal Builder.
	 */
	@Override
	public Map<Field, FieldValue> getProvisionalFieldValues() {
		// Gestione dello stato
		this.state.onQuerying(this);
		
		return Collections.unmodifiableMap(this.provisionalFieldable.getAllFieldValues());
	}
	
	/**
	 * Verifica che tutti i campi obbligatori siano compilati.
	 * 
	 * @return "true" se tutti i campi obbligatori sono compilati
	 */
	@Override
	public boolean verifyMandatoryFields() {
		// Gestione dello stato
		this.state.onQuerying(this);
		
		return this.provisionalFieldable.hasAllMandatoryField();
	}
	
	/**
	 * Verifica se lo stato in cui si trova il Builder è "READY",
	 * ossia verifica che non ci siano processi di creazione o modifica in corso.
	 * 
	 * Nota: questo metodo può equivalementente essere usato per capire se il Builder
	 * ha terminato il processo precedente (dopo una chiamata di finalise o cancel).
	 * 
	 * @return true se il Builder è pronto per cominciare un nuovo processo di creazione o modifica.
	 */
	public boolean isReady() {
		return (this.state instanceof ReadyState);
	}

}
