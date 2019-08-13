package it.unibs.ingesw.dpn.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fields.builder.EventBuilder;
import it.unibs.ingesw.dpn.model.fields.builder.FieldableBuilder;
import it.unibs.ingesw.dpn.model.fields.builder.UserBuilder;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.users.LoginManager;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

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
	private UserInterface userInterface;
	private FieldValueUIAcquirer acquirer;
	
	/** Attributi ausiliari */
	private Fieldable finalisedAuxiliaryFieldable = null;
	private Category auxiliaryCategory = null;
	
	/** Rappresentazione di un valore non inizializzato */
	private static final String EMPTY_FIELDVALUE = "- - - - -";
	private static final String CREATION_ENTRY_FORMAT = "%-50s : %s";
	
	/**
	 * Costruttore.<br>
	 * Richiede come parametro un oggetto {@link UserInterface}, in modo da prendere 
	 * il controllo dell'interfaccia utente per gestire al meglio i vari processi di creazione e
	 * modifica.
	 * 
	 * @param userInterface Un riferimento all'interfaccia utente, come oggetto {@link UserInterface}
	 */
	public BuilderUIAssistant(UserInterface userInterface) {
		if (userInterface == null) {
			throw new IllegalArgumentException("Impossibile istanziare un nuovo BuilderAssistant senza un corretto riferimento ad un'Interfaccia Utente");
		}
		this.userInterface = userInterface;
		this.acquirer = new FieldValueUIAcquirer(this.userInterface);
	}
	
	/**
	 * Metodo per la gestione del processo di creazione di un evento.
	 * 
	 * @param loginManager Un riferimento al manager dei login, utilizzato per recuperare il creatore dell'evento
	 * @return L'evento appena creato
	 */
	public Event createEvent(LoginManager loginManager) {
		// Selezione della categoria
		MenuAction categorySelectionMenu = prepareCategorySelectionMenu();
		userInterface.getter().getMenuChoice(categorySelectionMenu).execute(userInterface);
		Category selectedCategory = this.auxiliaryCategory;
		
		// Se ho annullato la creazione, termino immediatamente
		if (selectedCategory == null) {
			// Per segnalare la terminazione, restuisco un valore nullo
			return null;
		}
		
		EventBuilder eventBuilder = new EventBuilder(this.acquirer);
		
		// Comunico al Builder che comincio la creazione di un oggetto ben specifico.
		eventBuilder.startCreation(loginManager.getCurrentUser(), selectedCategory);

		do {
			MenuAction createUserMenu = prepareCreationMenu(eventBuilder);
			Action action = userInterface.getter().getMenuChoice(createUserMenu);
			action.execute(userInterface);
		} while (!eventBuilder.isReady());

		return (Event) this.finalisedAuxiliaryFieldable;
	}
	
	/**
	 * Gestisce il processo di creazione di un oggetto User.
	 * 
	 * @param usersManager Un riferimento alla lista di tutti gli utenti
	 * @return Il nuovo oggetto User
	 */
	public User createUser(UsersRepository usersManager) {		
		UserBuilder userBuilder = new UserBuilder(this.acquirer);
		
		boolean repeatFlag = true;
		
		do {
			// Comincio la creazione
			userBuilder.startCreation();
			
			do {
				MenuAction createUserMenu = prepareCreationMenu(userBuilder);
				Action action = userInterface.getter().getMenuChoice(createUserMenu);
				action.execute(userInterface);
			} while (!userBuilder.isReady());
		
			if (this.finalisedAuxiliaryFieldable != null &&
					usersManager.isNicknameExisting(
							this.finalisedAuxiliaryFieldable.getFieldValue(UserField.NICKNAME).toString())) {
				userInterface.renderer().renderError("Il nickname scelto è già in uso.\nSelezionare un altro nickname.");
			} else {
				repeatFlag = false;
			}
		} while (repeatFlag);
		
		return (User) this.finalisedAuxiliaryFieldable;
	}

	/**
	 * Gestisce il processo di modifica di un oggetto User.
	 * 
	 * @param selectedUser Un riferimento all'oggetto User di cui modificare i campi
	 */
	public void editUser(User selectedUser) {		
		UserBuilder userBuilder = new UserBuilder(this.acquirer);
		
		// Comincio la creazione
		userBuilder.startEditing(selectedUser);
		
		do {
			MenuAction createUserMenu = prepareEditingMenu(userBuilder);
			Action action = userInterface.getter().getMenuChoice(createUserMenu);
			try {
				action.execute(userInterface);
			}
			catch (Exception e) {
				userInterface.renderer().renderError("Non è possibile modificare il valore di questo campo.");
			}
		} while (!userBuilder.isReady());
		
	}
	
	/* METODI PRIVATI DI UTILITÀ */
	// Nel senso di "nascosti", non "privi"
	
	/**
	 * Permette la creazione di un menu per selezionare una categoria all'interno di tutte quelle disponibili
	 * nel programma.
	 * La categoria viene "salvata" nella variabile d'istanza "auxiliaryCategory".
	 * 
	 * @return La categoria selezionata 
	 */
	private MenuAction prepareCategorySelectionMenu() {
		
		MenuAction categorySelectorMenu = new MenuAction (
				"Selezione della categoria", 
				"Seleziona la categoria, fra quelle disponibili, in cui rientra l'evento che vuoi creare:");
		
		// Azione per annullare la scelta della categoria
		SimpleAction backAction = (userInterface) -> {
			auxiliaryCategory = null;
			};
		categorySelectorMenu.setBackEntry("Annulla la creazione", backAction);
			
		// Callback categorie
		for (Category category : Category.values()) {
			// L'azione corrispondente salva la categoria in una variabile temporanea di classe
			SimpleAction categorySelectionAction = (userInterface) -> {
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
	private MenuAction prepareCreationMenu(FieldableBuilder builder) {
		// Creazione del menu di creazione
		String title = "Menu di creazione";
		MenuAction creationMenu = new MenuAction(title, 
				"Seleziona i campi che vuoi impostare. \n"
				+ "I campi contrassegnati dall'asterisco (*) sono obbligatori.\n"
				+ "Quando avrai completato tutti i campi seleziona \"Esci e conferma\".");

		// Callback per abortire la creazione dell'utente
		SimpleAction abortAction = (userInterface) -> {
			builder.cancel();
			finalisedAuxiliaryFieldable = null;
			userInterface.renderer().renderTextInFrame("Creazione annullata");
			};
		// Aggiunta dell'opzione di uscita
		creationMenu.setBackEntry("Annulla la creazione e torna al menu principale", abortAction);
		
		// Aggiungo tutte le entries al menu
		creationMenu.addAllEntry(prepareCreationMenuEntries(builder));

		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (builder.verifyMandatoryFields()) {
			
			// Callback per finalizzare la creazione dell'utente
			SimpleAction finaliseAction = (userInterface) -> {
				// Finalizzo la modifica
				finalisedAuxiliaryFieldable = builder.finalise();
				userInterface.renderer().renderTextInFrame("Creazione completata");
				};
				
			// In caso affermativo, aggiungo l'opzione di terminazione
			creationMenu.addEntry("Esci e conferma", finaliseAction);
		}
		
		return creationMenu;
	}
	
	/**
	 * Fornisce un menu di MODIFICA aggiornato con le entry relative ai campi coinvolti finora.
	 * 
	 * @param builder Il Builder con cui si sta lavorando
	 * @return Il menu di modifica dell'oggetto Fieldable
	 */
	private MenuAction prepareEditingMenu(FieldableBuilder builder) {
		// Creazione del menu di editing
		String title = "Menu di modifica";
		MenuAction editingMenu = new MenuAction(title, 
				"Seleziona i campi che vuoi modificare. \n"
				+ "Soltanto i campi contrassegnati dal cancelletto (#) sono modificabili.\n"
				+ "Quando avrai completato tutti i campi seleziona \"Esci e conferma\".");

		// Callback per abortire la modifica dell'utente
		SimpleAction abortAction = (userInterface) -> {
			builder.cancel();
			userInterface.renderer().renderTextInFrame("Modifica annullata");
			};
		// Aggiungo l'opzione di uscita al menu
		editingMenu.setBackEntry("Annulla le modifiche e torna al menu principale", abortAction);
		
		// Aggiungo tutte le entries al menu
		editingMenu.addAllEntry(prepareEditingMenuEntries(builder));
			
		// Verifico che tutti i campi obbligatori siano stati acquisiti
		if (builder.verifyMandatoryFields()) {

			// Callback per finalizzare la modifica dell'utente
			SimpleAction finaliseAction = (userInterface) -> {
				// Finalizzo la modifica
				builder.finalise();
				userInterface.renderer().renderTextInFrame("Modifica completata");
				};
				
			// In caso affermativo, aggiungo l'opzione di terminazione
			editingMenu.addEntry("Esci e conferma", finaliseAction);
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
			SimpleAction fieldAction = (userInterface) -> {
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
			SimpleAction fieldAction = (userInterface) -> {
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
