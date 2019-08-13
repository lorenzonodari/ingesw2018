package it.unibs.ingesw.dpn.ui;

/**
 * Classe che implementa l'interfaccia {@link Action}. <br>
 * Prendendo spunto dalla classica finestra "di conferma", rappresenta un'azione per cui è
 * necessaria la conferma dell'utente. Le due opzioni presentate sono di default:
 * <ul>
 * 	<li> Conferma </li>
 * 	<li> Annulla </li>
 * </ul>
 * Se confermata, procede poi all'esecuzione dell'azione {@link Action} a cui 
 * fa riferimento. <br>
 * L'esecuzione di questa Action termina dopo l'esecuzione (eventuale) dell'azione a cui 
 * fa riferimento. Si differenzia dalla classe {@link MenuAction} poiché quest'ultima forza 
 * l'utente a rimanere nel menu finché non viene selezionata l'opzione di uscita.
 * <br>
 * Nota: segue il pattern <em>Composite</em> insieme alla classe {@link Action}.
 * 
 * @author Michele Dusi
 *
 */
public class ConfirmAction implements Action {
	
	private String message;
	private Action referredAction;
	private OptionStrings options;
	
	/**
	 * Enum per la personalizzazione delle due opzioni.
	 */
	public static enum OptionStrings {
		CONFIRM_CANCEL_OPTIONS ("Conferma", "Annulla"),
		YES_NO_OPTIONS ("Sì", "No");

		private String confirmString;
		private String cancelString;
		
		/** Crea un oggetto OptionStrings. Costruttore privato. */
		private OptionStrings(String confirmString, String cancelString) {
			this.confirmString = confirmString;
			this.cancelString = cancelString;
		}

		/** Restituisce la stringa per eseguire l'azione. */
		public String getConfirmString() {
			return this.confirmString;
		}
		
		/** Restituisce la stringa per annullare l'azione. */
		public String getCancelString() {
			return this.cancelString;
		}
	}
	
	/**
	 * Costruttore della classe che richiede un'azione prima della quale
	 * verrà chiesta all'utente la conferma.
	 * 
	 * Precondizione: message != null && message != ""
	 * Precondizione: actionToConfirm != null
	 * 
	 * @param message Il messaggio da presentare per la conferma
	 * @param actionToConfirm L'azione da "inglobare" nella conferma
	 */
	public ConfirmAction(String message, Action actionToConfirm) {
		// Verifica delle precondizioni
		if (message == null || message.equals("") || actionToConfirm == null) {
			throw new IllegalArgumentException("Impossibile istanziare un oggetto ConfirmAction con parametri nulli");
		}
		
		this.message = message;
		this.referredAction = actionToConfirm;
		this.options = OptionStrings.CONFIRM_CANCEL_OPTIONS;
	}
	
	/**
	 * Imposta le stringhe da utilizzare come opzioni dicotomiche per 
	 * la visualizzazione ed esecuzione di questa ConfirmAction.<br>
	 * Il parametro richiesto è un'enumerazione che può essere trovata come
	 * attributo di classe di {@link ConfirmAction}.
	 * 
	 * Precondizione: options != null
	 * 
	 * @param options Le opzioni da usare.
	 */
	public void setOptionStrings(OptionStrings options) {
		// Verifico le precondizioni
		if (options == null) {
			throw new IllegalArgumentException("Impossibile impostare le opzioni con un parametro nullo");
		}
		this.options = options;
	}
	
	/**
	 * Restituisce il messaggio da presentare prima delle opzioni.
	 * 
	 * @return Il messaggio di questa azione di conferma
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Restituisce la stringa di conferma.
	 * 
	 * @return La stringa di conferma
	 */
	public String getConfirmString() {
		return this.options.getConfirmString();
	}

	/**
	 * Restituisce la stringa di cancella.
	 * 
	 * @return La stringa di cancella
	 */
	public String getCancelString() {
		return this.options.getCancelString();
	}

	/**
	 * Esegue l'azione associata a questa {@link ConfirmAction}.
	 * Questo comprende:
	 * <ul>
	 * 	<li>Visualizzare il prompt di conferma</li>
	 * 	<li>Richiedere all'utente la selezione di un'opzione mediante l'interfaccia utente</li>
	 * 	<li>Eseguire ricorsivamente l'azione selezionata se viene selezionata l'opzione di conferma.<br>
	 * 		Altrimenti esegue un'azione semplice nulla (trovata in {@link SimpleAction}).</li>
	 * </ul>
	 * 
	 * @param userInterface L'interfaccia utente
	 */
	@Override
	public void execute(UserInterface userInterface) {
		boolean selection = userInterface.getter().getConfirmChoice(this);
		if (selection) {
			this.referredAction.execute(userInterface);
		} else {
			SimpleAction.EMPTY_ACTION.execute(userInterface);
		}
	}

}
