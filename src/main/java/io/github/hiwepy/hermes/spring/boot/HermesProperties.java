package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesCliConfig;
import io.github.hiwepy.hermes.HermesHttpClientConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Hermes Spring Boot 配置属性。
 */
@ConfigurationProperties(prefix = HermesProperties.PREFIX)
@Data
public class HermesProperties {

    public static final String PREFIX = "hermes";

    /** 是否启用本 Starter 提供的 Bean */
    private boolean enabled = true;

    /** HTTP/API Server 相关配置 */
    @NestedConfigurationProperty
    private final HermesHttpClientConfig http = new HermesHttpClientConfig();

    /** 本地 CLI 相关配置 */
    @NestedConfigurationProperty
    private final HermesCli cli = new HermesCli();

    /**
     * Starter 扩展的 CLI 配置，包含启动探测开关。
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class HermesCli extends HermesCliConfig {

        /** 是否在应用启动时执行本机 {@code hermes --version} 探测 */
        private boolean startupCheckEnabled = true;

        /** 启动探测失败时是否中断应用启动 */
        private boolean failFastOnUnavailable = false;
    }
}
