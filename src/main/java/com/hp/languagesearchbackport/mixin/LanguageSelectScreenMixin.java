package com.hp.languagesearchbackport.mixin;

import com.hp.languagesearchbackport.client.LanguageListFilterable;
import com.hp.languagesearchbackport.client.LanguageSearchConstants;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 为 1.20.1 原版 LanguageSelectScreen 移植 1.21.11+（26.1.2）顶栏搜索框、初始焦点、
 * 缩放重建状态保持与光标闪烁，并避免聚焦搜索框时被原版空格/回车快捷键误关界面。
 */
@Mixin(LanguageSelectScreen.class)
public abstract class LanguageSelectScreenMixin extends OptionsSubScreen {
    @Shadow
    @Final
    LanguageManager languageManager;

    @Unique
    @Nullable
    private EditBox languagesearchbackport$search;

    @Unique
    @Nullable
    private LanguageListFilterable languagesearchbackport$listFilterable;

    @Unique
    private String languagesearchbackport$savedSearch = "";

    @Unique
    @Nullable
    private String languagesearchbackport$savedSelectedCode;

    protected LanguageSelectScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void languagesearchbackport$addSearchBox(CallbackInfo ci) {
        // 1.20.1 窗口缩放会触发 rebuildWidgets -> init()，先保留缩放前的选中语言。
        if (this.languagesearchbackport$listFilterable != null) {
            this.languagesearchbackport$savedSelectedCode = this.languagesearchbackport$listFilterable.languagesearchbackport$getLastSelectedCode();
        }

        int searchX = (this.width - LanguageSearchConstants.SEARCH_BOX_WIDTH) / 2;
        EditBox searchBox = new EditBox(
                this.font,
                searchX,
                LanguageSearchConstants.SEARCH_BOX_Y,
                LanguageSearchConstants.SEARCH_BOX_WIDTH,
                LanguageSearchConstants.SEARCH_BOX_HEIGHT,
                Component.empty()
        );
        searchBox.setHint(LanguageSearchConstants.SEARCH_HINT);
        // 先恢复保存的搜索词，再绑定监听，避免列表尚未重建时触发空回调。
        searchBox.setValue(this.languagesearchbackport$savedSearch);
        searchBox.setResponder(this::languagesearchbackport$onSearchChanged);

        this.languagesearchbackport$search = searchBox;
        // 放在控件列表首位，使 Tab 切换顺序与 26.1.2 / 1.21.1 保持一致（搜索框 -> 列表 -> 底部按钮）。
        this.addRenderableWidget(searchBox);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void languagesearchbackport$bindListAndInitialFocus(CallbackInfo ci) {
        this.languagesearchbackport$listFilterable = null;
        for (GuiEventListener child : this.children()) {
            if (child instanceof LanguageListFilterable filterable) {
                this.languagesearchbackport$listFilterable = filterable;
                break;
            }
        }

        if (this.languagesearchbackport$listFilterable != null && this.languagesearchbackport$savedSelectedCode != null) {
            this.languagesearchbackport$listFilterable.languagesearchbackport$restoreSelectedCode(this.languagesearchbackport$savedSelectedCode);
        }
        if (!this.languagesearchbackport$savedSearch.isEmpty()) {
            this.languagesearchbackport$onSearchChanged(this.languagesearchbackport$savedSearch);
        }
        if (this.languagesearchbackport$search != null) {
            this.setInitialFocus(this.languagesearchbackport$search);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void languagesearchbackport$preventSearchKeyConfirm(
            int keyCode,
            int scanCode,
            int modifiers,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // 1.20.1 原版 LanguageSelectScreen#keyPressed 会直接把空格和回车当作确认并关闭界面；
        // 当搜索框聚焦时跳过该分支，直接转交 Screen#keyPressed 处理编辑、Tab、Esc 与字符输入。
        if (this.languagesearchbackport$search != null && this.languagesearchbackport$search.isFocused()) {
            cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
        }
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = 16))
    private int languagesearchbackport$adjustTitleY(int originalTitleY) {
        // 将标题从 y=16 上移至 y=4，为下方 y=17..32 的搜索框留出居中位置。
        return LanguageSearchConstants.TITLE_Y;
    }

    @Override
    public void tick() {
        super.tick();
        // 1.20.1 EditBox 依靠 tick() 推进光标闪烁计数，必须在此驱动。
        if (this.languagesearchbackport$search != null) {
            this.languagesearchbackport$search.tick();
        }
    }

    @Unique
    private void languagesearchbackport$onSearchChanged(String filter) {
        this.languagesearchbackport$savedSearch = filter;
        if (this.languagesearchbackport$listFilterable != null) {
            this.languagesearchbackport$listFilterable.languagesearchbackport$filterEntries(this.languageManager, filter);
        }
    }
}
