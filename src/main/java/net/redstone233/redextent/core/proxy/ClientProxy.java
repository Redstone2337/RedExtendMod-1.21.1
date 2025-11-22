package net.redstone233.redextent.core.proxy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientProxy extends CommonProxy {
    /* 单例 */
    private static final ClientProxy INSTANCE = new ClientProxy();

    public static ClientProxy instance() {
        return INSTANCE;
    }

    /* 原成员变量 */
    private final Set<String> clientDisabled = new HashSet<>();

    /* 供包处理线程调用 */
    public void onReceiveDisabledList(List<String> modids) {
        clientDisabled.clear();
        clientDisabled.addAll(modids);
        // 这里可以继续抛事件、刷新 JEI 等
    }

    /* 给业务代码静态调用 */
    public static boolean isModSoftDisabled(String modid) {
        return instance().clientDisabled.contains(modid);
    }
}
