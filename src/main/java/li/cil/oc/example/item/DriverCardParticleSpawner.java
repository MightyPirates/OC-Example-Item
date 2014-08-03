package li.cil.oc.example.item;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.*;
import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

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
    public ManagedEnvironment createEnvironment(ItemStack stack, Container container) {
        return new Environment(container);
    }

    public class Environment extends li.cil.oc.api.prefab.ManagedEnvironment {
        protected final Container container;

        public Environment(Container container) {
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

            Random rng = container.world().rand;
            double x = container.xPosition() + args.checkDouble(1);
            double y = container.yPosition() + args.checkDouble(2);
            double z = container.zPosition() + args.checkDouble(3);
            double velocity = args.count() > 4 ? args.checkDouble(4) : (container.world().rand.nextDouble() * 0.1);
            ModExampleItem.sendParticlePacket(name, container.world().provider.dimensionId, x, y, z, velocity * rng.nextGaussian(), velocity * rng.nextGaussian(), velocity * rng.nextGaussian());
            return new Object[]{true};
        }
    }
}
