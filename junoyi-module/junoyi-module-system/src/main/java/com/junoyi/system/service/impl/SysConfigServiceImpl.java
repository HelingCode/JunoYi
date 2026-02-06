package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.system.convert.SysConfigConverter;
import com.junoyi.system.domain.dto.SysConfigDTO;
import com.junoyi.system.domain.dto.SysConfigQueryDTO;
import com.junoyi.system.domain.po.SysConfig;
import com.junoyi.system.domain.vo.SysConfigVO;
import com.junoyi.system.mapper.SysConfigMapper;
import com.junoyi.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统参数配置业务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements ISysConfigService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysConfigServiceImpl.class);
    private final SysConfigMapper sysConfigMapper;
    private final SysConfigConverter sysConfigConverter;

    private static final String CACHE_KEY_PREFIX = "sys:config:";

    /**
     * 分页查询系统参数配置列表
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果对象，包含VO列表、总数、当前页码、每页大小
     */
    @Override
    public PageResult<SysConfigVO> getConfigList(SysConfigQueryDTO queryDTO) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(queryDTO.getConfigName()), SysConfig::getConfigName, queryDTO.getConfigName())
                .like(StringUtils.isNotBlank(queryDTO.getConfigKey()), SysConfig::getConfigKey, queryDTO.getConfigKey())
                .eq(StringUtils.isNotBlank(queryDTO.getConfigType()), SysConfig::getConfigType, queryDTO.getConfigType())
                .eq(StringUtils.isNotBlank(queryDTO.getConfigGroup()), SysConfig::getConfigGroup, queryDTO.getConfigGroup())
                .eq(queryDTO.getIsSystem() != null, SysConfig::getIsSystem, queryDTO.getIsSystem())
                .orderByAsc(SysConfig::getSort)
                .orderByDesc(SysConfig::getCreateTime);

        Page<SysConfig> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<SysConfig> resultPage = sysConfigMapper.selectPage(page, wrapper);

        List<SysConfigVO> voList = sysConfigConverter.toVoList(resultPage.getRecords());
        return PageResult.of(voList, resultPage.getTotal(), (int) resultPage.getCurrent(), (int) resultPage.getSize());
    }

    /**
     * 根据ID获取系统参数配置信息
     *
     * @param id 参数配置ID
     * @return 系统参数配置VO对象，如果不存在则返回null
     */
    @Override
    public SysConfigVO getConfigById(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        return config != null ? sysConfigConverter.toVo(config) : null;
    }

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值，如果不存在则返回null
     */
    @Override
    public String getConfigByKey(String configKey) {
        // 先从缓存获取
        String cacheKey = CACHE_KEY_PREFIX + configKey;
        String cachedValue = RedisUtils.getCacheObject(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getStatus, 0);
        SysConfig config = sysConfigMapper.selectOne(wrapper);

        if (config != null) {
            String value = config.getConfigValue();
            // 存入缓存（永久有效，手动刷新）
            RedisUtils.setCacheObject(cacheKey, value);
            return value;
        }

        return null;
    }

    /**
     * 新增系统参数配置
     *
     * @param configDTO 系统参数配置DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(SysConfigDTO configDTO) {
        // 检查键名是否已存在
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configDTO.getConfigKey());
        Long count = sysConfigMapper.selectCount(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("参数键名已存在");
        }

        SysConfig config = sysConfigConverter.toEntity(configDTO);

        // 设置默认值
        if (config.getIsSystem() == null) {
            config.setIsSystem(0); // 默认非系统内置参数
        }
        if (config.getStatus() == null) {
            config.setStatus(0); // 默认正常状态
        }
        if (config.getSort() == null) {
            config.setSort(0); // 默认排序
        }
        if (config.getConfigType() == null) {
            config.setConfigType("text"); // 默认文本类型
        }
        if (config.getConfigGroup() == null) {
            config.setConfigGroup("default"); // 默认分组
        }

        sysConfigMapper.insert(config);

        // 清除缓存
        clearCache(config.getConfigKey());
        log.info("Config", "添加系统参数: {}", config.getConfigKey());
    }

    /**
     * 更新系统参数配置
     *
     * @param configDTO 系统参数配置DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SysConfigDTO configDTO) {
        SysConfig oldConfig = sysConfigMapper.selectById(configDTO.getConfigId());
        if (oldConfig == null) {
            throw new IllegalArgumentException("参数不存在");
        }

        // 系统内置参数不允许修改键名
        if (oldConfig.getIsSystem() == 1 && !oldConfig.getConfigKey().equals(configDTO.getConfigKey())) {
            throw new IllegalArgumentException("系统内置参数不允许修改键名");
        }

        // 转换DTO到实体
        SysConfig config = sysConfigConverter.toEntity(configDTO);
        config.setConfigId(configDTO.getConfigId());

        // 保留原有的字段值（DTO中没有的字段）
        config.setIsSystem(oldConfig.getIsSystem()); // 不允许修改是否为系统内置
        if (config.getConfigType() == null) {
            config.setConfigType(oldConfig.getConfigType());
        }
        if (config.getConfigGroup() == null) {
            config.setConfigGroup(oldConfig.getConfigGroup());
        }
        if (config.getSort() == null) {
            config.setSort(oldConfig.getSort());
        }
        if (config.getStatus() == null) {
            config.setStatus(oldConfig.getStatus());
        }

        sysConfigMapper.updateById(config);

        // 清除缓存
        clearCache(oldConfig.getConfigKey());
        if (!oldConfig.getConfigKey().equals(config.getConfigKey())) {
            clearCache(config.getConfigKey());
        }

        log.info("Config", "更新系统参数: {}", config.getConfigKey());
    }

    /**
     * 删除系统参数配置
     *
     * @param id 系统参数配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null) {
            throw new IllegalArgumentException("参数不存在");
        }

        // 系统内置参数不允许删除
        if (config.getIsSystem() == 1) {
            throw new IllegalArgumentException("系统内置参数不允许删除");
        }

        sysConfigMapper.deleteById(id);

        // 清除缓存
        clearCache(config.getConfigKey());
        log.info("Config", "删除系统参数: {}", config.getConfigKey());
    }

    /**
     * 批量删除系统参数配置
     *
     * @param ids 系统参数配置ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigBatch(List<Long> ids) {
        // 检查是否包含系统内置参数
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysConfig::getConfigId, ids)
                .eq(SysConfig::getIsSystem, 1);
        Long count = sysConfigMapper.selectCount(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("不能删除系统内置参数");
        }

        // 获取所有配置的键名，用于清除缓存
        List<SysConfig> configs = sysConfigMapper.selectBatchIds(ids);

        // 批量删除
        for (Long id : ids) {
            sysConfigMapper.deleteById(id);
        }

        // 清除缓存
        configs.forEach(config -> clearCache(config.getConfigKey()));
        log.info("Config", "批量删除系统参数: {} 条", ids.size());
    }

    /**
     * 刷新系统参数缓存
     */
    @Override
    public void refreshCache() {
        // 清除所有配置缓存
        RedisUtils.deleteKeys(CACHE_KEY_PREFIX + "*");
        log.info("Config", "刷新系统参数缓存");
    }

    /**
     * 清除指定键名的缓存
     *
     * @param configKey 配置键名
     */
    private void clearCache(String configKey) {
        String cacheKey = CACHE_KEY_PREFIX + configKey;
        RedisUtils.deleteObject(cacheKey);
    }
}
