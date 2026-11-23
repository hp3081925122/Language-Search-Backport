package com.hp.languagesearchbackport.client;

import net.minecraft.client.resources.language.LanguageManager;

/**
 * 为包私有内部类 LanguageSelectionList 暴露搜索过滤能力的桥接接口。
 */
public interface LanguageListFilterable {
    /** 根据输入关键字实时过滤语言列表条目并刷新滚动位置。 */
    void languagesearchbackport$filterEntries(LanguageManager languageManager, String filter);
}
