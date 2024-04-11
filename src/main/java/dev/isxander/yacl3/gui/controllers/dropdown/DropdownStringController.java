package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;

import java.util.List;

public class DropdownStringController extends AbstractDropdownController<String> {

	public DropdownStringController(Option<String> option, List<String> allowedValues, boolean allowEmptyValue, boolean allowAnyValue) {
		super(option, allowedValues, allowEmptyValue, allowAnyValue);
	}

	@Override
	public String getString() {
		return option().pendingValue();
	}

	@Override
	public void setFromString(String value) {
		option().requestSet(getValidValue(value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new DropdownStringControllerElement(this, screen, widgetDimension);
	}

}
