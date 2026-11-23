package com.hp.languagesearchbackport.mixin;

import com.hp.languagesearchbackport.client.LanguageEntryAccess;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 向 1.20.1 原版语言列表条目注入只读访问接口，避免反射或访问转换器依赖。
 */
@Mixin(targets = "net.minecraft.client.gui.screens.LanguageSelectScreen$LanguageSelectionList$Entry")
public abstract class LanguageEntryMixin implements LanguageEntryAccess {
    @Shadow
    @Final
    String code;

    @Shadow
    @Final
    private Component language;

    @Override
    public String languagesearchbackport$getCode() {
        return this.code;
    }

    @Override
    public Component languagesearchbackport$getLanguageComponent() {
        return this.language;
    }
}
