package dev.isxander.yacl3.test;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;

public class Entrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

	}
}
//?} elif neoforge {
/*import net.neoforged.fml.common.Mod;

@Mod("yacl_test")
public class Entrypoint {
    public Entrypoint() {

    }
}
*///?}
