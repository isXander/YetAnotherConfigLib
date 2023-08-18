package dev.isxander.yacl3.gui.controllers.dropdown;

public enum DropdownMode {
	/**
	 * Allows only configured values
	 */
	ALLOW_VALUES,
	/**
	 * Allows configured values plus the empty string
	 */
	ALLOW_EMPTY,
	/**
	 * Allows any values, uses configured values as suggestions
	 */
	ALLOW_ANY
}
