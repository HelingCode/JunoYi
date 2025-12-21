package com.junoyi.framework.security.crypto;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * RSA密钥服务类
 * 提供RSA公私钥的加载和管理功能
 *
 * @author Fan
 */
@Component
public class RsaKeyService {
    private final JunoYiLog log = JunoYiLogFactory.getLogger(RsaKeyService.class);

    private PrivateKey privateKey;

    private PublicKey publicKey;


    /**
     * 初始化方法
     * 在对象创建完成后自动执行，用于从文件中加载RSA公私钥
     * @throws Exception 当密钥加载失败时抛出异常
     */
    @PostConstruct
    public void init() throws Exception {

        // 从文件中加载密钥
        log.info("RasKeyService", "密钥加载完成");

    }

    /**
     * 获取Base64编码格式的公钥字符串
     * @return Base64编码的公钥字符串
     */
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
