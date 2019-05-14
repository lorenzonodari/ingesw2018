package it.unibs.ingesw.dpn.ui;

import java.util.Scanner;

/**
 * Implementazione dell'interfaccia InputGetter per la gestione dell'input da tastiera.
 * Offre i pruncipali metodi per l'acquisizione di input da tastiera in maniera "sicura", 
 * gestendo le eccezioni e garantendo la corretta formattazione degli input.
 * 
 * @author Lorenzo Nodari, Michele Dusi, Emanuele Poggi
 */
public class ConsoleInputGetter implements InputGetter {
	
	private Scanner input;
	private UIRenderer renderer;

	private static final String PARSING_INTEGER_EXCEPTION = "Impossibile interpretare il valore \"%s\" come numero intero";
	private static final String PARSING_FLOAT_EXCEPTION = "Impossibile interpretare il valore \"%s\" come numero intero";
	private static final String LOWERBOUND_NUM_EXCEPTION = "Il valore %d è inferiore al minimo previsto %d";
	private static final String UPPERBOUND_NUM_EXCEPTION = "Il valore %d è superiore al massimo previsto %d";
	
	/**
	 * Crea una nuova istanza di {@link ConsoleInputGetter}.
	 */
	public ConsoleInputGetter() {
		this.input = new Scanner(System.in);
		this.renderer = new TextRenderer(); // SI PUO' FARE COSI'???
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
			throw new IllegalArgumentException();
		}
		
		int parsedValue = 0;
		boolean okFlag = false;
		
		do {
			String userInput = input.nextLine().trim();
			try {
				parsedValue = Integer.parseInt(userInput);
			} catch (NumberFormatException e) {
				renderer.renderError(String.format(
						PARSING_INTEGER_EXCEPTION,
						userInput));
				continue;
			}		
			if (parsedValue < min) {
				renderer.renderError(String.format(
						LOWERBOUND_NUM_EXCEPTION,
						parsedValue,
						min));
			} else if (parsedValue > max) {
				renderer.renderError(String.format(
						UPPERBOUND_NUM_EXCEPTION,
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
			throw new IllegalArgumentException();
		}
		
		float parsedValue = 0;
		boolean okFlag = false;
		
		do {
			String userInput = input.nextLine().trim();
			try {
				parsedValue = Float.parseFloat(userInput);
			} catch (NumberFormatException e) {
				renderer.renderError(String.format(
						PARSING_FLOAT_EXCEPTION,
						userInput));
				continue;
			}			
			if (parsedValue < min) {
				renderer.renderError(String.format(
						LOWERBOUND_NUM_EXCEPTION,
						parsedValue,
						min));
			} else if (parsedValue > max) {
				renderer.renderError(String.format(
						UPPERBOUND_NUM_EXCEPTION,
						parsedValue,
						max));
			} else {
				okFlag = true;
			}
		} while (!okFlag);
		
		return parsedValue;
	}
	
	/**
	 * Implementazione del metodo getString di InputGetter
	 * 
	 * Postcondizione: la stringa restituita non e' vuota
	 * 
	 */
	@Override
	public String getString() {
		
		StringBuffer buffer = new StringBuffer();
		
		do {
			
			buffer.append(input.nextLine().trim());
			
		} while (buffer.length() == 0);
		
		assert buffer.length() > 0;
		
		return buffer.toString();
		
	}

}
