package it.unibs.ingesw.dpn.model.persistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.junit.Test;

import it.unibs.ingesw.dpn.model.events.EventBoard;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

public class ModelTest {
	EventBoard mockEB = mock(EventBoard.class);
	UsersRepository mockUR = mock(UsersRepository.class);
	Model modelTest = new Model(mockEB, mockUR);
	
	@Test
	public void testGetEventBoard() {
		EventBoard eb = modelTest.getEventBoard();
		assertEquals(mockEB, eb);
	}

	@Test
	public void testGetUsersRepository() {
		UsersRepository ur = modelTest.getUsersRepository();
		assertEquals(mockUR, ur);
	}

}
