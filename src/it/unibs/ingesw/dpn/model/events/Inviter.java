package it.unibs.ingesw.dpn.model.events;

import java.util.ArrayList;
import java.util.List;

import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.users.Invite;
import it.unibs.ingesw.dpn.model.users.User;

public class Inviter {
	
	private Event target;
	private List<User> notInvited;
	private List<User> invited = new ArrayList<>();
		
	public Inviter(Event target, ModelManager model) {
		this.target = target;
		notInvited = model.getEventBoard()
				.getListOfOldSubscribersFromPastEvents(target.getCreator());
		
	}
	/**
	 * metodo che prende un utente della lista delle persone da non invitare e 
	 * lo mette in quella degli utenti da invitare 
	 * @param invite: utente da invitare
	 * @return false se il processo non è andato a buon fine
	 */
	public boolean addInvitation(User invite) {
		if(!notInvited.contains(invite))
			return false;
		notInvited.remove(invite);
		invited.add(invite);
		return true;
	}
	/**
	 * Metodo che prende un utente dell lista degli utenti da invitare e lo mette
	 * nella lista di quelli da non invitare 
	 * @param toRemove: utente da rimuovere dalla lista degli utenti da invitare
	 * @return false se il processo non è andato a buon fine
	 */
	public boolean removeInvitation(User toRemove) {
		if(!invited.contains(toRemove))
				return false;
		notInvited.add(toRemove);
		invited.remove(toRemove);
		return true;
	}
	/**
	 * Metodo che restituisce la lista degli eventi da invitare
	 */
	public List<User> getInvited(){
		return invited;
	}
	/**
	 * Metodo che restituisce la lista degli utenti da non invitare
	 */
	public List<User> getNotInvited(){
		return notInvited;
	}
	//TODO
	public void sendInvites() {
		for(User p : invited) {
			p.getMailbox().deliver(new Invite(this.target));
		}
	}
}
