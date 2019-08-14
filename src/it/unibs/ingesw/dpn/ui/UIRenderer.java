package it.unibs.ingesw.dpn.ui;

import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;

/**
 * Interfaccia utilizzata per caratterizzare un oggetto in grado di dare una
 * rappresentazione grafica ai menu dell'interfaccia utente
 */
public interface UIRenderer {

	/**
	 * Metodo adibito al rendering dell'intero menu.
	 * 
	 * @param menu Il menu da renderizzare
	 */
	void renderMenu(MenuAction menu);

	/**
	 * Metodo adibito al rendering di un prompt di conferma.
	 * 
	 * @param confirm Il prompt di conferma da renderizzare
	 */
	void renderConfirm(ConfirmAction confirm);
	
	/**
	 * Metodo adibito al rendering di una finestra di dialogo.
	 *
	 * @param dialog Il prompt di dialogo da renderizzare
	 */
	void renderDialog(DialogAction dialog);

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
	 * Metodo che visualizza un testo passato come parametro.
	 * Al termine della visualizzazione va a capo automaticamente.
	 * 
	 * @param text Il testo da visualizzare
	 */
	void renderText(String text);

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
	 * Renderizza un testo con intorno una cornice.
	 * Il testo può essere lungo a piacere, può contenere caratteri di "newline" che il metodo
	 * gestirà in maniera automatica. In caso una riga di testo avesse una lunghezza superiore al valore
	 * massimo di default, questa verrà renderizzata su più linee.
	 * <br><br>
	 * Questo metodo equivale al corrispondente metodo <code>renderLongTextInFrame(String, int)</code>
	 * ma dove il parametro sulla lunghezza è impostato di default.
	 * 
	 * @param text Il testo da renderizzare
	 */
	public void renderLongTextInFrame(String text);
	
	/**
	 * Renderizza un testo con intorno una cornice.
	 * Il testo può essere lungo a piacere, può contenere caratteri di "newline" che il metodo
	 * gestirà in maniera automatica. In caso una riga di testo avesse una lunghezza superiore al valore di 
	 * "maxLenght", questa verrà spezzata (con "\n") al primo spazio (" ") disponibile.
	 * 
	 * Nota: il parametro maxLenght NON indica l'ampiezza massima della cornice, bensì la massima lunghezza 
	 * (in numero di caratteri) di una singola riga di testo, esclusi appunto i caratteri necessari per la 
	 * visualizzazione grafica.
	 * 
	 * @param text Il testo da renderizzare
	 * @param maxLenght La massima lunghezza di una riga di testo
	 */
	public void renderLongTextInFrame(String text, int maxLenght);
	
	/**
	 * Metodo che visualizza un testo di errore passato come parametro.
	 * 
	 * @param errorText Il messaggio d'errore da visualizzare
	 */
	public void renderError(String errorText);

}
