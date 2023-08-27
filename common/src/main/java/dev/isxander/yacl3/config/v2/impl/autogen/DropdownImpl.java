package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.DropdownStringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.Dropdown;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;

public class DropdownImpl extends SimpleOptionFactory<Dropdown, String> {
	@Override
	protected ControllerBuilder<String> createController(Dropdown annotation, ConfigField<String> field, OptionAccess storage, Option<String> option) {
		return DropdownStringControllerBuilder.create(option)
				.values(annotation.values())
				.allowEmptyValue(annotation.allowEmptyValue())
				.allowAnyValue(annotation.allowAnyValue());
	}
}
