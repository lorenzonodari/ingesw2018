package it.unibs.ingesw.dpn.ui;

/**
 * Interfaccia utilizzata dalle classi adibite all'acquisizione di input dall'utente
 */
public interface InputGetter {

	/**
	 * Acquisisce un numero intero nell'intervallo [min, max] (estremi inclusi).
	 * In caso l'input non sia interpretabile come valore intero, o in caso il numero acquisito
	 * non rientri nell'intervallo previsto, il metodo segnala all'utente che c'è stato un errore 
	 * e ripropone l'acquisizione del dato.
	 * 
	 * Precondizione: min <= max
	 * 
	 * @return Un numero intero nell'intervallo specificato
	 */
	public int getInteger(int min, int max);
	
	/**
	 * Acquisisce un numero intero, senza porre vincoli sull'input.
	 * In caso l'input non sia interpretabile come numero intero, il metodo segnala all'utente che c'è stato
	 * un errore e ripropone l'acquisizione del dato.
	 * 
	 * @return Il numero intero acquisito come input
	 */
	public default int getInteger() {
		return getInteger(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Acquisisce un numero in virgola mobile nell'intervallo [min, max] (estremi inclusi).
	 * In caso l'input non sia interpretabile come numero in virgola mobile, o in caso il numero acquisito
	 * non rientri nell'intervallo previsto, il metodo segnala all'utente che c'è stato un errore 
	 * e ripropone l'acquisizione del dato.
	 * 
	 * Precondizione: min <= max
	 * 
	 * @return Un numero in virgola mobile nell'intervallo specificato
	 */
	public float getFloat(float min, float max);

	/**
	 * Acquisisce un numero in virgola mobile, senza porre vincoli sull'input.
	 * In caso l'input non sia interpretabile come numero in virgola mobile, 
	 * il metodo segnala all'utente che c'è stato un errore e ripropone l'acquisizione del dato.
	 * 
	 * @return Il numero in virgola mobile acquisito come input
	 */
	public default float getFloat() {
		return getFloat(Float.MIN_VALUE, Float.MAX_VALUE);
	}
	
	/**
	 * Acquisisce una stringa, eliminando gli spazi bianchi a destra del primo carattere e a sinistra dell'ultimo.
	 * La stringa restituita non puo' essere vuota.
	 * 
	 * @return La stringa acquisita in input
	 */
	public String getString();
	
	/**
	 * Acquisisce una stringa solo se questa "matcha" l'espressione regaolare passata come parametro.
	 * 
	 * @param regex L'espressione regolare da confrontare con la stringa acquisita
	 * @return La stringa in input.
	 */
	public String getMatchingString(String regex);
	
	/**
	 * Acquisisce un valore booleano.
	 * In caso l'input non sia interpretabile come valore booleano, il metodo
	 * segnala all'utente che c'è stato un errore e ripropone l'acquisizione del dato.
	 * 
	 * @return Il valore booleano acquisito
	 */
	public boolean getBoolean();
	
}
