package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.DropdownStringControllerBuilderImpl;

import java.util.List;

public interface DropdownStringControllerBuilder extends StringControllerBuilder {
	DropdownStringControllerBuilder values(List<String> values);
	DropdownStringControllerBuilder values(String... values);
	DropdownStringControllerBuilder allowEmpty(boolean allowEmpty);

	static DropdownStringControllerBuilder create(Option<String> option) {
		return new DropdownStringControllerBuilderImpl(option);
	}
}
