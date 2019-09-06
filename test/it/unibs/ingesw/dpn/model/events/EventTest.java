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
		
		// Mocks configuration
		EventState initialState = mock(EventState.class);
		when(initialState.canDoPublication()).thenReturn(true);
		when(initialState.getStateName()).thenReturn("TEST STATE");
		when(initialState.canDoSubscription()).thenReturn(true);
		
		// Test code
		Event event = this.testEvent;
		event.setState(initialState);
		event.publish();
		
		assertTrue(event.hasSubscriber(TestEvent.CREATOR));
		
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
	public void subscribeTest_whenCanNotDo() throws Exception {
		
		// Mocks configuration
		User user = mock(User.class);
		EventState state = mock(EventState.class);
		when(state.getStateName()).thenReturn("TEST STATE");
		when(state.canDoSubscription()).thenReturn(false);
		
		// Test code
		Event event = this.testEvent;
		event.setState(state);

		try {
			
			event.subscribe(user);
		
		}
		catch (IllegalStateException ex) {
			
			return;
		}
		
		fail("Questo test dovrebbe generare un'eccezione");
		
	}
	
	@Test
	public void subscribeTest_whenCanDoAndCreator() {
		
		// Mock configurations
		EventState state = mock(EventState.class);
		when(state.getStateName()).thenReturn("TEST STATE");
		when(state.canDoSubscription()).thenReturn(true);
		
		// Test code
		Event event = this.testEvent;
		event.setState(state);
		event.subscribe(TestEvent.CREATOR);
		
		verify(TestEvent.CREATOR, never()).receive(isA(Notification.class));
		assertTrue(event.hasSubscriber(TestEvent.CREATOR));
		
	}
	
	@Test
	public void subscribeTest_whenCanDo() {
		
		// Mocks configuration
		User user = mock(User.class);
		EventState state = mock(EventState.class);
		when(state.getStateName()).thenReturn("TEST STATE");
		when(state.canDoSubscription()).thenReturn(true);
		
		// Test code
		Event event = this.testEvent;
		event.setState(state);
		event.setFieldValue(CommonField.QUOTA_INDIVIDUALE, new MoneyAmountFieldValue(5.0f));
		event.subscribe(user);
		
		verify(user, times(1)).receive(isA(Notification.class));
		assertTrue(event.hasSubscriber(user));
		
	}
	
	@Test
	public void unsubscribeTest_whenCanDo() throws Exception {
		
		// Mocks configuration
		User user = mock(User.class);
		OptionalCostsFieldValue userDependantField = mock(OptionalCostsFieldValue.class);
		EventState state = mock(EventState.class);
		when(state.getStateName()).thenReturn("TEST STATE");
		when(state.canDoUnsubscription()).thenReturn(true);
		
		// Test code
		Event event = new ConferenceEvent(TestEvent.CREATOR);
		event.setFieldValue(CommonField.TITOLO, new StringFieldValue("TEST EVENT"));
		event.setFieldValue(ConferenceField.SPESE_OPZIONALI, userDependantField);
		event.setState(state);
		
		java.lang.reflect.Field partecipantsField = Event.class.getDeclaredField("partecipants");
		partecipantsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<User> partecipants = (List<User>) partecipantsField.get(event);
		partecipants.add(user);
		
		event.unsubscribe(user);
		
		verify(userDependantField, times(1)).forgetUserCustomization(user);
		verify(user, times(1)).receive(isA(Notification.class));
		assertFalse(event.hasSubscriber(user));		
		
	}
	
	@Test
	public void unsubscribeTest_whenCanNotDo() throws Exception {
		
		// Mock configuration
		User user = mock(User.class);
		
		// Test code
		Event event = this.testEvent;
		
		try {
			
			event.unsubscribe(user);
			
		}
		catch (IllegalStateException ex) {
			
			return;
			
		}
		
		fail("Questo test dovrebbe sollevare un'eccezione");
		
	}

}
