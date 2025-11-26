package net.redstone233.redextent.manager;

import net.redstone233.redextent.config.CommonConfig;
import net.redstone233.redextent.RedExtendMod;
import net.redstone233.redextent.task.ItemClearTask;

import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Optional;

/**
 * 掉落物清理管理器
 * 负责管理清理任务的启动、停止和状态查询
 */
public class ItemClearManager {
    private ItemClearTask clearTask;
    private boolean isRunning = false;

    /**
     * 启动掉落物清理任务
     * @param server Minecraft服务器实例
     */
    public void startClearTask(MinecraftServer server) {
        if (isRunning) {
            RedExtendMod.LOGGER.warn("掉落物清理任务已经在运行中");
            return;
        }

        if (!CommonConfig.isClearServerItemEnabled()) {
            RedExtendMod.LOGGER.info("掉落物清理功能未启用，跳过任务启动");
            return;
        }

        this.clearTask = new ItemClearTask(server);
        this.clearTask.start();
        this.isRunning = true;

        int clearTimeTicks = CommonConfig.getClearTime();
        int clearTimeSeconds = clearTimeTicks / 20;
        RedExtendMod.LOGGER.info("掉落物清理任务已启动，清理间隔: {}秒 ({} tick)", clearTimeSeconds, clearTimeTicks);
    }

    /**
     * 停止掉落物清理任务
     */
    public void stopClearTask() {
        if (!isRunning || clearTask == null) {
            return;
        }

        clearTask.stop();
        clearTask = null;
        isRunning = false;

        RedExtendMod.LOGGER.info("掉落物清理任务已停止");
    }

    /**
     * 重新加载配置并重启清理任务
     * @param server Minecraft服务器实例
     */
    public void reloadConfig(MinecraftServer server) {
        if (isRunning) {
            RedExtendMod.LOGGER.info("重新加载掉落物清理配置");
            stopClearTask();

            if (CommonConfig.isClearServerItemEnabled()) {
                startClearTask(server);
            }
        }
    }

    /**
     * 检查清理任务是否正在运行
     * @return 运行状态
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 获取当前清理任务信息
     * @return 清理任务信息
     */
    public Optional<String> getTaskInfo() {
        if (!isRunning || clearTask == null) {
            return Optional.empty();
        }

        String filterInfo;
        if (CommonConfig.isItemFilterEnabled()) {
            List<String> whitelist = CommonConfig.getItemWhitelist();
            if (whitelist.isEmpty()) {
                filterInfo = "过滤器: 启用 (白名单为空，清理所有物品)";
            } else {
                filterInfo = String.format("过滤器: 启用 (白名单中有 %d 种物品受保护)", whitelist.size());
            }
        } else {
            filterInfo = "过滤器: 禁用";
        }

        int clearTimeTicks = CommonConfig.getClearTime();
        int clearTimeSeconds = clearTimeTicks / 20;

        return Optional.of(String.format(
                "掉落物清理任务运行中 - 间隔: %d秒 (%dt), %s, 调试模式: %s",
                clearTimeSeconds, clearTimeTicks,
                filterInfo,
                CommonConfig.isDebugModeEnabled() ? "启用" : "禁用"
        ));
    }
}