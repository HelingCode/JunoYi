package com.junoyi.system.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.system.convert.SysDictDataConverter;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysDictDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统字典 API 实现类
 * 供其他模块调用的字典服务实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysDictApiImpl implements com.junoyi.system.api.SysDictApi {

    private final SysDictDataMapper sysDictDataMapper;
    private final SysDictDataConverter sysDictDataConverter;

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    public List<SysDictDataVO> getDictDataByType(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return List.of();
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        return sysDictDataConverter.toVoList(dictDataList);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签，如果不存在返回 null
     */
    @Override
    public String getDictLabel(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return null;
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .eq(SysDictData::getStatus, "0")
                .last("LIMIT 1");

        SysDictData dictData = sysDictDataMapper.selectOne(wrapper);
        return dictData != null ? dictData.getDictLabel() : null;
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值，如果不存在返回 null
     */
    @Override
    public String getDictValue(String dictType, String dictLabel) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictLabel)) {
            return null;
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictLabel, dictLabel)
                .eq(SysDictData::getStatus, "0")
                .last("LIMIT 1");

        SysDictData dictData = sysDictDataMapper.selectOne(wrapper);
        return dictData != null ? dictData.getDictValue() : null;
    }

    /**
     * 检查字典数据是否存在
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsDictData(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return false;
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .eq(SysDictData::getStatus, "0");

        Long count = sysDictDataMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 批量根据字典类型查询字典数据
     *
     * @param dictTypes 字典类型列表
     * @return 字典类型为key，字典数据列表为value的Map
     */
    @Override
    public Map<String, List<SysDictDataVO>> getDictDataByTypes(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysDictData::getDictType, dictTypes)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        List<SysDictDataVO> voList = sysDictDataConverter.toVoList(dictDataList);

        // 按字典类型分组
        return voList.stream()
                .collect(Collectors.groupingBy(SysDictDataVO::getDictType));
    }
}
