package com.junoyi.framework.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "junoyi.security")
public class SecurityProperties {


}