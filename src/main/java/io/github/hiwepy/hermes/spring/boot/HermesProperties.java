package io.github.easy4j.hermes.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Hermes Spring Boot 配置属性。
 */
@Data
@ConfigurationProperties(prefix = HermesProperties.PREFIX)
public class HermesProperties {

    public static final String PREFIX = "hermes";

    /**
     * 启用/禁用 Hermes starter。
     */
    private boolean enabled = true;

    /**
     * Hermes Server 根地址。
     */
    private String serverUrl = "http://localhost:8642";

    /**
     * API 密钥。
     */
    private String apiKey;

    /**
     * 连接超时（毫秒）。
     */
    private int connectTimeoutMillis = 15000;

    /**
     * 读取超时（毫秒）。
     */
    private int readTimeoutMillis = 300000;

    /**
     * 是否校验 HTTPS 证书。
     */
    private boolean verifySsl = true;

    /**
     * 本地 CLI 可执行文件名或绝对路径。
     */
    private String cliExecutable = "hermes";

    /**
     * 本地 CLI 命令超时（秒）。
     */
    private int cliTimeoutSeconds = 300;

    /**
     * 启动时是否探测 CLI 可用性。
     */
    private boolean startupCheckEnabled = true;

    /**
     * CLI 不可用时是否快速失败（启动失败）。
     */
    private boolean failFastOnUnavailable = false;

    /**
     * 默认模型。
     */
    private String defaultModel = "hermes-agent";

    /**
     * 默认指令。
     */
    private String defaultInstructions;

    /**
     * 默认提供商。
     */
    private String defaultProvider;
}
