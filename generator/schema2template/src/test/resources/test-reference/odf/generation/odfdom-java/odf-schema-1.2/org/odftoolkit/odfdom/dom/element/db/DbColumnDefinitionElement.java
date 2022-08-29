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

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.db;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.db.DbDataTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbIsAutoincrementAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbIsEmptyAllowedAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbIsNullableAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbPrecisionAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbScaleAttribute;
import org.odftoolkit.odfdom.dom.attribute.db.DbTypeNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeBooleanValueAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeCurrencyAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeDateValueAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeStringValueAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeTimeValueAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element db:column-definition}.
 *
 */
public class DbColumnDefinitionElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.DB, "column-definition");

	/**
	 * Create the instance of <code>DbColumnDefinitionElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public DbColumnDefinitionElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element db:column-definition}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbDataTypeAttribute</code> , See {@odf.attribute db:data-type}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDbDataTypeAttribute() {
		DbDataTypeAttribute attr = (DbDataTypeAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "data-type");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbDataTypeAttribute</code> , See {@odf.attribute db:data-type}
	 *
	 * @param dbDataTypeValue   The type is <code>String</code>
	 */
	public void setDbDataTypeAttribute(String dbDataTypeValue) {
		DbDataTypeAttribute attr = new DbDataTypeAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(dbDataTypeValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbIsAutoincrementAttribute</code> , See {@odf.attribute db:is-autoincrement}
	 *
	 * @return - the <code>Boolean</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Boolean getDbIsAutoincrementAttribute() {
		DbIsAutoincrementAttribute attr = (DbIsAutoincrementAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "is-autoincrement");
		if (attr != null) {
			return Boolean.valueOf(attr.booleanValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbIsAutoincrementAttribute</code> , See {@odf.attribute db:is-autoincrement}
	 *
	 * @param dbIsAutoincrementValue   The type is <code>Boolean</code>
	 */
	public void setDbIsAutoincrementAttribute(Boolean dbIsAutoincrementValue) {
		DbIsAutoincrementAttribute attr = new DbIsAutoincrementAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setBooleanValue(dbIsAutoincrementValue.booleanValue());
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbIsEmptyAllowedAttribute</code> , See {@odf.attribute db:is-empty-allowed}
	 *
	 * @return - the <code>Boolean</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Boolean getDbIsEmptyAllowedAttribute() {
		DbIsEmptyAllowedAttribute attr = (DbIsEmptyAllowedAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "is-empty-allowed");
		if (attr != null) {
			return Boolean.valueOf(attr.booleanValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbIsEmptyAllowedAttribute</code> , See {@odf.attribute db:is-empty-allowed}
	 *
	 * @param dbIsEmptyAllowedValue   The type is <code>Boolean</code>
	 */
	public void setDbIsEmptyAllowedAttribute(Boolean dbIsEmptyAllowedValue) {
		DbIsEmptyAllowedAttribute attr = new DbIsEmptyAllowedAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setBooleanValue(dbIsEmptyAllowedValue.booleanValue());
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbIsNullableAttribute</code> , See {@odf.attribute db:is-nullable}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDbIsNullableAttribute() {
		DbIsNullableAttribute attr = (DbIsNullableAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "is-nullable");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbIsNullableAttribute</code> , See {@odf.attribute db:is-nullable}
	 *
	 * @param dbIsNullableValue   The type is <code>String</code>
	 */
	public void setDbIsNullableAttribute(String dbIsNullableValue) {
		DbIsNullableAttribute attr = new DbIsNullableAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(dbIsNullableValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbNameAttribute</code> , See {@odf.attribute db:name}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDbNameAttribute() {
		DbNameAttribute attr = (DbNameAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbNameAttribute</code> , See {@odf.attribute db:name}
	 *
	 * @param dbNameValue   The type is <code>String</code>
	 */
	public void setDbNameAttribute(String dbNameValue) {
		DbNameAttribute attr = new DbNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(dbNameValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbPrecisionAttribute</code> , See {@odf.attribute db:precision}
	 *
	 * @return - the <code>Integer</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getDbPrecisionAttribute() {
		DbPrecisionAttribute attr = (DbPrecisionAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "precision");
		if (attr != null) {
			return Integer.valueOf(attr.intValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbPrecisionAttribute</code> , See {@odf.attribute db:precision}
	 *
	 * @param dbPrecisionValue   The type is <code>Integer</code>
	 */
	public void setDbPrecisionAttribute(Integer dbPrecisionValue) {
		DbPrecisionAttribute attr = new DbPrecisionAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setIntValue(dbPrecisionValue.intValue());
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbScaleAttribute</code> , See {@odf.attribute db:scale}
	 *
	 * @return - the <code>Integer</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getDbScaleAttribute() {
		DbScaleAttribute attr = (DbScaleAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "scale");
		if (attr != null) {
			return Integer.valueOf(attr.intValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbScaleAttribute</code> , See {@odf.attribute db:scale}
	 *
	 * @param dbScaleValue   The type is <code>Integer</code>
	 */
	public void setDbScaleAttribute(Integer dbScaleValue) {
		DbScaleAttribute attr = new DbScaleAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setIntValue(dbScaleValue.intValue());
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DbTypeNameAttribute</code> , See {@odf.attribute db:type-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDbTypeNameAttribute() {
		DbTypeNameAttribute attr = (DbTypeNameAttribute) getOdfAttribute(OdfDocumentNamespace.DB, "type-name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DbTypeNameAttribute</code> , See {@odf.attribute db:type-name}
	 *
	 * @param dbTypeNameValue   The type is <code>String</code>
	 */
	public void setDbTypeNameAttribute(String dbTypeNameValue) {
		DbTypeNameAttribute attr = new DbTypeNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(dbTypeNameValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeBooleanValueAttribute</code> , See {@odf.attribute office:boolean-value}
	 *
	 * @return - the <code>Boolean</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Boolean getOfficeBooleanValueAttribute() {
		OfficeBooleanValueAttribute attr = (OfficeBooleanValueAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "boolean-value");
		if (attr != null) {
			return Boolean.valueOf(attr.booleanValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeBooleanValueAttribute</code> , See {@odf.attribute office:boolean-value}
	 *
	 * @param officeBooleanValueValue   The type is <code>Boolean</code>
	 */
	public void setOfficeBooleanValueAttribute(Boolean officeBooleanValueValue) {
		OfficeBooleanValueAttribute attr = new OfficeBooleanValueAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setBooleanValue(officeBooleanValueValue.booleanValue());
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeCurrencyAttribute</code> , See {@odf.attribute office:currency}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getOfficeCurrencyAttribute() {
		OfficeCurrencyAttribute attr = (OfficeCurrencyAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "currency");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeCurrencyAttribute</code> , See {@odf.attribute office:currency}
	 *
	 * @param officeCurrencyValue   The type is <code>String</code>
	 */
	public void setOfficeCurrencyAttribute(String officeCurrencyValue) {
		OfficeCurrencyAttribute attr = new OfficeCurrencyAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(officeCurrencyValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeDateValueAttribute</code> , See {@odf.attribute office:date-value}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getOfficeDateValueAttribute() {
		OfficeDateValueAttribute attr = (OfficeDateValueAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "date-value");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeDateValueAttribute</code> , See {@odf.attribute office:date-value}
	 *
	 * @param officeDateValueValue   The type is <code>String</code>
	 */
	public void setOfficeDateValueAttribute(String officeDateValueValue) {
		OfficeDateValueAttribute attr = new OfficeDateValueAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(officeDateValueValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeStringValueAttribute</code> , See {@odf.attribute office:string-value}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getOfficeStringValueAttribute() {
		OfficeStringValueAttribute attr = (OfficeStringValueAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "string-value");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeStringValueAttribute</code> , See {@odf.attribute office:string-value}
	 *
	 * @param officeStringValueValue   The type is <code>String</code>
	 */
	public void setOfficeStringValueAttribute(String officeStringValueValue) {
		OfficeStringValueAttribute attr = new OfficeStringValueAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(officeStringValueValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeTimeValueAttribute</code> , See {@odf.attribute office:time-value}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getOfficeTimeValueAttribute() {
		OfficeTimeValueAttribute attr = (OfficeTimeValueAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "time-value");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeTimeValueAttribute</code> , See {@odf.attribute office:time-value}
	 *
	 * @param officeTimeValueValue   The type is <code>String</code>
	 */
	public void setOfficeTimeValueAttribute(String officeTimeValueValue) {
		OfficeTimeValueAttribute attr = new OfficeTimeValueAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(officeTimeValueValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeValueAttribute</code> , See {@odf.attribute office:value}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>Double</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Double getOfficeValueAttribute() {
		OfficeValueAttribute attr = (OfficeValueAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "value");
		if (attr != null) {
			return Double.valueOf(attr.doubleValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeValueAttribute</code> , See {@odf.attribute office:value}
	 *
	 * @param officeValueValue   The type is <code>Double</code>
	 */
	public void setOfficeValueAttribute(Double officeValueValue) {
		OfficeValueAttribute attr = new OfficeValueAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setDoubleValue(officeValueValue.doubleValue());
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>OfficeValueTypeAttribute</code> , See {@odf.attribute office:value-type}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getOfficeValueTypeAttribute() {
		OfficeValueTypeAttribute attr = (OfficeValueTypeAttribute) getOdfAttribute(OdfDocumentNamespace.OFFICE, "value-type");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>OfficeValueTypeAttribute</code> , See {@odf.attribute office:value-type}
	 *
	 * @param officeValueTypeValue   The type is <code>String</code>
	 */
	public void setOfficeValueTypeAttribute(String officeValueTypeValue) {
		OfficeValueTypeAttribute attr = new OfficeValueTypeAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(officeValueTypeValue);
	}

  /**
   * Accept an visitor instance to allow the visitor to do some operations. Refer to visitor design
   * pattern to get a better understanding.
   *
   * @param visitor an instance of DefaultElementVisitor
   */
	@Override
	public void accept(ElementVisitor visitor) {
		if (visitor instanceof DefaultElementVisitor) {
			DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
			defaultVisitor.visit(this);
		} else {
			visitor.visit(this);
		}
	}
}