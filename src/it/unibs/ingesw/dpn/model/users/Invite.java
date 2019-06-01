package it.unibs.ingesw.dpn.model.users;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.unibs.ingesw.dpn.model.events.Event;

public class Invite {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M - HH:mm");

	private String message;
	private Date date;
	private Event event;
	
	/**
	 * Crea una notifica con il contenuto dato. La data della notifica e' impostata automaticamente
	 * alla data in cui l'istanza viene creata.
	 * 
	 * @param msg Il messaggio della notifica
	 */
	public Invite(String msg, Event event) {
		this.message = msg;
		this.date = new Date();
		this.event = event;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	
	public Date getDate() {
		return this.date;
	}
	public Event getEvent() {
		return this.event;
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
