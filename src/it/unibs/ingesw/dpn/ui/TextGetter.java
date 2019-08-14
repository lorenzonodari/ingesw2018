package it.unibs.ingesw.dpn.ui;

import java.util.Scanner;

import it.unibs.ingesw.dpn.ui.actions.Action;
import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;

/**
 * Implementazione dell'interfaccia InputGetter per la gestione dell'input da tastiera.
 * Offre i pruncipali metodi per l'acquisizione di input da tastiera in maniera "sicura", 
 * gestendo le eccezioni e garantendo la corretta formattazione degli input.
 * 
 * @author Lorenzo Nodari, Michele Dusi, Emanuele Poggi
 */
public class TextGetter implements UIGetter {
	
	private Scanner input;
	private UIRenderer renderer;
	
	private static final String NULL_RENDERER_EXCEPTION = "Impossibile utrilizzare un Renderer con riferimento nullo";
	private static final String INVALID_INT_INTERVAL_EXCEPTION = "L'intervallo [%d, %d] non è un intervallo valido";
	private static final String INVALID_FLOAT_INTERVAL_EXCEPTION = "L'intervallo [%f, %f] non è un intervallo valido";
	
	private static final String PARSING_INTEGER_ERROR = "Impossibile interpretare il valore \"%s\" come numero intero";
	private static final String PARSING_FLOAT_ERROR = "Impossibile interpretare il valore \"%s\" come numero in virgola mobile";
	private static final String PARSING_BOOLEAN_ERROR = "Impossibile interpretare il valore \"%s\" come valore booleano. Usare [V/F], [vero/falso], [T/F] o [true/false].";
	private static final String LOWERBOUND_INTEGER_ERROR = "Il valore %d è inferiore al minimo previsto %d";
	private static final String UPPERBOUND_INTEGER_ERROR = "Il valore %d è superiore al massimo previsto %d";
	private static final String LOWERBOUND_FLOAT_ERROR = "Il valore %f è inferiore al minimo previsto %f";
	private static final String UPPERBOUND_FLOAT_ERROR = "Il valore %f è superiore al massimo previsto %f";
	private static final String INVALID_FORMAT_STRING_ERROR = "La stringa \"%s\" non corrisponde al formato atteso";

	/**
	 * Crea una nuova istanza di {@link TextGetter}.
	 * Alla costruzione crea anche un nuovo oggetto UIRenderer per la segnalazione di errori.
	 */
	@Deprecated
	public TextGetter() {
		this.input = new Scanner(System.in);
		this.renderer = new TextRenderer(); // Non consigliato
	}
	
	/**
	 * Crea una nuova istanza di {@link TextGetter}.
	 * Richiede come parametro un oggetto UIRenderer che si occupi della visualizzazione di eventuali errori.
	 * 
	 * @param renderer L'oggetto che renderizza gli errori durante l'acquisizione
	 */
	public TextGetter(UIRenderer renderer) {
		this.input = new Scanner(System.in);
		if (renderer != null) {
			this.renderer = renderer;
		} else {
			throw new IllegalArgumentException(NULL_RENDERER_EXCEPTION);
		}
	}

	/**
	 * Implementazione del metodo getInteger di InputGetter.
	 * Acquisisce un intero compreso fra due estremi (uno inferiore e uno superiore), estremi inclusi.
	 * 
	 * Precondizione: min <= max
	 * 
	 * @param min Il minimo valore accettato
	 * @param max Il massimo valore accettato
	 */
	@Override
	public int getInteger(int min, int max) {
		
		// Verifica della precondizione
		if (min > max) {
			throw new IllegalArgumentException(String.format(
					INVALID_INT_INTERVAL_EXCEPTION, 
					min, 
					max));
		}
		
		int parsedValue = 0;
		boolean okFlag = false;
		
		do {
			renderer.renderEmptyPrompt();
			String userInput = input.nextLine().trim();
			try {
				parsedValue = Integer.parseInt(userInput);
			} catch (NumberFormatException e) {
				renderer.renderError(String.format(
						PARSING_INTEGER_ERROR,
						userInput));
				continue;
			}		
			if (parsedValue < min) {
				renderer.renderError(String.format(
						LOWERBOUND_INTEGER_ERROR,
						parsedValue,
						min));
			} else if (parsedValue > max) {
				renderer.renderError(String.format(
						UPPERBOUND_INTEGER_ERROR,
						parsedValue,
						max));
			} else {
				okFlag = true;
			}
		} while (!okFlag);
		
		return parsedValue;
	}

