/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.pkg.manifest;

import java.net.URLConnection;
import org.odftoolkit.odfdom.pkg.OdfElement;

public class OdfFileEntry {

  private FileEntryElement mFileEntryElement;

  private static final String EMPTY_STRING = "";
  private static final String DEFAULT_TYPE = "application/octet-stream";

  /** @param fileEntryElement the OdfElement of the <manifest:file-entry> */
  public OdfFileEntry(FileEntryElement fileEntryElement) {
    mFileEntryElement = fileEntryElement;
  }

  public OdfFileEntry getCopy() {
    return new OdfFileEntry((FileEntryElement) this.mFileEntryElement.cloneNode(true));
  }

  public void setPath(String path) {
    mFileEntryElement.setFullPathAttribute(path);
  }

  public String getPath() {
    return mFileEntryElement.getFullPathAttribute();
  }

  /**
   * @param mediaType of the file. Use <code>null</code> or an empty string to unset the mediaType
   *     to an empty string.
   */
  public void setMediaTypeString(String mediaType) {
    if (mediaType != null) {
      mFileEntryElement.setMediaTypeAttribute(mediaType);
    } else {
      mFileEntryElement.setMediaTypeAttribute(EMPTY_STRING);
    }
  }

  /**
   * @return the mediatype of the mandatory &lt;manifest:file-entry&gt; attribute. If no mediatype
   *     exists an empty string is returned
   */
  public String getMediaTypeString() {
    return mFileEntryElement.getMediaTypeAttribute();
  }

  /**
   * Get the media type from the given file reference
   *
   * @param fileRef the reference to the file the media type is questioned
   * @return the mediaType string of the given file reference
   */
  public static String getMediaTypeString(String fileRef) {
    String mediaType = null;

    mediaType = URLConnection.guessContentTypeFromName(fileRef);
    if (mediaType == null) {
      mediaType = DEFAULT_TYPE;
    }
    return mediaType;
  }

  public void setSize(Integer size) {
    if (size == null) {
      mFileEntryElement.removeAttributeNS(
          SizeAttribute.ATTRIBUTE_NAME.getUri(), SizeAttribute.ATTRIBUTE_NAME.getLocalName());
    } else {
      mFileEntryElement.setSizeAttribute(size);
    }
  }

  /** Get the size. */
  public Integer getSize() {
    return mFileEntryElement.getSizeAttribute();
  }

  public void setEncryptionData(EncryptionDataElement encryptionData) {
    EncryptionDataElement encryptionDataEle = getEncryptionData();
    if (encryptionData != null) {
      if (encryptionDataEle != null) {
        mFileEntryElement.replaceChild(encryptionData, encryptionDataEle);
      } else {
        mFileEntryElement.appendChild(encryptionData);
      }
    } else {
      if (encryptionDataEle != null) {
        mFileEntryElement.removeChild(encryptionDataEle);
      }
    }
  }

  /** @return null if no encryption data had been set */
  public EncryptionDataElement getEncryptionData() {
    return OdfElement.findFirstChildNode(EncryptionDataElement.class, mFileEntryElement);
  }

  /**
   * Gets the OdfElement of this OdfFileEntry.
   *
   * @return the OdfElement of this OdfFileEntry.
   */
  public FileEntryElement getOdfElement() {
    return mFileEntryElement;
  }
}
