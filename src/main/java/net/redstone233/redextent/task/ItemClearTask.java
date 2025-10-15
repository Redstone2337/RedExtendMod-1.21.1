package net.redstone233.redextent.task;

import net.redstone233.redextent.Config;
import net.redstone233.redextent.RedExtendMod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 掉落物清理任务
 * 基于 tick 的检查机制，避免高版本兼容性问题
 */
public class ItemClearTask {
    private final MinecraftServer server;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;
    private int nextClearCountdown;

    /* 配置常量 */
    private static final int CHECK_INTERVAL = 20; // 1 秒 = 20 tick
    private int maxLifeTick; // 最大存活时间（tick）

    /* 记录掉落物 -> 生成时的 server tick */
    private final WeakHashMap<ItemEntity, Integer> spawnTickMap = new WeakHashMap<>();

    public ItemClearTask(MinecraftServer server) {
        this.server = server;
        this.maxLifeTick = Config.getClearTime(); // 直接使用游戏刻
        // 倒计时显示为秒，所以需要将游戏刻转换为秒
        this.nextClearCountdown = maxLifeTick / 20;
    }

    /**
     * 启动清理任务
     */
    public void start() {
        if (scheduler != null && !scheduler.isShutdown()) {
            RedExtendMod.LOGGER.warn("清理任务已经在运行");
            return;
        }

        this.scheduler = Executors.newScheduledThreadPool(1);

        // 使用游戏刻作为清理间隔，但转换为秒用于调度
        int clearIntervalSeconds = maxLifeTick / 20;

        // 启动定时任务（基于现实时间）
        this.scheduledTask = scheduler.scheduleAtFixedRate(() -> {
            if (server.isRunning()) {
                executeClear();
            }
        }, clearIntervalSeconds, clearIntervalSeconds, TimeUnit.SECONDS);

        // 启动倒计时更新任务（每秒更新一次）
        scheduler.scheduleAtFixedRate(this::updateCountdown, 1, 1, TimeUnit.SECONDS);

        if (Config.isDebugModeEnabled()) {
            RedExtendMod.LOGGER.info("清理任务已启动，间隔: {}秒 ({} tick)", clearIntervalSeconds, maxLifeTick);
        }
    }

    /**
     * 停止清理任务
     */
    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTask = null;
        }

        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            scheduler = null;
        }

        // 清空记录
        spawnTickMap.clear();
        this.maxLifeTick = Config.getClearTime();
        this.nextClearCountdown = maxLifeTick / 20;
    }

    /**
     * 更新下一次清理的倒计时
     */
    private void updateCountdown() {
        if (nextClearCountdown > 1) {
            nextClearCountdown--;
        } else {
            nextClearCountdown = maxLifeTick / 20;
        }
    }

    /**
     * 执行清理操作
     * 使用基于 tick 的检查机制，避免直接获取所有 ItemEntity
     */
    private void executeClear() {
        try {
            int now = server.getTickCount();
            ClearStatistics stats = new ClearStatistics();

            // 遍历所有世界和所有掉落物
            server.getAllLevels().forEach(level -> {
                // 使用 EntityType.ITEM 获取所有掉落物实体
                for (ItemEntity item : level.getEntities(EntityType.ITEM, ent -> true)) {
                    if (!item.isAlive()) continue;

                    // 检查物品是否应该被清理
                    if (shouldClearItem(item, now)) {
                        // 统计信息
                        stats.addItem(item);
                        // 执行清理
                        item.discard();
                        spawnTickMap.remove(item);
                    }
                }
            });

            // 如果没有清理任何物品，则返回
            if (stats.getTotalEntities() == 0) {
                if (Config.isDebugModeEnabled()) {
                    RedExtendMod.LOGGER.debug("没有发现需要清理的掉落物");
                }
                return;
            }

            // 发送清理消息
            broadcastClearMessage(stats);

            // 记录调试信息
            logClearStatistics(stats);

        } catch (Exception e) {
            RedExtendMod.LOGGER.error("执行掉落物清理时发生错误", e);
        }
    }

    /**
     * 检查物品是否应该被清理
     * @param item 掉落物实体
     * @param now 当前服务器 tick
     * @return 是否应该清理
     */
    private boolean shouldClearItem(ItemEntity item, int now) {
        // 记录或获取物品的生成时间
        int born = spawnTickMap.computeIfAbsent(item, k -> now);

        // 检查物品存活时间是否超过限制
        if (now - born <= maxLifeTick) {
            return false; // 物品存活时间不足，不清理
        }

        // 如果开启了物品过滤器，检查物品是否在白名单外
        if (Config.isItemFilterEnabled()) {
            return !isInWhitelist(item);
        }

        // 未开启过滤器，清理所有超时物品
        return true;
    }

    /**
     * 检查物品是否在白名单中
     * @param item 掉落物实体
     * @return 是否在白名单中
     */
    private boolean isInWhitelist(ItemEntity item) {
        List<String> whitelist = Config.getItemWhitelist();

        // 如果白名单为空，则默认所有物品都不在白名单中（即全部清理）
        if (whitelist.isEmpty()) {
            return false;
        }

        // 获取物品ID并检查是否在白名单中
        String itemId = item.getItem().getItem().toString();
        return whitelist.contains(itemId);
    }

    /**
     * 广播清理消息
     * @param stats 清理统计信息
     */
    private void broadcastClearMessage(ClearStatistics stats) {
        String textHead = Config.getDisplayTextHead();
        String textBody = Config.getDisplayTextBody();

        // 格式化消息（支持两个 %s 占位符）
        String formattedMessage = String.format(textBody, stats.getTypeCount(), nextClearCountdown);
        String fullMessage = textHead + formattedMessage;

        // 向所有在线玩家广播消息
        server.getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(Component.literal(fullMessage));
        });

        if (Config.isDebugModeEnabled()) {
            RedExtendMod.LOGGER.info("广播清理消息: {}", fullMessage);
        }
    }

    /**
     * 记录清理统计信息
     * @param stats 统计信息
     */
    private void logClearStatistics(ClearStatistics stats) {
        if (Config.isDebugModeEnabled()) {
            RedExtendMod.LOGGER.info("清理了 {} 个掉落物实体，涉及 {} 种物品",
                    stats.getTotalEntities(), stats.getTypeCount());

            stats.getItemCounts().forEach((itemName, count) -> {
                RedExtendMod.LOGGER.info("  - {}: {}", itemName, count);
            });
        }
    }

    /**
     * 清理统计信息类
     */
    private static class ClearStatistics {
        private int totalEntities = 0;
        private final Map<String, Integer> itemCounts = new HashMap<>();

        public void addItem(ItemEntity itemEntity) {
            totalEntities++;
            String itemName = itemEntity.getItem().getItem().toString();
            int count = itemEntity.getItem().getCount();
            itemCounts.put(itemName, itemCounts.getOrDefault(itemName, 0) + count);
        }

        public int getTotalEntities() { return totalEntities; }
        public int getTypeCount() { return itemCounts.size(); }
        public Map<String, Integer> getItemCounts() { return itemCounts; }
    }
}