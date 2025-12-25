# JunoYi 权限系统设计文档

## 一、设计理念

### 1.1 核心思想

打破传统 RBAC 框架（RuoYi/Sa-Token/Shiro）的局限性，采用 **权限节点 + 权限组** 的设计模式，灵感来源于 Minecraft 服务器的 LuckPerms 权限插件。

### 1.2 与传统框架对比

| 问题 | 传统框架 | JunoYi 方案 |
|------|----------|-------------|
| 权限与菜单耦合 | 权限必须绑定菜单 | 权限节点独立，与菜单解耦 |
| API 权限 | 每个 API 必须对应一个菜单 | 权限节点可独立定义 |
| 按钮权限 | 按钮是特殊菜单 | 按钮是独立权限节点 |
| 非界面操作 | 无处安放 | 任意定义权限节点 |
| 权限颗粒度 | 只到按钮级别 | 支持字段级、行级 |
| 通配符支持 | 不支持 | 支持 `system.user.*` |
| 数据范围 | 硬编码或简单配置 | 动态策略引擎 |

---

## 二、核心概念

### 2.1 权限节点（Permission Node）

权限节点是权限系统的最小单元，采用点分隔的层级结构：

```
system.user.create      # 创建用户
system.user.delete      # 删除用户
system.user.update      # 更新用户
system.user.view        # 查看用户
system.user.*           # 用户模块所有权限
system.*                # 系统模块所有权限
*                       # 超级管理员（所有权限）
```

#### 权限节点规范

```
<模块>.<资源>.<操作>.<维度>

示例：
system.user.delete              # API 级：删除用户接口
system.user.view.field.salary   # 字段级：查看薪资字段
system.user.view.row.dept       # 行级：查看本部门用户
order.export.button             # 按钮级：导出按钮
dashboard.chart.sales           # 组件级：销售图表
```

### 2.2 权限组（Permission Group）

权限组是权限节点的集合，用于批量授权：

```
用户 → 权限组 → 权限节点
```

权限组可以关联：
- 角色（Role）
- 部门（Department）
- 用户（User）

**注意：权限组不与菜单关联！**

### 2.3 权限维度

| 维度 | 说明 | 示例 |
|------|------|------|
| API 级 | 接口是否可调用 | `system.user.delete` |
| 菜单级 | 菜单是否可见 | `menu.system.user` |
| 组件级 | 页面组件是否可见 | `component.dashboard.chart` |
| 按钮级 | 操作按钮是否可见 | `button.user.export` |
| 行级 | 可访问的数据行范围 | `row.user.dept` |
| 字段级 | 可读/写的字段 | `field.user.salary.read` |

---

## 三、数据模型设计

### 3.1 ER 图

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   sys_user      │     │ sys_user_group  │     │ sys_perm_group  │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ id              │────<│ user_id         │>────│ id              │
│ username        │     │ group_id        │     │ group_code      │
│ ...             │     │ expire_time     │     │ group_name      │
└─────────────────┘     └─────────────────┘     │ parent_id       │
                                                │ priority        │
┌─────────────────┐     ┌─────────────────┐     └────────┬────────┘
│   sys_role      │     │ sys_role_group  │              │
├─────────────────┤     ├─────────────────┤              │
│ id              │────<│ role_id         │>─────────────┤
│ role_code       │     │ group_id        │              │
│ ...             │     └─────────────────┘              │
└─────────────────┘                                      │
                        ┌─────────────────┐              │
┌─────────────────┐     │ sys_dept_group  │              │
│   sys_dept      │     ├─────────────────┤              │
├─────────────────┤     │ dept_id         │              │
│ id              │────<│ group_id        │>─────────────┤
│ dept_name       │     └─────────────────┘              │
│ ...             │                                      │
└─────────────────┘     ┌─────────────────┐              │
                        │sys_group_permission│            │
                        ├─────────────────┤              │
                        │ group_id        │>─────────────┘
                        │ permission_id   │>─────────────┐
                        │ effect          │              │
                        └─────────────────┘              │
                                                         │
                        ┌─────────────────┐              │
                        │ sys_permission  │              │
                        ├─────────────────┤              │
                        │ id              │<─────────────┘
                        │ node            │  (system.user.delete)
                        │ name            │
                        │ type            │  (API/MENU/BUTTON/FIELD/ROW)
                        │ parent_id       │
                        │ resource        │
                        │ action          │
                        └─────────────────┘
