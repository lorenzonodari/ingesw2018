package it.unibs.ingesw.dpn.ui;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.LocalDateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersManager;

/**
 * Classe che permette la creazione e la modifica di utenti in maniera "controllata", seguendo un preciso processo.
 * 
 * Per la creazione di un utente è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - acquireFieldValue(..);		<- Quante volte si vuole, permette l'inizializzazione dei valori dei campi
 * - finalise(..);				<- Restituisce un nuovo User
 * 
 * Allo stesso modo, per la modifica di un utente è necessario chiamare, nell'ordine:
 * - startEditing(..);
 * - acquireFieldValue(..);		<- Quante volte si vuole, permette la modifica dei campi già presenti
 * - finalise(..);				<- Restituisce lo User modificato
 * 
 * Solo dopo aver chiamato i tre metodi, o eventualmente dopo aver cancellato la creazione
 * o la modifica con il metodo "cancel", è possibile ricominciare un nuovo processo di creazione o modifica.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class UserBuilder {

	/** Input/Output */
	private final UIRenderer renderer;
	private final InputGetter getter;
	
	/** Classi per recuperare dati ausiliari */
	private final UsersManager usersManager;

	/** Flag per mantenere lo stato della factory */
	private boolean creationOn = false;
	private boolean editingOn = false;

	/** Attributi che aiutano la creazione e la modifica di un evento */
	private Map<Field, FieldValue> provisionalFieldValues = null;
	private User currentEditedUser = null;

	/** Stringhe */
	private static final String EMPTY_FIELDVALUE_STRING = "- - - - -";
	private static final String ILLEGAL_MODE_EXCEPTION = "Impossibile acquisire dati se non è stata prima avviata la creazione o la modifica di un nuovo utente";
	
	/** Parametri */
	private static final int AGE_LIMIT = 12;

	/**
	 * Costruttore pubblico.
	 * 
	 * Nota: l'oggetto "usersManager" richiesto come parametro è necessario perché la factory
	 * ha bisgono di sapere quali utenti sono presenti nel sistema e quali nickname sono già utilizzati.
	 * 
	 * Precondizione: i parametri non possono ovviamente essere nulli.
	 * 
	 * @param renderer Il renderizzatore dei prompt e dei messaggi d'errore
	 * @param getter L'acquisitore di dati primitivi
	 * @param usersManager La classe che si occupa della gestione degli utenti.
	 */
	public UserBuilder(UIRenderer renderer, InputGetter getter, UsersManager usersManager) {
		// verifico la precondizione
		if (renderer == null || getter == null || usersManager == null) {
			throw new IllegalArgumentException("impossibile creare un oggetto UserFactory con parametri nulli");
		}
		
		this.renderer = renderer;
		this.getter = getter;
		this.usersManager = usersManager;
	}
	
	/**
	 * Comincia la creazione di un utente.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni o modifiche in corso. Una factory può costruire un solo
	 * utente alla volta, secondo il processo descritto nell'introduzione alla classe.
	 * 
	 * @param defaultNickname Il nickname di default con cui cominciare l'iscrizione
	 */
	public void startCreation(String defaultNickname) {
		// Verifico la precondizione
		if (this.creationOn || this.editingOn) {
			// E' già in corso la creazione o la modifica di un utente, non è possibile cominciarne una nuova
			throw new IllegalStateException("Impossibile cominciare la creazione di un nuovo utente finché non viene terminata quella corrente");
		} else {
			// Comincio la creazione di un nuovo utente
			this.creationOn = true;
		}
		
		// Preparo i campi vuoti, pronti per essere inizializzati
		this.provisionalFieldValues = new HashMap<Field, FieldValue>();

		for (Field f : UserField.values()) {
			this.provisionalFieldValues.put(f, null);
		};
		
		// Inizializzo già il nickname
		// Nota: se il valore del nickname è nullo, è come se non fosse ancora inizializzato
		this.provisionalFieldValues.put(UserField.NICKNAME, new StringFieldValue(defaultNickname));
	}

	/**
	 * Comincia la modifica di un utente.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni o modifiche in corso. Una factory può modificare
	 * un solo utente alla volta, secondo il processo descritto nell'introduzione alla classe.
	 * 
	 * Precondizione: l'utente che si intende modificare non deve essere un parametro nullo.
	 * 
	 * @param userToBeEdited L'utente {@link User} che dev'essere modificato
	 */
	public void startEditing(User userToBeEdited) {
		// Verifico che il parametro non sia null
		if (userToBeEdited == null) {
			throw new IllegalArgumentException("Impossibile iniziare la modifica di un utente nullo");
		}
		
		if (this.creationOn || this.editingOn) {
			// E' già in corso la creazione o la modifica di un utente, non è possibile cominciarne una nuova
			throw new IllegalStateException("Impossibile cominciare la modifica di un nuovo utente finché non viene terminata quella corrente");
		} else {
			// Comincio la modifica di un nuovo utente
			this.editingOn = true;
		}

		// Preparo gli attributi ausiliari durante il processo
		this.currentEditedUser = userToBeEdited;
		this.provisionalFieldValues = this.currentEditedUser.getAllFieldValues();
	}

	/**
	 * Gestisce l'acquisizione del dato {@link FieldValue} relativo al campo {@link Field} passato come parametro.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation". OPPURE la factory deve essere in modalità "modifica", ossia deve essere
	 * stato chiamato in precedenza il metodo "startEditing".
	 * 
	 * Precondizione: il campo {@link Field} deve essere un campo previsto per un utente, ossia deve
	 * essere un campo della classe {@link UserField}. Questa condizione è verificata in automatico poiché il metodo chiamante
	 * presente in {@link UIManager} ha come valori possibili per i campi soltanto oggetti {@link UserField}.
	 * 
	 * Precondizione: nel caso in cui ci si trovi in modalità "modifica" (e non in modalità "creazione")
	 * è necessario che il campo sia modificabile, ossia che il campo preveda la modifica posteriore alla creazione
	 * di un oggetto {@link User}. In caso la precondizione non fosse soddisfatta, il metodo si interrompe
	 * anticipatamente e richiede al renderer la visualizzazione di un messaggio d'errore.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 */
	public void acquireFieldValue(Field field) {
		// Verifico di essere in modalità "creazione di un nuovo utente" o in modalità "modifica di un vecchio utente"
		if (!creationOn && !editingOn) {
			throw new IllegalStateException(ILLEGAL_MODE_EXCEPTION);
		}
		/* Nota: il caso in cui entrambi i flag siano veri non viene filtrato esplicitamente dall'IF, ma è 
		 * verificato implicitamente dal momento che per settare uno dei due flag a "true", l'altro deve essere
		 * "false", e questa cosa è fattibile solo nei metodi "startCreation" e "startEditing".
		 */
		
		// Verifico che il parametro non sia null
		if (field == null) {
			throw new IllegalArgumentException("Parametro nullo: impossibile acquisire un dato senza specificare il campo");
		}
		
		// Verifico che, in caso ci si trovi in modalità "editing", il campo sia un campo editabile.
		if (editingOn && !field.isEditable()) {
			renderer.renderError(String.format(
					"Impossibile modificare il campo immutabile \"%s\"", 
					field.getName()
					));
			// Termino forzatamente l'esecuzione del metodo
			return;
		}
		
		// Prompt e interazione con l'utente
		renderer.renderLineSpace();
		renderer.renderText(String.format(
				" ### %-50s",
				field.getName().toUpperCase()));
		renderer.renderText(String.format(
				" ### %s",
				field.getDescription()));
		renderer.renderLineSpace();
		
		// Acquisizione del dato, basata su Field
		FieldValue value = null;
		
		// Campi previsti per un utente
		if (field instanceof UserField) {
			value = acquireUserFieldValue((UserField) field);
		
		// Campi estranei
		} else {
			throw new IllegalArgumentException("Campo \"Field\" non riconosciuto: impossibile acquisire un valore");
		}
		
		// Aggiungo il dato acquisito alla mappa di valori provvisori
		this.provisionalFieldValues.put(field, value);
	}

	/**
	 * Metodo che verifica se sono stati compilati TUTTI i campi obbligatori previsti.
	 * In tal caso restituisce true, altrimenti false.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation". OPPURE la factory deve essere in modalità "modifica", ossia deve essere
	 * stato chiamato in precedenza il metodo "startEditing".
	 * 
	 * @return "true" se tutti i campi obbligatori sono stati compilati
	 */
	public boolean verifyMandatoryFields() {
		// Verifico di essere in modalità "creazione di un nuovo utente" o in modalità "modifica di un vecchio utente"
		if (!creationOn && !editingOn) {
			throw new IllegalStateException(ILLEGAL_MODE_EXCEPTION);
		}
		/* Nota: il caso in cui entrambi i flag siano veri non viene filtrato esplicitamente dall'IF, ma è 
		 * verificato implicitamente dal momento che per settare uno dei due flag a "true", l'altro deve essere
		 * "false", e questa cosa è fattibile solo nei metodi "startCreation" e "startEditing".
		 */
		
		// Verifico tutti i campi
		boolean checkMandatoryFieldsFlag = true;
		for (Field f : this.provisionalFieldValues.keySet()) {
			if (f.isMandatory() && provisionalFieldValues.get(f) == null) {
				checkMandatoryFieldsFlag = false;
				break;
			}
		}
		return checkMandatoryFieldsFlag;
	
	}

	/**
	 * Restituisce i valori parzialmente compilati dell'utente che si sta creando o la lista dei valori
	 * dei campi dell'utente che si sta modificando.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation". OPPURE la factory deve essere in modalità "modifica", ossia deve essere
	 * stato chiamato in precedenza il metodo "startEditing".
	 * 
	 * @return la mappa <Field, FieldValue> non completa di valori
	 */
	public Map<Field, FieldValue> getProvisionalFieldValues() {
		// Verifico di essere in modalità "creazione di un nuovo utente" o in modalità "modifica di un vecchio utente"
		if (!creationOn && !editingOn) {
			throw new IllegalStateException(ILLEGAL_MODE_EXCEPTION);
		}
		/* Nota: il caso in cui entrambi i flag siano veri non viene filtrato esplicitamente dall'IF, ma è 
		 * verificato implicitamente dal momento che per settare uno dei due flag a "true", l'altro deve essere
		 * "false", e questa cosa è fattibile solo nei metodi "startCreation" e "startEditing".
		 */
		
		return this.provisionalFieldValues;
	}
	
	/**
	 * Restituisce un valore testuale per la visualizzazione del valore del campo.
	 * Nel caso in cui il campo non sia ancora stato inizializzato, viene restituita una stringa di 
	 * default "- - - - -".
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation". OPPURE la factory deve essere in modalità "modifica", ossia deve essere
	 * stato chiamato in precedenza il metodo "startEditing".
	 * 
	 * @param f Il campo di cui si vuole ottenere il valore come stringa
	 * @return Un testo rappresentante il valore del campo richiesto
	 */
	public String getProvisionalFieldValueString(Field f) {
		// Verifico di essere in modalità "creazione di un nuovo utente" o in modalità "modifica di un vecchio utente"
		if (!creationOn && !editingOn) {
			throw new IllegalStateException(ILLEGAL_MODE_EXCEPTION);
		}
		/* Nota: il caso in cui entrambi i flag siano veri non viene filtrato esplicitamente dall'IF, ma è 
		 * verificato implicitamente dal momento che per settare uno dei due flag a "true", l'altro deve essere
		 * "false", e questa cosa è fattibile nei metodi "startCreation" e "startEditing".
		 */
		
		FieldValue value = this.provisionalFieldValues.get(f);
		if (value != null) {
			return value.toString();
		} else {
			return EMPTY_FIELDVALUE_STRING;
		}
	}

	/**
	 * Metodo che annulla la creazione o la modifica dell'utente e provoca il reset della fabbrica.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation". OPPURE la factory deve essere in modalità "modifica", ossia deve essere
	 * stato chiamato in precedenza il metodo "startEditing".
	 * 
	 * Postcondizione: al termine della chiamata la factory non è più in modalità "creazione" o "modifica".
	 * Tutti i valori immessi fino a questo momento vengono cancellati, tutte le modifiche fatte fino a
	 * questo momento vengono annullate. Nel caso di modifica di un utente, l'utente viene ripristinato
	 * a com'era quando è stato chiamato il metodo "startEditing".
	 * 
	 */
	public void cancel() {
		// Verifico di essere in modalità "creazione di un nuovo utente" o in modalità "modifica di un vecchio utente"
		if (!creationOn && !editingOn) {
			throw new IllegalStateException(ILLEGAL_MODE_EXCEPTION);
		}
		/* Nota: il caso in cui entrambi i flag siano veri non viene filtrato esplicitamente dall'IF, ma è 
		 * verificato implicitamente dal momento che per settare uno dei due flag a "true", l'altro deve essere
		 * "false", e questa cosa è fattibile nei metodi "startCreation" e "startEditing".
		 */
		
		this.creationOn = false;
		this.editingOn = false;
		
		// Reset di tutte gli attributi
		this.provisionalFieldValues = null;
		this.currentEditedUser = null;
	}

	/**
	 * Termina la creazione o la modifica di un utente con tutti i campi acquisiti finora.
	 * <ul>
	 * 		<li> Nel caso in cui sia stata avviata una creazione con il metodo "startCreation", restituisce un nuovo oggetto {@linkplain User}.
	 * 		<li> Nel caso in cui sia stata avviata una modifica con il metodo "startEditing", restituisce l'utente passato come
	 * parametro all'inizio, contenente i nuovi valori dei campi.
	 * </ul> 
	 * 
	 * <b>Nota:</b> Il valore di ritorno, in questo secondo caso, non è fondamentale, poiché si tratta solo 
	 * di una reference ad un oggetto che il metodo o la classe chiamante dovrebbero avere già.
	 * Volendo, perciò, sarebbe possibile continuare ad usare il riferimento "vecchio" senza sovrascriverlo.
	 * Si faccia però attenzione ad una cosa: le modifiche operate mediante questa factory non vengono attuate
	 * sull'oggetto {@link User} se non <emph>dopo</emph> la chiamata di questo metodo. Pertanto:
	 * <ol>
	 * 		<li> Non sarà possibile ottenere una modifica dell'oggetto {@link User} in tempo reale.
	 * 		<li> Qualunque chiamata esterna a metodi propri della classe {@linkplain User} che modifichino i campi dell'utente (esempio: setFieldValue, setAllFieldValues, etc..)
	 * verranno sovrascritti al momento della chiamata del metodo "finalise()".
	 * </ol>
	 * 
	 * <ul>
	 * 
	 * <li>Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation". OPPURE la factory deve essere in modalità "modifica", ossia deve essere
	 * stato chiamato in precedenza il metodo "startEditing".
	 * </li>
	 * 
	 * <li>Precondizione: tutti i campi obbligatori devono essere stati inizializzati. Per verificare questa condizione
	 * è possibile invocare (cosa che viene fatta anche in questo metodo) il metodo "verifyMandatoryFields".
	 * </li>
	 * 
	 * </ul>
	 * 
	 * @return Il nuovo utente creato o l'utente modificato.
	 */
	public User finalise() {
		// Verifico di essere in modalità "creazione di un nuovo utente" o in modalità "modifica di un vecchio utente"
		if (!creationOn && !editingOn) {
			throw new IllegalStateException(ILLEGAL_MODE_EXCEPTION);
		}
		/* Nota: il caso in cui entrambi i flag siano veri non viene filtrato esplicitamente dall'IF, ma è 
		 * verificato implicitamente dal momento che per settare uno dei due flag a "true", l'altro deve essere
		 * "false", e questa cosa è fattibile nei metodi "startCreation" e "startEditing".
		 */
		
		User finalisedUser = null;
		
		if (creationOn) {
			// Creo il nuovo oggetto User
			finalisedUser = new User(this.provisionalFieldValues);
			
		} else if (editingOn) {
			// Modifico l'oggetto User vecchio
			finalisedUser = this.currentEditedUser;
			finalisedUser.setAllFieldValues(this.provisionalFieldValues);
			
		} else {
			// WTF
			throw new IllegalArgumentException("ERROREEEEE"); // Non dovrebbe succedere
		}
		
		// Azzero i campi provvisori
		this.provisionalFieldValues = null;
		this.currentEditedUser = null;
		
		// Termino la modalità creazione o la modalità modifica
		this.creationOn = false;
		this.editingOn = false;
		
		// Restituisco l'evento
		return finalisedUser;
	}

	/* METODI PRIVATI DI UTILITA' */

	/**
	 * Metodo che acquisisce e restituisce il valore di un campo "UserField".
	 * 
	 * Precondizione: Il campo che si vuole acquisire deve essere contenuto nell'enum {@link UserField},
	 * ossia deve essere uno dei campi previsti per la caratterizzazione di un utente.
	 * 
	 * @param field Il campo di cui si vuole acquisire il valore
	 * @return Il valore acquisito
	 */
	private FieldValue acquireUserFieldValue(UserField field) {
		switch (field) {
		
		case NICKNAME :
		{
			boolean okFlag = false;
			StringFieldValue nick = null;
			// Prompt e interazione con l'utente
			do {
				nick = StringFieldValue.acquireValue(renderer, getter);

				// Verifiche
				if (this.usersManager.isNicknameExisting(nick.toString())) {
					renderer.renderError("Questo nickname è già utilizzato, per favore scegli un altro nickname personale.");
				} else {
					okFlag = true;
				}
			} while (!okFlag);

			// Restituzione del valore acquisito
			return nick;
			
		}
			
		case DATA_DI_NASCITA :
		{
			boolean okFlag = false;
			LocalDateFieldValue date = null;
			// Prompt e interazione con l'utente
			do {
				try {
					date = LocalDateFieldValue.acquireValue(renderer, getter);
				}
				catch (DateTimeException e) {
					renderer.renderError("Data non accettabile");
					continue;
				}
				
				// Verifiche
				if (date.getLocalDate().isAfter(LocalDate.now())) {
					renderer.renderError("Impossibile accettare una data futura");
				} else if (date.getLocalDate().isAfter(LocalDate.now().minusYears(AGE_LIMIT))) {
					renderer.renderError(String.format(
							"Per utilizzare questo programma devi avere almeno %d anni.\nInserisci la tua vera data di nascita o termina l'esecuzione del programma.",
							AGE_LIMIT));
				} else {
					okFlag = true;
				}
				
			} while (!okFlag);
			
			// Restituzione del valore acquisito
			return date;
		}
		
		case CATEGORIE_DI_INTERESSE : 
		{
			// Inizializzo le variabili ausiliarie
			int option = 0;
			CategoryProvider provider = CategoryProvider.getProvider();
			CategoryEnum [] categories = CategoryEnum.values();
			boolean [] checksArray = new boolean[categories.length];
			
			// Creo la variabile che conterrà il valore finale del campo
			CategoryListFieldValue list = (CategoryListFieldValue) this.provisionalFieldValues.get(UserField.CATEGORIE_DI_INTERESSE);
			
			// Verifico se ho già un valore inizializzato
			if (list != null) {
				for (int i = 0; i < categories.length; i++) {
					if (list.contains(categories[i])) {
						checksArray[i] = true;
					}
				}
			} else {
				list = new CategoryListFieldValue();
			}
			
			// Ciclo di interazione con l'utente
			do {
				renderer.renderText("Seleziona le categorie a cui sei interessato/a:");
				// Per ciascuna categoria creo e visualizzo l'opzione relativa
				for (int i = 0; i < categories.length; i++) {
					renderer.renderText(String.format(
							"%3d) %-50s [%s]",
							(i + 1),
							provider.getCategory(categories[i]).getName(),
							(checksArray[i] ? "X" : " ")
							));
				}
				renderer.renderText(String.format("%3d) %-50s", 0, "Esci e conferma"));
				renderer.renderLineSpace();
				option = getter.getInteger(0, categories.length);
				
				// Inverto il check dell'opzione selezionata
				if (option != 0) {
					checksArray[option - 1] ^= true;
				}
				// Continuo finché l'utente non decide di uscire
			} while (option != 0);

			for (int i = 0; i < categories.length; i++) {
				if (checksArray[i]) {
					list.addCategory(categories[i]);
				} else {
					list.removeCategory(categories[i]);
				}
			}
			// Restituzione del dato acquisito
			return list;
		}
		
		}
		
		// Se non matcho nulla
		throw new IllegalArgumentException("Il campo non corrisponde ad alcun campo della categoria \"Partita di calcio\"");
	}
	
}
