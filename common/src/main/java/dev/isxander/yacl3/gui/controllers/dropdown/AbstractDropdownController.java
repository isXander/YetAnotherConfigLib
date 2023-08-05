package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;

import java.util.Collections;
import java.util.List;

public abstract class AbstractDropdownController<T> implements IStringController<T> {
	protected final Option<T> option;
	private final List<String> allowedValues;

	/**
	 * Constructs a dropdown controller
	 *
	 * @param option bound option
	 * @param allowedValues possible values
	 */
	protected AbstractDropdownController(Option<T> option, List<String> allowedValues) {
		this.option = option;
		this.allowedValues = allowedValues;
	}

	protected AbstractDropdownController(Option<T> option) {
		this(option, Collections.emptyList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Option<T> option() {
		return option;
	}

	public List<String> getAllowedValues() {
		return allowedValues;
	}

	public boolean isValueValid(String value) {
		return getAllowedValues().contains(value);
	}

	protected String getValidValue(String value) {
		return getValidValue(value, 0);
	}
	protected String getValidValue(String value, int offset) {
		if (offset == -1) return getString();
		return getAllowedValues().stream()
				.filter(val -> val.toLowerCase().contains(value.toLowerCase()))
				.sorted()
				.skip(offset)
				.findFirst()
				.orElseGet(this::getString);
	}


}
