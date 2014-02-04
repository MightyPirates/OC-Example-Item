package li.cil.oc.example.item;

import cpw.mods.fml.common.network.PacketDispatcher;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.Slot;
import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet63WorldParticles;
import net.minecraft.tileentity.TileEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DriverCardParticleSpawner extends DriverItem {
    protected DriverCardParticleSpawner() {
        super(new ItemStack(ModExampleItem.cardParticleSpawner));
    }

    // We want our item to be a card component, i.e. it can be placed into
    // computers' card slots.

    @Override
    public Slot slot(ItemStack stack) {
        return Slot.Card;
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack stack, TileEntity container) {
        return new Environment(container);
    }

    public class Environment extends li.cil.oc.api.prefab.ManagedEnvironment {
        protected final TileEntity container;

        public Environment(TileEntity container) {
            this.container = container;
            node = Network.newNode(this, Visibility.Neighbors).
                    withComponent("particle").
                    create();
        }

        // We allow spawning particle effects. The parameters are the particle
        // name, the position relative to the block the card is in to spawn
        // the particle at, as well as - optionally - the initial velocity.

        // The parameters to the annotation have the following effect:
        // direct = true
        //   this makes calls to the function 'direct', i.e. they are executed
        //   from the computer's own thread. This speeds up calls, since
        //   they are not synchronized into the main server thread, but you'll
        //   have to ensure that any interaction with other parts of Minecraft
        //   from within the method are synchronized / thread safe yourself.
        // limit = 16
        //   when this is set, it limits the number of calls that can be made
        //   to the method in one tick. If the computer calls the method more
        //   often than this per tick, it will be forced to make that call a
        //   synchronized one, meaning it'll execute the call from within the
        //   server's main thread.
        //   We limit this here, to avoid spamming the network with a massive
        //   amount of packets in case the function is called from within a
        //   loop, for example.

        @Callback(direct = true, limit = 16)
        public Object[] spawn(Context context, Arguments args) {
            String name = args.checkString(0);

            if (name.length() > Short.MAX_VALUE) {
                return new Object[]{false, "name too long"};
            }

            double x = container.xCoord + 0.5 + args.checkDouble(1);
            double y = container.yCoord + 0.5 + args.checkDouble(2);
            double z = container.zCoord + 0.5 + args.checkDouble(3);
            double velocity = args.count() > 4 ? args.checkDouble(4) : (container.getWorldObj().rand.nextGaussian() * 0.1);

            Packet63WorldParticles packet = createParticlePacket(name, (float) x, (float) y, (float) z, (float) velocity);
            if (packet != null) {
                PacketDispatcher.sendPacketToAllAround(container.xCoord + 0.5, container.yCoord + 0.5, container.zCoord + 0.5, 64, container.getWorldObj().getWorldInfo().getVanillaDimension(), packet);
                return new Object[]{true};
            } else return new Object[]{false};
        }
    }

    // Utility stuff.

    protected static Packet63WorldParticles createParticlePacket(String name, float x, float y, float z, float velocity) {
        try {
            // Is there a cleaner way to trigger particle spawns on the client
            // from the server? I don't know, but this works, so whatever.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(name.length());
            dos.writeChars(name);
            dos.writeFloat(x);
            dos.writeFloat(y);
            dos.writeFloat(z);
            dos.writeFloat(0f);
            dos.writeFloat(0f);
            dos.writeFloat(0f);
            dos.writeFloat(velocity);
            dos.writeInt(1);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            DataInputStream dis = new DataInputStream(bis);
            Packet63WorldParticles packet = new Packet63WorldParticles();
            packet.readPacketData(dis);
            return packet;
        } catch (Throwable e) {
            return null;
        }
    }
}
