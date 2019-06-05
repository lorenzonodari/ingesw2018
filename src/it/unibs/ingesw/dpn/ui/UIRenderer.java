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
	void renderError(String errorText);

}
