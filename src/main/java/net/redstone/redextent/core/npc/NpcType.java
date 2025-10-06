/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
 
package net.redstone.redextent.core.npc;

/**
 * NPC 类型枚举
 */
public enum NPCType {
    /** 战斗类型 - 格式同 dragon_1.json */
    BATTLE,
    /** 聊天类型 - 格式同 evostones_1.json */
    CHATTING,
    /** 对话类型 - 格式同 evostones_1.json */
    DIALOGUE,
    /** 商店店主类型 - 格式同 evostones_1.json */
    SHOPKEEPER,
    /** 训练师类型 - 格式同 evostones_1.json */
    TRAINERS,
    /** 导师类型 - 格式同 evostones_1.json */
    TUTOR,
    /** 宝可梦中心类型 - 格式同 doctor_john.json */
    POKECENTER,
    /** 工具类型 - 格式同 doctor_john.json */
    UTILITY
}