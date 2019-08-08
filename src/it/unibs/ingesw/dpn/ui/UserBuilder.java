package it.unibs.ingesw.dpn.ui;

import java.util.Arrays;

import it.unibs.ingesw.dpn.model.fields.AbstractBuilder;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.users.User;

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
public class UserBuilder extends AbstractBuilder {
	
	/**
	 * Costruttore pubblico.
	 * 
	 * @param acquirer L'acquisitore di FieldValue
	 */
	public UserBuilder(FieldValueAcquirer acquirer) {
		super(acquirer);
	}
	
	/**
	 * Comincia la creazione di un utente.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni o modifiche in corso. Una factory può costruire un solo
	 * utente alla volta, secondo il processo descritto nell'introduzione alla classe.
	 */
	public void startCreation() {
		// Richiamo il metodo padre
		super.startCreation(new User(Arrays.asList(UserField.values())));
	}
	
}