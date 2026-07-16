package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesClientConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = HermesProperties.PREFIX)
public class HermesProperties extends HermesClientConfig {

    public static final String PREFIX = "hermes";

    /** 启用/禁用 Hermes starter。 */
    private boolean enabled = true;

    /** 启动时是否探测 CLI 可用性。 */
    private boolean startupCheckEnabled = true;

    /** CLI 不可用时是否快速失败。 */
    private boolean failFastOnUnavailable = false;

}
