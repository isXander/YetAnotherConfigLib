package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDropdownController<T> implements IStringController<T> {
	protected final Option<T> option;
	private final List<String> allowedValues;
	public final boolean allowEmptyValue;
	public final boolean allowAnyValue;

	/**
	 * Constructs a dropdown controller
	 *
	 * @param option bound option
	 * @param allowedValues possible values
	 */
	protected AbstractDropdownController(Option<T> option, List<String> allowedValues, boolean allowEmptyValue, boolean allowAnyValue) {
		this.option = option;
		this.allowedValues = allowedValues;
		this.allowEmptyValue = allowEmptyValue;
		this.allowAnyValue = allowAnyValue;
	}

	protected AbstractDropdownController(Option<T> option, List<String> allowedValues) {
		this(option, allowedValues, false, false);
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
		return getAllowedValues("");
	}
	public List<String> getAllowedValues(String inputField) {
		List<String> values = new ArrayList<>(allowedValues);
		if (allowEmptyValue && !values.contains("")) values.add("");
		if (allowAnyValue && !inputField.isBlank() && !allowedValues.contains(inputField)) {
			values.add(inputField);
		}
		String currentValue = getString();
		if (allowAnyValue && !allowedValues.contains(currentValue)) {
			values.add(currentValue);
		}
		return values;
	}

	public boolean isValueValid(String value) {
		if (value.isBlank()) return allowEmptyValue;
		return allowAnyValue || getAllowedValues().contains(value);
	}

	protected String getValidValue(String value) {
		return getValidValue(value, 0);
	}
	protected String getValidValue(String value, int offset) {
		if (offset == -1) return getString();

		return getAllowedValues(value).stream()
				.filter(val -> val.toLowerCase().contains(value.toLowerCase()))
				.sorted((s1, s2) -> {
					if (s1.startsWith(value) && !s2.startsWith(value)) return -1;
					if (!s1.startsWith(value) && s2.startsWith(value)) return 1;
					return s1.compareTo(s2);
				})
				.skip(offset)
				.findFirst()
				.orElseGet(this::getString);
	}

}
