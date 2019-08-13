package it.unibs.ingesw.dpn.ui;

/**
 * Classe che implementa l'interfaccia {@link Action}. <br>
 * Prendendo spunto dalla classica finestra "di dialogo", rappresenta un breve messaggio di testo
 * da visualizzare all'utente.<br>
 * L'esecuzione di questa Action termina dopo la visualizzazione del messaggio e la selezione da parte
 * dell'utente dell'(unica) opzione di avanzamento presente.<br>
 * Si differenzia dalla classe {@link MenuAction} poiché quest'ultima forza 
 * l'utente a rimanere nel menu finché non viene selezionata l'opzione di uscita.
 * <br>
 * Nota: segue il pattern <em>Composite</em> insieme alla classe {@link Action}.
 * 
 * @author Michele Dusi
 *
 */
public class DialogAction implements Action {

	private String message;
	private String option;
	
	private static final String DEFAULT_OPTION = "Avanti";
	
	/**
	 * Costruisce una nuova istanza della classe {@link DialogAction},
	 * dato il messaggio da visualizzare e l'opzione di avanzamento.
	 * 
	 * @param message Il messaggio da visualizzare all'utente
	 * @param option L'opzione di avanzamento che l'utente dovrà selezionare per procedere
	 */
	public DialogAction(String message, String option) {
		// Verifica della precondizione
		if (message == null) {
			throw new IllegalArgumentException("Impossibile istanziare un oggetto DialogAction con parametri nulli");
		}
		
		this.message = message;
		
		if (option == null || option.equals("")) {
			this.option = DEFAULT_OPTION;
		} else {
			this.option = option;
		}
	}
	
	/**
	 * Restituisce il messaggio del dialogo.
	 * 
	 * @return Il messaggio visualizzato dall'utente
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Restituisce l'opzione di avanzamento del dialogo.
	 * 
	 * @return L'opzione di avanzamento preentata all'utente
	 */
	public String getOption() {
		return this.option;
	}
	
	/**
	 * Metodo che sovrascrive il metodo dell'interfaccia {@link Action}.
	 * Provoca la visualizzazione del messaggio e attende che l'utente prema un 
	 * qualunque tasto per avanzare.
	 */
	@Override
	public void execute(UserInterface userInterface) {
		userInterface.getter().getDialogInteraction(this);
	}
	
}
