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
 * In caso contrario, non effettua azioni a meno che non venga impostata un'azione di "uscita".<br>
 * L'esecuzione di questa Action termina dopo l'esecuzione dell'azione a cui 
 * fa riferimento o dell'eventuale azione di uscita. 
 * Si differenzia dalla classe {@link MenuAction} poiché quest'ultima forza 
 * l'utente a rimanere nel menu finché non viene selezionata l'opzione di uscita.
 * <br>
 * Nota: segue il pattern <em>Composite</em> insieme alla classe {@link Action}.
 * 
 * @author Michele Dusi
 *
 */
public class ConfirmAction implements Action {
	
	private String message;
	private Action confirmAction;
	private Action cancelAction;
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
		this.confirmAction = actionToConfirm;
		this.cancelAction = Action.EMPTY_ACTION; // Azione vuota di default
		this.options = OptionStrings.CONFIRM_CANCEL_OPTIONS;
	}
	
	/**
	 * Permette di impostare un'azione da eseguire quando viene selezionata l'opzione di "uscita".<br>
	 * Anche in questo caso, come nel caso dell'azione di conferma, l'esecuzione provoca la terminazione; 
	 * in altre parole -a differenza della classe {@link MenuAction}- non viene eseguito alcun ciclo che ripropone
	 * le opzioni finché non si seleziona quella di uscita.
	 * 
	 * @param cancelAction L'azione da eseguire in caso di annullamento
	 */
	public void setCancelAction(Action cancelAction) {
		// Verifica delle precondizioni
		if (cancelAction == null) {
			throw new IllegalArgumentException("Impossibile impostare un'azione nulla");
		}
		
		this.cancelAction = cancelAction;
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
			this.confirmAction.execute(userInterface);
		} else {
			this.cancelAction.execute(userInterface);
		}
	}

}
