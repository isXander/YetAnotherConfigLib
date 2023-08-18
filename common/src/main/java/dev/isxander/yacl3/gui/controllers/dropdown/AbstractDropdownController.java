package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDropdownController<T> implements IStringController<T> {
	protected final Option<T> option;
	private final List<String> allowedValues;
	public final DropdownMode allowMode;

	/**
	 * Constructs a dropdown controller
	 *
	 * @param option bound option
	 * @param allowedValues possible values
	 */
	protected AbstractDropdownController(Option<T> option, List<String> allowedValues, DropdownMode allowMode) {
		this.option = option;
		this.allowedValues = allowedValues;
		this.allowMode = allowMode;
	}

	protected AbstractDropdownController(Option<T> option) {
		this(option, Collections.emptyList(), DropdownMode.ALLOW_VALUES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Option<T> option() {
		return option;
	}

	private List<String> getAllowedValuesOr(String additionalEntry) {
		if (allowedValues.contains(additionalEntry)) return allowedValues;
		List<String> values = new ArrayList<>(allowedValues);
		values.add(additionalEntry);
		return values;
	}
	public List<String> getAllowedValues() {
		return switch (allowMode) {
			case ALLOW_VALUES -> allowedValues;
			case ALLOW_EMPTY -> getAllowedValuesOr("");
			case ALLOW_ANY -> getAllowedValuesOr(getString());
		};
	}

	public boolean isValueValid(String value) {
		return allowMode == DropdownMode.ALLOW_ANY || getAllowedValues().contains(value);
	}

	protected String getValidValue(String value) {
		return getValidValue(value, 0);
	}
	protected String getValidValue(String value, int offset) {
		if (offset == -1) return getString();
		List<String> activeValues;
		if (allowMode == DropdownMode.ALLOW_ANY) activeValues = getAllowedValuesOr(value);
		else activeValues = getAllowedValues();

		return activeValues.stream()
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