```

### 3.2 核心表结构

#### sys_permission（权限节点表）

```sql
CREATE TABLE sys_permission (
    id              BIGINT PRIMARY KEY,
    node            VARCHAR(200) NOT NULL UNIQUE,  -- 权限节点：system.user.delete
    name            VARCHAR(100) NOT NULL,         -- 权限名称：删除用户
    type            VARCHAR(20) NOT NULL,          -- 类型：API/MENU/BUTTON/COMPONENT/FIELD/ROW
    parent_id       BIGINT DEFAULT 0,              -- 父节点ID（构建权限树）
    resource        VARCHAR(100),                  -- 资源标识
    action          VARCHAR(50),                   -- 操作标识
    description     VARCHAR(500),                  -- 描述
    sort            INT DEFAULT 0,                 -- 排序
    status          TINYINT DEFAULT 1,             -- 状态
    create_time     DATETIME,
    update_time     DATETIME
);
```

#### sys_perm_group（权限组表）

```sql
CREATE TABLE sys_perm_group (
    id              BIGINT PRIMARY KEY,
    group_code      VARCHAR(50) NOT NULL UNIQUE,   -- 权限组编码
    group_name      VARCHAR(100) NOT NULL,         -- 权限组名称
    parent_id       BIGINT DEFAULT 0,              -- 父权限组（支持继承）
    priority        INT DEFAULT 0,                 -- 优先级（数值越大优先级越高）
    description     VARCHAR(500),
    status          TINYINT DEFAULT 1,
    create_time     DATETIME,
    update_time     DATETIME
);
```

#### sys_group_permission（权限组-权限关联表）

```sql
CREATE TABLE sys_group_permission (
    id              BIGINT PRIMARY KEY,
    group_id        BIGINT NOT NULL,
    permission_id   BIGINT NOT NULL,
    effect          VARCHAR(10) DEFAULT 'ALLOW',   -- ALLOW/DENY（支持黑名单）
    conditions      JSON,                          -- 条件表达式（ABAC）
    create_time     DATETIME,
    UNIQUE KEY (group_id, permission_id)
);
```

#### sys_user_group（用户-权限组关联表）

```sql
CREATE TABLE sys_user_group (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    group_id        BIGINT NOT NULL,
    expire_time     DATETIME,                      -- 过期时间（支持临时授权）
    create_time     DATETIME,
    UNIQUE KEY (user_id, group_id)
);
```

#### sys_role_group（角色-权限组关联表）

```sql
CREATE TABLE sys_role_group (
    id              BIGINT PRIMARY KEY,
    role_id         BIGINT NOT NULL,
    group_id        BIGINT NOT NULL,
    create_time     DATETIME,
    UNIQUE KEY (role_id, group_id)
);
```

#### sys_dept_group（部门-权限组关联表）

```sql
CREATE TABLE sys_dept_group (
    id              BIGINT PRIMARY KEY,
    dept_id         BIGINT NOT NULL,
    group_id        BIGINT NOT NULL,
    create_time     DATETIME,
    UNIQUE KEY (dept_id, group_id)
);
```

---

## 四、权限匹配引擎

### 4.1 通配符匹配规则

```java
// 匹配规则优先级（从高到低）
system.user.delete      // 精确匹配
system.user.*           // 单级通配
system.**               // 多级通配
*                       // 全局通配
```

### 4.2 匹配算法

```java
public class PermissionMatcher {
    
