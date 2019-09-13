/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.odfdom.pkg;

import org.odftoolkit.odfdom.pkg.OdfPackage.OdfFile;

/**
 * This class is used for validation of the ODF Package.
 * It contains the constraint messages are taken from the OASIS ODF 1.2 part 3, the ODF package specification.
 * These messages are used by the <code>ValidationException</code> for ODF validation.
 * The validation is enabled, when an <code>ErrorHandler</code> was provided to the <code>OdfPackage</code>.
 */
public enum OdfPackageConstraint implements ValidationConstraint {
	// NOTE FOLLOWING CODE CONVENTION:
	// The first parameter is ALWAYS the source path. The OdfValidationException adds a preceding whitespace in case the source is not null.

	/** In case a ZIP entry is using neither STORED and DEFLATED as compression method.*/
	PACKAGE_ENTRY_USING_INVALID_COMPRESSION("The compression method of the ZIP entry '%2$s' is not allowed within the%1$s ODF package file!"),
	/** The ODF package shall contain the \"/META-INF/manifest.xml\" file. */
	MANIFEST_NOT_IN_PACKAGE("The ODF package%1$s shall contain the '" + OdfFile.MANIFEST.getPath() + "' file!"),
	/** A directory is not a sub-document and should not be listed in the \"/META-INF/manifest.xml\" file of ODF package. */
	MANIFEST_LISTS_DIRECTORY("The directory '%2$s' is not a sub-document and should not be listed in the '" + OdfFile.MANIFEST.getPath() + "' file of ODF package%1$s!"),
	/** A directory is a document and should be listed in the \"/META-INF/manifest.xml\" file of ODF package. */
	MANIFEST_DOES_NOT_LIST_DIRECTORY("The directory of the sub-document '%2$s' should be listed in the '" + OdfFile.MANIFEST.getPath() + "' file of ODF package%1$s!"),
	/** A file shall not be listed in the \"/META-INF/manifest.xml\" file as it does not exist in the ODF package. */
	MANIFEST_LISTS_NONEXISTENT_FILE("The file '%2$s' shall not be listed in the '" + OdfFile.MANIFEST.getPath() + "' file as it does not exist in the ODF package%1$s!"),
	/** A file shall be listed in the \"/META-INF/manifest.xml\" file as it exists in the ODF package. */
	MANIFEST_DOES_NOT_LIST_FILE("The file '%2$s' shall be listed in the '" + OdfFile.MANIFEST.getPath() + "' file as it exists in the ODF package%1$s!"),
	/** The ODF package contains a \"mediatype\" file, which content differs from the mediatype of the root document!" */
	MIMETYPE_DIFFERS_FROM_PACKAGE("The ODF package%1$s contains a '" + OdfFile.MEDIA_TYPE.getPath() + "' file containing '%2$s', which differs from the mediatype of the root document '%3$s'!"),
	/** The ODF package contains a \"mediatype\" file, but no mediatype its root document in the \"/META-INF/manifest.xml\" file of ODF package! */
	MIMETYPE_WITHOUT_MANIFEST_MEDIATYPE("The ODF package%1$s contains a '" + OdfFile.MEDIA_TYPE.getPath() + "' file containing '%2$s', but no mediatype for its root document in its '" + OdfFile.MANIFEST.getPath() + "'!"),
	/** The root document shall be listed in the \"/META-INF/manifest.xml\" file using a '/' as path within the ODF package. */
	MANIFEST_WITH_EMPTY_PATH("The root document shall be listed in the '" + OdfFile.MANIFEST.getPath() + "' file using a '/' as path within the ODF package%1$s!"),
	/** There shall be no extra field for the \"mediatype\" file of ODF package. */
	MIMETYPE_HAS_EXTRA_FIELD("There shall be no extra field for the '" + OdfFile.MEDIA_TYPE.getPath() + "' file of ODF package%1$s!"),
	/** The file \"mediatype\" shall not be compressed in the ODF package. */
	MIMETYPE_IS_COMPRESSED("The file '" + OdfFile.MEDIA_TYPE.getPath() + "' shall not be compressed in the ODF package%1$s!"),
	/** The file \"mediatype\" is not the first file in the ODF package. */
	MIMETYPE_NOT_FIRST_IN_PACKAGE("The file '" + OdfFile.MEDIA_TYPE.getPath() + "' is not the first file in the ODF package%1$s!"),
	/** The ODF package contains no \"mediatype\" file. */
	MIMETYPE_NOT_IN_PACKAGE("The ODF package%1$s contains no '" + OdfFile.MEDIA_TYPE.getPath() + "' file!"),
	/** The ODF package shall be a ZIP file. */
	PACKAGE_IS_NO_ZIP("The ODF package%1$s shall be a ZIP file!");
	private final String mMessage;

	/**
	 * Creates a ODF Package constraint
	 * @param message of the constraint */
	OdfPackageConstraint(String message) {
		mMessage = message;
	}

	/**
	 * Creates a localized description of a Constraint.
	 * Subclasses may override this method in order to produce a
	 * locale-specific message.  For subclasses that do not override this
	 * method, the default implementation returns the same result as
	 * <code>getMessage()</code>.
	 *
	 * @return  The localized description of this constraint. */
	public String getLocalizedMessage() {
//    FUTURE FEATUE: LOCALIZATION:
//    =============================
//    There should be a property files e.g. OdfPackageConstraint_de_DE.properties Sourcecode:
//    PACKAGE_IS_NO_ZIP=%s muss eine ZIP Datei sein, wie in [ZIP] definiert. Alle Dateien innerhalbe des ZIPS muessen entwder unkomprimiert (STORED) oder komprimiert sein (DEFLATED) und den DEFLATE aloritmus verwenden.
//    MANIFEST_NOT_IN_PACKAGE=.....
//    ResourceBundle bundle = ResourceBundle.getBundle(OdfPackageConstraint.class.getName());
//    return bundle.getString(this.name());
		return mMessage;
	}

	/**
	 * Returns the detail message string of this Constraint.
	 *
	 * @return  the detail message string of this <tt>Constraint</tt> instance
	 *          (which may be <tt>null</tt>).
	 */
	public String getMessage() {
		return mMessage;
	}
}
