package it.unibs.ingesw.dpn.ui;

import java.util.ArrayList;
import java.util.List;

import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;
import it.unibs.ingesw.dpn.ui.actions.MenuEntry;

/**
 * Implementazione dell'interfaccia UIRenderer utilizzata per realizzare un'interfaccia
 * utente da riga di comando.
 */
public class TextRenderer implements UIRenderer {
	
	private static final String OPTION_FORMAT = "%2d - %s\n";
	
	private static final String CLI_PROMPT = ">> ";
	private static final String ERROR_PREFIX = "ERRORE: ";
	
	private static final int HORIZ_PADDING = 3;
	private static final int MAX_WIDTH = 100;
	
	private static final char [] BOX_DRAWING_CHARS_DOUBLE = {'═', '║', '╝', '╚', '╗', '╔'};
	private static final char [] BOX_DRAWING_CHARS_SINGLE = {'─', '│', '┘', '└', '┐', '┌'};
	private static final char T_EAST = '╟';
	private static final char T_WEST = '╢';
	
	/** Stili di default. */
	private static final CharStyle DEFAULT_FRAME_STYLE = CharStyle.SINGLE;
	private static final CharStyle DEFAULT_CONFIRM_STYLE = CharStyle.SINGLE;
	private static final CharStyle DEFAULT_DIALOG_STYLE = CharStyle.SINGLE;
	
	/**
	 * Rappresenta un pezzo di cornice.
	 */
	private static enum CharFrame {
		HORIZONTAL,
		VERTICAL,
		NORTH_WEST,
		NORTH_EAST,
		SOUTH_WEST,
		SOUTH_EAST
	}
	
	/**
	 * Rappresenta uno stile di cornice.
	 */
	public static enum CharStyle {
		SINGLE (BOX_DRAWING_CHARS_SINGLE),
		DOUBLE (BOX_DRAWING_CHARS_DOUBLE);
		
		private char [] charSet;
		
		private CharStyle(char [] charSet) {
			this.charSet = charSet;
		}
		
		/**
		 * Restituisce il carattere di cornice selezionato.
		 * 
		 * @param framePiece Il pezzo di cornice da ottenere
		 * @return Il pezzo di cornice dello stile corrispondente
		 */
		public char get(CharFrame framePiece) {
			return this.charSet[framePiece.ordinal()];
		}
		
	}

	/**
	 * Metodo adibito al rendering dell'intero menu.
	 * 
	 * @param menu Il menu da renderizzare
	 */
	@Override
	public void renderMenu(MenuAction menu) {
		
		// Costruisco la stringa totale
		StringBuffer result = new StringBuffer();
		
	// Preparo il contenuto dell'intestazione
		String titleLine = addHorizontalPadding(menu.getTitle());
		
	// Preparo il corpo
		StringBuffer body = new StringBuffer("\n");
		// Se la descrizione è presente, la aggiungo
		if (!menu.getDescription().equals("")) {
			body.append(menu.getDescription() + "\n\n");
		}
		// Aggiungo le opzioni
		List<MenuEntry> entries = menu.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			body.append(toStringMenuEntry(entries.get(i), (i + 1)));
		}
		// Aggiungo l'opzione finale
		body.append(toStringMenuEntry(menu.getBackEntry(), 0));
		body.append(" "); // Aggiungo una riga vuota finale
		
	// Separo il corpo in linee
		List<String> bodyLines = getStringLines(body.toString(), MAX_WIDTH);
		
	// Ricavo i parametri
		int titleWidth = titleLine.length() + 2;		// Il "2" + dato da i due caratteri esterni della cornice
		int bodyTextWidth = getMaxLength(bodyLines);
		int bodyWidth = bodyTextWidth + 2 * HORIZ_PADDING + 2;
		// Verifico che la larghezza del corpo abbia una larghezza minima superiore all'intestazione
		if (bodyWidth < titleWidth + 2) {
			bodyWidth = titleWidth + 2;
			bodyTextWidth = bodyWidth - (2 * HORIZ_PADDING + 2);
		}
		int bodyOverwidth = bodyWidth - titleWidth - 2;
		
		// Stile del menu
		CharStyle titleStyle = CharStyle.DOUBLE;
		CharStyle boxStyle = CharStyle.SINGLE;
		
		// Costruisco il bordo superiore
		result.append(" " + getFrameTopLine(titleWidth, titleStyle) + "\n");
		
