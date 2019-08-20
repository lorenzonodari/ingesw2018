package it.unibs.ingesw.dpn.model.events;

import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

import it.unibs.ingesw.dpn.model.users.User;

public class EventBoardTest {
	
	@Test
	public void noEventIsReturned_whenBoardIsEmpty() {
		
		EventBoard board = new EventBoard();
		assertTrue(board.getEvents().size() == 0);
		
	}
	
	@Test
	public void allEventsAreReturned_whenBoardHasEvents() {
		
		// Mock configuration
		Event event1 = mock(Event.class);
		Event event2 = mock(Event.class);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(event1);
		board.addEvent(event2);
		
		assertTrue(board.getEvents().contains(event1));
		assertTrue(board.getEvents().contains(event2));
		
	}
	
	@Test
	public void eventRemoved_whenWithdrawable() {
		
		// Mock configuration
		Event event = mock(Event.class);
		when(event.withdraw()).thenReturn(true);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(event);
		
		assertTrue(board.removeEvent(event));
		assertFalse(board.getEvents().contains(event));
		
	}
	
	@Test
	public void eventNotRemoved_whenNotWithdrawable() {
		
		// Mock configuration
		Event event = mock(Event.class);
		when(event.withdraw()).thenReturn(false);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(event);
		
		assertFalse(board.removeEvent(event));
		assertTrue(board.getEvents().contains(event));
		
	}
	
	@Test
	public void eventsByStateTest() {
		
		// Mock configuration
		Event openEvent1 = mock(Event.class);
		when(openEvent1.getState()).thenReturn("A");
		
		Event openEvent2 = mock(Event.class);
		when(openEvent2.getState()).thenReturn("A");
		
		Event closedEvent = mock(Event.class);
		when(closedEvent.getState()).thenReturn("B");
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(openEvent1);
		board.addEvent(openEvent2);
		board.addEvent(closedEvent);
		List<Event> closedEvents = board.getEventsByState("B");
		
		assertTrue(closedEvents.contains(closedEvent));
		assertTrue(closedEvents.size() == 1);
		
	}
	
	@Test
	public void eventsByAuthorTest() {
		
		// Mock configuration
		User user1 = mock(User.class);
		User user2 = mock(User.class);
		
		// Evento creato da user 1 in stato APERTO
		Event user1Event1 = mock(Event.class);
		when(user1Event1.getCreator()).thenReturn(user1);
		when(user1Event1.getState()).thenReturn(EventState.OPEN);
		
		// Evento creato da user 1 in stato CHIUSO
		Event user1Event2 = mock(Event.class);
		when(user1Event2.getCreator()).thenReturn(user1);
		when(user1Event2.getState()).thenReturn(EventState.CLOSED);
		
		// Evento creato da user 2 in stato APERTO
		Event user2Event1 = mock(Event.class);
		when(user2Event1.getCreator()).thenReturn(user2);
		when(user2Event1.getState()).thenReturn(EventState.OPEN);
		
		// Evento creato da user 2 in stato CHIUSO
		Event user2Event2 = mock(Event.class);
		when(user2Event2.getCreator()).thenReturn(user2);
		when(user2Event2.getState()).thenReturn(EventState.CLOSED);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(user1Event1);
		board.addEvent(user1Event2);
		board.addEvent(user2Event1);
		board.addEvent(user2Event2);
		
		// Il metodo deve ritornare solo gli eventi creati da user2, in stato APERTO
		List<Event> user2Events = board.getEventsByAuthor(user2);
		assertTrue(user2Events.contains(user2Event1));
		assertTrue(user2Events.size() == 1);
		
	}
	
	@Test
	public void resetEventsTest() {
		
		// Mock configuration
		Event event1 = mock(Event.class);
		Event event2 = mock(Event.class);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(event1);
		board.addEvent(event2);
		board.resetEventStates();
		
		verify(event1, times(1)).resetState();
		verify(event2, times(1)).resetState();
		
	}
	
