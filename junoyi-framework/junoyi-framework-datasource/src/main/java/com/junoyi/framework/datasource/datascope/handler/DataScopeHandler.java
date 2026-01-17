package com.junoyi.framework.datasource.datascope.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder;
import com.junoyi.framework.datasource.datascope.DataScopeContextHolder.DataScopeContext;
import com.junoyi.framework.datasource.datascope.DataScopeType;
import com.junoyi.framework.datasource.datascope.annotation.DataScope;
import com.junoyi.framework.datasource.datascope.annotation.IgnoreDataScope;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MyBatis-Plus 数据权限处理器
 * <p>
 * 实现 DataPermissionHandler 接口，自动为查询添加数据范围过滤条件。
 * 支持两种模式：
 * 1. 注解模式：只对标注了 @DataScope 的 Mapper 方法生效
 * 2. 全局模式：对所有查询生效（需要配置），但会自动忽略系统表
 *
 * @author Fan
 */
public class DataScopeHandler implements DataPermissionHandler {

    /**
     * 系统表前缀，全局模式下自动忽略
     */
    private static final Set<String> IGNORE_MAPPER_PREFIXES = new HashSet<>(Arrays.asList(
            "com.junoyi.system.mapper.SysUserMapper",
            "com.junoyi.system.mapper.SysRoleMapper",
            "com.junoyi.system.mapper.SysDeptMapper",
            "com.junoyi.system.mapper.SysMenuMapper",
            "com.junoyi.system.mapper.SysPermissionMapper",
            "com.junoyi.system.mapper.SysSessionMapper",
            "com.junoyi.system.mapper.SysPermGroupMapper",
            "com.junoyi.system.mapper.SysUserRoleMapper",
            "com.junoyi.system.mapper.SysUserDeptMapper",
            "com.junoyi.system.mapper.SysUserGroupMapper",
            "com.junoyi.system.mapper.SysUserPermMapper",
            "com.junoyi.system.mapper.SysRoleGroupMapper",
            "com.junoyi.system.mapper.SysDeptGroupMapper"
    ));

    /**
     * @IgnoreDataScope 注解缓存（避免重复反射）
     * key: mappedStatementId, value: 是否有忽略注解
     */
    private static final Map<String, Boolean> IGNORE_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    /**
     * @DataScope 注解缓存（避免重复反射）
     * key: mappedStatementId, value: DataScope 注解（null 表示无注解）
     */
    private static final Map<String, DataScope> DATA_SCOPE_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    /**
     * 系统 Mapper 判断缓存
     */
    private static final Map<String, Boolean> SYSTEM_MAPPER_CACHE = new ConcurrentHashMap<>();

    /**
     * 是否启用全局数据范围（对所有查询生效）
     */
    private final boolean globalEnabled;

    /**
     * 默认部门字段名
     */
    private final String defaultDeptField;

    /**
     * 默认用户字段名
     */
    private final String defaultUserField;

    public DataScopeHandler() {
        this(false, "dept_id", "create_by");
    }

    public DataScopeHandler(boolean globalEnabled, String defaultDeptField, String defaultUserField) {
        this.globalEnabled = globalEnabled;
        this.defaultDeptField = defaultDeptField;
        this.defaultUserField = defaultUserField;
    }

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 检查是否有 @IgnoreDataScope 注解（使用缓存）
        if (hasIgnoreAnnotationCached(mappedStatementId)) {
            return where;
        }

        // 检查是否是系统表（无论什么模式都跳过系统表）
        if (isSystemMapperCached(mappedStatementId)) {
            return where;
        }

        // 获取 @DataScope 注解配置（使用缓存）
        DataScope dataScope = getDataScopeAnnotationCached(mappedStatementId);

        // 非全局模式下，没有注解则不处理
        if (!globalEnabled && dataScope == null) {
            return where;
        }

        // 获取上下文（在确认需要处理数据范围后再获取）
        DataScopeContext context = DataScopeContextHolder.get();

        // 无上下文，直接放行（避免 NPE）
        if (context == null) {
            return where;
        }
        
        // 超级管理员，直接放行
        if (context.isSuperAdmin()) {
            return where;
        }

        // 全部数据权限，直接放行
        if (context.getScopeType() == null || context.getScopeType() == DataScopeType.ALL) {
            return where;
        }

        // 获取字段配置
        String tableAlias = dataScope != null ? dataScope.tableAlias() : "";
        String deptField = dataScope != null ? dataScope.deptField() : defaultDeptField;
        String userField = dataScope != null ? dataScope.userField() : defaultUserField;

