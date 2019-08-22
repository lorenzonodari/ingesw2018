package it.unibs.ingesw.dpn.model.users;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import it.unibs.ingesw.dpn.model.users.*;

public class LoginManagerTest {
	
	LoginManager test = new LoginManager();
	UsersRepository mockur = mock(UsersRepository.class);
	private String String;


	@Test
	public void loginReturnTrue_whenThereIsTheUser() {
		User maybeCurrent = mock(User.class);
		when(mockur.getUser(String)).thenReturn(maybeCurrent);
		assertTrue(test.login(mockur, String));
	}
	
	@Test
	public void loginReturnFalse_whenThereIsNotTheUser() {
		when(mockur.getUser(String)).thenReturn(null);
		assertFalse(test.login(mockur, String));
	}
	@Test
	public void testLogout() {
		test.logout();
		assertEquals(test.getCurrentUser(), null);
	}

	@Test
	public void testGetCurrentUser() {
		User maybeCurrent = mock(User.class);
		when(mockur.getUser(String)).thenReturn(maybeCurrent);
		test.login(mockur, String);
		assertEquals(test.getCurrentUser(), maybeCurrent);
	}

}
