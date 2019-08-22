package it.unibs.ingesw.dpn.model.persistence;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;

public class DiskSerializationStrategyTest {
	
	File testfile = mock(File.class);
	DiskSerializationStrategy testdss = new DiskSerializationStrategy(testfile);
	
	@Test
	public void sameDatabaseFile_whenItExists() {
		fail("non possibile da testare tramite test di unità");
	}

	@Test
	public void testSaveModel() {
		fail("non possibile da testare tramite test di unità");
	}

}
