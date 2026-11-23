package com.hp.languagesearchbackport.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * 1.20.1 语言选择界面搜索栏的布局常量、提示样式与筛选匹配工具。
 */
public final class LanguageSearchConstants {
    /** 与原版 26.1.2 EditBox.SEARCH_HINT_STYLE 一致的灰色斜体提示样式。 */
    public static final Style SEARCH_HINT_STYLE = Style.EMPTY.applyFormats(ChatFormatting.GRAY, ChatFormatting.ITALIC);

    /** 搜索框未聚焦且为空时显示的提示文本。 */
    public static final Component SEARCH_HINT = Component.translatable("gui.language.search").withStyle(SEARCH_HINT_STYLE);

    /** 搜索框宽度，与原版 26.1.2 LanguageSelectScreen 保持一致。 */
    public static final int SEARCH_BOX_WIDTH = 200;

    /** 搜索框高度，与原版 26.1.2 SEARCH_BOX_HEIGHT 保持一致。 */
    public static final int SEARCH_BOX_HEIGHT = 15;

    /** 顶栏标题绘制 Y 坐标（原版 1.20.1 为 16，上移至 4 以腾出搜索框空间）。 */
    public static final int TITLE_Y = 4;

    /** 搜索框绘制 Y 坐标（1.20.1 边框外扩 1px，视觉占 16..33，上下与标题和列表各留 3px 间距）。 */
    public static final int SEARCH_BOX_Y = 17;

    /** 顶栏总高度 / 列表顶部 y0，与 26.1.2 / 1.21.1 的 (int)(12.0 + 9.0 + 15.0) = 36 一致。 */
    public static final int HEADER_HEIGHT = (int) (12.0 + 9.0 + SEARCH_BOX_HEIGHT);

    private LanguageSearchConstants() {
    }

    /**
     * 按原版 26.1.2 规则判断某条语言信息是否命中搜索词：
     * 空搜索词全部匹配；非空时忽略大小写（Locale.ROOT）匹配语言名称或地区名称。
     * 若语言信息缺失（例如其他模组额外注入的条目），退回到界面显示文本进行匹配。
     */
    public static boolean matches(@Nullable LanguageInfo info, @Nullable Component fallbackText, String filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }
        String lowerFilter = filter.toLowerCase(Locale.ROOT);
        if (info != null) {
            return info.name().toLowerCase(Locale.ROOT).contains(lowerFilter)
                    || info.region().toLowerCase(Locale.ROOT).contains(lowerFilter);
        }
        return fallbackText != null && fallbackText.getString().toLowerCase(Locale.ROOT).contains(lowerFilter);
    }
}
