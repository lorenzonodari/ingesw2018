package it.unibs.ingesw.dpn.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione dell'interfaccia UIRenderer utilizzata per realizzare un'interfaccia
 * utente da riga di comando.
 */
public class TextRenderer implements UIRenderer {
	
	private static final String CLI_PROMPT = ">> ";
	private static final String ERROR_PREFIX = "ERRORE: ";
	private static final String ENTRY_NUM_SEPARATOR = " - ";
	
	private static final int HORIZ_PADDING = 3;
	private static final int MAX_WIDTH = 200;

	private static final char [] BOX_DRAWING_CHARS_BOLD = {'═', '║', '╔', '╗', '╚', '╝'};
	private static final char [] BOX_DRAWING_CHARS_SINGLE = {'─', '│', '┌', '┐', '└', '┘'};
	private static final char T_EAST = '╟';
	private static final char T_WEST = '╢';
	private static final int HORIZ = 0;
	private static final int VERTI = 1;
	private static final int NO_WE = 2;
	private static final int NO_EA = 3;
	private static final int SO_WE = 4;
	private static final int SO_EA = 5;

	@Override
	public void renderMenu(Menu menu) {
		
		// Costruisco la stringa totale
		StringBuffer result = new StringBuffer();
		
	// Preparo il contenuto dell'intestazione
		String titleLine = addHorizPadding(menu.getTitle());
		
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
		body.append(toStringMenuEntry(menu.getQuitEntry(), 0));
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
		
		
		// Costruisco il bordo superiore
		result.append(" " + getTopFrame(titleWidth, BOX_DRAWING_CHARS_BOLD) + "\n");
		
		// Costruisco la riga centrale
		result.append(BOX_DRAWING_CHARS_SINGLE[NO_WE]);
		result.append(T_WEST);
		result.append(titleLine);
		result.append(T_EAST);
		result.append(iterateChar(
				BOX_DRAWING_CHARS_SINGLE[HORIZ], 
				bodyOverwidth));
		result.append(BOX_DRAWING_CHARS_SINGLE[NO_EA]);
		result.append("\n");
		
		// Costruisco la riga di chiusura del titolo
		result.append(BOX_DRAWING_CHARS_SINGLE[VERTI]);
		result.append(getBottomFrame(titleLine.length() + 2, BOX_DRAWING_CHARS_BOLD));
		result.append(iterateChar(' ', bodyOverwidth));
		result.append(BOX_DRAWING_CHARS_SINGLE[VERTI]);
		result.append("\n");
		
		for (String line : bodyLines) {
			result.append(BOX_DRAWING_CHARS_SINGLE[VERTI]);
			result.append(addHorizPadding(String.format("%-" + bodyTextWidth + "s", line)));
			result.append(BOX_DRAWING_CHARS_SINGLE[VERTI]);
			result.append("\n");
		}
		
		result.append(getBottomFrame(bodyWidth, BOX_DRAWING_CHARS_SINGLE));
		
		System.out.println(result.toString());
		
		
	}
	
