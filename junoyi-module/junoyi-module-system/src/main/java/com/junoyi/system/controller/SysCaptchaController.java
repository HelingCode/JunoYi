package com.junoyi.system.controller;

import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
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

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysCaptchaController.class);

    private final ISysCaptchaService captchaService;

    /**
     * 获取图形验证码
     */
    @GetMapping("/image")
    public R<CaptchaResult> getImageCaptcha() {
        return R.ok(captchaService.getImageCaptcha());
    }

    /**
     * 校验图片验证码
     */
    @PostMapping("/validate")
    public R<Boolean> validate(@RequestParam String captchaId, @RequestParam String code) {
        return R.ok(captchaService.validate(captchaId, code));
    }

}
