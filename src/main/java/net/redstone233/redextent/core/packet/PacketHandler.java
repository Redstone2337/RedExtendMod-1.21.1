package net.redstone233.redextent.core.packet;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.redstone233.redextent.RedExtendMod;

public class PacketHandler {
    public static final String PROTOCOL = "1";

    public static void init(RegisterPayloadHandlersEvent evt) {
        PayloadRegistrar reg = evt.registrar(RedExtendMod.MOD_ID).versioned(PROTOCOL);
        reg.playToClient(S2CDisabledModListPacket.TYPE,
                S2CDisabledModListPacket.STREAM_CODEC,
                S2CDisabledModListPacket::handle);
    }
}