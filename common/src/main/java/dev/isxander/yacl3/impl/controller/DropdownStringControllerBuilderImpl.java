package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.DropdownStringControllerBuilder;
import dev.isxander.yacl3.gui.controllers.dropdown.DropdownStringController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DropdownStringControllerBuilderImpl extends StringControllerBuilderImpl implements DropdownStringControllerBuilder {
	private List<String> values;
	private boolean allowEmpty = false;

	public DropdownStringControllerBuilderImpl(Option<String> option) {
		super(option);
	}

	@Override
	public DropdownStringControllerBuilderImpl values(List<String> values) {
		this.values = values;
		return this;
	}

	@Override
	public DropdownStringControllerBuilderImpl values(String... values) {
		this.values = Arrays.asList(values);
		return this;
	}

	@Override
	public DropdownStringControllerBuilder allowEmpty(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
		return this;
	}

	@Override
	public Controller<String> build() {
		if (allowEmpty && !values.contains("")) {
			// We need to duplicate the list because it might have been passed in as an immutable list
			values = new ArrayList<>(values);
			values.add("");
		}
		return new DropdownStringController(option, values);
	}

}
