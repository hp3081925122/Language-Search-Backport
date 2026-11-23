package com.hp.languagesearchbackport;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * 模组主入口类，保持双端安全，不直接引用任何客户端专用界面类。
 */
@Mod(LanguageSearchBackport.MOD_ID)
public final class LanguageSearchBackport {
    public static final String MOD_ID = "languagesearchbackport";

    public LanguageSearchBackport(IEventBus modEventBus, ModContainer modContainer) {
        // 语言搜索栏全部逻辑位于客户端 Mixin，此处无需额外注册服务端内容。
    }
}
