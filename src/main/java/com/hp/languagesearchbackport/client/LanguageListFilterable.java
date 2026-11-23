package com.hp.languagesearchbackport.client;

import javax.annotation.Nullable;
import net.minecraft.client.resources.language.LanguageManager;

/**
 * 为包私有内部类 LanguageSelectionList 暴露搜索过滤与选中态同步能力的桥接接口。
 */
public interface LanguageListFilterable {
    /** 根据输入关键字实时过滤语言列表条目并刷新滚动位置。 */
    void languagesearchbackport$filterEntries(LanguageManager languageManager, String filter);

    /** 读取当前或最近一次被选中的语言代码，用于在窗口缩放重建列表时保持状态。 */
    @Nullable
    String languagesearchbackport$getLastSelectedCode();

    /** 在列表重建后恢复之前选中的语言条目并居中滚动。 */
    void languagesearchbackport$restoreSelectedCode(@Nullable String code);
}
