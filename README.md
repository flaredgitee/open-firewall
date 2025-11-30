# OpenFirewall

OpenFirewall 是一个极简 CLI，用于根据本机公网 IP 动态刷新腾讯云 Lighthouse 实例的防火墙规则。工具一次只执行一次：你的外层调度器（例如
`watch`、`cron`、systemd timer）负责循环调度，它负责解析配置、获取公网 IP、比对与 Lighthouse 当前规则的差异，并在需要时提交更新。

## 功能特性

- ✅ 单次运行即完成一次“获取 IP → 规划 → 更新”流程，方便与任何外部调度器组合。
- ✅ CLI 自带完整 `--help` 输出，无需查阅源码或额外文档即可了解配置格式。
- ✅ 采用 `Result` 语义串联步骤，发生错误时会打印原因并以非零码退出，便于脚本感知失败。
- ✅ Tencent Cloud 交互已封装，可按需替换/扩展成其他供应商实现。

## 运行前提

- JDK 17+
- 已在运行环境配置腾讯云 API 访问凭证（参见 [Tencent Cloud SDK 文档](https://www.tencentcloud.com/document/sdk)）。
- 可执行 `ipCommand`能够输出纯文本公网 IP。

## 配置文件

CLI 通过 `-c/--config` 读取 TOML 配置，字段说明与示例如下：

```toml
instanceId = "lhins-123456"
ruleName = "openfirewall"
ipCommand = "pwsh -Command curl -s https://getip.example.com"
region = "ap-beijing"
```

字段含义：

- `instanceId`：需要写入规则的 Lighthouse 实例 ID。
- `ruleName`：写入规则的描述值，也是判定“是否需要更新”的唯一标识。
- `ipCommand`：子进程命令，输出当前公网 IP（IPv4 或 IPv6 均可）。
- `region`：腾讯云地域 ID，例如 `ap-beijing`。

## 构建与分发

生成可执行 JAR（包含依赖）并确认 Manifest：

```shell
./gradlew build
```

构建产物位于 `build\libs\OpenFirewall-<version>.jar`。由于 `jar` 任务已打包运行时依赖，用户只需该单个文件即可。

## 运行方式

最简单的使用方式：

```shell
java -jar build\libs\OpenFirewall-0.1.0.jar --config C:\path\to\config.toml
```

程序会在需要更新规则时输出 API 响应，若本次 IP 与远端一致，则提示 “No firewall update needed.” 并立即退出。任何步骤异常都会打印原因并返回非零退出码。

## 故障排查

- `no main manifest attribute`：确认使用 `./gradlew jar` 重新打包，项目已在 `build.gradle` 中声明 `Main-Class`。
- `InvalidParameterValue ... Ipv6CidrBlock`：通常来自云端返回的空 CIDR，本项目会自动过滤空字符串。若仍出现，检查你的
  `ipCommand` 是否输出 IPv6，但远端规则只支持 IPv4，或反之。
- 网络/凭证错误：查看堆栈中的 `TencentCloudSDKException`，确认密钥、地域、实例 ID 均正确。

## 许可证

本项目使用 Apache License 2.0，详情见仓库根目录的 `LICENSE` 文件。

