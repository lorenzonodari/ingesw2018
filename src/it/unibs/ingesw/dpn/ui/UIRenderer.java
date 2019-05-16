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
	
//	/**
//	 * Metodo adibito al rendering di un componente grafico dedicato alla richiesta
//	 * di informazioni all'utente.
//	 * 
//	 * @param question La richiesta da porre all'utente
//	 */
//	void renderPrompt(String question);
	
	/**
	 * Renderizza uno spazio vuoto orizzontale.
	 * Nelle interfacce testuali stampa una riga vuota.
	 */
	void renderLineSpace();

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
