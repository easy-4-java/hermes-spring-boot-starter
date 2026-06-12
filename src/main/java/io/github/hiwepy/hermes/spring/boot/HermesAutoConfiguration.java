package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesClient;
import io.github.hiwepy.hermes.HermesClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HermesClient.class)
@ConditionalOnProperty(prefix = HermesProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HermesProperties.class)
public class HermesAutoConfiguration {

    /**
     * 将 {@code hermes.cli-executable} 映射至 {@code localExecutable}，
     * 直接返回 properties 作为 {@link HermesClientConfig}（HermesProperties 继承自它）。
     */
    @Bean
    @ConditionalOnMissingBean
    public HermesClientConfig hermesClientConfig(HermesProperties properties) {
        if (properties.getCliExecutable() != null && !properties.getCliExecutable().isEmpty()) {
            properties.setLocalExecutable(properties.getCliExecutable());
        }
        return properties;
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public HermesClient hermesClient(HermesClientConfig config) {
        return new HermesClient(config);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = HermesProperties.PREFIX, name = "startup-check-enabled", havingValue = "true", matchIfMissing = true)
    public HermesCliStartupChecker hermesCliStartupChecker(HermesClientConfig config, HermesProperties properties) {
        return new HermesCliStartupChecker(config, properties.isFailFastOnUnavailable());
    }
}
