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

    /**
     * 创建 HermesClient Bean。
     * <p>客户端会在初始化时根据配置执行启动检查：</p>
     * <ul>
     *   <li>HTTP：调用 /health 端点检查服务可用性</li>
     *   <li>CLI：探测 hermes 可执行文件是否可用</li>
     * </ul>
     * <p>检查行为由以下配置控制：</p>
     * <ul>
     *   <li>hermes.http.enabled - 是否启用 HTTP 客户端</li>
     *   <li>hermes.http.startup-check-enabled - 是否在启动时检查 HTTP 服务</li>
     *   <li>hermes.http.fail-fast-on-unavailable - HTTP 检查失败时是否抛异常</li>
     *   <li>hermes.cli.enabled - 是否启用 CLI</li>
     *   <li>hermes.cli.startup-check-enabled - 是否在启动时检查 CLI 可用性</li>
     *   <li>hermes.cli.fail-fast-on-unavailable - CLI 检查失败时是否抛异常</li>
     * </ul>
     *
     * @param properties 配置属性
     * @return HermesClient 实例
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public HermesClient hermesClient(HermesProperties properties) {
        return new HermesClient(properties.getHttp(), properties.getCli());
    }

}
