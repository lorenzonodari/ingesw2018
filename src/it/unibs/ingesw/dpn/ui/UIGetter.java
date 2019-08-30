package it.unibs.ingesw.dpn.ui;

import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;
import it.unibs.ingesw.dpn.ui.actions.MenuEntry;

/**
 * Interfaccia utilizzata dalle classi adibite all'acquisizione di input dall'utente
 */
public interface UIGetter {

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
	
	/**
	 * Dopo aver presentato le opzioni presenti nel menu, richiede all'utente di selezionarne una.
	 * Se la selezione è stata effettuata correttamente, restituisce l'azione associata a tale opzione.
	 * 
	 * Nota: La visualizzazione del menu <b>è compresa</b> nella chiamata di questo metodo.
	 * 
	 * @param menu Il menu da cui attingere 
	 * @return L'azione corrispondente all'opzione selezionata
	 */
	public MenuEntry getMenuChoice(MenuAction menu);

	/**
	 * Presenta un prompt di conferma e richiede all'utente di selezionare una delle due opzioni.<br>
	 * Restituisce "true" se viene selezionata l'opzione di conferma, "false" altrimenti.
	 * 
	 * @param confirm Il menu di conferma da cui attingere
	 * @return Il boolean logicamente associato alla conferma o all'annullamento
	 */
	public boolean getConfirmChoice(ConfirmAction confirm);

	/**
	 * Visualizza un prompt di dialogo e attende un inserimento generico da parte dell'utente.
	 * 
	 * @param dialogAction La finestra di dialogo da visualizzare
	 */
	public void getDialogInteraction(DialogAction dialogAction);
	
}
