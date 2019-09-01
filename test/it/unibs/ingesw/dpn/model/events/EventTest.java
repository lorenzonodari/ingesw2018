package it.unibs.ingesw.dpn.model.events;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.users.Notification;
import it.unibs.ingesw.dpn.model.users.User;

public class EventTest {
	
	@SuppressWarnings("serial")
	public static class TestEvent extends Event {
		
		static final User CREATOR = mock(User.class);
		
		public TestEvent() {
			super(CREATOR, Category.PARTITA_DI_CALCIO);
		}
		
	}
	
	private Event testEvent;
	
	@Before
	public void buildTestEvent() {
		
		// Mocks configuration
		EventState state = mock(EventState.class);
		when(state.getStateName()).thenReturn("MOCK STATE");
		

		this.testEvent = new TestEvent();
		this.testEvent.setFieldValue(CommonField.TITOLO, new StringFieldValue("Evento di test"));
		this.testEvent.setState(state);
		
	}
	
	/**
	 * Dopo ogni test dobbiamo resettare il mock del creatore dell'evento di test al fine di far
	 * dimenticare a Mockito le interazioni subite da tale mock. Cio' e' necessario per far si che le
	 * verifiche sul numero di invocazioni di metodi su di esso non vengano influenzate dagli altri test
	 */
	@After
	public void resetCreatorMock() {
		
		reset(TestEvent.CREATOR);
		
	}

	@Test 
	public void stateisValid_whenConstructed() {
		
		Event event = new TestEvent();
		assertEquals(event.getState(), EventState.VALID);
		
	}
	
	@Test
	public void publishTest_whenInCorrectState() {
		
		/*
		 * Ho provato un po' a modificare ma sto solo combinando casini.
		 * Come faccio??
		 */
		
		// Mocks configuration
		Event event = this.testEvent;
		when(event.canBePublished()).thenReturn(true);
		when(event.canSubscribe(null)).thenReturn(true);
		EventState initialState = mock(EventState.class);
		when(initialState.canDoPublication()).thenReturn(true);
		when(initialState.getStateName()).thenReturn("TEST STATE");
		event.setState(initialState);
		
		// Pubblico
		event.publish();
		
		assertTrue(event.hasSubscriber(TestEvent.CREATOR));
		assertFalse(event.canBePublished());
		assertEquals(event.getState(), EventState.OPEN);
		
	}
	
	@Test
	public void publishTest_whenInWrongState() {
		
		// Mocks configuration
		EventState initialState = mock(EventState.class);
		when(initialState.getStateName()).thenReturn("TEST STATE");
		doThrow(new IllegalStateException()).when(initialState).onPublication(this.testEvent);
		
		// Test code
		Event event = this.testEvent;
		event.setState(initialState);
		
		assertFalse(event.canBePublished());
		assertThrows(IllegalStateException.class, () -> {
			event.publish();
		});

		assertFalse(event.hasSubscriber(TestEvent.CREATOR));
		assertEquals(event.getState(), "TEST STATE");
		
	}
	
	@Test
	public void withdrawTest_whenInCorrectState() {
		
//		Event event = this.testEvent;
//		boolean success = event.withdraw();
//		
//		assertTrue(success);
		
	}
	
	@Test
	public void withdrawTest_whenInWrongState() {
		
		// Mock configurations
		EventState initialState = mock(EventState.class);
		when(initialState.getStateName()).thenReturn("TEST STATE");
		doThrow(new IllegalStateException()).when(initialState).onWithdrawal(this.testEvent);
		
		// Test code
		Event event = this.testEvent;
		event.setState(initialState);
//		boolean success = event.withdraw();
		
//		assertFalse(success);
		assertEquals(event.getState(), "TEST STATE");
		
	}
	
	@Test
	public void notifyCreatorTest() {
		
		Event event = this.testEvent;
		event.notifyCreator("TEST");
		
		verify(TestEvent.CREATOR, times(1)).receive(isA(Notification.class));
		
	}
	
	@Test
	public void notifyPartecipantsTest() throws Exception {
		
		// Mocks configuration
		User user1 = mock(User.class);
		User user2 = mock(User.class);
		
		// Test code
		Event event = this.testEvent;
		event.setFieldValue(CommonField.TOLLERANZA_NUMERO_DI_PARTECIPANTI, new IntegerFieldValue(1));
		
		Field partecipantsField = Event.class.getDeclaredField("partecipants");
		partecipantsField.setAccessible(true);
		
		@SuppressWarnings("unchecked")
		List<User> partecipants = (List<User>) partecipantsField.get(event);
		
		partecipants.add(user1);
		partecipants.add(user2);
		
		event.notifyPartecipants("TEST");
		
		verify(TestEvent.CREATOR, never()).receive(isA(Notification.class));
		verify(user1, times(1)).receive(isA(Notification.class));
		verify(user2, times(1)).receive(isA(Notification.class));
		
	}
	
