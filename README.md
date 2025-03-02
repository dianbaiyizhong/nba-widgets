# NBA桌面小插件

<p align="center">
  <img src="./images/1740881016200.gif" alt="实时比赛比分预览" width="400"/>
  <br>
  <em>实时比赛比分预览</em>
</p>


---

## 🏀 功能特性
- **实时比分更新**：每分钟自动刷新比赛数据（支持自定义刷新频率）
- **多场比赛追踪**：滚动展示当日所有NBA比赛
- **球队主题色匹配**：小部件背景色动态适配主队颜色
- **低功耗模式**：仅在有比赛时唤醒网络请求
- **点击交互**：跳转到ESPN/NBA官网查看详情

---

## 📥 安装指南

### 要求
- Android 12+
- 至少 4x2 的桌面空间

### 步骤
1. 下载最新APK：[Releases页面](your-release-link)
2. 长按桌面 → 选择"小部件" → 找到 **NBA Score Widget**
3. 选择尺寸（推荐 4x2）并放置到桌面
4. 首次使用需在设置中[申请网络权限](#权限说明)

---

## ⚙️ 配置说明

### 设置项表格
| 选项 | 值类型 | 默认值 | 说明 |
|------|--------|--------|------|
| 刷新间隔 | 1/5/10分钟 | 5分钟 | 数据更新频率 |
| 主队优先 | 开关 | 开 | 始终将支持的主队显示在左侧 |
| 夜间模式 | 自动/开/关 | 自动 | 22:00-7:00自动切换深色主题 |

```markdown
![设置界面截图](your-settings-screenshot-link)
```

---

## 🔌 数据来源
- 官方数据源：通过 `NBA Stats API` 实时获取（[文档链接](#)）
- 备用数据源：`ESPN Cricinfo` 的公开接口（当主API不可用时自动切换）

---

## 📸 截图示例

| 白天模式 | 夜间模式 | 比赛进行中 |
|----------|----------|------------|
| ![Day](day.png) | ![Night](night.png) | ![Live](live.gif) |

---

## ❓ 常见问题

<details>
<summary><b>如何降低耗电量？</b></summary>

1. 将刷新间隔设为10分钟
2. 在"省电设置"中启用「仅Wi-Fi更新」
3. 关闭非关注球队的比赛通知
</details>

<details>
<summary><b>为什么小部件显示"No Games Today"？</b></summary>

可能原因：
- 当前无进行中的比赛（美国东部时间）
- API服务临时中断（可尝试手动刷新）
- 未授予网络权限
</details>

---

## 🛠️ 开发者文档

### 项目架构
```plaintext
src/
├── data/       # 网络请求与缓存逻辑
│   ├── NBAAPIClient.kt
│   └── CacheManager.kt
├── ui/         # 小部件界面
│   ├── WidgetProvider.kt
│   └── ScoreView.kt
└── utils/      # 工具类
    ├── TeamColorMapper.kt
    └── DateFormatter.kt
```

### 编译说明
```bash
# 使用 Android Studio 导入项目
./gradlew assembleRelease
```

---

## 🤝 贡献指南
欢迎通过以下方式参与：
1. 提交新的球队主题色配置（参考 `teams_colors.json`）
2. 翻译多语言支持（目前需要中文/英文）
3. 优化数据缓存逻辑

请先阅读 [CONTRIBUTING.md](your-contrib-link)

---

## 📄 许可证
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)

---

> 📧 问题反馈：通过 [Issues页面](your-issues-link) 或发送邮件至 your@email.com

---

**提示**：你可以通过以下方式增强Wiki表现力：
1. 添加实际运行视频/GIF
2. 在「数据来源」章节补充API调用示例代码
3. 使用 [Shields.io](https://shields.io) 添加构建状态徽章
4. 插入用户评价精选（如Reddit/Twitter反馈截图）

如果需要更动态的文档，推荐使用 [docsify](https://docsify.js.org) 生成交互式文档并部署到GitHub Pages！