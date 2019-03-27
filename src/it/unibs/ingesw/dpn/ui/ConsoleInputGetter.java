package it.unibs.ingesw.dpn.ui;

import java.util.Scanner;

/**
 * Implementazione dell'interfaccia InputGetter per la gestione
 * dell'input da tastiera
 */
public class ConsoleInputGetter implements InputGetter {
	
	private Scanner input;
	
	public ConsoleInputGetter() {
		this.input = new Scanner(System.in);
	}

	/**
	 * Implementazione del metodo getInteger di InputGetter
	 */
	@Override
	public int getInteger(int min, int max) throws NumberFormatException {
		
		// Verifica della precondizione
		if (min > max) {
			throw new IllegalArgumentException();
		}
		
		String userInput = input.nextLine();
		int parsedValue = Integer.parseInt(userInput); // throws NumberFormatException
		
		if (parsedValue < min || parsedValue > max) {
			throw new NumberFormatException();
		}
		
		return Integer.parseInt(userInput);
	}

}