	@Test
	public void subscribeTest_firstReturn() throws Exception {
		
		// Mocks configuration
		User user = mock(User.class);
		
		// Test code
		Event event = this.testEvent;
		Field partecipantsField = Event.class.getDeclaredField("partecipants");
		partecipantsField.setAccessible(true);
		
		@SuppressWarnings("unchecked")
		List<User> partecipants = (List<User>) partecipantsField.get(event);
		partecipants.add(user);
		
		event.subscribe(user);
		
//		assertFalse(success);
		assertTrue(event.hasSubscriber(user));
		
	}
	
	@Test
	public void subscribeTest_secondReturn() {
		
		// Mock configurations
		User user = mock(User.class);
		EventState state = mock(EventState.class);
		when(state.getStateName()).thenReturn("TEST STATE");
		doThrow(new IllegalStateException()).when(state).onSubscription(this.testEvent);
		
		// Test code
		Event event = this.testEvent;
		event.setState(state);
		event.subscribe(user);
		
//		assertFalse(success);
		assertFalse(event.hasSubscriber(user));
		
	}
	
	@Test
	public void subscribeTest_thirdReturn() {
		
		// Mocks configuration
		User user = mock(User.class);
		
		// Test code
		Event event = this.testEvent;
		event.setFieldValue(CommonField.QUOTA_INDIVIDUALE, new MoneyAmountFieldValue(5.0f));
//		boolean success = event.subscribe(user);
		
//		assertTrue(success);
		assertTrue(event.hasSubscriber(user));
		
	}
	
	@Test
	public void unsubscribeTest_firstReturn() {
		
		// Mocks configuration
		User notSubscriber = mock(User.class);
		
		
		// Test code
		Event event = this.testEvent;
		event.unsubscribe(notSubscriber);
//		boolean success = event.unsubscribe(notSubscriber);
		
//		assertFalse(success);
//		
	}
	
	@Test
	public void unsubscribeTest_secondReturn() throws Exception {
		
		// Mock configuration
		User aSubscriber = mock(User.class);
		EventState wrongState = mock(EventState.class);
		doThrow(new IllegalStateException()).when(wrongState).onUnsubscription(this.testEvent);
		when(wrongState.getStateName()).thenReturn("TEST STATE");
		
		// Test code
		Event event = this.testEvent;
		event.setState(wrongState);
		
		Field partecipantsField = Event.class.getDeclaredField("partecipants");
		partecipantsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<User> partecipants = (List<User>) partecipantsField.get(event);
		partecipants.add(aSubscriber);
		
//		boolean success = event.unsubscribe(aSubscriber);
		
//		assertFalse(success);
		assertTrue(partecipants.contains(aSubscriber));
		
	}
	
	@Test
	public void unsubscribeTest_thirdReturn() throws Exception {
		
		// Mock configuration
		User aSubscriber = mock(User.class);
		OptionalCostsFieldValue userDependantField = mock(OptionalCostsFieldValue.class);
		EventState correctState = mock(EventState.class);
		when(correctState.getStateName()).thenReturn("TEST STATE");
		
		// Test code
		
		// Abbiamo bisogno di un evento che contenga userDependantFields per testare
		// completamente il metodo
		Event event = new ConferenceEvent(TestEvent.CREATOR);
		event.setState(correctState);
		event.setFieldValue(ConferenceField.SPESE_OPZIONALI, userDependantField);
		event.setFieldValue(CommonField.TITOLO, new StringFieldValue("TEST EVENT"));
		
		Field partecipantsField = Event.class.getDeclaredField("partecipants");
		partecipantsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<User> partecipants = (List<User>) partecipantsField.get(event);
		partecipants.add(aSubscriber);
		
//		boolean success = event.unsubscribe(aSubscriber);
		
		verify(aSubscriber, times(1)).receive(isA(Notification.class));
		verify(userDependantField, times(1)).forgetUserCustomization(aSubscriber);
		assertFalse(partecipants.contains(aSubscriber));
//		assertTrue(success);
		
	}

}
