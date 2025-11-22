package net.redstone233.redextent.core.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.redstone233.redextent.core.proxy.ClientProxy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

import static net.minecraft.advancements.critereon.LocationPredicate.Builder.location;
import static net.redstone233.redextent.RedExtendMod.MOD_ID;


public record S2CDisabledModListPacket(List<String> modids) implements CustomPacketPayload {
    public static final Type<S2CDisabledModListPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "disabled_mod_list"));

    public static final StreamCodec<FriendlyByteBuf, S2CDisabledModListPacket> STREAM_CODEC =
            StreamCodec.ofMember(S2CDisabledModListPacket::encode, S2CDisabledModListPacket::decode);

    private static void encode(FriendlyByteBuf buf, S2CDisabledModListPacket pkt) {
        buf.writeCollection(pkt.modids, FriendlyByteBuf::writeUtf);
    }
    private static S2CDisabledModListPacket decode(FriendlyByteBuf buf) {
        return new S2CDisabledModListPacket(buf.readList(FriendlyByteBuf::readUtf));
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientProxy.instance().onReceiveDisabledList(modids));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}