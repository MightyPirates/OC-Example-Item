package li.cil.oc.example.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCardParticleSpawner extends Item {
    public ItemCardParticleSpawner(int itemId) {
        super(itemId);
        setUnlocalizedName("Particle Spawner Card");
        setCreativeTab(CreativeTabs.tabAllSearch);
    }
}
