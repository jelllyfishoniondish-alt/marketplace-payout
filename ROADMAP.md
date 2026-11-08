# 路线图（给 Yumeng）

骨架已经搭好：订单服务完整可用；结算服务的 Kafka 接入、数据库和 API 都接好了。
**两个核心业务逻辑留给你写**（第 1、2 步），测试已经写好，你的目标是让它们变绿。
面试时这两块最容易被问到，自己写过才讲得清楚。

---

## 第 0 步：环境（约 30 分钟）

1. 安装 **JDK 21**（`brew install openjdk@21`，或在 IntelliJ 里下载）
2. 安装 **Maven**（`brew install maven`），或直接用 IntelliJ 自带的
3. 安装并打开 **Docker Desktop**
4. 用 IntelliJ 打开 `marketplace-payout` 文件夹（选根目录的 `pom.xml`，作为 Maven 项目导入）
5. 运行 `mvn verify`。**预期结果**：
   - order-service 的 3 个测试类全部通过 ✅
   - `CommissionCalculatorTest` 失败 ❌（第 1 步）
   - `PayoutServiceTest.ignoresADuplicateEvent` 失败 ❌（第 2 步）
   - 其他失败（编译错误、依赖下载失败）→ 把报错发给我

## 第 1 步：佣金计算 `CommissionCalculator.split`（约 1 小时）

文件：`payout-service/.../domain/CommissionCalculator.java`
规则写在 TODO 注释里。只运行这个测试：`mvn -pl payout-service test -Dtest=CommissionCalculatorTest`

想清楚这几个问题（面试常问）：
- 为什么钱用 `long` 分（cents）而不是 `double` 欧元？（在 jshell 里试试 `0.1 + 0.2`）
- 为什么 net = gross − commission，而不是 gross × 0.9 再四舍五入？（提示：会丢 1 分钱）
- HALF_UP 和 HALF_EVEN（银行家舍入）有什么区别？

## 第 2 步：幂等消费 `PayoutService.record`（约 1 小时）

文件：`payout-service/.../domain/PayoutService.java`
测试：`mvn -pl payout-service test -Dtest=PayoutServiceTest`

想清楚：
- Kafka 为什么是「至少一次」投递？什么情况下同一条消息会来两次？（consumer 崩溃、rebalance、offset 还没提交）
- 先查 `existsByOrderId` 再插入，两个线程同时来会怎样？为什么数据库唯一约束还要保留？
- 做完后跑 `PayoutFlowIntegrationTest`，对比第 2 步前后的日志（之前有异常和 10 次重试，之后只有一行 "Duplicate event ignored"）

## 第 3 步：端到端跑起来（约 30 分钟）

按 README 的「Run it locally」启动，用 curl 下两个订单，查卖家余额。
截一张图，或录一个 30 秒的 GIF，放进 README。

## 第 4 步：上 GitHub（约 30 分钟）

```bash
git init && git add . && git commit -m "Initial skeleton: order and payout services"
gh repo create marketplace-payout --public --source=. --push   # 或在 github.com 手动建仓库
```
看 Actions 页面的 CI 变绿 ✅，再把仓库链接加进 CV 和 LinkedIn。

---

## 之后的扩展（按对目标岗位的价值排序）

| 扩展 | 学到什么 | 对应岗位 |
|---|---|---|
| **退款**：`OrderRefundedEvent` 冲正已结算的金额 | 事件建模、账本思维 | Mirakl、Natixis |
| **Transactional outbox** | 分布式一致性（面试高频） | Murex、Equativ、Back Market |
| **Dead-letter topic** | 生产环境的错误处理 | Equativ、Back Market |
| **Flyway** 数据库迁移 | 生产级数据库管理 | 所有 |
| **Angular + TypeScript 后台** | 补前端短板 | Mirakl、Sopra Steria、Natixis、EDF |
| **Dockerfile + Kubernetes**（kind） | 部署 | Equativ、Sia、Back Market |
| **压测**（k6 或 Gatling，测 p50/p99） | 性能，延续你测延迟的经验 | Equativ、Murex |

## 面试时的 2 分钟讲法（做完第 1 到 4 步后练）

1. **问题**：卖家应该拿到多少钱？要异步算，而且不能重复付款。
2. **架构**：两个服务、各自一个库，通过 Kafka 事件通信，按 sellerId 分区保证顺序。
3. **难点**：至少一次投递 → 幂等消费者 + 唯一约束；金额用整数分 + BigDecimal。
4. **验证**：单元测试 + Testcontainers 集成测试，模拟重复投递；CI 自动运行。
5. **局限和下一步**：dual write → outbox。
