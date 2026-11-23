# Language Search Backport (Minecraft 1.21.1 NeoForge)

Backports the vanilla **Language Selection Screen search box** (introduced in Minecraft Java Edition 1.21.11 / snapshot 25w44a) to Minecraft 1.21.1 NeoForge.

## Features

- Vanilla-faithful centered `200×15` search box under the Language title (`headerHeight = 36`) with gray italic `Search...` hint when unfocused.
- Automatic initial keyboard focus on the search box.
- Case-insensitive (`Locale.ROOT`) real-time filtering by language name and region name with scroll clamping.
- Fixes `MC-313081` by preserving the active selection highlight when still matching the filter.
- Client-side UI enhancement that loads safely on dedicated servers.

## Branches

- `1211-neoforge`: Minecraft 1.21.1 (NeoForge)
- `1201-forge`: Minecraft 1.20.1 (Forge)

---

# Language Search Backport（Minecraft 1.21.1 NeoForge）

将原版 **Minecraft Java 版 1.21.11（快照 25w44a）** 新增的语言选择界面搜索栏完整向后移植至 Minecraft 1.21.1 NeoForge。

## 功能特性

- 还原原版顶栏布局（`headerHeight = 36`、居中 `200×15` 搜索框），失焦且为空时显示灰色斜体 `搜索…` / `Search...` 提示。
- 打开语言界面自动聚焦搜索框，可直接打字筛选。
- 按 `Locale.ROOT` 忽略大小写实时匹配语言名与地区名，自动钳制滚动条。
- 同步修复 `MC-313081`，筛选时保持当前可见已选语言的高亮状态。
- 纯客户端界面增强，服务端挂载安全不崩溃。
