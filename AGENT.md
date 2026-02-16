# JunoYi 子项目贡献指南

## 目录与模块
- `junoyi-server/`：启动入口与运行配置（`src/main/resources`）。
- `junoyi-framework/`：基础能力（安全、Web、Redis、日志、数据源等）。
- `junoyi-module-api/`：DTO/VO/PO/Mapper 等通用业务接口层。
- `junoyi-module/`：业务实现层（如 `junoyi-module-system`）。
- `sql/MySQL/junoyi.sql`：初始化与演进 SQL 脚本。

## 常用命令
在 `JunoYi/` 目录执行：

- `mvn -pl junoyi-server -am spring-boot:run`：启动后端服务。
- `mvn clean install`：全量构建所有模块。
- `mvn test`：执行测试。
- `mvn -pl junoyi-module/junoyi-module-system -am test`：仅验证系统模块。

## 代码规范
- Java 21，UTF-8，4 空格缩进。
- 包名全小写（`com.junoyi...`），类名 `PascalCase`，方法/字段 `camelCase`。
- Controller 仅做参数与响应编排，业务逻辑放 Service。
- 领域对象命名保持语义清晰：`LoginDTO`、`LoginBO`、`UserInfoVO`。

## 测试与验证
- 测试代码放 `src/test/java`，命名 `*Test.java`。
- 新增接口至少做手工回归：登录、鉴权、权限、核心 CRUD。
- 若改动安全链路（token/filter/session），需验证登录、刷新、退出全流程。

## 提交与 SQL 变更
- 提交信息遵循 Conventional Commits，如 `feat(system): ...`、`fix(auth): ...`。
- 涉及表结构或初始化数据，必须同步更新 `sql/MySQL/junoyi.sql`。
- PR 说明需写明：影响模块、配置变化、兼容性与回滚方案。

## 配置安全
- 不提交真实数据库、Redis、密钥配置。
- 本地调试优先使用 `application-local.yml`，生产参数由部署系统注入。
