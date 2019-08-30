package it.unibs.ingesw.dpn.model.events;

import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

/**
 * Classe adibita all'invio di notifiche relative alla creazione di un nuovo evento rientrante nelle
 * categorie di interesse dei destinatari. Questa classe e' stata creata sulla base del pattern GRASP
 * Pure Fabrication, al fine di migliorare la coesione della classe Inviter, che precedentemente si occupava
 * di cio', oltre che dei suoi altri compiti. 
 * 
 * @author Lorenzo Nodari
 *
 */
public class NewEventNotifier {
	
	private Event target;
	private UsersRepository users;
	
	public NewEventNotifier(Event target, UsersRepository users) {
		
		this.target = target;
		this.users = users;
		
	}
	
	/**
	 * Metodo che invia  notifiche agli utenti che hanno selezionato la categoria dell'evento come categoria di interesse
	 */
	public void sendNotifications() {
		
		StringBuffer notificationContent = new StringBuffer("Un evento appartenente ad una tua categoria di interesse è appena stato creato: ");
		notificationContent.append(target.getTitle());
		
		for(User u : users.getUserByCategoryOfInterest(target.getCategory())) {
			
			if (u == target.getCreator()) {
				continue;
			}
			
			u.receive(new Notification(notificationContent.toString()));
		}
		
	}

}
