package it.unibs.ingesw.dpn.model.users;

public class LoginManager {
	
	private User currentUser;

	public LoginManager() {
		this.currentUser = null;
	}
	
	/**
	 * Effettua il login con lo nickname dato.
	 * Se lo nickname non è presente all'interno del sistema, il metodo restituisce "false".
	 * Se invece il login va a buon fine, il metodo restituisce "true".
	 * 
	 * Precondizione: nessun altro utente deve essere attualmente connesso al sistema.
	 * 
	 * @param user L'utente da connettere al sistema
	 * 
	 * @return Un valore booleano che indica se l'utente si è connesso correttamente.
	 */
	public boolean login(UsersRepository users, String nickname) {
		
		// Verifica delle precondizioni
		if (this.currentUser != null) {
			throw new IllegalStateException();
		}
		
		User user = users.getUser(nickname);
		
		// Verifico se l'utente non esiste
		if (user == null) {
			return false;			
		}
		
		// Se invece esiste
		this.currentUser = user;
		return true;		
	}
	
	/**
	 * Disconnette l'utente attualmente connesso al sistema.
	 * 
	 * Precondizione: un utente deve essere attualmente connesso al sistema
	 * 
	 */
	public void logout() {
		
		// Verifica delle precondizioni
		if (this.currentUser == null) {
			throw new IllegalStateException();
		}
		
		this.currentUser = null;
		
	}
	
	/**
	 * Restituisce l'utente attualmente connesso al sistema.
	 * 
	 * @return L'utente attualmente connesso al sistema
	 */
	public User getCurrentUser() {
	
		return this.currentUser;
		
	}

}
