package it.unibs.ingesw.dpn.model.users;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;

public class LoginLogoutTest {
	
	private static final String TEST_USER_NICKNAME = "Utente di test";
	private static final String ANOTHER_USER_NICKNAME = "Altro utente di test";
	private static User testUser;
	private static User anotherUser;
	
	private LoginManager loginManager;
	private UsersRepository users;
	
	@BeforeClass
	public static void createTestUsers() {
		
		testUser = new User();
		testUser.setFieldValue(UserField.NICKNAME, new StringFieldValue(TEST_USER_NICKNAME));
		
		anotherUser = new User();
		anotherUser.setFieldValue(UserField.NICKNAME, new StringFieldValue(ANOTHER_USER_NICKNAME));
		
	}
	
	@Before
	public void createObjects() {
		
		this.loginManager = new LoginManager();
		this.users = new UsersRepository();
		
	}

	@Test
	public void loginFails_whenUserNotExisting_andNoCurrentUser() {
		
		boolean success = loginManager.login(users, TEST_USER_NICKNAME);
		
		assertFalse(success);
		assertEquals(loginManager.getCurrentUser(), null);
		
	}
	
	@Test(expected = IllegalStateException.class)
	public void loginFails_whenUserExists_butAnotherUserIsLogged() {
		
		users.addUser(testUser);
		users.addUser(anotherUser);
		
		boolean success1 = loginManager.login(users, ANOTHER_USER_NICKNAME);
		assertTrue(success1);
		
		@SuppressWarnings("unused")
		boolean success2 = loginManager.login(users, TEST_USER_NICKNAME);
		fail("L'istruzione precedente avrebbe dovuto generare un'eccezione");
		
	}
	
	@Test
	public void loginSucceds_whenUserExists_andNoCurrentUser() {
		
		users.addUser(testUser);
		
		boolean success = loginManager.login(users, TEST_USER_NICKNAME);
		
		assertTrue(success);
		assertEquals(loginManager.getCurrentUser(), testUser);
		
	}
	
	@Test(expected = IllegalStateException.class)
	public void loginFails_whenUserNotExisting_andAnotherUserIsLogged() {
		
		users.addUser(anotherUser);
		
		boolean success1 = loginManager.login(users, ANOTHER_USER_NICKNAME);
		assertTrue(success1);
		assertEquals(loginManager.getCurrentUser(), anotherUser);
		
		@SuppressWarnings("unused")
		boolean success2 = loginManager.login(users, TEST_USER_NICKNAME);
		fail("L'istruzione precedente avrebbe dovuto generare un'eccezione");
		
	}
	
	@Test
	public void logoutSucceds_whenUserLogged() {
		
		users.addUser(testUser);
		
		boolean success = loginManager.login(users, TEST_USER_NICKNAME);
		assertTrue(success);
		assertEquals(loginManager.getCurrentUser(), testUser);
		
		loginManager.logout();
		assertEquals(loginManager.getCurrentUser(), null);
		
	}
	
	@Test(expected = IllegalStateException.class)
	public void logoutFails_whenNoUserIsLogged() {
		
		loginManager.logout();
		fail("L'istruzione precedente avrebbe dovuto generare un'eccezione");
		
	}

}
