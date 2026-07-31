package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.HermesClient;
import io.github.easy4j.hermes.HermesClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hermes 自动配置。
 */
@Configuration
@ConditionalOnClass(HermesClient.class)
@ConditionalOnProperty(prefix = HermesProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HermesProperties.class)
public class HermesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HermesAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public HermesClientConfig hermesClientConfig(HermesProperties properties) {
        HermesClientConfig config = new HermesClientConfig();
        config.setServerUrl(properties.getServerUrl());
        config.setApiKey(properties.getApiKey());
        config.setConnectTimeoutMillis(properties.getConnectTimeoutMillis());
        config.setReadTimeoutMillis(properties.getReadTimeoutMillis());
        config.setVerifySsl(properties.isVerifySsl());
        config.setLocalExecutable(properties.getCliExecutable());
        config.setLocalTimeoutSeconds(properties.getCliTimeoutSeconds());
        config.setLocalProbeTimeoutSeconds(5);
        config.setDefaultModel(properties.getDefaultModel());
        config.setDefaultInstructions(properties.getDefaultInstructions());
        config.setDefaultProvider(properties.getDefaultProvider());
        log.info("Hermes client configured: serverUrl={}", config.getServerUrl());
        return config;
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
