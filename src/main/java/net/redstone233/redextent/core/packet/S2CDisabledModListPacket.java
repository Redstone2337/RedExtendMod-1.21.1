package net.redstone233.redextent.core.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.core.proxy.ClientProxy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.redstone233.redextent.core.util.PacketUtil.location;


public record S2CDisabledModListPacket(List<String> modids) implements CustomPacketPayload {

    /* ----------------  类型  ---------------- */
    public static final Type<S2CDisabledModListPacket> TYPE =
            new Type<>(location(RedExtendMod.MOD_ID, "dis_list"));

    /* ----------------  StreamCodec：ofMember  ---------------- */
    private static final StreamMemberEncoder<FriendlyByteBuf, S2CDisabledModListPacket> ENCODER =
            (buf, pkt) -> encodeStatic(pkt, buf);

    private static final StreamDecoder<FriendlyByteBuf, S2CDisabledModListPacket> DECODER =
            S2CDisabledModListPacket::decodeStatic;

    public static final StreamCodec<FriendlyByteBuf, S2CDisabledModListPacket> STREAM_CODEC =
            StreamCodec.ofMember(ENCODER, DECODER);

    /* ----------------  序列化  ---------------- */
    private static void encodeStatic(FriendlyByteBuf buf, S2CDisabledModListPacket pkt) {
        buf.writeVarInt(pkt.modids.size());
        for (String s : pkt.modids) buf.writeUtf(s);
    }

    private static S2CDisabledModListPacket decodeStatic(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) list.add(buf.readUtf());
        return new S2CDisabledModListPacket(list);
    }

    /* ----------------  客户端逻辑  ---------------- */
    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientProxy.instance().onReceiveDisabledList(modids));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}