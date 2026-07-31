package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.HermesClientConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

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

}
