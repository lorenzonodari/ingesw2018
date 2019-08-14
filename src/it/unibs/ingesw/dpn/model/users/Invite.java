package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.unibs.ingesw.dpn.model.events.Event;

/**
 * Classe utilizzata per rappresentare gli inviti scambiati tra gli utenti.
 * Una volta creata, un'instanza di questa classe non puo' essere modificata.
 */
public class Invite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1835460544841249957L;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy @ HH:mm");

	private Date date;
	private Event event;
	
	/**
	 * Crea una notifica con il contenuto dato. La data della notifica e' impostata automaticamente
	 * alla data in cui l'istanza viene creata.
	 * 
	 * @param msg Il messaggio della notifica
	 */
	public Invite(Event event) {
		this.date = new Date();
		this.event = event;
	}
		
	public Date getDate() {
		return this.date;
	}
	
	public Event getEvent() {
		return this.event;
	}
	
	/**
	 * Fornisce una comoda rappresentazione testuale dell'invito
	 * 
	 * @return Una rappresentazione testuale dell'invito
	 */
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		// Formato: giorno/mese - ora:minuti : messaggio
		buffer.append(DATE_FORMAT.format(this.date));
		buffer.append(" : ");
		buffer.append(String.format("%s ti ha invitato all'evento \"%s\"", 
				                    event.getCreator().getNickname(),
				                    event.getTitle()));
		
		return buffer.toString();
		
	}
}
