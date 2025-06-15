package net.mehvahdjukaar.randomium.common;

import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.randomium.Randomium;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;

public class ModNetwork {

    public static void init() {
        NetworkHelper.addNetworkRegistration(ModNetwork::registerMessages, 1);
    }

    public static void registerMessages(NetworkHelper.RegisterMessagesEvent event) {
        event.registerClientBound(ClientBoundTeleportParticleMessage.TYPE_CODEC);
    }

    public record ClientBoundTeleportParticleMessage(BlockPos start, BlockPos end) implements Message {
        public static final TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundTeleportParticleMessage> TYPE_CODEC = Message
                .makeType(Randomium.res("teleport_particle"),
                        ClientBoundTeleportParticleMessage::new);

        public ClientBoundTeleportParticleMessage(RegistryFriendlyByteBuf buf) {
            this(buf.readBlockPos(), buf.readBlockPos());
        }

        @Override
        public void write(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            registryFriendlyByteBuf.writeBlockPos(start);
            registryFriendlyByteBuf.writeBlockPos(end);
        }

        @Override
        public void handle(Context context) {
            var level = context.getPlayer().level();
            var random = level.random;
            for (int j = 0; j < 64; ++j) {
                double position = random.nextDouble();
                float speedX = (random.nextFloat() - 0.5F) * 0.2F;
                float speedY = (random.nextFloat() - 0.5F) * 0.2F;
                float speedZ = (random.nextFloat() - 0.5F) * 0.2F;
                double px = Mth.lerp(position, start.getX(), end.getX()) + random.nextDouble();
                double py = Mth.lerp(position, start.getY(), end.getY()) + random.nextDouble();
                double pz = Mth.lerp(position, start.getZ(), end.getZ()) + random.nextDouble();
                level.addParticle(ParticleTypes.PORTAL, px, py, pz, speedX, speedY, speedZ);
            }
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE_CODEC.type();
        }
    }
}
