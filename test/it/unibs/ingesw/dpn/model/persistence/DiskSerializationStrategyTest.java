package it.unibs.ingesw.dpn.model.persistence;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import it.unibs.ingesw.dpn.model.events.ConferenceEvent;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventBoard;
import it.unibs.ingesw.dpn.model.events.EventState;
import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DiskSerializationStrategyTest {
	
	private static final String TEST_USER_NICKNAME = "Test user";
	private static final String TEST_EVENT_TITLE = "Evento di test";
	private static final File TEST_DB_FILE = new File("serialization_test.db");
	
	private static final long ENDING_DATE_MILLIS = 2524678200000L; 		// 01/01/2050 @ 7:30
	private static final long SUBSCRIPTION_END_MILLIS = 2524677900000L; // 01/01/2050 @ 7:25
	
	private static final UsersRepository testUsers = new UsersRepository();
	private static final EventBoard testBoard = new EventBoard();
	
	@BeforeClass
	public static void createTestDatabase() throws Exception {
		
		User aUser = new User();
		aUser.setFieldValue(UserField.NICKNAME, new StringFieldValue(TEST_USER_NICKNAME));
		
		Event anEvent = new ConferenceEvent(aUser);
		anEvent.setFieldValue(CommonField.TITOLO, new StringFieldValue(TEST_EVENT_TITLE));
		anEvent.setFieldValue(CommonField.NUMERO_DI_PARTECIPANTI, new IntegerFieldValue(100));
		anEvent.setFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE, new DateFieldValue(SUBSCRIPTION_END_MILLIS));
		anEvent.setFieldValue(CommonField.DATA_E_ORA, new DateFieldValue(ENDING_DATE_MILLIS));
		anEvent.setDefaultFieldValues();
				
		
		testUsers.addUser(aUser);
		testBoard.addEvent(anEvent);
		
		ObjectOutputStream output = new ObjectOutputStream(
										new FileOutputStream(TEST_DB_FILE));
		
		output.writeObject(testBoard);
		output.writeObject(testUsers);
		output.close();
		
	}
	
	@AfterClass
	public static void cleanUp() {
		
		TEST_DB_FILE.delete();
		
	}
	
	@Test
	public void loadedModelIsEmpty_whenDatabaseDoesNotExist() {
		
		File unexistentFile = new File("i/do/not/exist");
		DiskSerializationStrategy strategy = new DiskSerializationStrategy(unexistentFile);
		Model output = null;
		
		try {
			output = strategy.loadModel();
		}
		catch (PersistenceException ex) {
			fail("Questa eccezione non dovrebbe essere sollevata");
		}
		
		assertNotEquals(output, null);
		
		EventBoard eventBoard = output.getEventBoard();
		UsersRepository users = output.getUsersRepository();
		
		assertNotEquals(eventBoard, null);
		assertNotEquals(users, null);
		
		assertTrue(eventBoard.isEmpty());
		assertTrue(users.isEmpty());
		
	}
	
	@Test
	public void loadedModelIsCorrect_whenDatabaseExists() throws Exception {
		
		DiskSerializationStrategy strategy = new DiskSerializationStrategy(TEST_DB_FILE);
		Model output = strategy.loadModel();
		
		EventBoard eventBoard = output.getEventBoard();
		UsersRepository users = output.getUsersRepository();
		
		assertEquals(eventBoard.getEvents().size(), 1);
		assertFalse(users.isEmpty());
		
		Event loadedEvent = eventBoard.getEvents().get(0);
		User loadedUser = users.getUser(TEST_USER_NICKNAME);
		
		assertNotEquals(loadedUser, null);
		assertEquals(loadedEvent.getTitle(), TEST_EVENT_TITLE);
		assertEquals(loadedEvent.getState(), EventState.OPEN);
		
	}
	
	@Test
	public void savedModelIsCorrect() throws Exception {
		
		File database = new File("save_model_test.db");
		DiskSerializationStrategy strategy = new DiskSerializationStrategy(database);
		Model model = new Model(testBoard, testUsers);
		strategy.saveModel(model);
		
		assertTrue(database.exists());
		assertTrue(database.canRead());
		
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(database));
		
		EventBoard loadedBoard = (EventBoard) input.readObject();
		UsersRepository loadedUsers = (UsersRepository) input.readObject();
		
		input.close();
		
		assertEquals(loadedBoard.getEvents().size(), 1);
		assertFalse(loadedUsers.isEmpty());
		
		User loadedUser = loadedUsers.getUser(TEST_USER_NICKNAME);
		Event loadedEvent = loadedBoard.getEvents().get(0);
		
		assertNotEquals(loadedUser, null);
		assertEquals(loadedEvent.getTitle(), TEST_EVENT_TITLE);	
		assertEquals(loadedEvent.getState(), EventState.OPEN);
		
	}

}
