package org.odftoolkit.odfdom.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/** Test utitility class providing resources for the test in- and output */
public final class ResourceUtilities {
	private ResourceUtilities() {
	}

	/** The relative path of the test file will be resolved and the absolute will be returned
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return the absolute path of the test file
	 */
	public static String getAbsolutePath(String relativeFilePath) {
		URL uri = ResourceUtilities.class.getClassLoader().getResource(relativeFilePath);
		return uri.getPath();
	}

	/** The relative path of the test file will be resolved and the absolute will be returned
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return the URI created based on the relativeFilePath
	 * @throws URISyntaxException if no URI could be created from the given relative path
	 */
	public static URI getURI(String relativeFilePath) throws URISyntaxException
	{
		return ResourceUtilities.class.getClassLoader().getResource(relativeFilePath).toURI();
	}

	/** The relative path of the test file will be used to determine an absolute
	 *  path to a temporary directory in the output directory.
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return absolute path to a test output
	 * @throws IOException if no absolute Path could be created.
	 */
	public static String getTestOutput(String relativeFilePath) throws IOException {
		return File.createTempFile(relativeFilePath, null).getAbsolutePath();
	}

	/** The Input of the test file will be resolved and the absolute will be returned
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return the absolute path of the test file
	 */
	public static InputStream getTestResourceAsStream(String relativeFilePath)
	{
		return ResourceUtilities.class.getClassLoader().getResourceAsStream(relativeFilePath);
	}

	/** Relative to the test output directory a test file will be returned dependent on the relativeFilePath provided.
	 * @param relativeFilePath Path of the test output resource relative to <code>target/test-classes/</code>.
	 * @return the empty <code>File</code> of the test output (to be filled)
	 */
	public static File newTestOutputFile(String relativeFilePath)
	{
		String filepath = ResourceUtilities.class.getClassLoader().getResource("").getPath()
						+relativeFilePath;
		return new File(filepath);
	}

	/** 
	 * @return the absolute path of the test output folder, which is usually <code>target/test-classes/</code>.
	 */
	public static String getTestOutputFolder()
	{
		return ResourceUtilities.class.getClassLoader().getResource("").getPath();
	}

}
