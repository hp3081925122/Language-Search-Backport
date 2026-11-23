package com.hp.languagesearchbackport.mixin;

import com.hp.languagesearchbackport.client.LanguageEntryAccess;
import com.hp.languagesearchbackport.client.LanguageListFilterable;
import com.hp.languagesearchbackport.client.LanguageSearchConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为 1.20.1 原版语言选择列表调整顶栏高度并移植 1.21.11+（26.1.2）的搜索过滤与滚动刷新逻辑，
 * 同时同步修复 MC-313081（过滤时保留当前仍可见的已选语言高亮状态）。
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(targets = "net.minecraft.client.gui.screens.LanguageSelectScreen$LanguageSelectionList")
public abstract class LanguageSelectionListMixin extends ObjectSelectionList implements LanguageListFilterable {
    @Unique
    private List<AbstractSelectionList.Entry<?>> languagesearchbackport$allEntries = List.of();

    @Unique
    @Nullable
    private String languagesearchbackport$lastSelectedCode;

    protected LanguageSelectionListMixin(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void languagesearchbackport$captureInitialEntries(
            LanguageSelectScreen screen,
            Minecraft minecraft,
            CallbackInfo ci
    ) {
        // 将列表顶部从原版 32 下移到 36，与 26.1.2 / 1.21.1 顶栏高度对齐并重新居中滚动。
        this.y0 = LanguageSearchConstants.HEADER_HEIGHT;
        Object selected = this.getSelected();
        if (selected instanceof AbstractSelectionList.Entry<?> selectedEntry) {
            this.centerScrollOn(selectedEntry);
        }

        // 缓存构造完成后的全部原版条目，既保持原始排序又兼容其他模组对条目对象的增强。
        this.languagesearchbackport$allEntries = List.copyOf(this.children());
        if (selected instanceof LanguageEntryAccess entryAccess) {
            this.languagesearchbackport$lastSelectedCode = entryAccess.languagesearchbackport$getCode();
        }
    }

    @Override
    @Nullable
    public String languagesearchbackport$getLastSelectedCode() {
        Object currentSelected = this.getSelected();
        if (currentSelected instanceof LanguageEntryAccess entryAccess) {
            this.languagesearchbackport$lastSelectedCode = entryAccess.languagesearchbackport$getCode();
        }
        return this.languagesearchbackport$lastSelectedCode;
    }

    @Override
    public void languagesearchbackport$restoreSelectedCode(@Nullable String code) {
        if (code == null) {
            return;
        }
        this.languagesearchbackport$lastSelectedCode = code;
        for (Object child : this.children()) {
            if (child instanceof LanguageEntryAccess entryAccess && code.equals(entryAccess.languagesearchbackport$getCode())) {
                AbstractSelectionList.Entry<?> entry = (AbstractSelectionList.Entry<?>) child;
                this.setSelected(entry);
                this.centerScrollOn(entry);
                return;
            }
        }
    }

    @Override
    public void languagesearchbackport$filterEntries(LanguageManager languageManager, String filter) {
        // 过滤前先同步用户最近手动选中的语言，避免在筛选期间丢失最新选择。
        Object currentSelected = this.getSelected();
        if (currentSelected instanceof LanguageEntryAccess entryAccess) {
            this.languagesearchbackport$lastSelectedCode = entryAccess.languagesearchbackport$getCode();
        }

        SortedMap<String, LanguageInfo> languages = languageManager.getLanguages();
        List<AbstractSelectionList.Entry<?>> filteredEntries = new ArrayList<>();
        AbstractSelectionList.Entry<?> restoredSelection = null;

        for (AbstractSelectionList.Entry<?> entry : this.languagesearchbackport$allEntries) {
            if (entry instanceof LanguageEntryAccess entryAccess) {
                String code = entryAccess.languagesearchbackport$getCode();
                LanguageInfo info = languages.get(code);
                if (LanguageSearchConstants.matches(info, entryAccess.languagesearchbackport$getLanguageComponent(), filter)) {
                    filteredEntries.add(entry);
                    if (code.equals(this.languagesearchbackport$lastSelectedCode)) {
                        restoredSelection = entry;
                    }
                }
            } else if (filter == null || filter.isEmpty()) {
                filteredEntries.add(entry);
            }
        }

        this.replaceEntries(filteredEntries);
        if (restoredSelection != null) {
            this.setSelected(restoredSelection);
        }
        // 1.20.1 的 setScrollAmount 内部会自动按 [0, getMaxScroll()] 钳制，等价于 26.1.2 的 refreshScrollAmount()。
        this.setScrollAmount(this.getScrollAmount());
    }
}
