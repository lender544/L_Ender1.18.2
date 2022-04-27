package L_Ender.cataclysm.message;

import L_Ender.cataclysm.cataclysm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageAddStunProgress {

    private int entityID;
    private float amount;


    public MessageAddStunProgress(LivingEntity entity, float amount) {
        entityID = entity.getId();
        this.amount = amount;
    }

    public MessageAddStunProgress() {
    }

    public static void write(MessageAddStunProgress message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeFloat(message.amount);
    }

    public static MessageAddStunProgress read(FriendlyByteBuf buf) {
        final MessageAddStunProgress message = new MessageAddStunProgress();
        message.entityID = buf.readVarInt();
        message.amount = buf.readFloat();
        return message;
    }

    public static class Handler implements BiConsumer<MessageAddStunProgress, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(final MessageAddStunProgress message, final Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                Entity entity = Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) entity;
                    FrozenCapability.IFrozenCapability frozenCapability = CapabilityHandler.getCapability(living, FrozenCapability.FrozenProvider.FROZEN_CAPABILITY);
                    if (frozenCapability != null) {
                        frozenCapability.setFreezeProgress(frozenCapability.getFreezeProgress() + message.amount);
                        frozenCapability.setFreezeDecayDelay(FrozenCapability.MAX_FREEZE_DECAY_DELAY);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}