# Language Search Backport (Minecraft 1.20.1 Forge)

Backports the vanilla **Language Selection Screen search box** (introduced in Minecraft Java Edition 1.21.11 / snapshot 25w44a) to Minecraft 1.20.1 Forge.

## Features

- Centered `200×15` search box under the Language title (`y = 17`, title shifted to `y = 4`, list `y0 = 36`) with gray italic `Search...` hint when unfocused.
- Automatic initial keyboard focus and `tick()`-driven cursor blinking.
- Prevents 1.20.1's `LanguageSelectScreen#keyPressed` from closing the screen when typing spaces or pressing Enter inside the search box.
- Case-insensitive (`Locale.ROOT`) real-time filtering by language name and region name, scroll clamping, `MC-313081` selection preservation, and search state preservation across window resizes.
- Client-side UI enhancement with `displayTest="IGNORE_ALL_VERSION"`, safe on dedicated servers.

## Branches

- `1211-neoforge`: Minecraft 1.21.1 (NeoForge)
- `1201-forge`: Minecraft 1.20.1 (Forge)

---

# Language Search Backport（Minecraft 1.20.1 Forge）

将原版 **Minecraft Java 版 1.21.11（快照 25w44a）** 新增的语言选择界面搜索栏完整向后移植至 Minecraft 1.20.1 Forge。

## 功能特性

- 标题上移至 `y = 4`、居中 `200×15` 搜索框置于 `y = 17`、列表顶部下移至 `y0 = 36`，失焦且为空时显示灰色斜体 `搜索…` / `Search...` 提示。
- 打开语言界面自动聚焦搜索框，并驱动 1.20.1 `EditBox#tick()` 正常闪烁光标。
- 聚焦搜索框时绕过 1.20.1 原版空格/回车快捷键确认逻辑，输入含空格的语言名或按回车不会误关界面。
- 按 `Locale.ROOT` 忽略大小写实时匹配语言名与地区名，自动钳制滚动条、修复 `MC-313081`，并在窗口缩放重建控件时完整保留搜索词与筛选结果。
- 声明 `displayTest="IGNORE_ALL_VERSION"`，单端联机不红叉，专用服务端挂载安全不崩溃。
