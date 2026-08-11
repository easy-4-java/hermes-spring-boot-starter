package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.HermesClientConfig;
import io.github.easy4j.hermes.HermesCliConfig;
import io.github.easy4j.hermes.HermesHttpClientConfig;
import io.github.easy4j.hermes.HermesDebugConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = HermesProperties.PREFIX)
public class HermesProperties extends HermesClientConfig {

    public static final String PREFIX = "hermes";

    /**
     * 启用/禁用 Hermes starter。
     * <p>为 false 时不创建 HermesClient Bean。</p>
     */
    private boolean enabled = true;

    /** SDK 所有通道共享的分级调试配置。 */
    @NestedConfigurationProperty
    private final HermesDebugConfig debug = new HermesDebugConfig();

    /** 启动时是否探测 CLI 可用性。 */
    private boolean startupCheckEnabled = true;

    /** CLI 不可用时是否快速失败。 */
    private boolean failFastOnUnavailable = false;

    /** Hermes HTTP API 配置。 */
    @NestedConfigurationProperty
    private final HermesHttpClientConfig http = new HermesHttpClientConfig(debug);

    /** Hermes 本地 CLI 配置。 */
    @NestedConfigurationProperty
    private final HermesCliConfig cli = new HermesCliConfig(debug);
}
