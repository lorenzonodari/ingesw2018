package it.unibs.ingesw.dpn.model.users;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import it.unibs.ingesw.dpn.model.users.Notification;

public class NotificationTest {
	
	private Notification test;
	private String msg = "testnotifica";
	// penso non abbia senso testare la correttezza della data
	
	@Before
	public void setUp() throws Exception {
		test = new Notification(msg);
		
	}

	@Test
	public void testmsg() {
		assertEquals("Messaggio sbagliato", msg, test.getMessage());
	}

}
