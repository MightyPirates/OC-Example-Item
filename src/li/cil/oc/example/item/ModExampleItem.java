package li.cil.oc.example.item;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * This mod demonstrates how to add item components, i.e. items that can be
 * placed in a computer and provide methods to it.
 */
@Mod(modid = "OpenComputers|ExampleItem",
        name = "OpenComputers Addon Example - Item",
        version = "1.0.0",
        dependencies = "required-after:OpenComputers@[1.2.0,)")
public class ModExampleItem {
    @Mod.Instance
    public static ModExampleItem instance;

    public static ItemCardParticleSpawner cardParticleSpawner;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        cardParticleSpawner = new ItemCardParticleSpawner(4610);
        GameRegistry.registerItem(cardParticleSpawner, "oc:card_pfx_spawner");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        li.cil.oc.api.Driver.add(new DriverCardParticleSpawner());
    }
}
