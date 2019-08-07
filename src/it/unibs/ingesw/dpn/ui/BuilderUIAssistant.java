package it.unibs.ingesw.dpn.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fields.FieldableBuilder;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersManager;

/**
 * Classe che si occupa di visualizzare l'interfaccia utente necessaria per il processo
 * di creazione o modifica di un oggetto {@link User} o {@link Event}.
 * In pratica, visualizza e richiede in maniera controllata e standard tutti i dati necessari.
 * 
 * @author Michele Dusi
 *
 */
public class BuilderUIAssistant {
	
	/** Input / Output */
	private UIRenderer renderer;
	private InputGetter getter;
	private FieldValueAcquirer acquirer;
	
	/** Attributi ausiliari */
	private Fieldable finalisedAuxiliaryFieldable = null;
	private Category auxiliaryCategory = null;
	
	/** Rappresentazione di un valore non inizializzato */
	private static final String EMPTY_FIELDVALUE = "- - - - -";
	private static final String CREATION_ENTRY_FORMAT = "%-50s : %s";
	
	public BuilderUIAssistant(UIRenderer renderer, InputGetter getter) {
		if (renderer == null || getter == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo BuilderAssistant con parametri nulli");
		}
		this.renderer = renderer;
		this.getter = getter;
		this.acquirer = new FieldValueAcquirer(renderer, getter);
	}
	
	public Event createEvent(UsersManager usersManager) {
		// TEST
		renderer.renderTextInFrame("...Processo di creazione di un Evento...");
		// TEST
		
		// Selezione della categoria
		Menu categorySelectionMenu = prepareCategorySelectionMenu();
		getter.getMenuChoice(categorySelectionMenu).execute();
		Category selectedCategory = this.auxiliaryCategory;
		
		// Se ho annullato la creazione, termino immediatamente
		if (selectedCategory == null) {
			// Per segnalare la terminazione, restuisco un valore nullo
			return null;
		}
		
		EventBuilder eventBuilder = new EventBuilder(this.acquirer);
		
		// Comunico al Builder che comincio la creazione di un oggetto ben specifico.
		eventBuilder.startCreation(usersManager.getCurrentUser(), selectedCategory);

		do {
			Menu createUserMenu = prepareCreationMenu(eventBuilder);
			MenuAction action = getter.getMenuChoice(createUserMenu);
			action.execute();
		} while (!eventBuilder.isReady());

		return (Event) this.finalisedAuxiliaryFieldable;
	}
	
	/**
	 * Gestisce il processo di creazione di un oggetto User.
	 * 
	 * @param usersManager Un riferimento alla lista di tutti gli utenti
	 * @return Il nuovo oggetto User
	 */
	public User createUser(UsersManager usersManager) {
		// TEST
		renderer.renderTextInFrame("...Processo di creazione di un Utente...");
		// TEST
		
		UserBuilder userBuilder = new UserBuilder(this.acquirer);
		
		// Comincio la creazione
		userBuilder.startCreation();

		do {
			Menu createUserMenu = prepareCreationMenu(userBuilder);
			MenuAction action = getter.getMenuChoice(createUserMenu);
			action.execute();
		} while (!userBuilder.isReady());
		
		// TODO Controllare se il nickname NON è già presente nella lista di UsersManager
		// Una roba tipo: usersManager.isNicknameExisting(user.getNickname());
		
		return (User) this.finalisedAuxiliaryFieldable;
	}
	
	public void editUser(User selectedUser) {
		// TEST
		renderer.renderTextInFrame("...Processo di modifica di un Utente...");	
		// TEST
		
		UserBuilder userBuilder = new UserBuilder(this.acquirer);
		
		// Comincio la creazione
		userBuilder.startEditing(selectedUser);
		
		do {
			Menu createUserMenu = prepareEditingMenu(userBuilder);
			MenuAction action = getter.getMenuChoice(createUserMenu);
			action.execute();
		} while (!userBuilder.isReady());
		
	}
	
	/* METODI PRIVATI DI UTILITÀ */
	
	/**
	 * Permette la creazione di un menu per selezionare una categoria all'interno di tutte quelle disponibili
	 * nel programma.
	 * La categoria viene "salvata" nella variabile d'istanza "auxiliaryCategory".
	 * 
	 * @return La categoria selezionata 
	 */
	private Menu prepareCategorySelectionMenu() {
		
		// Azione per annullare la scelta della categoria
		MenuAction backAction = () -> {
			auxiliaryCategory = null;
			};
		
		Menu categorySelectorMenu = new Menu (
				"Selezione della categoria", 
				"Seleziona la categoria, fra quelle disponibili, in cui rientra l'evento che vuoi creare:", 
				"Annulla la creazione", 
				backAction);
		
		// Callback categorie
		for (Category category : Category.values()) {
			// L'azione corrispondente salva la categoria in una variabile temporanea di classe
			MenuAction categorySelectionAction = () -> {
				auxiliaryCategory = category;
				};
			// Aggiungo la entry al menu	
			categorySelectorMenu.addEntry(category.getName(), categorySelectionAction);
		}
		
		return categorySelectorMenu;
	}

