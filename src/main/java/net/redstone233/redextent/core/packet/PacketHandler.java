package net.redstone233.redextent.core.packet;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
    private static final String PROTOCOL = "1";
    /* 保存 registrar，用来发包的 */
    private static PayloadRegistrar REG;

    public static void init(RegisterPayloadHandlersEvent evt) {
        REG = evt.registrar("rem")
                .versioned(PROTOCOL)
                .optional();
        REG.playToClient(
                S2CDisabledModListPacket.TYPE,
                S2CDisabledModListPacket.STREAM_CODEC,
                S2CDisabledModListPacket::handle);
    }

    /* 给外面调用的工具 */
    public static void sendToPlayer(ServerPlayer player, S2CDisabledModListPacket pkt) {
//        REG.sendToPlayer(player, pkt);
        PacketDistributor.sendToPlayer(player, pkt);
    }
}