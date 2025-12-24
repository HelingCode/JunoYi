package com.junoyi.framework.captcha.generator;

import cn.hutool.core.util.IdUtil;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.captcha.store.CaptchaStore;

/**
 * 滑块验证码生成器 (基于AJ-Captcha)
 *
 * @author Fan
 */
public class SliderCaptchaGenerator implements CaptchaGenerator {

    private final CaptchaProperties properties;
    private final CaptchaStore captchaStore;
    private final CaptchaService captchaService;

    public SliderCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore, CaptchaService captchaService) {
        this.properties = properties;
        this.captchaStore = captchaStore;
        this.captchaService = captchaService;
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.SLIDER;
    }

    @Override
    public CaptchaResult generate() {
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaType("blockPuzzle");
        ResponseModel response = captchaService.get(captchaVO);

        if (!response.isSuccess()) {
            throw new RuntimeException("Failed to generate slider captcha: " + response.getRepMsg());
        }

        CaptchaVO data = (CaptchaVO) response.getRepData();
        String captchaId = IdUtil.fastSimpleUUID();

        // 存储AJ-Captcha的token，用于后续验证
        captchaStore.save(captchaId, data.getToken(), properties.getExpireSeconds());

        return new CaptchaResult()
                .setCaptchaId(captchaId)
                .setType(CaptchaType.SLIDER)
                .setBackgroundImage(data.getOriginalImageBase64())
                .setSliderImage(data.getJigsawImageBase64())
                .setBackgroundWidth(properties.getSlider().getWidth())
                .setBackgroundHeight(properties.getSlider().getHeight())
                .setExpireSeconds(properties.getExpireSeconds());
    }

    @Override
    public boolean validate(String captchaId, Object params) {
        if (params == null) return false;

        String token = captchaStore.get(captchaId);
        if (token == null) return false;

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaType("blockPuzzle");
        captchaVO.setToken(token);
        captchaVO.setPointJson(params.toString());

        ResponseModel response = captchaService.check(captchaVO);

        // 验证后删除
        captchaStore.remove(captchaId);

        return response.isSuccess();
    }
}
