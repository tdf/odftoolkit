package org.odftoolkit.simple.form;

/**
 * This class defines the common used constants in form handling methods.
 * 
 * @since 0.8
 */
public class FormTypeDefinition {
	/**
	 * Common used image position of form from users perspective.
	 * <p>
	 * This attibute specifies the location of an image relative to the text of
	 * a control.
	 * 
	 */
	public static enum FormImageLocation {
		BOTTOM("bottom"), CENTER("center"), END("end"), START("start"), TOP(
				"top");

		private String formImageLocation = "center";

		FormImageLocation(String location) {
			this.formImageLocation = location;
		}

		@Override
		public String toString() {
			return formImageLocation;
		}
	}

	/**
	 * Common used command type of form from users perspective. *
	 * <p>
	 * This attibute specifies the type of command to execute on a data source.
	 */
	public static enum FormCommandType {
		COMMAND("command"), QUERY("query"), TABLE("table");

		private String formCommandType = "command";

		FormCommandType(String type) {
			this.formCommandType = type;
		}

		public static FormCommandType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (FormCommandType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Form Command Type!");
		}

		@Override
		public String toString() {
			return formCommandType;
		}
	}

	// table, query, sql, sql-pass-through, value-list or table-fields
	/**
	 * Common used source type of entry list from users perspective.
	 * <p>
	 * This attibutes pecifies how to populate the entry list in a combo box or
	 * list box control.
	 */
	public static enum FormListSourceType {
		TABLE("table"), QUERY("query"), SQL("sql"), SQL_PASS_THROUGH(
				"sql-pass-through"), VALUE_LIST("value-list"), TABLE_FIELDS(
				"table-fields");

		private String formListSourceType = "sql";

		FormListSourceType(String type) {
			this.formListSourceType = type;
		}

		public static FormListSourceType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (FormListSourceType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Form Command Type!");
		}

		@Override
		public String toString() {
			return formListSourceType;
		}
	}

	/**
	 * Common used state for a check box from users perspective.
	 * <p>
	 *This attibutes pecifies the default state of a check box control.
	 */
	public static enum FormCheckboxState {
		CHECKED("checked"), UNCHECKED("unchecked"), UNKNOWN("unknown");

		private String formCheckboxState = "unchecked";

		FormCheckboxState(String state) {
			this.formCheckboxState = state;
		}

		public static FormCheckboxState enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (FormCheckboxState aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Check Box State!");
		}

		@Override
		public String toString() {
			return formCheckboxState;
		}
	}
}
