package it.unibs.ingesw.dpn.ui.actions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import it.unibs.ingesw.dpn.ui.TestGetter;
import it.unibs.ingesw.dpn.ui.TestUI;
import it.unibs.ingesw.dpn.ui.TextRenderer;
import it.unibs.ingesw.dpn.ui.UserInterface;

public class MenuActionTest {

	/** Stringhe di DEFAULT del menu */
	public static final String TITLE_DEFAULT = "Titolo";
	public static final String TEXT_DEFAULT = "Testo del menu";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// TODO Serve??
	}
	
	@Test
	public void constructorTest() {		
		// Costruisco un oggetto valido
		assertNotNull(new MenuAction(TITLE_DEFAULT, TEXT_DEFAULT));
		assertNotNull(new MenuAction(TITLE_DEFAULT, null));
		
		// Costruisco un oggetto non valido
		assertThrows(IllegalArgumentException.class, () -> { new MenuAction(null, null); });
		assertThrows(IllegalArgumentException.class, () -> { new MenuAction(null, TITLE_DEFAULT); });
	}

	@Test
	public void getTitleTest() {
		// Test con valore di default
		MenuAction menu1 = newMenu();
		assertEquals(TITLE_DEFAULT, menu1.getTitle());
		
		// Test con titolo casuale
		String randomTitle = String.format("Titolo_casuale_%.9f", Math.random());
		MenuAction menu2 = new MenuAction(randomTitle, null);
		assertEquals(randomTitle, menu2.getTitle());
	}
	
	@Test
	public void getTextTest() {
		// Test con titolo di default
		MenuAction menu1 = newMenu();
		assertEquals(TEXT_DEFAULT, menu1.getDescription());
		
		// Test con testo casuale
		String randomText = String.format("Testo_casuale_%.9f", Math.random());
		MenuAction menu2 = new MenuAction(TITLE_DEFAULT, randomText);
		assertEquals(randomText, menu2.getDescription());
	}

	@Test
	public void getEntriesTest_whenEmpty() {
		MenuAction menu = newMenu();
		
		// Test di una lista vuota di entry
		assertNotNull(menu.getEntries());
		assertTrue(menu.getEntries().isEmpty());
	}

	@Test
	public void getEntriesTest_whenAddingEntry() {
		MenuAction menu = newMenu();
		// Creo un'entry vuota
		MenuEntry entry = mock(MenuEntry.class);
		menu.addEntry(entry);
		
		// Test di una lista con una sola entry
		assertNotNull(menu.getEntries());
		assertTrue(menu.getEntries().size() == 1);
	}

	@Test
	public void getBackEntryTest_whenDefault() {
		MenuAction menu = newMenu();
		// Testo che l'entry di uscita restituita sia quella di default
		MenuEntry backEntry = menu.getBackEntry();
		assertNotNull(backEntry);
		// L'entry di default è composta dalla stringa di default "Indietro" e dall'azione vuota
		assertEquals(backEntry.getName(), MenuAction.BACK_ENTRY_TITLE);
		assertEquals(backEntry.getAction(), Action.EMPTY_ACTION);
		assertTrue(backEntry.isTerminatingAction());
	}

	@Test
	public void getBackEntryTest_whenSetBefore() {
		MenuAction menu = newMenu();
		// Creo la nuova entry
		String entryTitle = randomString();
		Action entryAction = mock(Action.class);
		menu.setBackEntry(entryTitle, entryAction);
		// Testo che l'entry di uscita restituita sia quella di default
		MenuEntry backEntry = menu.getBackEntry();
		assertNotNull(backEntry);
		// L'entry di default è composta dalla stringa di default "Indietro" e dall'azione vuota
		assertEquals(backEntry.getName(), entryTitle);
		assertEquals(backEntry.getAction(), entryAction);
		assertTrue(backEntry.isTerminatingAction());
	}
	
	@Test
	public void addEntry_previouslyBuilt() {
		MenuAction menu = newMenu();
		// Creo la nuova entry
		String entryTitle = randomString();
		Action entryAction = mock(Action.class);
		MenuEntry entry = new MenuEntry(entryTitle, entryAction);
		// Aggiungo l'entry al menu
		menu.addEntry(entry);
		// Verifico che la lista delle entries contenga esattamente un'entry
		assertTrue(menu.getEntries().size() == 1);
		// Verifico che l'entry sia presente nella lista di entry
		assertTrue(menu.getEntries().contains(entry));
	}
	
	@Test
	public void addEntry_withoutSpecifyingTermination() {
		MenuAction menu = newMenu();
		// Creo la nuova entry
		String entryTitle = randomString();
		Action entryAction = mock(Action.class);
		// Aggiungo l'entry al menu
		menu.addEntry(entryTitle, entryAction);
		// Verifico che la lista delle entries contenga esattamente un'entry
		assertTrue(menu.getEntries().size() == 1);
		// Verifico che l'entry sia presente nella lista di entry
		boolean found = false;
		for (MenuEntry entry : menu.getEntries()) {
			if (entry.getName().equals(entryTitle) 
					&& entry.getAction().equals(entryAction)
					&& !entry.isTerminatingAction()) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
	
	@Test
	public void addEntry_withSpecifyingTermination() {
		MenuAction menu = newMenu();
		// Creo la nuova entry
		String entryTitle = randomString();
		Action entryAction = mock(Action.class);
		boolean entryTermination = true;
		// Aggiungo l'entry al menu
		menu.addEntry(entryTitle, entryAction, entryTermination);
		// Verifico che la lista delle entries contenga esattamente un'entry
		assertTrue(menu.getEntries().size() == 1);
		// Verifico che l'entry sia presente nella lista di entry
		boolean found = false;
		for (MenuEntry entry : menu.getEntries()) {
			if (entry.getName().equals(entryTitle) 
					&& entry.getAction().equals(entryAction)
					&& entry.isTerminatingAction() == entryTermination) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void addEntry_withNullArguments() {
		MenuAction menu = newMenu();
		// Verifico che almeno un argomento nullo provochi un'eccezione:
		assertThrows(IllegalArgumentException.class, () -> {
			menu.addEntry(null, mock(Action.class));
		});
		assertThrows(IllegalArgumentException.class, () -> {
			menu.addEntry(randomString(), null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			menu.addEntry(null, null);
		});
	}
	
	@Test
	public void addNullEntry() {
		MenuAction menu = newMenu();
		// Verifico che un'entry nulla provochi un'eccezione:
		assertThrows(IllegalArgumentException.class, () -> {
			menu.addEntry(null);
		});
	}

	@Test
	public void addAllEntry_withNullOrEmptyList() {
		MenuAction menu = newMenu();
		// Verifico che venga generata un'eccezione per una lista nulla
		assertThrows(IllegalArgumentException.class, () -> {
			menu.addAllEntry(null);
		});
		// Verifico che venga generata un'eccezione per una lista vuota
		List<MenuEntry> entries = new LinkedList<>();
		assertTrue(entries.isEmpty());
		assertThrows(IllegalArgumentException.class, () -> {
			menu.addAllEntry(entries);
		});
	}
	
	@Test
	public void addAllEntry_withValidList() {
		MenuAction menu = newMenu();
		// Verifico che la lista di entry all'inizio sia vuota
		assertTrue(menu.getEntries().isEmpty());
		int entriesSize = 0;
		// TEST_CASE
		int testcases = 5;
		for (int i = 0; i < testcases; i++) {
			// Creo una lista di entry di dimensione casuale
			int randomSize = randomInt(1, 50);
			List<MenuEntry> entries = new ArrayList<>(randomSize);
			for (int j = 0; j < randomSize; j++) {
				entries.add(new MenuEntry(randomString(), mock(Action.class)));
			}
			// Aggiungo la lista di entry al menu
			menu.addAllEntry(entries);
			// Verifico che ciascuna entry sia stata aggiunta
			for (MenuEntry entry : entries) {
				assertTrue(menu.getEntries().contains(entry));
			}
			// Verifico che la dimensione delle entry sia corretta fino a qui
			assertTrue(menu.getEntries().size() == entriesSize + randomSize);
			entriesSize += randomSize;
			// Verifico che ciascuna entry sia stata aggiunta
			for (MenuEntry entry : entries) {
				assertTrue(menu.getEntries().contains(entry));
			}
		}
	}
	
	@Test
	public void executeTest_withNonTerminantEntries() {
		// Parametri del test
		final int entriesSize = randomInt(1, 10);
		// Creo il menu
		MenuAction menu = newMenu();
		for (int i = 0; i < entriesSize; i++) {
			// Creo una semplice azione di verifica
			// Ogni azione di verifica è unica, e ha successo solamente se viene letto il valore uguale 
			// All'indice della entry stessa
			// In questo modo riesco a verificare dall'esterno se la selezione avviene in maniera corretta
			final int value = i + 1;
			SimpleAction action = (userInterface) -> {
				assertTrue(userInterface.getter().getInteger() == value);
			};
			// Aggiungo la entry alla lista
			menu.addEntry(new MenuEntry(randomString(),action));
		}
		// Creo l'interfaccia utente di test
		TestGetter getter = new TestGetter(new TextRenderer());
		UserInterface userInterface = new TestUI(getter);
		for (int j = 1; j <= entriesSize; j++) {
			// Per la selezione
			getter.feedArguments(String.format("%d", j));
			// Per l'istruzione di assert
			getter.feedArguments(String.format("%d", j));
		}
		getter.feedArguments("0"); // Istruzione di uscita
		menu.execute(userInterface);
		
	}
	
	
	// METODI PRIVATI
	
	/**
	 * Restituisce un nuovo oggetto {@link MenuAction} con stringhe di default.
	 * 
	 * @return Un nuovo menu istanziato con stringhe di default
	 */
	private static MenuAction newMenu() {
		return new MenuAction(TITLE_DEFAULT, TEXT_DEFAULT);
	}

	/**
	 * Crea e restituisce una stringa casuale (formata da dieci diverse cifre numeriche).
	 * 
	 * @return Una stringa casuale
	 */
	private static String randomString() {
		int mod = (int) 1e10;
		return String.format("%010d", randomInt(0, mod));
	}
	
	/**
	 * Crea e restituisce un intero casuale compreso fra min e max (estremi INCLUSI).
	 * 
	 * @return Un intero casuale
	 */
	private static int randomInt(int min, int max) {
		if (max <= min) {
			throw new IllegalArgumentException();
		}
		int range = max - min + 1;
		return (int) (Math.random() * range + min);
	}

}
