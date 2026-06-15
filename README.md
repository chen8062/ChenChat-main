<h1 align="center">ChenChat Server 🖥️</h1>
<p align="center"><strong>基于 Spring Boot + Netty 的即时通讯（IM）后端服务。<br>支持 WebSocket 实时通信、微信扫码登录、多种消息类型、分布式锁、频控等企业级实践。</strong></p>

<p align="center">
  <a href="#技术栈"><img src="https://img.shields.io/badge/Spring_Boot-2.6-brightgreen?style=flat-square&logo=springboot"></a>
  <a href="#技术栈"><img src="https://img.shields.io/badge/Java-8-orange?style=flat-square"></a>
  <a href="#技术栈"><img src="https://img.shields.io/badge/Netty-4.1-red?style=flat-square"></a>
</p>

---

## 📖 项目介绍

ChenChat Server 是一个即时通讯系统的后端服务，通过 **Netty** 实现与前端的高性能 **WebSocket** 长连接通信。项目采用 Spring Boot 作为基础框架，集成了微信 SDK、Minio 对象存储、Redis 分布式缓存等中间件。

### 核心功能

- 🔌 **WebSocket 实时通信** — 基于 Netty 实现高性能长连接，支持万人群聊
- 🔐 **微信扫码登录** — 对接微信公众号，实现扫码认证 + JWT 令牌管理
- 💬 **消息收发** — 支持文本、图片、语音、视频、文件、表情等消息类型
- ↩️ **消息操作** — 消息撤回、点赞、删除、已读回执
- 👥 **好友 & 群组** — 联系人管理、群组创建、成员管理、角色权限
- 📊 **消息推送** — 离线消息、未读计数、消息标记
- 🛡 **敏感词过滤** — DFA / AC 自动机 双引擎敏感词检测
- 🚦 **频控 & 限流** — 基于注解的频控和编程式限流防护
- 🔒 **分布式锁** — 基于 Redisson 的分布式锁注解，防止并发问题
- 📍 **IP 归属地解析** — 自动解析用户 IP 归属地
- 📦 **文件存储** — Minio 对象存储，支持图片/语音/视频/文件上传

> 💡 前端项目地址：[ChenChat-Web](https://github.com/chen8062/ChatWeb)

---

## 🗂 项目模块

```
chen-main/
├── chen-chat-server/           # 聊天核心服务
│   ├── chat/                   #   聊天业务逻辑
│   ├── user/                   #   用户模块
│   ├── websocket/              #   Netty WebSocket 处理
│   └── ...
├── chen-tools/                 # 工具模块集
│   ├── chen-common-starter/    #   通用基础组件
│   ├── chen-frequency-control/ #   频控 & 限流组件
│   ├── chen-oss-starter/       #   对象存储组件（Minio）
│   ├── chen-redis/             #   Redis 工具组件
│   └── chen-transaction/       #   分布式事务组件
├── docs/
│   └── chen.sql                # 数据库初始化脚本
└── pom.xml                     # 父 POM
```

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本要求 |
|:---|:---|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Minio | (可选，用于文件存储) |

### 本地启动

```bash
# 1. 克隆项目
git clone https://github.com/chen8062/ChenChat-main.git

# 2. 导入数据库
#    执行 docs/chen.sql 初始化数据库

# 3. 修改配置
#    编辑 application-test.properties，填写你的 MySQL、Redis、微信等配置

# 4. 启动项目
cd ChenChat-main
mvn clean install -DskipTests
cd chen-chat-server
mvn spring-boot:run
```

启动后，WebSocket 服务默认监听对应端口，前端可通过该端口建立 WebSocket 连接。

---

## 🛠 技术栈

| 技术 | 说明 | 版本 |
|:---|:---|:---|
| Spring Boot | 基础框架 | 2.6.7 |
| Netty | 高性能网络通信框架 | 4.1.76 |
| MyBatis Plus | ORM 框架 | 3.4.0 |
| MySQL | 关系型数据库 | 8.0 |
| Redis / Redisson | 缓存 & 分布式锁 | 3.17.1 |
| Minio | 对象存储 | 8.4.5 |
| JWT (jjwt) | 身份认证令牌 | 0.9.1 |
| Weixin-Java-MP | 微信公众号 SDK | 4.4.0 |
| Hutool | Java 工具库 | 5.8.18 |
| Lombok | 简化 Java 代码 | 1.18.30 |
| Swagger | API 文档 | 3.0.0 |

### 技术亮点

- **Netty WebSocket**: 自定义 WebSocket 握手认证，支持大规模并发连接
- **注解驱动**: 分布式锁注解、频控注解、AOP 日志，开箱即用
- **消息队列**: 使用 Redis Stream / 消费者模式解耦消息处理
- **模块化设计**: `chen-tools` 下的组件可独立复用

---

## 📊 数据库表概览

| 表名 | 说明 |
|:---|:---|
| user | 用户表 |
| contact | 联系人关系表 |
| message | 消息表 |
| message_mark | 消息标记（点赞等） |
| room | 房间（会话）表 |
| room_friend | 好友单聊房间 |
| room_group | 群组房间 |
| group_member | 群组成员 |
| wx_msg | 微信消息记录 |

---

## 🔗 相关链接

- **前端项目**: [ChenChat-Web](https://github.com/chen8062/ChatWeb)
