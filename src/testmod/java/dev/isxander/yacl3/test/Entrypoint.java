/*? if fabric {*/
package dev.isxander.yacl3.test;
import net.fabricmc.api.ClientModInitializer;

public class Entrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

	}
}

/*?} elif neoforge {*/
/*package dev.isxander.yacl3.test;

import net.neoforged.fml.common.Mod;

@Mod("yacl_test")
public class Entrypoint {
    public Entrypoint() {

    }
}
*//*?} elif forge {*/
/*package dev.isxander.yacl3.test;

import net.minecraftforge.fml.common.Mod;

@Mod("yacl_test")
public class Entrypoint {
    public Entrypoint() {

    }
}
*//*?}*/
