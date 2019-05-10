package it.unibs.ingesw.dpn.ui;

/**
 * Interfaccia utilizzata dalle classi adibite all'acquisizione di input dall'utente
 */
public interface InputGetter {

	/**
	 * Acquisisce un numero intero nell'intervallo [min, max] (estremi inclusi), lanciando
	 * un'eccezione se il dato acquisito non e' un numero o se tale numero non rientra nel range
	 * richiesto
	 * 
	 * Precondizione: min <= max
	 * 
	 * @throws NumberFormatException Se l'input non e' un numero valido
	 * @return Un numero intero positivo nell'intervallo specificato
	 */
	int getInteger(int min, int max) throws NumberFormatException;
	
	/**
	 * Acquisisce una stringa, eliminando gli spazi bianchi a destra del primo carattere e a sinistra dell'ultimo.
	 * La stringa restituita non puo' essere vuota.
	 * 
	 * @return La stringa acquisita
	 */
	String getString();
	
}
