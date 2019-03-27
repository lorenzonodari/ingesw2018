package it.unibs.ingesw.dpn.ui;

import java.util.List;

/**
 * Implementazione dell'interfaccia UIRenderer utilizzata per realizzare un'interfaccia
 * utente da riga di comando.
 */
public class TextRenderer implements UIRenderer {
	
	private static final String CLI_PROMPT = ">> ";
	private static final String ENTRY_NUM_SEPARATOR = " - ";

	@Override
	public void renderMenu(Menu menu) {
		
		System.out.println();
		System.out.println(menu.getTitle());
		System.out.println(menu.getDescription());
		System.out.println();
		
		List<MenuEntry> entries = menu.getEntries();
		for (int i = entries.size(); i > 0; i--) {
			System.out.print(" ");
			System.out.print(i);
			System.out.print(ENTRY_NUM_SEPARATOR);
			renderMenuEntry(entries.get(i - 1));
		}
		
		System.out.print(" ");
		System.out.print("0");
		System.out.print(ENTRY_NUM_SEPARATOR);
		renderMenuEntry(menu.getQuitEntry());
		
		System.out.println();
		
	}

	@Override
	public void renderMenuEntry(MenuEntry entry) {
		System.out.println(entry.getName());

	}
	
	@Override
	public void renderPrompt(String question) {
		System.out.println(question);
		System.out.print(CLI_PROMPT);
	}

}
