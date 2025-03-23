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
<summary><b>push报错：Support for password authentication was removed on August 13, 2021.？</b></summary>

github上点击进入Settings==》Developer Settings==》Personal access tokens (classic)，重新生成一个密码来作为登陆密码



---

## 🛠️ 开发者文档



### 如何发布
```bash
git tag v1.0.0
git push origin v1.0.0

```



