package it.unibs.ingesw.dpn.ui;

import java.time.LocalDate;

import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe che permette la creazione di utenti in maniera "controllata", secondo il pattern "Factory" e
 * secondo un preciso processo.
 * 
 * Per la creazione di un utente è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - metodi di acquisizione dati (acquireNickname, acquireBirthday, ...)
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
	private String provisionalNickname = null;
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
	 * @param creator L'utente che ha creato l'evento
	 * @param category La categoria dell'evento
	 */
	public void startCreation() {		
		if (this.creationOn) {
			// E' già in corso la creazione di un evento, non è possibile cominciarne una nuova
			throw new IllegalStateException("Impossibile cominciare la creazione di un nuovo evento: una creazione di un evento è già in corso");
		} else {
			// Comincio la creazione di un nuovo evento
			this.creationOn = true;
		}
		
		// Preparo i campi vuoti, pronti per essere inizializzati
		this.provisionalNickname = null;
		this.provisionalBirthday = null;
		
	}

	/**
	 * Acquisisce una stringa NON VUOTA come nickname dell'utente che si sta creando.
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * TODO Implementare la verifica che il nickname non sia già usato.
	 */
	public void acquireNickname() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		
		// Prompt e interazione con l'utente
		renderer.renderText("Inserisci il nickname del nuovo utente:");
		this.provisionalNickname = getter.getString(); // TODO CONTROLLARE CHE IL NICK SIA DISPONIBILE
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
					  "(0([1-9])|[1-2][0-9]|3([0-1]))" // Giorno
					+ "(/|-)" // Divisore
					+ "(0([1-9])|1([0-2]))" // Mese
					+ "(/|-)" // Divisore
					+ "(19|20|21)([0-9][0-9])"); // Anno
			int giorno = Integer.parseInt(data.substring(0, 2));
			int mese = Integer.parseInt(data.substring(3, 5));
			int anno = Integer.parseInt(data.substring(6, 10));
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
		if (this.provisionalNickname == null ) {
			return false;
		} else if (this.provisionalBirthday == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Restituisce un valore testuale per il nickname dell'utente.
	 * Nel caso in cui il nickname non sia ancora stato inizializzato, viene restituita una stringa di 
	 * default "- - - - -".
	 * 
	 * Precondizione: la factory deve essere in modalità "creazione", ossia deve essere stato chiamato in precedenza
	 * il metodo "startCreation".
	 * 
	 * @return Un testo rappresentante il valore del campo nickname
	 */
	public String getProvisionalNicknameString() {
		// Verifico di essere in modalità "creazione di un nuovo evento"
		if (!creationOn) {
			throw new IllegalStateException(CREATION_MODE_OFF_EXCEPTION);
		}
		// Restituisco un valore in base all'inizializzazione
		return (this.provisionalNickname != null ? 
				this.provisionalNickname :
					EMPTY_FIELDVALUE_STRING);
		
	}
	
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
				this.provisionalBirthday.toString() :
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
		this.provisionalNickname = null;
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
		User newUser = new User(this.provisionalNickname, this.provisionalBirthday);
		
		// Azzero i campi provvisori
		this.provisionalNickname = null;
		this.provisionalBirthday = null;
		
		// Termino la modalità creazione
		this.creationOn = false;
		
		// Restituisco l'evento
		return newUser;
	}
	
}
