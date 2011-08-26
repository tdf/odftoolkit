package org.odftoolkit.odfdom.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class ResourceUtilities {
	private ResourceUtilities() {
	}
	
	public static String getTestResource(String filename) {
		URL uri = ResourceUtilities.class.getClassLoader().getResource(filename);
		return uri.getPath();
	}
	
	public static URI getTestResourceURI(String filename) throws URISyntaxException
	{
		return ResourceUtilities.class.getClassLoader().getResource(filename).toURI();
	}
	
	public static String getTestOutput(String filename) throws IOException {
		return File.createTempFile(filename, null).getAbsolutePath();
	}
	
	public static InputStream getTestResourceAsStream(String filename)
	{
		return ResourceUtilities.class.getClassLoader().getResourceAsStream(filename);
	}
	
	public static File createTestResource(String filename)
	{
		String filepath = ResourceUtilities.class.getClassLoader().getResource("").getPath()
						+filename;
		return new File(filepath);
	}
	
	public static String getTestResourceFolder()
	{
		return ResourceUtilities.class.getClassLoader().getResource("").getPath();
	}

}
