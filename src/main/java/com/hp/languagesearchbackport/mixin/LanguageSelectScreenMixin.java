package com.hp.languagesearchbackport.mixin;

import com.hp.languagesearchbackport.client.LanguageListFilterable;
import com.hp.languagesearchbackport.client.LanguageSearchConstants;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为 1.21.1 原版 LanguageSelectScreen 移植 1.21.11+（26.1.2）顶栏搜索框与初始焦点逻辑。
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

    protected LanguageSelectScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Override
    protected void addTitle() {
        // 与 26.1.2 原版保持一致：垂直居中排列标题与 200x15 搜索框，并将顶栏高度调整为 36。
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical().spacing(LanguageSearchConstants.HEADER_SPACING));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(this.title, this.font));
        this.languagesearchbackport$search = header.addChild(
                new EditBox(
                        this.font,
                        0,
                        0,
                        LanguageSearchConstants.SEARCH_BOX_WIDTH,
                        LanguageSearchConstants.SEARCH_BOX_HEIGHT,
                        Component.empty()
                )
        );
        this.languagesearchbackport$search.setHint(LanguageSearchConstants.SEARCH_HINT);
        this.languagesearchbackport$search.setResponder(this::languagesearchbackport$onSearchChanged);
        this.layout.setHeaderHeight(LanguageSearchConstants.HEADER_HEIGHT);
    }

    @Override
    protected void setInitialFocus() {
        // 打开语言界面时默认聚焦搜索框，可直接键盘输入筛选。
        if (this.languagesearchbackport$search != null) {
            this.setInitialFocus(this.languagesearchbackport$search);
        } else {
            super.setInitialFocus();
        }
    }

    @Inject(method = "addContents", at = @At("TAIL"))
    private void languagesearchbackport$captureSelectionList(CallbackInfo ci) {
        // 通过布局遍历获取刚创建的语言列表引用，避免直接引用包私有内部类产生字段签名不匹配。
        this.languagesearchbackport$listFilterable = null;
        this.layout.visitWidgets(widget -> {
            if (widget instanceof LanguageListFilterable filterable) {
                this.languagesearchbackport$listFilterable = filterable;
            }
        });
        // 若此前搜索框已有内容，则在列表重建后立即同步一次过滤状态。
        if (this.languagesearchbackport$search != null && !this.languagesearchbackport$search.getValue().isEmpty()) {
            this.languagesearchbackport$onSearchChanged(this.languagesearchbackport$search.getValue());
        }
    }

    @Unique
    private void languagesearchbackport$onSearchChanged(String filter) {
        if (this.languagesearchbackport$listFilterable != null) {
            this.languagesearchbackport$listFilterable.languagesearchbackport$filterEntries(this.languageManager, filter);
        }
    }
}