        // 构建过滤条件
        Expression scopeExpression = buildScopeExpression(context, tableAlias, deptField, userField);
        if (scopeExpression == null) {
            return where;
        }

        // 合并条件
        if (where == null) {
            return scopeExpression;
        }
        return new AndExpression(new Parenthesis(scopeExpression), where);
    }

    /**
     * 检查是否是系统 Mapper（使用缓存）
     */
    private boolean isSystemMapperCached(String mappedStatementId) {
        return SYSTEM_MAPPER_CACHE.computeIfAbsent(mappedStatementId, this::isSystemMapper);
    }

    /**
     * 检查是否是系统 Mapper（全局模式下自动忽略）
     */
    private boolean isSystemMapper(String mappedStatementId) {
        for (String prefix : IGNORE_MAPPER_PREFIXES) {
            if (mappedStatementId.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有 @IgnoreDataScope 注解（使用缓存）
     */
    private boolean hasIgnoreAnnotationCached(String mappedStatementId) {
        return IGNORE_ANNOTATION_CACHE.computeIfAbsent(mappedStatementId, this::hasIgnoreAnnotation);
    }

    /**
     * 检查是否有 @IgnoreDataScope 注解
     */
    private boolean hasIgnoreAnnotation(String mappedStatementId) {
        try {
            int lastDot = mappedStatementId.lastIndexOf('.');
            String className = mappedStatementId.substring(0, lastDot);
            String methodName = mappedStatementId.substring(lastDot + 1);

            Class<?> mapperClass = Class.forName(className);
            
            // 检查类级别注解
            if (mapperClass.isAnnotationPresent(IgnoreDataScope.class)) {
                return true;
            }
            
            // 检查方法级别注解
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.isAnnotationPresent(IgnoreDataScope.class);
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 获取 Mapper 方法上的 @DataScope 注解（使用缓存）
     */
    private DataScope getDataScopeAnnotationCached(String mappedStatementId) {
        // 使用特殊标记处理 null 值缓存
        if (DATA_SCOPE_ANNOTATION_CACHE.containsKey(mappedStatementId)) {
            return DATA_SCOPE_ANNOTATION_CACHE.get(mappedStatementId);
        }
        DataScope annotation = getDataScopeAnnotation(mappedStatementId);
        DATA_SCOPE_ANNOTATION_CACHE.put(mappedStatementId, annotation);
        return annotation;
    }

    /**
     * 获取 Mapper 方法上的 @DataScope 注解
     */
    private DataScope getDataScopeAnnotation(String mappedStatementId) {
        try {
            int lastDot = mappedStatementId.lastIndexOf('.');
            String className = mappedStatementId.substring(0, lastDot);
            String methodName = mappedStatementId.substring(lastDot + 1);

            Class<?> mapperClass = Class.forName(className);
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 构建数据范围过滤表达式
     */
    private Expression buildScopeExpression(DataScopeContext context, String tableAlias,
                                            String deptField, String userField) {
        // 防御性检查
        if (context == null) {
            return null;
        }
        
        String prefix = (tableAlias == null || tableAlias.isEmpty()) ? "" : tableAlias + ".";

        DataScopeType scopeType = context.getScopeType();
        
        // scopeType 为 null 时不处理
        if (scopeType == null) {
            return null;
        }

        switch (scopeType) {
            case DEPT:
                return buildDeptInExpression(prefix + deptField, context.getDeptIds());

            case DEPT_AND_CHILD:
                return buildDeptInExpression(prefix + deptField, context.getAccessibleDeptIds());

            case SELF:
                return buildUserEqualsExpression(prefix + userField, context.getUserName());

            default:
                return null;
        }
    }

    /**
     * 构建部门 IN 表达式
     */
    private Expression buildDeptInExpression(String fieldName, Set<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            // 返回 1=0 表示无数据
            return new EqualsTo(new LongValue(1), new LongValue(0));
        }

        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(fieldName));
        inExpression.setRightExpression(new ExpressionList(
                deptIds.stream().map(LongValue::new).collect(Collectors.toList())
        ));
        return inExpression;
    }

    /**
     * 构建用户等于表达式（使用用户名字符串）
     */
    private Expression buildUserEqualsExpression(String fieldName, String userName) {
        if (userName == null || userName.isEmpty()) {
            // 返回 1=0 表示无数据
            return new EqualsTo(new LongValue(1), new LongValue(0));
        }

        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(fieldName));
        equalsTo.setRightExpression(new StringValue(userName));
        return equalsTo;
    }
}