    /**
     * 检查用户是否拥有指定权限
     * @param userPermissions 用户拥有的权限节点列表
     * @param requiredPermission 需要的权限节点
     */
    public boolean hasPermission(Set<String> userPermissions, String requiredPermission) {
        // 1. 精确匹配
        if (userPermissions.contains(requiredPermission)) {
            return true;
        }
        
        // 2. 通配符匹配
        for (String pattern : userPermissions) {
            if (matchWildcard(pattern, requiredPermission)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 通配符匹配
     * system.user.* 匹配 system.user.delete
     * system.** 匹配 system.user.delete.field
     */
    private boolean matchWildcard(String pattern, String permission) {
        // 实现通配符匹配逻辑
    }
}
```

### 4.3 权限计算流程

```
用户登录
    ↓
获取用户直接关联的权限组
    ↓
获取用户角色关联的权限组
    ↓
获取用户部门关联的权限组
    ↓
合并所有权限组（按优先级）
    ↓
展开权限组继承链
    ↓
计算最终权限节点集合
    ↓
处理 ALLOW/DENY 冲突（DENY 优先）
    ↓
缓存到 Redis
```

---

## 五、多维度权限控制

### 5.1 API 级权限

```java
@RequiresPermission("system.user.delete")
@DeleteMapping("/{id}")
public Result deleteUser(@PathVariable Long id) {
    // ...
}
```

### 5.2 字段级权限

```java
@FieldPermission(read = "system.user.view.field.salary", write = "system.user.edit.field.salary")
private BigDecimal salary;

// 查询时自动过滤无权限字段
// 更新时自动忽略无权限字段
```

### 5.3 行级权限（数据范围）

```java
@DataScope(permission = "system.user.view.row", scopeField = "dept_id")
public List<User> listUsers() {
    // 自动注入数据范围条件
}
```

数据范围策略：

| 策略 | 说明 |
|------|------|
| ALL | 所有数据 |
| DEPT | 本部门数据 |
| DEPT_AND_CHILD | 本部门及子部门 |
| SELF | 仅本人数据 |
| CUSTOM | 自定义 SQL |

### 5.4 前端权限判断

```javascript
// 判断是否有权限
if (hasPermission('system.user.delete')) {
    // 显示删除按钮
}

// 判断是否在权限组
if (inGroup('admin')) {
    // 显示管理功能
}

// 字段级控制
<el-table-column v-if="hasPermission('field.user.salary.read')" prop="salary" />
```

---

## 六、缓存策略

### 6.1 缓存结构

```
Redis Key 设计：

# 用户权限缓存
permission:user:{userId}:nodes     -> Set<String>  # 权限节点集合
permission:user:{userId}:groups    -> Set<String>  # 权限组集合

# 权限组缓存
permission:group:{groupCode}:nodes -> Set<String>  # 权限组包含的节点

# 权限树缓存
permission:tree                    -> JSON         # 完整权限树
```

### 6.2 缓存更新策略

- 权限修改后立即清除相关用户缓存
- 支持热加载，无需重启
- 用户下次请求时重新计算权限

---

## 七、模块结构

```
junoyi-framework-permission/
├── src/main/java/com/junoyi/framework/permission/
│   ├── annotation/                    # 注解
│   │   ├── RequiresPermission.java   # 权限校验注解
│   │   ├── DataScope.java            # 数据范围注解
│   │   └── FieldPermission.java      # 字段权限注解
│   ├── core/                          # 核心
│   │   ├── PermissionMatcher.java    # 权限匹配器
│   │   ├── PermissionEvaluator.java  # 权限计算器
│   │   └── PermissionContext.java    # 权限上下文
│   ├── handler/                       # 处理器
│   │   ├── PermissionHandler.java    # 权限处理接口
│   │   ├── ApiPermissionHandler.java # API 权限处理
│   │   ├── DataScopeHandler.java     # 数据范围处理
│   │   └── FieldPermissionHandler.java # 字段权限处理
│   ├── interceptor/                   # 拦截器
│   │   └── PermissionInterceptor.java
│   ├── aspect/                        # 切面
│   │   ├── PermissionAspect.java
│   │   └── DataScopeAspect.java
│   ├── cache/                         # 缓存
│   │   ├── PermissionCache.java
│   │   └── PermissionCacheManager.java
│   ├── config/                        # 配置
│   │   └── PermissionAutoConfiguration.java
│   └── helper/                        # 工具
│       └── PermissionHelper.java     # 权限工具类
└── src/main/resources/
    └── META-INF/spring/
        └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 八、使用示例

### 8.1 后端使用

```java
// 1. 注解方式
@RequiresPermission("system.user.delete")
public void deleteUser(Long id) { }

// 2. 编程方式
if (PermissionHelper.hasPermission("system.user.delete")) {
    // 有权限
}

// 3. 数据范围
@DataScope(permission = "system.user.view.row")
public List<User> listUsers(UserQuery query) {
    // SQL 自动追加数据范围条件
}

// 4. 字段过滤
User user = userService.getById(id);
PermissionHelper.filterFields(user); // 自动移除无权限字段
```

### 8.2 前端使用

```vue
<template>
  <!-- 按钮权限 -->
  <el-button v-permission="'system.user.delete'" @click="handleDelete">删除</el-button>
  
  <!-- 权限组判断 -->
  <AdminPanel v-if="inGroup('admin')" />
  
  <!-- 字段权限 -->
  <el-table-column v-if="hasPermission('field.user.salary.read')" prop="salary" label="薪资" />
</template>

<script setup>
import { hasPermission, inGroup } from '@/utils/permission'
</script>
```

---

## 九、配置项

```yaml
junoyi:
  permission:
    enable: true
    # 缓存配置
    cache:
      enable: true
      expire: 3600          # 缓存过期时间（秒）
    # 超级管理员配置
    super-admin:
      enable: true
      user-ids: [1]         # 超级管理员用户ID
      permission: "*"       # 超级管理员权限节点
    # 默认权限组
    default-groups:
      - guest               # 新用户默认权限组
```

---

## 十、与现有系统集成

### 10.1 与 Spring Security 集成

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**").access(permissionAuthorizationManager())
            .anyRequest().authenticated()
        );
        return http.build();
    }
    
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> permissionAuthorizationManager() {
        return new JunoYiPermissionAuthorizationManager();
    }
}
```

### 10.2 菜单与权限的关系

菜单表增加 `permission_node` 字段，但菜单本身不存储权限，只是引用：

```sql
ALTER TABLE sys_menu ADD COLUMN permission_node VARCHAR(200);
-- 菜单可见性由 permission_node 控制，但权限定义在 sys_permission 表
```

---

## 十一、总结

JunoYi 权限系统的核心优势：

1. **权限与菜单解耦** - 权限节点独立定义，不依赖菜单
2. **通配符支持** - `system.user.*` 一次授权多个操作
3. **多维度控制** - API/菜单/按钮/组件/行/字段 全覆盖
4. **灵活的授权模型** - 用户/角色/部门 都可关联权限组
5. **动态权限** - 修改即生效，无需重启
6. **高性能** - Redis 缓存 + 高效匹配算法
