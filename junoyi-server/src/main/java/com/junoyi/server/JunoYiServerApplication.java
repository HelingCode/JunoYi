package com.junoyi.server;


import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.stater.JunoYiApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 启动服务端
 *
 * @author Fan
 */
@SpringBootApplication(
    scanBasePackages = {"com.junoyi"},
    exclude = {DataSourceAutoConfiguration.class}
)
@EnableCaching
public class JunoYiServerApplication {
    private static final JunoYiLog log = JunoYiLogFactory.getLogger(JunoYiServerApplication.class);

    public static void main(String[] args) {
        JunoYiApplication.run(JunoYiServerApplication.class,args);
        log.info("JunoYi Server", "Startup completed. System is now operational.");
    }
}