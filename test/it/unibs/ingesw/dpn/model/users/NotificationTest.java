package it.unibs.ingesw.dpn.model.users;

import static org.junit.Assert.*;

import org.junit.Test;

public class NotificationTest {
	
	@Test
	public void getMessageTest() {
		
		Notification n = new Notification("Test");
		
		assertEquals("Test", n.getMessage());
		
	}

}
