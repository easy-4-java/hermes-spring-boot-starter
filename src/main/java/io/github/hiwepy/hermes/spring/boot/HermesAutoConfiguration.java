package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesClient;
import io.github.hiwepy.hermes.cli.availability.HermesCliAvailabilityChecker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Hermes Spring Boot 自动配置。
 */
@Configuration
@ConditionalOnClass(HermesClient.class)
@ConditionalOnProperty(prefix = HermesProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HermesProperties.class)
public class HermesAutoConfiguration {

    /**
     * 注册 Hermes 客户端门面 Bean。
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public HermesClient hermesClient(HermesProperties properties) {
        return new HermesClient(properties.getHttp(), properties.getCli());
    }

    /**
     * 注册 CLI 可用性探测器。
     */
    @Bean
    @ConditionalOnMissingBean
    public HermesCliAvailabilityChecker hermesCliAvailabilityChecker() {
        return new HermesCliAvailabilityChecker();
    }

    /**
     * 启动时可选执行 CLI 探测。
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = HermesProperties.PREFIX + ".cli", name = "startup-check-enabled",
            havingValue = "true", matchIfMissing = true)
    public HermesCliStartupChecker hermesCliStartupChecker(HermesProperties properties,
                                                           HermesCliAvailabilityChecker checker,
                                                           Environment environment) {
        return new HermesCliStartupChecker(properties.getCli(), properties, checker, environment);
    }
}