	/**
	 * Implementazione del metodo getFloat di InputGetter.
	 * Acquisisce da tastiera un numero in virgola mobile compreso fra il minimo e il massimo
	 * passati come parametri.
	 * 
	 * Precondizione: min <= max
	 * 
	 * @param min Il minimo valore accettato
	 * @param max Il massimo valore accettato
	 */
	@Override
	public float getFloat(float min, float max) throws NumberFormatException {
		
		// Verifica della precondizione
		if (min > max) {
			throw new IllegalArgumentException(String.format(
					INVALID_FLOAT_INTERVAL_EXCEPTION, 
					min, 
					max));
		}
		
		float parsedValue = 0;
		boolean okFlag = false;
		
		do {
			renderer.renderEmptyPrompt();
			String userInput = input.nextLine().trim();
			try {
				parsedValue = Float.parseFloat(userInput);
			} catch (NumberFormatException e) {
				renderer.renderError(String.format(
						PARSING_FLOAT_ERROR,
						userInput));
				continue;
			}			
			if (parsedValue < min) {
				renderer.renderError(String.format(
						LOWERBOUND_FLOAT_ERROR,
						parsedValue,
						min));
			} else if (parsedValue > max) {
				renderer.renderError(String.format(
						UPPERBOUND_FLOAT_ERROR,
						parsedValue,
						max));
			} else {
				okFlag = true;
			}
		} while (!okFlag);
		
		return parsedValue;
	}
	
	/**
	 * Implementazione del metodo getString di InputGetter.
	 * 
	 * Postcondizione: la stringa restituita non e' vuota
	 * 
	 */
	@Override
	public String getString() {
		
		StringBuffer buffer = new StringBuffer();
		
		do {
			
			renderer.renderEmptyPrompt();
			buffer.append(input.nextLine().trim());
			
		} while (buffer.length() == 0);
		
		assert buffer.length() > 0;
		
		return buffer.toString();
		
	}

	/**
	 * Acquisisce una stringa solo se questa "matcha" l'espressione regaolare passata come parametro.
	 * 
	 * @param regex L'espressione regolare da confrontare con la stringa acquisita
	 * @return La stringa in input.
	 */
	public String getMatchingString(String regex) {
		boolean okFlag = false;
		String inputString = null;
		do {
			inputString = getString();
			if (inputString.matches(regex)) {
				okFlag = true;
			} else {
				renderer.renderError(String.format(
						INVALID_FORMAT_STRING_ERROR, 
						inputString));
			}
		} while(!okFlag);
		
		return inputString;
	}

	/**
	 * Acquisisce un valore booleano.
	 * In caso l'input non sia interpretabile come valore booleano, il metodo
	 * segnala all'utente che c'è stato un errore e ripropone l'acquisizione del dato.
	 * Il valore booleano viene interpretato sia a parola, sia come lettera. Inoltre è possibile 
	 * inserire sia il valore in italiano che in inglese.
	 * 
	 * @return Il valore booleano acquisito
	 */
	@Override
	public boolean getBoolean() {
		
		boolean parsedValue = false;
		boolean okFlag = false;
		
		do {
			renderer.renderEmptyPrompt();
			String userInput = input.nextLine().trim();
			String lowUserInput = userInput.toLowerCase();
			if (lowUserInput.equals("t") || 
					lowUserInput.equals("v") ||
					lowUserInput.equals("true") ||
					lowUserInput.equals("vero")) {
				parsedValue = true;
				okFlag = true;
			} else if (lowUserInput.equals("f") ||
					lowUserInput.equals("false") ||
					lowUserInput.equals("falso")) {
				parsedValue = false;
				okFlag = true;
			} else {
				renderer.renderError(String.format(PARSING_BOOLEAN_ERROR, userInput));
			}
			
		} while (!okFlag);
		
		return parsedValue;
	}

	/**
	 * Dopo aver presentato le opzioni presenti nel menu, richiede all'utente di selezionarne una.
	 * Se la selezione è stata effettuata correttamente, restituisce l'azione associata a tale opzione.
	 * 
	 * Nota: La visualizzazione del menu <b>è compresa</b> nella chiamata di questo metodo.
	 * 
	 * @param menu Il menu da cui attingere 
	 * @return L'azione corrispondente all'opzione selezionata
	 */
	public Action getMenuChoice(MenuAction menu) {
		// Render del menu e del prompt
		renderer.renderMenu(menu);
		
		// Acquisizione del numero relativo all'opzione
		int choice = this.getInteger(0, menu.getEntries().size());
		
		// Restituzione dell'opzione corrispondente
		if (choice == 0) {
			return menu.getBackEntry().getAction();
		} else {
			return menu.getEntries().get(choice - 1).getAction();
		}
	}

	@Override
	public boolean getConfirmChoice(ConfirmAction confirm) {
		// Render del testo di conferma e del prompt
		renderer.renderConfirm(confirm);
		
		// Acquisizione del numero relativo all'opzione (fra due)
		int choice = this.getInteger(0, 1);
		
		// Restituzione del boolean corrispondente
		return (choice == 0) ? false : true;
	}

	@Override
	public void getDialogInteraction(DialogAction dialogAction) {
		// Render della finestra di dialogo
		renderer.renderDialog(dialogAction);
		
		// acquisizione di un valore qualsiasi
//		renderer.renderEmptyPrompt();
		input.nextLine(); // TODO Potrebbe richiedere una correzione
	}

}