	/**
	 * Fornisce un menu di CREAZIONE aggiornato con le entry relative ai campi inizializzati finora.
	 * 
	 * @param builder Il Builder con cui si sta lavorando
	 * @return Il menu di creazione dell'oggetto Fieldable
	 */
	private Menu prepareCreationMenu(FieldableBuilder builder) {
		// Callback per abortire la creazione dell'utente
		MenuAction abortAction = () -> {
			builder.cancel();
			renderer.renderTextInFrame("Creazione annullata");
			};

		String title = "Menu di creazione";
		Menu creationMenu = new Menu(title, 
				"Seleziona i campi che vuoi impostare. \n"
				+ "I campi contrassegnati dall'asterisco (*) sono obbligatori.\n"
				+ "Quando avrai completato tutti i campi seleziona \"Esci e conferma\".",
				"Annulla la creazione e torna al menu principale", abortAction);
		
		// TODO
		// FARE UN METODO MIGLIORE PER AGGIUNGERE ENTRY IN UN COLPO SOLO
		for (MenuEntry entry : prepareCreationMenuEntries(builder)) {
			creationMenu.addEntry(entry.getName(), entry.getAction());
		}

		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (builder.verifyMandatoryFields()) {
			// In caso affermativo, aggiungo l'opzione di terminazione
			creationMenu.addEntry("Esci e conferma", () -> {
				// Termino la creazione dell'utente
				this.finalisedAuxiliaryFieldable = builder.finalise();
			});
		}
		
		return creationMenu;
	}
	
	/**
	 * Fornisce un menu di MODIFICA aggiornato con le entry relative ai campi coinvolti finora.
	 * 
	 * @param builder Il Builder con cui si sta lavorando
	 * @return Il menu di modifica dell'oggetto Fieldable
	 */
	private Menu prepareEditingMenu(FieldableBuilder builder) {
		// Callback per abortire la creazione dell'utente
		MenuAction abortAction = () -> {
			builder.cancel();
			renderer.renderTextInFrame("Modifica annullata"); // TODO BACKTRACKING
			};

		String title = "Menu di modifica";
		Menu editingMenu = new Menu(title, 
				"Seleziona i campi che vuoi modificare. \n"
				+ "Soltanto i campi contrassegnati dal cancelletto (#) sono modificabili.\n"
				+ "Quando avrai completato tutti i campi seleziona \"Esci e conferma\".",
				"Annulla la creazione e torna al menu principale", abortAction);
		
		// TODO
		// FARE UN METODO MIGLIORE PER AGGIUNGERE ENTRY IN UN COLPO SOLO
		for (MenuEntry entry : prepareEditingMenuEntries(builder)) {
			editingMenu.addEntry(entry.getName(), entry.getAction());
		}

		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (builder.verifyMandatoryFields()) {
			// In caso affermativo, aggiungo l'opzione di terminazione
			editingMenu.addEntry("Esci e conferma", () -> {
				// Termino la creazione dell'utente
				builder.finalise();		// Nota: in questo caso non mi serve il valore di ritorno
			});
		}
		
		return editingMenu;
	}
	
	/**
	 * Prepara la lista di entries aggiornata con i valori attualmente inizializzati nel Builder.
	 * 
	 * @param builder Il Builder con cui si sta lavorando attualmente
	 * @return La lista di entry per il menu di selezione
	 */
	private List<MenuEntry> prepareCreationMenuEntries(FieldableBuilder builder) {
		// Prendo i campi attualmente inizializzati
		Map<Field, FieldValue> provisionalFieldValues = builder.getProvisionalFieldValues();
		
		// Inizializzo la lista vuota
		List<MenuEntry> entries = new ArrayList<>();
		
		// Per tutti i valori della mappa
		for (Field f : provisionalFieldValues.keySet()) {
			
			// Preparo l'oggetto Action
			MenuAction fieldAction = () -> {
				builder.acquireFieldValue(f);
				};
				
			// Recupero il valore relativo inizializzato attualmente
			FieldValue fv = provisionalFieldValues.get(f);

			// Creo la entry
			String entryTitle = String.format(
					CREATION_ENTRY_FORMAT,
					f.getName() + ((f.isMandatory()) ? " (*)" : ""),
					((fv == null) ? EMPTY_FIELDVALUE : fv.toString())
					);
			
			// Aggiungo la entry alla lista
			entries.add(new MenuEntry(entryTitle, fieldAction));
		}
		
		return entries;
	}

	/**
	 * Prepara la lista di entries aggiornata con i valori attualmente inizializzati nel Builder.
	 * 
	 * @param builder Il Builder con cui si sta lavorando attualmente
	 * @return La lista di entry per il menu di selezione
	 */
	private List<MenuEntry> prepareEditingMenuEntries(FieldableBuilder builder) {
		// Prendo i campi attualmente inizializzati
		Map<Field, FieldValue> provisionalFieldValues = builder.getProvisionalFieldValues();
		
		// Inizializzo la lista vuota
		List<MenuEntry> entries = new ArrayList<>();
		
		// Per tutti i valori della mappa
		for (Field f : provisionalFieldValues.keySet()) {
			
			// Preparo l'oggetto Action
			MenuAction fieldAction = () -> {
				builder.acquireFieldValue(f);
				};
				
			// Recupero il valore relativo inizializzato attualmente
			FieldValue fv = provisionalFieldValues.get(f);

			// Creo la entry
			String entryTitle = String.format(
					CREATION_ENTRY_FORMAT,
					f.getName() + ((f.isEditable()) ? " (#)" : ""),
					((fv == null) ? EMPTY_FIELDVALUE : fv.toString())
					);
			
			// Aggiungo la entry alla lista
			entries.add(new MenuEntry(entryTitle, fieldAction));
		}
		
		return entries;
	}
	
}
