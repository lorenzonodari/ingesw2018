package it.unibs.ingesw.dpn.ui;


import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.FieldCompatibilityException;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;

public class FieldValueUIAcquirer {
	
	/** Input / Output */
	private final UserInterface userInterface;

	/**
	 * Costruttore. 
	 * 
	 * @param userInterface Un'istanza di {@link UserInterface} che fa riferimento all'interfaccia utente da utilizzare.
	 */
	public FieldValueUIAcquirer(UserInterface userInterface) {
		if (userInterface == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo FieldValueAcquirer con parametri nulli");
		}
		// Inizializzo I/O
		this.userInterface = userInterface;
	}
	
	/**
	 * Metodo che permette l'acquisizione di un valore per un dato campo {@link Field}, su un dato
	 * oggetto {@link Fieldable}.
	 * Il primo parametro serve ovviamente per capire come procedere con l'acquisizione, mentre il secondo
	 * permette di effettuare controlli comparati con altri campi dello stesso oggetto che possono portare ad una
	 * migliore costruzione dello stesso (mantenendo più di una volta un'importante coerenza interna fra i campi).
	 * 
	 * Invariante di metodo: il parametro "provisionalFieldable" non viene modificato in alcun modo.
	 * Sarà quindi compito del metodo chiamante assegnare il valore corretto al campo, valore che viene ritornato
	 * da questo metodo.
	 * Questa scelta è stata fatta per evitare effetti collaterali sui parametri.
	 * 
	 * @param provisionalFieldable L'oggetto per cui si vuole acquisire un campo, utilizzato per comparazioni con altri campi.
	 * @param field Il campo di cui si vuole acquisire il valore.
	 * @return Il valore acquisito dal campo.
	 */
	public FieldValue acquireFieldValue(Fieldable provisionalFieldable, Field field) {
		// Verifico le precondizioni
		if (field == null || provisionalFieldable == null) {
			throw new IllegalArgumentException("Impossibile acquisire un campo nullo o su un oggetto nullo");
		}
		
		// Stampo sul renderer una breve introduzione
		this.printFieldIntro(field);
		
		boolean repeatFlag = true;
		FieldValue value = null;

		do {
			// Tento di acquisire un valore
			try {
				// Il primo livello di verifica che faccio è sul valore stesso, deve essere accettabile in quanto tale
				
				value = field.createBlankFieldValue();
					
				// Il secondo livello di verifica coinvolge anche il Field e l'oggetto Fieldable interessato,
				// e si compone di tre fasi:
				// 1) Controllo sul tipo del Field
				// 2) Controllo della compatibilità con gli altri campi
				// 3) Propagazione del valore ad altri valori "legati" dello stesso oggetto Fieldable
				field.checkTypeAndCompatibilityAndPropagateValueAcquisition(provisionalFieldable, value);
				
				// Se termino i controlli, termino anche la ripetizione
				repeatFlag = false;
			}
			catch (FieldCompatibilityException e) {
				// Se durante il processo si verifica un'eccezione, mostro l'eccezione e ripeto l'acquisizione
				userInterface.renderer().renderError(e.getMessage());
				
				// Ripeto l'acquisizione
				repeatFlag = true;
			}
			
		} while (repeatFlag);
		
		return value;
		
	}

	/* METODI PRIVATI DI UTILITA' */
	
	/**
	 * Metodo che stampa sul renderer una breve introduzione al campo che si intende acquisire.
	 * 
	 * @param field Il campo di cui si vogliono visualizzare le informazioni
	 */
	private void printFieldIntro(Field field) {
		userInterface.renderer().renderLineSpace();
		userInterface.renderer().renderText(String.format(
				" ### %-50s",
				field.getName().toUpperCase()));
		userInterface.renderer().renderText(String.format(
				" ### %s",
				field.getDescription()));
		userInterface.renderer().renderLineSpace();
	}

}
