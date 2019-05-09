package it.unibs.ingesw.dpn.model.events;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Classe che si occupa di memorizzare la storia di un evento.
 * Al suo interno contiene una struttura LIFO per memorizzare tutti gli avvenimenti
 * notevoli dell'evento.
 * 
 * @author Michele Dusi
 *
 */
public class EventHistory {
	
	/**
	 * Classe interna che implementa un singolo avvenimento da memorizzare all'interno della storia.
	 * E' di fatto una struttura che accorpa un messaggio testuale e una data.
	 * 
	 * @author Michele Dusi
	 *
	 */
	private class Log {
		
		private String message;
		private Date timestamp;
		
		public Log(String message) {
			this.message = message;
			this.timestamp = new Date();
		}

		public Date getDate() {
			return this.timestamp;
		}
		
		public String getMessage() {
			return this.message;
		}
	}
	
	private Deque<Log> chronology;
	
	/**
	 * Costruttore vuoto della classe.
	 */
	public EventHistory() {
		this.chronology = new LinkedList<>();
	}
	
	/**
	 * Elimina tutti gli aggiornamenti della cronologia memorizzati finora.
	 * 
	 * Postcondizione: la cronologia alla fine è vuota.
	 */
	void reset() {
		this.chronology.clear();
	}
	
	/**
	 * Aggiunge un nuovo aggiornamento alla cronologia.
	 * 
	 * @param message Il messaggio dell'aggiornamento
	 */
	public void addLog(String message) {
		if (message == null) {
			throw new IllegalArgumentException();
		} else {
			this.chronology.addFirst(new Log(message));
		}
	}

	/**
	 * Restituisce il messaggio dell'ultimo aggiornamento.
	 * 
	 * @return il messaggio dell'ultimo aggiornamento
	 */
	public String getLastMessage() {
		return this.chronology.peekFirst().getMessage();
	}
	
	/**
	 * Restituisce la data dell'ultimo aggiornamento.
	 * 
	 * @return la data dell'ultimo aggiornamento
	 */
	public Date getLastUpdate() {
		return this.chronology.peekFirst().getDate();
	}
	
	/**
	 * Restituisce tutti i messaggi salvati come array di stringhe.
	 * 
	 * @return L'array dei messaggi
	 */
	public String [] getAllMessages() {
		// Verifico che non sia vuoto
		if (this.chronology.isEmpty()) {
			return null;
		} else {
			// Preparo l'array
			String [] results = new String[this.chronology.size()];
			int i = 0;
			for (Log l : this.chronology) {
				// Salvo ciascun messaggio nell'array (dal più recente al meno recente)
				results[i] = l.getMessage();
				i++;
			}
			return results;
		}
	}

}
