package it.unibs.ingesw.dpn.ui;

/**
 * Interfaccia utilizzata per caratterizzare un oggetto in grado di dare una
 * rappresentazione grafica ai menu dell'interfaccia utente
 */
public interface UIRenderer {

	/**
	 * Metodo adibito al rendering dell'intero menu
	 * 
	 * @param menu Il menu da renderizzare
	 */
	void renderMenu(Menu menu);
	
	/**
	 * Metodo adibito al rendering di una singola voce del menu
	 * 
	 * @param entry La voce del menu' da renderizzare
	 */
	void renderMenuEntry(MenuEntry entry);

	/**
	 * Renderizza un basilare prompt per indicare all'utente l'acquisizione di un dato.
	 */
	void renderEmptyPrompt();
	
	/**
	 * Renderizza uno spazio vuoto orizzontale.
	 * Nelle interfacce testuali stampa una riga vuota.
	 */
	void renderLineSpace();
	
	/**
	 * Renderizza un testo con intorno una cornice.
	 * Utilizzato solitamente per titoli o testi particolarmente importanti.
	 * 
	 * Precondizione: il testo NON deve contenere caratteri speciali come "\n" o "\t".
	 * In altre parole, deve essere contenuto in un'unica linea compatta.
	 * 
	 * @param text Il testo da renderizzare
	 */
	public void renderTextInFrame(String text);

	/**
	 * Metodo che visualizza un testo passato come parametro.
	 * Al termine della visualizzazione va a capo automaticamente.
	 * 
	 * @param text Il testo da visualizzare
	 */
	void renderText(String text);
	
	/**
	 * Metodo che visualizza un testo di errore passato come parametro.
	 * 
	 * @param errorText Il messaggio d'errore da visualizzare
	 */
	void renderError(String errorText);

}
