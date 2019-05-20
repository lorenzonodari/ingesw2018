package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Classe utilizzata per rappresentare le varie notifiche inviate agli utenti del sistema.
 * Una volta creata, un'istanza di Notification non puo' essere modificata.
 */
public class Notification implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -217046894204205232L;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M - HH:mm");

	private String message;
	private Date date;
	
	/**
	 * Crea una notifica con il contenuto dato. La data della notifica e' impostata automaticamente
	 * alla data in cui l'istanza viene creata.
	 * 
	 * @param msg Il messaggio della notifica
	 */
	public Notification(String msg) {
		this.message = msg;
		this.date = new Date();
	}
	
	public String getMessage() {
		return this.message;
	}
	
	
	public Date getDate() {
		return this.date;
	}
	
	/**
	 * Fornisce una comoda rappresentazione testuale della notifica
	 * 
	 * @return Una rappresentazione testuale della notifica
	 */
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		// Formato: giorno/mese - ora:minuti : messaggio
		buffer.append(DATE_FORMAT.format(this.date));
		buffer.append(" : ");
		buffer.append(this.message);
		
		
		return buffer.toString();
		
	}
}
