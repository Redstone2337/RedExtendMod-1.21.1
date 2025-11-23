package net.redstone233.redextent.core.proxy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedClientProxy extends CommonProxy {

    /* ================ 单例 ================ */
    private static final RedClientProxy INSTANCE = new RedClientProxy();
    public static RedClientProxy instance() { return INSTANCE; }

    /* ================ 内部快照 ================ */
    private final Set<String> clientDisabled = new HashSet<>();

    /* 单例入口：给包回调用 */
    public void onReceiveDisabledList(List<String> modids) {
        apply(modids);
    }

    /* ================ 多例/工具：任何地方动态注入 ================ */
    public static void softDisable(List<String> modids) {
        instance().apply(modids);
    }

    /* 公共只读判断 */
    public static boolean isSoftDisabled(String modid) {
        return instance().clientDisabled.contains(modid);
    }

    /* 真正应用逻辑 */
    private void apply(List<String> modids) {
        clientDisabled.clear();
        clientDisabled.addAll(modids);
        // TODO: 抛客户端事件、刷新 JEI、跳过渲染等
    }
}