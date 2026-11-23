package com.hp.languagesearchbackport;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 模组主入口类，保持双端安全，不直接引用任何客户端专用界面类。
 */
@Mod(LanguageSearchBackport.MOD_ID)
public final class LanguageSearchBackport {
    public static final String MOD_ID = "languagesearchbackport";

    public LanguageSearchBackport(FMLJavaModLoadingContext context) {
        // 语言搜索栏全部逻辑位于客户端 Mixin，此处无需额外注册服务端内容。
    }
}
