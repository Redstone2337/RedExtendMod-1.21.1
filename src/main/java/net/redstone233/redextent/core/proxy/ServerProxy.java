package net.redstone233.redextent.core.proxy;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.redstone233.redextent.config.CommonConfig;
import net.redstone233.redextent.core.packet.S2CDisabledModListPacket;

import java.util.List;

public class ServerProxy extends CommonProxy {
    /* 供 RemMain 调用：发送当前列表给全体在线玩家 */
    public void syncToAll() {
        List<String> list = CommonConfig.getDisabledModList();
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()
                    .forEach(p -> PacketDistributor.sendToPlayer(p, new S2CDisabledModListPacket(list)));
        }
    }
}
