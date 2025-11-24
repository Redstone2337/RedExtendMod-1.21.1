package net.redstone233.redextent.manager;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 泛型列表配置管理器
 * 用于在不修改 Ponder Lib 的情况下处理 List<String> 配置
 */
public class ListConfigManager {

    private static final Map<String, ListConfigHandler> CONFIG_HANDLERS = new HashMap<>();

    /**
     * 注册列表配置处理器
     */
    public static void registerListConfig(String configKey,
                                          Supplier<List<String>> listGetter,
                                          Consumer<List<String>> listSetter) {
        CONFIG_HANDLERS.put(configKey, new ListConfigHandler(listGetter, listSetter));
    }

    /**
     * 获取列表配置的值
     */
    public static List<String> getListValue(String configKey) {
        ListConfigHandler handler = CONFIG_HANDLERS.get(configKey);
        if (handler != null) {
            return handler.getListValue();
        }
        return new ArrayList<>();
    }

    /**
     * 设置列表配置的值
     */
    public static void setListValue(String configKey, List<String> value) {
        ListConfigHandler handler = CONFIG_HANDLERS.get(configKey);
        if (handler != null) {
            handler.setListValue(value);
        }
    }

    /**
     * 获取字符串格式的值（用于配置界面）
     */
    public static String getStringValue(String configKey) {
        ListConfigHandler handler = CONFIG_HANDLERS.get(configKey);
        if (handler != null) {
            return handler.getStringValue();
        }
        return "";
    }

    /**
     * 设置字符串格式的值（来自配置界面）
     */
    public static void setStringValue(String configKey, String value) {
        ListConfigHandler handler = CONFIG_HANDLERS.get(configKey);
        if (handler != null) {
            handler.setStringValue(value);
        }
    }

    /**
     * 添加项目到列表
     */
    public static void addItem(String configKey, String item) {
        List<String> currentList = getListValue(configKey);
        if (!currentList.contains(item)) {
            currentList.add(item);
            setListValue(configKey, currentList);
        }
    }

    /**
     * 从列表移除项目
     */
    public static void removeItem(String configKey, String item) {
        List<String> currentList = getListValue(configKey);
        currentList.remove(item);
        setListValue(configKey, currentList);
    }

    /**
     * 检查列表是否包含项目
     */
    public static boolean containsItem(String configKey, String item) {
        return getListValue(configKey).contains(item);
    }

    /**
     * 清空列表
     */
    public static void clearList(String configKey) {
        setListValue(configKey, new ArrayList<>());
    }

    /**
     * 获取列表大小
     */
    public static int getListSize(String configKey) {
        return getListValue(configKey).size();
    }

    /**
     * 获取所有已注册的配置键
     */
    public static Set<String> getRegisteredKeys() {
        return CONFIG_HANDLERS.keySet();
    }

    /**
         * 列表配置处理器
         */
        private record ListConfigHandler(Supplier<List<String>> listGetter, Consumer<List<String>> listSetter) {

        public List<String> getListValue() {
                return listGetter.get();
            }

            public void setListValue(List<String> value) {
                listSetter.accept(value);
            }

            public String getStringValue() {
                List<String> list = getListValue();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                return String.join(", ", list);
            }

            public void setStringValue(String value) {
                if (value == null || value.trim().isEmpty()) {
                    setListValue(new ArrayList<>());
                } else {
                    List<String> list = Arrays.stream(value.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    setListValue(list);
                }
            }
        }
}