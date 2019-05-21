package it.unibs.ingesw.dpn.ui;

import java.util.List;

/**
 * Implementazione dell'interfaccia UIRenderer utilizzata per realizzare un'interfaccia
 * utente da riga di comando.
 */
public class TextRenderer implements UIRenderer {
	
	private static final String CLI_PROMPT = ">> ";
	private static final String ENTRY_NUM_SEPARATOR = " - ";
	private static final char FRAME_CHAR = '=';
	private static final String ERROR_PREFIX = "ERRORE: ";

	@Override
	public void renderMenu(Menu menu) {
		
		System.out.println();
		this.renderTextInFrame(menu.getTitle());
		
		// Renderizza la descrizione solo se presente
		if (!menu.getDescription().equals("")) {
			System.out.println(menu.getDescription());
		}
		
		System.out.println();
		
		List<MenuEntry> entries = menu.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			System.out.print(" ");
			System.out.printf("%2d", (i + 1));
			System.out.print(ENTRY_NUM_SEPARATOR);
			renderMenuEntry(entries.get(i));
		}
		
		System.out.print(" ");
		System.out.printf("%2d", 0);
		System.out.print(ENTRY_NUM_SEPARATOR);
		renderMenuEntry(menu.getQuitEntry());
		
		System.out.println();
		
	}

	@Override
	public void renderMenuEntry(MenuEntry entry) {
		System.out.println(entry.getName());

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
	
	@Override
	public void renderTextInFrame(String text) {
		int len = text.trim().length();
		StringBuffer line = new StringBuffer();
		for (int i = 0; i < len + 8; i++) {
			line.append(FRAME_CHAR);
		}
		// Stampa la cornice e il testo
		System.out.println(line.toString());
		System.out.println("||  " + text + "  ||");
		System.out.println(line.toString());
		
	}

	@Override
	public void renderError(String errorText) {
		System.out.println(ERROR_PREFIX + errorText);		
	}

}
