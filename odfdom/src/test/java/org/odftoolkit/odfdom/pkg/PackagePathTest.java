package org.odftoolkit.odfdom.pkg;


import org.junit.Assert;
import org.junit.Test;

public class PackagePathTest {

	@Test
	public void normalizePath() {
		Assert.assertEquals(OdfPackage.normalizePath("docA1///docA2/.."), "docA1/");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/../docA2"), "docA2");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/../docA2/./."), "docA2/");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/docA2/docA3/../../docA4/"), "docA1/docA4/");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/docA2/docA3/../docA4/../../docA5/"), "docA1/docA5/");
	}

}
