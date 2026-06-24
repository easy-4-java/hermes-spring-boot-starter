package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesClient;
import io.github.hiwepy.hermes.HermesHttpClientConfig;
import io.github.hiwepy.hermes.cli.availability.HermesCliAvailabilityChecker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Starter 装配冒烟测试。
 */
@SpringBootTest(classes = HermesAutoConfiguration.class)
@TestPropertySource(
        properties = {
                "hermes.http.server-url=http://hermes.example:8642",
                "hermes.http.api-key=test-api-key",
                "hermes.http.default-model=hermes-agent",
                "hermes.cli.startup-check-enabled=false"
        })
class HermesAutoConfigurationTest {

    @Autowired
    private HermesClient hermesClient;

    @Autowired
    private HermesProperties hermesProperties;

    @Autowired
    private HermesCliAvailabilityChecker hermesCliAvailabilityChecker;

    /**
     * 校验 Bean 创建且 HTTP 配置映射正确。
     */
    @Test
    void beansCreated() {
        assertNotNull(hermesClient);
        assertNotNull(hermesProperties);
        assertNotNull(hermesCliAvailabilityChecker);

        HermesHttpClientConfig httpConfig = hermesProperties.getHttp();
        assertEquals("http://hermes.example:8642", httpConfig.getServerUrl());
        assertEquals("test-api-key", httpConfig.getApiKey());
        assertEquals("hermes-agent", httpConfig.getDefaultModel());
    }
}
