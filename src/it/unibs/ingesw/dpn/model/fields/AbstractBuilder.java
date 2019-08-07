package it.unibs.ingesw.dpn.model.fields;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

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
	 * Questo significa che l'oggetto viene creato da zero, e poiché non è possibile sapere che oggetto
	 * si vuole questo metodo utilizza la reflection per recuperare il costruttore adatto, dati i parametri
	 * di istanziazione.
	 * 
	 * Nota: questo metodo permette alla classe padre di rimanere indipendente dalle classi figlie.
	 * 
	 * @param fieldableType La classe che implementa Fieldable che si vuole istanziare
	 * @param constructorParams la lista di parametri del costruttore di tale classe
	 */
	@Override
	public void startCreating(Class<? extends Fieldable> fieldableType, Object ... constructorParams) {
		// Gestione dello stato
		this.state.onStartingCreating(this);
		
		// Creazione di un'istanza del nuovo oggetto.
		Class<?> [] typeArray = (Class<?> []) Arrays.stream(constructorParams).map(o -> o.getClass()).toArray();
		try {
			this.provisionalFieldable = fieldableType.getConstructor(typeArray).newInstance(constructorParams);
		} catch (Exception e) {
			// In caso qualcosa vada storto nell'istanziazione, visualizzo l'errore e cancello il processo
			e.printStackTrace();
			this.cancel();
		}
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