		// Costruisco la riga centrale
		result.append(boxStyle.get(CharFrame.SOUTH_EAST));
		result.append(T_WEST);
		result.append(titleLine);
		result.append(T_EAST);
		result.append(iterateChar(
				boxStyle.get(CharFrame.HORIZONTAL), 
				bodyOverwidth));
		result.append(boxStyle.get(CharFrame.SOUTH_WEST));
		result.append("\n");
		
		// Costruisco la riga di chiusura del titolo
		result.append(boxStyle.get(CharFrame.VERTICAL));
		result.append(getFrameBottomLine(titleLine.length() + 2, titleStyle));
		result.append(iterateChar(' ', bodyOverwidth));
		result.append(boxStyle.get(CharFrame.VERTICAL));
		result.append("\n");
		
		// RIghe centrali del corpo del menu
		for (String line : bodyLines) {
			result.append(getFrameMiddleLine(bodyWidth, boxStyle, line));
			result.append("\n");
		}
		
		// Riga di chiusura
		result.append(getFrameBottomLine(bodyWidth, boxStyle));
		
		// Stampo tutto
		System.out.println(result.toString());
		
	}

	/**
	 * Metodo adibito al rendering di un prompt di conferma
	 * 
	 * @param confirm Il prompt di conferma da renderizzare
	 */
	@Override
	public void renderConfirm(ConfirmAction confirm) {
		
		// Costruisco la stringa totale
		StringBuffer result = new StringBuffer();
		
		// Messaggio
		result.append("\n" + confirm.getMessage() + "\n\n");
		// Aggiungo le opzioni
		result.append(toStringMenuEntry(confirm.getConfirmString(), 1));
		result.append(toStringMenuEntry(confirm.getCancelString(), 0));
		
		result.append(" "); // Aggiungo una riga vuota finale
	
		this.renderLongTextInFrame(result.toString(), MAX_WIDTH, DEFAULT_CONFIRM_STYLE);
		
	}

	/**
	 * Metodo adibito al rendering di una finestra di dialogo.
	 *
	 * @param dialog Il prompt di dialogo da renderizzare
	 */
	@Override
	public void renderDialog(DialogAction dialog) {
		StringBuffer text = new StringBuffer(" \n" + dialog.getMessage());
		text.append("\n\n");
		text.append(">> " + dialog.getOption());
		text.append(" [premi INVIO]\n ");
		
		this.renderLongTextInFrame(text.toString(), MAX_WIDTH, DEFAULT_DIALOG_STYLE);
	}
	
	/**
	 * Restituisce una stringa con la spaziatura laterale corretta.
	 * Nota: in caso la stringa abbia già degli spazi all'inizio o alla fine, essi NON verranno eliminati.
	 * 
	 * @param text La stringa di testo
	 * @return La stringa con la spaziatura corretta a lato
	 */
	private String addHorizontalPadding(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Impossibile accettare una stringa nulla");
		}
		
		StringBuffer s = new StringBuffer();
		s.append(iterateChar(' ', HORIZ_PADDING));
		s.append(text);
		s.append(iterateChar(' ', HORIZ_PADDING));
		return s.toString();
	}

	/**
	 * Restituisce la stringa che rappresenta la cornice superiore di una "box",
	 * formata con il set di caratteri specificato.
	 * 
	 * Precondizione: la larghezza "width" deve essere almeno 2.
	 * 
	 * @param width La larghezza della cornice, compresi i due angoli
	 * @param style Lo stile di caratteri da utilizzare
	 * @return
	 */
	private String getFrameTopLine(int width, CharStyle style) {
		if (width < 2) {
			throw new IllegalArgumentException("Impossibile creare una cornice con larghezza inferiore a 2");
		}
		
		StringBuffer s = new StringBuffer();
		s.append(style.get(CharFrame.SOUTH_EAST));
		s.append(iterateChar(style.get(CharFrame.HORIZONTAL), width - 2));
		s.append(style.get(CharFrame.SOUTH_WEST));
		// Restituisco il valore
		return s.toString();
	}
	
	/**
	 * Restituisce la stringa che rappresenta la cornice inferiore di una "box",
	 * formata con il set di caratteri specificato.
	 * 
	 * Precondizione: la larghezza "width" deve essere almeno 2.
	 * 
	 * @param width La larghezza della cornice, compresi i due angoli
	 * @param style Lo stile di caratteri da utilizzare
	 * @return La stringa formattata correttamente
	 */
	private String getFrameBottomLine(int width, CharStyle style) {
		if (width < 2) {
			throw new IllegalArgumentException("Impossibile creare una cornice con larghezza inferiore a 2");
		}
		
		StringBuffer s = new StringBuffer();
		s.append(style.get(CharFrame.NORTH_EAST));
		s.append(iterateChar(style.get(CharFrame.HORIZONTAL), width - 2));
		s.append(style.get(CharFrame.NORTH_WEST));
		// Restituisco il valore
		return s.toString();
	}
	
	/**
	 * Restituisce una linea "di mezzo" di una cornice, eventualmente con testo.
	 * 
	 * Precondizione: Il testo deve avere una lunghezza sufficiente per stare nella cornice, comprensiva di padding.
	 * 
	 * @param width La larghezza delle "box" (comprensiva di estremi)
	 * @param style Lo stile della "box"/cornice
	 * @param text Il testo da inserire
	 * @return La stringa rappresentante la linea
	 */
	private String getFrameMiddleLine(int width, CharStyle style, String text) {
		// Calcolo la dimensione massima che può prendere il testo
		int maxTextWidth = width - HORIZ_PADDING * 2 - 2;
		// Eventualmente, in caso di testo nullo, visualizzo una riga vuota
		if (text == null) {
			text = "";
		}
		// Verifica delle precondizioni
		if (text.trim().length() > maxTextWidth) {
			throw new IllegalArgumentException("Dimensione del testo eccessiva");
		}
		
		StringBuffer s = new StringBuffer();
		s.append(style.get(CharFrame.VERTICAL));
		s.append(iterateChar(' ', HORIZ_PADDING));
		s.append(String.format("%-" + maxTextWidth + "s", text.trim()));
		s.append(iterateChar(' ', HORIZ_PADDING));
		s.append(style.get(CharFrame.VERTICAL));
		
		return s.toString();		
	}
	
	/**
	 * Restituisce una stringa data dalla concatenazione di simboli uguali.
	 * 
	 * Precondizione: il numero di ripetizioni deve essere positivo o al più nullo.
	 * In caso sia "0", viene restituita la stringa vuota.
	 * 
	 * @param symbol Il simbolo da ripetere e concatenare.
	 * @param times Il numero di volte che il simbolo deve essere ripetuto.
	 * @return
	 */
	private String iterateChar(char symbol, int times) {
		if (times < 0) {
			throw new IllegalArgumentException("Impossibile interpretare una quantità negativa");
		}
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < times; i++) {
			s.append(symbol);
		}
		return s.toString();
	}

	/**
	 * Restituisce la stringa contenente il testo di una entry, più il numero iniziale.
	 * 
	 * @param entry L'opzione da renderizzare
	 * @param index Il numero associato all'opzione
	 * @return La stringa che rappresenta l'opzione
	 */
	private String toStringMenuEntry(MenuEntry entry, int index) {
		return String.format(
				OPTION_FORMAT,
				index,
				entry.getName());
	}
	
	/**
	 * Restituisce la stringa contenente un testo più il numero iniziale.
	 * 
	 * @param message Il testo da renderizzare
	 * @param index Il numero associato all'opzione
	 * @return La stringa che rappresenta l'opzione
	 */
	private String toStringMenuEntry(String option, int index) {
		return String.format(
				OPTION_FORMAT,
				index,
				option);
	}

	@Override
	public void renderEmptyPrompt() {
		System.out.print(CLI_PROMPT);
	}
	
	@Override
	public void renderLineSpace() {
		System.out.println();
	}
	
	/**
	 * Separa un testo lungo, passato come parametro in un'unica stringa, in più sotto-stringhe
	 * di una certa lunghezza massima.
	 * 
	 * @param text Il testo totale
	 * @param maxLenght La lunghezza massima di ogni linea
	 * @return La lista di tutte le linee
	 */
	private List<String> getStringLines(String text, int maxLenght) {
		// Spezzo la stringa di testo in più sottostringhe.
		List<String> textLines = new ArrayList<>();
		for (String sentence : text.split("\n")) {
			// Verifico se la stringa rientra nella dimensione massima
			if (sentence.length() <= maxLenght) {
				// In tal caso è già pronta per essere aggiunta
				textLines.add(sentence);
			} else {
				// In caso contrario, identifico la posizione migliore per andare a capo
				StringBuffer buffer = new StringBuffer();
				for (String word : sentence.split(" ")) {
					// Verifico se la parola può rientrare nella riga
					if (buffer.length() + word.length() < maxLenght) {
						// In caso affermativo, la aggiungo alla riga che sto costruendo pezzo per pezzo
						buffer.append(word + " ");
					} else {
						// Altrimenti, la riga era già pronta.
						// La aggiungo alla lista di righe
						textLines.add(buffer.toString());
						// Resetto il buffer, aggiungendo la parola appena trovata
						buffer = new StringBuffer(word + " ");
						
						/* TODO : al momento il metodo si incasina se ho una parola più lunga 
						 * del massimo numero di caratteri per riga. Tuttavia è un caso 
						 * talmente specifico che dubito accada.
						 */
					}
				}
				// Aggiungo tutte le parole rimanenti
				textLines.add(buffer.toString());
			}
		}
		return textLines;
	}
	
	/**
	 * Restituisce la lunghezza massima delle stringhe contenute in una lista.
	 * 
	 * @param strings La lista di stringhe
	 * @return La massima lunghezza
	 */
	private int getMaxLength(List<String> strings) {
		if (strings == null || strings.isEmpty()) {
			throw new IllegalArgumentException("Impossibile analizzare una lista vuota o nulla di stringhe.");
		}
		return strings
				.stream()
				.sorted((s1, s2) -> {return s2.length() - s1.length();})
				.findFirst()
				.get()
				.length();
	}

	/**
	 * Stampa il testo su una linea vuota.
	 * 
	 * @param Il testo da visualizzare
	 */
	@Override
	public void renderText(String text) {
		System.out.println(text);		
	}

	/**
	 * Renderizza un testo con intorno una cornice.
	 * Utilizzato solitamente per titoli o testi particolarmente importanti.
	 * 
	 * Precondizione: il testo NON deve contenere caratteri speciali come "\n" o "\t".
	 * In altre parole, deve essere contenuto in un'unica linea compatta.
	 * 
	 * @param text Il testo da renderizzare
	 */
	@Override
	public void renderTextInFrame(String text) {
		this.renderTextInFrame(text, DEFAULT_FRAME_STYLE);
	}
	
	/**
	 * Stampa il testo in una cornice con stile predefinito.
	 * La cornice NON ha una lunghezza massima, ma si adatta al testo.
	 * Il testo NON deve contenere caratteri speciali come "\n" o "\t", ma <em>deve essere contenuto su un'unica linea</em>.
	 * 
	 * @param text Il testo da visualizzare
	 * @param style Lo stile della cornice
	 */
	private void renderTextInFrame(String text, CharStyle style) {
		// Calcolo delle dimensioni
		int frameWidth = text.trim().length() + HORIZ_PADDING * 2 + 2;
		// Costruisco la stringa finale
		StringBuffer result = new StringBuffer();
		
		// Costruisco la cornice, aggregando tre linee di testo 
		result.append(getFrameTopLine(frameWidth, style));
		result.append("\n");
		result.append(getFrameMiddleLine(frameWidth, style, text.trim()));
		result.append("\n");
		result.append(getFrameBottomLine(frameWidth, style));
		
		// Stampo tutto
		System.out.println(result.toString());
	}

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
	@Override
	public void renderLongTextInFrame(String text) {
		this.renderLongTextInFrame(text, MAX_WIDTH);
	}

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
	@Override
	public void renderLongTextInFrame(String text, int maxLenght) {
		this.renderLongTextInFrame(text, maxLenght, DEFAULT_FRAME_STYLE);
	}
	
	/**
	 * Renderizza un testo in una finestra di dimensione massima prefissata,
	 * con lo stile predefinito.
	 * 
	 * @param text Il testo da renderizzare
	 * @param maxLenght La massima lunghezza di una riga di testo
	 * @param style Lo stile della finestra
	 */
	private void renderLongTextInFrame(String text, int maxLenght, CharStyle style) {
		// Spezzo il testo in linee di una certa massima lunghezza
		List<String> textLines = getStringLines(text, maxLenght);
		
		// A questo punto textLines contiene tutte le righe perfettamente formate
		// Ne calcolo la massima lunghezza
		int textWidth = getMaxLength(textLines);
		int boxWidth = textWidth + HORIZ_PADDING * 2 + 2;
		
		// Costruisco la stringa finale
		StringBuffer result = new StringBuffer();
		
		// Cornice superiore
		result.append(getFrameTopLine(boxWidth, style));
		result.append("\n");

		// Costruisco le righe centrali
		for (String line : textLines) {
			result.append(getFrameMiddleLine(boxWidth, style, line.trim()));
			result.append("\n");
		}
		
		// Cornice inferiore
		result.append(getFrameBottomLine(boxWidth, style));
		
		// Stampo tutto
		System.out.println(result.toString());
	}

	@Override
	public void renderError(String errorText) {
		System.out.println(ERROR_PREFIX + errorText);		
	}

}
