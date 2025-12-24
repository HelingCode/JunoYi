package com.junoyi.system.controller;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.system.service.ISysCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码控制器
 *
 * @author Fan
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class SysCaptchaController {

    private final ISysCaptchaService captchaService;

    /**
     * 获取图形验证码
     *
     * @return R<CaptchaResult> 包含图形验证码结果的响应对象
     */
    @GetMapping("/image")
    public R<CaptchaResult> getImageCaptcha() {
        CaptchaResult result = captchaService.getImageCaptcha();
        return R.ok(result);
    }

    /**
     * 获取滑块验证码
     *
     * @return R<CaptchaResult> 包含滑块验证码结果的响应对象
     */
    @GetMapping("/slider")
    public R<CaptchaResult> getSliderCaptcha(){

        return R.ok();
    }

    /**
     * 获取点击验证码
     *
     * @return R<CaptchaResult> 包含点击验证码结果的响应对象
     */
    @GetMapping("/click")
    public R<CaptchaResult> getClickCaptcha(){
        return R.ok();
    }
}
