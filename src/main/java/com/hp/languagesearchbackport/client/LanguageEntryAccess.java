package com.hp.languagesearchbackport.client;

import net.minecraft.network.chat.Component;

/**
 * 提供读取包私有内部类 LanguageSelectionList.Entry 字段的桥接接口。
 */
public interface LanguageEntryAccess {
    /** 获取该条目对应的语言代码（例如 zh_cn、en_us）。 */
    String languagesearchbackport$getCode();

    /** 获取该条目的界面显示组件，用于在 LanguageInfo 缺失时兜底匹配。 */
    Component languagesearchbackport$getLanguageComponent();
}
