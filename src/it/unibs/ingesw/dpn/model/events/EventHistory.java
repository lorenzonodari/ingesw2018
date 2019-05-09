package it.unibs.ingesw.dpn.model.events;

import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Classe che si occupa di memorizzare la storia di un evento.
 * Al suo interno contiene una struttura LIFO (implementata tramite una Deque) 
 * per memorizzare tutti gli avvenimenti notevoli dell'evento.
 * Gli aggiornamenti più recenti sono contenuti in cima alla pila, perciò vengono utilizzati i metodi
 * <code>peekFirst()</code> e <code>addFirst()</code> dell'interfaccia {@link Deque}.
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
		
		private static final String LOG_FORMAT = "%s - %s";
		
		private String message;
		private Date timestamp;
		
		/**
		 * Costruttore.
		 * 
		 * @param message Il messsaggio da associare al log
		 */
		public Log(String message) {
			if (message == null) {
				throw new IllegalArgumentException();
			} else {
				this.message = message;
				this.timestamp = new Date();
			}
		}

		/**
		 * Restituisce la data del log.
		 * 
		 * @return La data del log
		 */
		public Date getDate() {
			return this.timestamp;
		}
		
		/**
		 * Restituisce il messaggio del log.
		 * 
		 * @return Il messaggio del log
		 */
		public String getMessage() {
			return this.message;
		}
		
		/**
		 * Restituisce la stringa descrittiva del log, contenente data e messaggio.
		 * 
		 * @return la stringa descrittiva del log
		 */
		public String toString() {
			return String.format(LOG_FORMAT, this.getDate().toString(), this.getMessage());
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
	 * Restituisce la data dell'ultimo aggiornamento.
	 * 
	 * @return la data dell'ultimo aggiornamento
	 */
	public Date getLastUpdate() {
		return this.chronology.peekFirst().getDate();
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
	 * Questo metodo restituisce gli N messaggi più recenti contenuti all'interno del log.
	 * Se il numero di messaggi richiesto è superiore al numero di messaggi contenuti nella cronologia,
	 * viene semplicemente restituita tutta la cronologia (come nel metodo <code>getAllMessages()</code>).
	 * 
	 * Precondizione: deve essere presente almeno un log di un avvenimento all'interno, altrimenti il 
	 * metodo restituisce "null". Questa condizione è soddisfatta se l'evento ha uno stato definito e 
	 * la cronologia di questa {@link EventHistory} non è stata appena cancellata.
	 * 
	 * @param numMessages Il numero di messaggi da restituire
	 * @return Gli ultimi N messaggi registrati nella cronologia
	 */
	public String [] getLastNMessages(int numMessages) {
		// Verifico che il numero di messaggi sia positivo
		if (numMessages <= 0) {
			throw new IllegalArgumentException("L'argomento deve essere positivo");
		} else if (this.chronology.isEmpty()) { // Verifico che non sia vuota la cronologia
			return null;
		}
		// Tutte le condizioni sono soddisfatte
		// Preparo l'iterator
		Iterator<Log> iterator = this.chronology.iterator();
		// Preparo l'array vuoto
		String [] lastMessages = new String[numMessages];
		// Ciclo sui primi N elementi (o eventualmente su tutti gli elementi della cronologia)
		for (int i = 0; i < numMessages && iterator.hasNext(); i++) {
			lastMessages[i] = iterator.next().getMessage();
		}
		// Restituisco l'array di messaggi
		return lastMessages;
	}
	
	/**
	 * Restituisce tutti i messaggi salvati come array di stringhe.
	 * 
	 * Precondizione: deve essere presente almeno un log di un avvenimento all'interno, altrimenti il 
	 * metodo restituisce "null". Questa condizione è soddisfatta se l'evento ha uno stato definito e 
	 * la cronologia di questa {@link EventHistory} non è stata appena cancellata.
	 * 
	 * @return L'array dei messaggi
	 */
	public String [] getAllMessages() {
		return this.getLastNMessages(this.chronology.size());
		/*
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
		*/
	}
	
	/**
	 * Restituisce la descrizione completa di tutti gli avvenimenti salvati all'interno dell'istanza
	 * di {@link EventHistory}, comprendente per ciascuno la data di invio e il messaggio associato.
	 * 
	 * Precondizione: deve essere presente almeno un log di un avvenimento all'interno, altrimenti il 
	 * metodo restituisce "null". Questa condizione è soddisfatta se l'evento ha uno stato definito e 
	 * la cronologia di questa {@link EventHistory} non è stata appena cancellata.
	 * 
	 * @return L'elenco completo di tutti gli avvenimenti dell'evento associato
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Log l : this.chronology) {
			s.append(l.toString() + "\n");
		}
		return s.toString();
	}

}
