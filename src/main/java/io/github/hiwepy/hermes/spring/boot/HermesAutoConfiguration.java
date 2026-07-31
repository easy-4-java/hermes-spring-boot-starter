package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.HermesClient;
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

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public HermesClient hermesClient(HermesProperties properties) {
        return new HermesClient(properties.getHttp(), properties.getCli());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = HermesProperties.PREFIX, name = "startup-check-enabled", havingValue = "true", matchIfMissing = true)
    public HermesCliStartupChecker hermesCliStartupChecker(HermesProperties properties) {
        return new HermesCliStartupChecker(properties.getCli(), properties.isFailFastOnUnavailable());
    }
}
