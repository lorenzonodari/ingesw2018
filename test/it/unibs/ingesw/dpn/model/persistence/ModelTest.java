package it.unibs.ingesw.dpn.model.persistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.junit.Test;

import it.unibs.ingesw.dpn.model.events.EventBoard;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

public class ModelTest {
	EventBoard mockEB = mock(EventBoard.class);
	UsersRepository mockUR = mock(UsersRepository.class);
	Model test = new Model(mockEB, mockUR);
	@Test
	public void testGetEventBoard() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUsersRepository() {
		fail("Not yet implemented");
	}

}
