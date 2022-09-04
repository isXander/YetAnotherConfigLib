package dev.isxander.yacl.gui.controllers.string;

import dev.isxander.yacl.api.Controller;
import net.minecraft.text.Text;

public interface IStringController<T> extends Controller<T> {
    String getString();
    void setFromString(String value);

    @Override
    default Text formatValue() {
        return Text.of(getString());
    }
}
