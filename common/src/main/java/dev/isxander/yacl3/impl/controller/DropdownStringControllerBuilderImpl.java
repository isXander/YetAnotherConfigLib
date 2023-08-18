package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.DropdownStringControllerBuilder;
import dev.isxander.yacl3.gui.controllers.dropdown.DropdownMode;
import dev.isxander.yacl3.gui.controllers.dropdown.DropdownStringController;

import java.util.Arrays;
import java.util.List;

public class DropdownStringControllerBuilderImpl extends StringControllerBuilderImpl implements DropdownStringControllerBuilder {
	private List<String> values;
	private DropdownMode allowMode = DropdownMode.ALLOW_VALUES;

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
	public DropdownStringControllerBuilder allow(DropdownMode allow) {
		this.allowMode = allow;
		return this;
	}

	@Override
	public Controller<String> build() {
		return new DropdownStringController(option, values, allowMode);
	}

}