	/**
	 * Restituisce una stringa con la spaziatura laterale corretta.
	 * Nota: in caso la stringa abbia già degli spazi all'inizio o alla fine, essi NON verranno eliminati.
	 * 
	 * @param text La stringa di testo
	 * @return La stringa con la spaziatura corretta a lato
	 */
	private String addHorizPadding(String text) {
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
	 * @param charSet
	 * @return
	 */
	private String getTopFrame(int width, char [] charSet) {
		if (width < 2) {
			throw new IllegalArgumentException("Impossibile creare una cornice con larghezza inferiore a 2");
		}
		
		StringBuffer s = new StringBuffer();
		s.append(charSet[NO_WE]);
		s.append(iterateChar(charSet[HORIZ], width - 2));
		s.append(charSet[NO_EA]);
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
	 * @param charSet Il set di caratteri da utilizzare
	 * @return La stringa formattata correttamente
	 */
	private String getBottomFrame(int width, char [] charSet) {
		if (width < 2) {
			throw new IllegalArgumentException("Impossibile creare una cornice con larghezza inferiore a 2");
		}
		
		StringBuffer s = new StringBuffer();
		s.append(charSet[SO_WE]);
		s.append(iterateChar(charSet[HORIZ], width - 2));
		s.append(charSet[SO_EA]);
		// Restituisco il valore
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
	 * @return
	 */
	private String toStringMenuEntry(MenuEntry entry, int index) {
		return String.format(
				"%2d%s%s\n",
				index,
				ENTRY_NUM_SEPARATOR,
				entry.getName());
	}

	@Override
	public void renderEmptyPrompt() {
		System.out.print(CLI_PROMPT);
	}
	
	@Override
	public void renderLineSpace() {
		System.out.println();
	}

	@Override
	public void renderText(String text) {
		System.out.println(text);		
	}
	
//	@Override
//	public void renderTextInFrame(String text) {
//		int len = text.trim().length();
//		StringBuffer line = new StringBuffer();
//		for (int i = 0; i < len + 8; i++) {
//			line.append(FRAME_CHAR);
//		}
//		// Stampa la cornice e il testo
//		System.out.println(line.toString());
//		System.out.println("||  " + text.trim() + "  ||");
//		System.out.println(line.toString());
//		
//	}
	
	@Override
	public void renderTextInFrame(String text) {
		int len = text.trim().length();
		StringBuffer orizLine = new StringBuffer();
		for (int i = 0; i < len + HORIZ_PADDING * 2; i++) {
			orizLine.append(BOX_DRAWING_CHARS_BOLD[HORIZ]);
		}
		// Costruisco la stringa finale
		StringBuffer result = new StringBuffer();
		
		// Costruisco la prima riga, la cornice orizzontale di apertura
		result.append(BOX_DRAWING_CHARS_BOLD[NO_WE]);
		result.append(orizLine.toString());
		result.append(BOX_DRAWING_CHARS_BOLD[NO_EA]);
		result.append("\n");
		
		// Costruisco la riga centrale
		result.append(BOX_DRAWING_CHARS_BOLD[VERTI]);
		for (int i = 0; i < HORIZ_PADDING; i++) { result.append(" "); }
		result.append(text.trim());
		for (int i = 0; i < HORIZ_PADDING; i++) { result.append(" "); }
		result.append(BOX_DRAWING_CHARS_BOLD[VERTI]);
		result.append("\n");
		
		// Costruisco la riga di chiusura
		result.append(BOX_DRAWING_CHARS_BOLD[SO_WE]);
		result.append(orizLine.toString());
		result.append(BOX_DRAWING_CHARS_BOLD[SO_EA]);
		
		// Stampo tutto
		System.out.println(result.toString());
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
						buffer = new StringBuffer(word);
						
						// TODO : al momento il metodo si incasina se ho una parola più lunga del massimo numero di caratteri per riga
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
	
	@Override
	public void renderLongTextInFrame(String text, int maxLenght) {
		// Spezzo il testo in linee di una certa massima lunghezza
		List<String> textLines = getStringLines(text, maxLenght);
		
		// A questo punto textLines contiene tutte le righe perfettamente formate
		// Ne calcolo la massima lunghezza
		int textWidth = getMaxLength(textLines);

		StringBuffer orizLine = new StringBuffer();
		for (int i = 0; i < textWidth + HORIZ_PADDING * 2; i++) {
			orizLine.append(BOX_DRAWING_CHARS_BOLD[HORIZ]);
		}
		// Costruisco la stringa finale
		StringBuffer result = new StringBuffer();
		
		// Costruisco la prima riga, la cornice orizzontale di apertura
		result.append(BOX_DRAWING_CHARS_BOLD[NO_WE]);
		result.append(orizLine.toString());
		result.append(BOX_DRAWING_CHARS_BOLD[NO_EA]);
		result.append("\n");

		// Costruisco le righe centrali
		for (String line : textLines) {
			result.append(BOX_DRAWING_CHARS_BOLD[VERTI]);
			for (int i = 0; i < HORIZ_PADDING; i++) { result.append(" "); }
			result.append(String.format("%-" + textWidth + "s", line.trim()));
			for (int i = 0; i < HORIZ_PADDING; i++) { result.append(" "); }
			result.append(BOX_DRAWING_CHARS_BOLD[VERTI]);
			result.append("\n");
		}
		
		// Costruisco la riga finale
		result.append(BOX_DRAWING_CHARS_BOLD[SO_WE]);
		result.append(orizLine.toString());
		result.append(BOX_DRAWING_CHARS_BOLD[SO_EA]);
		
		// Stampo tutto
		System.out.println(result.toString());

	}

	@Override
	public void renderError(String errorText) {
		System.out.println(ERROR_PREFIX + errorText);		
	}

}
