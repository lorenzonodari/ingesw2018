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
	 * Metodo adibito al rendering di un componente grafico dedicato alla richiesta
	 * di informazioni all'utente
	 * 
	 * @param question La richiesta da porre all'utente
	 */
	void renderPrompt(String question);
}