	@Test
	public void openSubscriptionsByUserTest() {
		
		// Mock configuration
		User user = mock(User.class);
		
		// Evento APERTO al quale l'utente di interesse e' iscritto
		Event event1 = mock(Event.class);
		when(event1.getState()).thenReturn(EventState.OPEN);
		when(event1.hasSubscriber(user)).thenReturn(true);
		
		// Evento CHIUSO al quale l'utente di interesse e' iscritto
		Event event2 = mock(Event.class);
		when(event2.getState()).thenReturn(EventState.CLOSED);
		when(event2.hasSubscriber(user)).thenReturn(true);
		
		// Evento APERTO al quale l'utente di interesse NON e' iscritto
		Event event3 = mock(Event.class);
		when(event3.getState()).thenReturn(EventState.OPEN);
		when(event3.hasSubscriber(user)).thenReturn(false);
		
		// Evento CHIUSO al quale l'utente di interesse NON e' iscritto
		Event event4 = mock(Event.class);
		when(event4.getState()).thenReturn(EventState.CLOSED);
		when(event4.hasSubscriber(user)).thenReturn(false);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(event1);
		board.addEvent(event2);
		board.addEvent(event3);
		board.addEvent(event4);
		List<Event> foundEvents = board.getOpenSubscriptionsByUser(user);
		
		assertTrue(foundEvents.contains(event1));
		assertTrue(foundEvents.size() == 1);
		
	}
	
	@Test
	public void openSubscriptionsNotProposedByUserTest() {
		
		// Mock configuration
		User ourUser = mock(User.class);
		User anotherUser = mock(User.class);
		
		// Evento APERTO al quale l'utente di interesse e' iscritto poiche' creatore
		Event event1 = mock(Event.class);
		when(event1.getState()).thenReturn(EventState.OPEN);
		when(event1.hasSubscriber(ourUser)).thenReturn(true);
		when(event1.getCreator()).thenReturn(ourUser);
		
		// Evento CHIUSO al quale l'utente di interesse e' iscritto poiche' creatore
		Event event2 = mock(Event.class);
		when(event2.getState()).thenReturn(EventState.CLOSED);
		when(event2.hasSubscriber(ourUser)).thenReturn(true);
		when(event2.getCreator()).thenReturn(ourUser);
		
		// Evento APERTO al quale l'utente di interesse NON e' iscritto
		Event event3 = mock(Event.class);
		when(event3.getState()).thenReturn(EventState.OPEN);
		when(event3.hasSubscriber(ourUser)).thenReturn(false);
		
		// Evento CHIUSO al quale l'utente di interesse NON e' iscritto
		Event event4 = mock(Event.class);
		when(event4.getState()).thenReturn(EventState.CLOSED);
		when(event4.hasSubscriber(ourUser)).thenReturn(false);
		
		// Evento APERTO al quale l'utente di interesse e' iscritto MA non e' creatore
		Event event5 = mock(Event.class);
		when(event5.getState()).thenReturn(EventState.OPEN);
		when(event5.hasSubscriber(ourUser)).thenReturn(true);
		when(event5.getCreator()).thenReturn(anotherUser);
		
		// Evento CHIUSO al quale l'utente di interesse e' iscritto MA non e' creatore
		Event event6 = mock(Event.class);
		when(event6.getState()).thenReturn(EventState.CLOSED);
		when(event6.hasSubscriber(ourUser)).thenReturn(true);
		when(event6.getCreator()).thenReturn(anotherUser);
		
		// Test code
		EventBoard board = new EventBoard();
		board.addEvent(event1);
		board.addEvent(event2);
		board.addEvent(event3);
		board.addEvent(event4);
		board.addEvent(event5);
		board.addEvent(event6);
		List<Event> foundEvents = board.getOpenSubscriptionsNotProposedByUser(ourUser);
		
		assertTrue(foundEvents.contains(event5));
		assertTrue(foundEvents.size() == 1);
		
	}
	
	

	

}
