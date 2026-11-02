# TM手机Agent

TM手机Agent 是基于 wangzan101/Mika 的二次开发版本，面向微信客服场景。
底层复用 Mika 的虚拟屏、ADB 助手、无障碍读屏与 LLM 工具循环，本版只加客服层与品牌显示名。

## 与原版的关系与授权

- 原项目：https://github.com/wangzan101/Mika ，Apache-2.0 开源。
- 本仓库保留原项目的 LICENSE、NOTICE 与第三方组件声明，不得删除原作者归属。
  TM手机Agent 是 fork 后的再开发，不是原项目的官方版，也不是从零原创。
- 上游版本基线：commit `ebb0e44`（版本 0.1.33）。

## 本版 v1 做了什么

- 微信客服 Skill：`core/src/main/resources/skills/wechat-customer-service/SKILL.md`
  巡查未读、分类、按 FAQ 起草，人工确认后才发送。
- FAQ 占位模板：`docs/wechat-customer-service/faq-template.md`
  未填之前，所有价格、库存、发货时间一律答“待确认”，不编造。
- 分类草稿：`core/src/main/kotlin/com/wangzan101/mika/core/cs/WeChatCsPolicy.kt`
  未接入发送链路，不构成硬拦截。
- 详细说明与测试清单：`docs/wechat-customer-service/v1.md`
- 应用显示名：桌面图标与系统里显示为“TM手机Agent”
  （`app/src/main/res/values/strings.xml` 的 `app_name`）。

## v1 刻意不做的事（重要）

- 不默认自动发送、不群发、不主动加好友、不做营销。
- 不代付、不代退款、不改订单、不在微信里输密码和验证码。
- 不承诺绕过微信风控。UI 自动化做微信客服本身就有被限制、封号的风险，
  测试必须用测试号，不能用主号。
- “人工确认”目前不是硬保证：原项目已有的 `messaging` 技能写的是
  “用户要发的，写好就发，不用先问”，客服任务可能同时命中它。
  每次使用时必须明确说“只起草不发”，由你逐条确认。
  真正的硬拦截要改审批层并真机验证，是 v2 的工作。

## 这次品牌改动为什么只改显示名

- 应用显示名只改了一处（`app_name`），因为 AndroidManifest 的 label 引用它，
  各语言版本会回退到这个默认值。
- 包名与内部类名暂不改：代码里有 4000 多处 `com.wangzan101.mika` 引用，
  涉及 AIDL、助手进程、Provider authority、无障碍服务组件名等，
  全量改名需要重构 + 全量真机回归，v1 强行改很容易导致助手连不上、
  虚拟屏建不起来。包名改名是 v2 的独立任务，不是顺手改字符串。
- 上游 README.md 与翻译文件暂不改：原项目有测试守着生成段与翻译指纹，
  改标题会先把那套测试弄红。本仓库的对外说明以本文件为准；
  如果要在 GitHub 首页直接显示 TM，请在建仓后把本文件重命名为 README.md
  并同步处理翻译测试，而不是直接覆盖上游 README。

## 构建前置（与原项目一致）

- Android SDK、Gradle、JDK 17。
- 本机（本开发 VM）无 Java，Gradle 单测未在本机跑通。
  合并后请在有 JDK 17 的机器上跑：
  `./gradlew :core:test --tests "*BundledKnowledgeTest*" --tests "*ReadmeSectionsTest*"`
- 构建与真机设置仍按原项目 `docs/setup-guide.md`：
  无线调试配对、虚拟屏、无障碍、兼容性自检，测试号、测试机先行。

## 提交到 GitHub 之前必须做的事

1. 在 GitHub 上新建一个空仓库（不要直接推到原 Mika 仓库；
   本地克隆的 remote 现在还是 `https://github.com/wangzan101/Mika.git`，
   推送前必须换掉）。
2. 把 `app/src/main/java/com/wangzan101/mika/about/MikaLinks.kt` 里的
   `REPO`、`REPO_LABEL`、`LATEST_RELEASE_API` 改成你自己的仓库地址，
   并发布第一个 Release，否则检查更新仍会指向原 Mika 仓库，
   还可能因签名不同被系统拒绝安装更新。
3. 检查提交内容里没有 API Key、Token、测试微信账号密码、客户聊天记录。
   本版的 FAQ 模板是占位符，填真实业务信息前请确认是否适合公开。
4. 用你自己的 Git 身份提交，不要用别人的名字或邮箱提交。

推送命令示例（把下面两处换成你自己的地址，仓库建好后再执行）：

```sh
git remote set-url origin https://github.com/<你的用户名>/<你的仓库名>.git
git push -u origin main
```

注意：如果本地当前分支不是 `main`，先确认分支名再推，不要直接覆盖别人的默认分支。
