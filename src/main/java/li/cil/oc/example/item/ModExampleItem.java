package li.cil.oc.example.item;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.registry.GameRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;

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

    public static FMLEventChannel channel;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        cardParticleSpawner = new ItemCardParticleSpawner();
        GameRegistry.registerItem(cardParticleSpawner, "oc:card_pfx_spawner");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        li.cil.oc.api.Driver.add(new DriverCardParticleSpawner());
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("OpenComputers|ExampleItem");
        channel.register(this);
    }

    public static void sendParticlePacket(String name, int dimension, double x, double y, double z, double vx, double vy, double vz) {
        ByteBuf data = Unpooled.buffer();
        byte[] nameBytes = name.getBytes();
        data.writeShort(nameBytes.length);
        data.writeBytes(nameBytes);
        data.writeFloat((float) x);
        data.writeFloat((float) y);
        data.writeFloat((float) z);
        data.writeFloat((float) vx);
        data.writeFloat((float) vy);
        data.writeFloat((float) vz);
        channel.sendToAllAround(new FMLProxyPacket(data, "OpenComputers|ExampleItem"), new NetworkRegistry.TargetPoint(dimension, x, y, z, 64));
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        ByteBuf data = event.packet.payload();
        int nameLength = data.readShort();
        String name = new String(data.readBytes(nameLength).array());
        double x = data.readFloat();
        double y = data.readFloat();
        double z = data.readFloat();
        double vx = data.readFloat();
        double vy = data.readFloat();
        double vz = data.readFloat();
        Minecraft.getMinecraft().thePlayer.getEntityWorld().spawnParticle(name, x, y, z, vx, vy, vz);
    }
}
