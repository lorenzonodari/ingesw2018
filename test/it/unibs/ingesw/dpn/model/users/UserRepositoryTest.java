package it.unibs.ingesw.dpn.model.users;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

public class UserRepositoryTest {

	UsersRepository test = new UsersRepository();
	
	@Test
	public void testGetUser_whenCantFindUsername() {
		User t1 = mock(User.class);
		when(t1.getNickname()).thenReturn("");
		test.addUser(t1);
		User t2 = mock(User.class);
		when(t2.getNickname()).thenReturn("");
		test.addUser(t2);
		User t3 = mock(User.class);
		when(t3.getNickname()).thenReturn("");
		test.addUser(t3);
		User t4 = mock(User.class);
		when(t4.getNickname()).thenReturn("");
		test.addUser(t4);
		
		assertEquals(test.getUser("gigi"), null);
	}
	public void testGetUser_whenCanFindUsername() {
		User t1 = mock(User.class);
		when(t1.getNickname()).thenReturn("");
		test.addUser(t1);
		User t2 = mock(User.class);
		when(t2.getNickname()).thenReturn("");
		test.addUser(t2);
		User t3 = mock(User.class);
		when(t3.getNickname()).thenReturn("gigi");
		test.addUser(t3);
		User t4 = mock(User.class);
		when(t4.getNickname()).thenReturn("");
		test.addUser(t4);
		
		assertEquals(test.getUser("gigi"), t3);
	}
	/*
	 * errore dovuto alla dichiarazione List listatest
	 */
	@Test
	public void testGetUserByCategoryOfInterest() {
		Category target = mock(Category.class);
		CategoryListFieldValue targetOnList = mock(CategoryListFieldValue.class);
		when(targetOnList.contains(target)).thenReturn(true);
		User t1 = mock(User.class);
		when(t1.getFieldValue(UserField.CATEGORIE_DI_INTERESSE)).thenReturn(null);
		test.addUser(t1);
		User t2 = mock(User.class);
		when(t2.getFieldValue(UserField.CATEGORIE_DI_INTERESSE)).thenReturn(null);
		test.addUser(t2);
		User t3 = mock(User.class);
		when(t3.getFieldValue(UserField.CATEGORIE_DI_INTERESSE)).thenReturn( targetOnList);
		test.addUser(t3);
		User t4 = mock(User.class);
		when(t4.getFieldValue(UserField.CATEGORIE_DI_INTERESSE)).thenReturn(null);
		test.addUser(t4);
		List listatest = test.getUserByCategoryOfInterest(target);
		assertTrue(listatest.contains(target));
		
	}

	@Test
	public void testIsNicknameExisting_whenExist() {
		User t1 = mock(User.class);
		when(t1.getNickname()).thenReturn("");
		test.addUser(t1);
		User t2 = mock(User.class);
		when(t2.getNickname()).thenReturn("");
		test.addUser(t2);
		User t3 = mock(User.class);
		when(t3.getNickname()).thenReturn("gigi");
		test.addUser(t3);
		User t4 = mock(User.class);
		when(t4.getNickname()).thenReturn("");
		test.addUser(t4);
		
		assertTrue(test.isNicknameExisting("gigi"));
	}


	@Test
	public void testIsNicknameExisting_whenNotExist() {
		User t1 = mock(User.class);
		when(t1.getNickname()).thenReturn("");
		test.addUser(t1);
		User t2 = mock(User.class);
		when(t2.getNickname()).thenReturn("");
		test.addUser(t2);
		User t3 = mock(User.class);
		when(t3.getNickname()).thenReturn("gigi");
		test.addUser(t3);
		User t4 = mock(User.class);
		when(t4.getNickname()).thenReturn("");
		test.addUser(t4);
		
		assertFalse(test.isNicknameExisting("maffe"));
	}

}
