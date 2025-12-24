package com.junoyi.framework.captcha.config;

import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.junoyi.framework.captcha.generator.ImageCaptchaGenerator;
import com.junoyi.framework.captcha.generator.SliderCaptchaGenerator;
import com.junoyi.framework.captcha.generator.CaptchaGenerator;
import com.junoyi.framework.captcha.helper.CaptchaHelper;
import com.junoyi.framework.captcha.helper.CaptchaHelperImpl;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.captcha.store.CaptchaStore;
import com.junoyi.framework.captcha.store.RedisCaptchaStore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 * 验证码模块自动配置
 *
 * @author Fan
 */
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@ConditionalOnProperty(prefix = "junoyi.captcha", name = "enable", havingValue = "true", matchIfMissing = true)
public class CaptchaConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CaptchaConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(CaptchaStore.class)
    public CaptchaStore captchaStore(RedissonClient redissonClient) {
        log.info("[Captcha] Redis captcha store initialized");
        return new RedisCaptchaStore(redissonClient);
    }

    @Bean
    public ImageCaptchaGenerator imageCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore) {
        log.info("[Captcha] Image captcha generator initialized, code type: {}", properties.getImage().getCodeType());
        return new ImageCaptchaGenerator(properties, captchaStore);
    }

    @Bean
    @ConditionalOnMissingBean(CaptchaService.class)
    public CaptchaService captchaService(CaptchaProperties captchaProperties) {
        CaptchaProperties.SliderCaptcha slider = captchaProperties.getSlider();
        log.info("[Captcha] AJ-Captcha service initialized, size: {}x{}, tolerance: {}",
                slider.getWidth(), slider.getHeight(), slider.getTolerance());

        Properties props = new Properties();
        props.setProperty("captcha.type", "blockPuzzle");
        props.setProperty("captcha.water.mark", slider.getWaterMark());
        props.setProperty("captcha.slip.offset", String.valueOf(slider.getTolerance()));
        props.setProperty("captcha.aes.status", String.valueOf(slider.isAesStatus()));
        props.setProperty("captcha.interference.options", String.valueOf(slider.getInterferenceOptions()));

        return CaptchaServiceFactory.getInstance(props);
    }

    @Bean
    public SliderCaptchaGenerator sliderCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore, CaptchaService captchaService) {
        log.info("[Captcha] Slider captcha generator initialized");
        return new SliderCaptchaGenerator(properties, captchaStore, captchaService);
    }

    @Bean
    @ConditionalOnMissingBean(CaptchaHelper.class)
    public CaptchaHelper captchaHelper(CaptchaProperties properties, List<CaptchaGenerator> generators) {
        log.info("[Captcha] CaptchaHelper initialized, default type: {}, available generators: {}",
                properties.getType(), generators.size());
        return new CaptchaHelperImpl(properties, generators);
    }
}
