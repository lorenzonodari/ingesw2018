package it.unibs.ingesw.dpn.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe che permette la creazione di utenti in maniera "controllata", secondo il pattern "Factory" e
 * secondo un preciso processo.
 * 
 * Per la creazione di un utente è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - metodi di acquisizione dati (acquireUsername, acquireBirthday, ...)
 * - finalizeCreation(..);
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class UserFactory {

	/** Input/Output */
	private final UIRenderer renderer;
	private final InputGetter getter;
	
	/** Flag per mantenere lo stato della factory */
	private boolean creationOn = false;

	/** Attributi che aiutano la creazione di un evento */
	private String provisionalUsername = null;
	private LocalDate provisionalBirthday = null;

	/** Stringhe */
	private static final String EMPTY_FIELDVALUE_STRING = "- - - - -";
	private static final String CREATION_MODE_OFF_EXCEPTION = "Impossibile acquisire dati se non è stata inizializzata la creazione di un nuovo evento";
	
	/** Parametri */
	private static final int AGE_LIMIT = 12;

	/**
	 * Costruttore pubblico.
	 * 
	 * @param renderer Il renderizzatore dei prompt e dei messaggi d'errore
	 * @param getter L'acquisitore di dati primitivi
	 */
	public UserFactory(UIRenderer renderer, InputGetter getter) {
		this.renderer = renderer;
		this.getter = getter;
	}
	

	/**
	 * Comincia la creazione di un utente.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni in corso. Una factory puà costruire un solo
	 * evento alla volta, secondo il processo descritto nell'introduzione alla classe.
	 * 
	 * @param defaultUsername Il username di default con cui cominciare l'iscrizione
	 */
	public void startCreation(String defaultUsername) {		
		if (this.creationOn) {
			// E' già in corso la creazione di un evento, non è possibile cominciarne una nuova
			throw new IllegalStateException("Impossibile cominciare la creazione di un nuovo evento: una creazione di un evento è già in corso");
		} else {
			// Comincio la creazione di un nuovo evento
			this.creationOn = true;
		}
		
		// Preparo i campi vuoti, pronti per essere inizializzati
		this.provisionalUsername = defaultUsername;
		this.provisionalBirthday = null;
		
	}

	/**
	 * Acquisisce una stringa NON VUOTA come username dell'utente che si sta creando.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * TODO Implementare la verifica che lo username non sia già usato.
	 */
	public void acquireUsername() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Prompt e interazione con l'utente
		renderer.renderText("Inserisci il username del nuovo utente:");
		this.provisionalUsername = getter.getString(); // TODO CONTROLLARE CHE IL NICK SIA DISPONIBILE
	}
	
	/**
	 * Acquisisce la data di nascita dell'utente che si sta creando.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 */
	public void acquireBirthday() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		boolean okFlag = false;
		LocalDate date = null;
		// Prompt e interazione con l'utente
		do {
			renderer.renderText("Inserisci il giorno in formato (GG/MM/AAAA)");
			String data = getter.getMatchingString(
					"(0?([1-9])|[1-2][0-9]|3([0-1]))" // Giorno
					+ DateFieldValue.DATE_DELIMITER // Divisore
					+ "(0?([1-9])|1([0-2]))" // Mese
					+ DateFieldValue.DATE_DELIMITER // Divisore
					+ "(19|20|21)([0-9][0-9])"); // Anno
			// Estraggo i dati
			Scanner scanDate = new Scanner(data);
			scanDate.useDelimiter(DateFieldValue.DATE_DELIMITER);		
			int giorno = scanDate.nextInt();
			int mese = scanDate.nextInt();
			int anno = scanDate.nextInt();
			scanDate.close();
			
			date = LocalDate.of(anno, mese, giorno);
			
			// Verifiche
			if (date.isAfter(LocalDate.now())) {
				renderer.renderError("Impossibile accettare una data futura");
			} else if (date.isAfter(LocalDate.now().minusYears(AGE_LIMIT))) {
				renderer.renderError(String.format(
						"Per utilizzare questo programma devi avere almeno %d anni.\nInserisci la tua vera data di nascita o termina l'esecuzione del programma.",
						AGE_LIMIT));
			} else {
				okFlag = true;
			}
			
		} while (!okFlag);
		
		this.provisionalBirthday = date;
	}
	
	/**
	 * Metodo che verifica se sono stati compilati TUTTI i campi obbligatori previsti.
	 * In tal caso restituisce true, altrimenti false.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return "true" se tutti i campi obbligatori sono stati compilati
	 */
	public boolean verifyMandatoryFields() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Verifico tutti i campi
		if (this.provisionalUsername == null ) {
			return false;
//		} else if (this.provisionalBirthday == null) { 
//			return false;
			/* NOTA : Al momento la compilazione della data NON è obbligatoria */
		} else {
			return true;
		}
	}

	/**
	 * Restituisce un valore testuale per il username dell'utente.
	 * Nel caso in cui il username non sia ancora stato inizializzato, viene restituita una stringa di 
	 * default "- - - - -".
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return Un testo rappresentante il valore del campo username
	 */
	public String getProvisionalUsernameString() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		// Restituisco un valore in base all'inizializzazione
		return (this.provisionalUsername != null ? 
				this.provisionalUsername :
					EMPTY_FIELDVALUE_STRING);
		
	}
	
	/** Formato della data */
	private static final String DATE_FORMAT_STRING = "dd/MM/yyyy";
	
	/**
	 * Restituisce un valore testuale per la data di nascita dell'utente.
	 * Nel caso in cui la data non sia ancora stata inizializzata, viene restituita una stringa di 
	 * default "- - - - -".
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return Un testo rappresentante il valore della data di nascita
	 */
	public String getProvisionalBirthdayString() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		// Restituisco un valore in base all'inizializzazione
		return (this.provisionalBirthday != null ? 
				this.provisionalBirthday.format(DateTimeFormatter.ofPattern(DATE_FORMAT_STRING)) :
					EMPTY_FIELDVALUE_STRING);
	}
	
	/**
	 * Metodo che annulla la creazione dell'utente e provoca il reset della fabbrica.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Postcondizione: al termine della chiamata la factory non è più in modalità creazione.
	 * Tutti i valori immessi fino a questo momento vengono cancellati.
	 * 
	 */
	public void cancelCreation() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		this.creationOn = false;
		
		// Reset di tutte gli attributi
		this.provisionalUsername = null;
		this.provisionalBirthday = null;
	}
	
	/**
	 * Termina la creazione di un utente con tutti i campi acquisiti finora.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * Precondizione: tutti i campi obbligatori devono essere stati inizializzati. Per verificare questa condizione
	 * è possibile invocare (cosa che viene fatta anche in questo metodo) il metodo "verifyMandatoryFields".
	 * 
	 * @return L'utente creato correttamente
	 */
	public User finalizeCreation() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Creo l'effettivo oggetto User
		User newUser = new User(this.provisionalUsername, this.provisionalBirthday);
		
		// Azzero i campi provvisori
		this.provisionalUsername = null;
		this.provisionalBirthday = null;
		
		// Termino la modalità creazione
		this.creationOn = false;
		
		// Restituisco l'evento
		return newUser;
	}
	
}
