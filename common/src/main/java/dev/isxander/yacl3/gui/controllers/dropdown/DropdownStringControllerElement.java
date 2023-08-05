package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;

import java.util.List;

public class DropdownStringControllerElement extends AbstractDropdownControllerElement<String, String> {
	private final DropdownStringController controller;

	public DropdownStringControllerElement(DropdownStringController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
		this.controller = control;
	}

	@Override
	public List<String> getMatchingValues() {
		return controller.getAllowedValues().stream()
				.filter(this::matchingValue)
				.sorted()
				.toList();
	}

	public String getString(String object) {
		return object;
	}
}
