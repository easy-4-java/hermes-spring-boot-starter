package io.github.easy4j.hermes.spring.boot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.hermes.HermesClient;
import io.github.easy4j.hermes.HermesCliConfig;
import io.github.easy4j.hermes.HermesHttpClientConfig;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

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
    public HermesClient hermesClient(HermesHttpClientConfig httpConfig,
                                     HermesCliConfig cliConfig,
                                     ObjectMapper objectMapper,
                                     @Qualifier("hermesOkHttpClient") OkHttpClient okHttpClient) {
        return new HermesClient(httpConfig, cliConfig, objectMapper, okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public HermesHttpClientConfig hermesHttpClientConfig(HermesProperties properties) {
        return properties.getHttp();
    }

    @Bean
    @ConditionalOnMissingBean
    public HermesCliConfig hermesCliConfig(HermesProperties properties) {
        return properties.getCli();
    }

    /** 创建 Hermes 独立的高并发 OkHttpClient。 */
    @Bean("hermesOkHttpClient")
    @ConditionalOnMissingBean(name = "hermesOkHttpClient")
    public OkHttpClient hermesOkHttpClient(HermesHttpClientConfig http,
                                           ObjectProvider<OkHttpClient.Builder> builderProvider) {
        OkHttpClient.Builder baseBuilder = builderProvider.getIfAvailable(OkHttpClient.Builder::new);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(Math.max(1, http.getMaxRequests()));
        dispatcher.setMaxRequestsPerHost(Math.max(1, http.getMaxRequestsPerHost()));
        OkHttpClient.Builder providerBuilder = baseBuilder.build().newBuilder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(
                        Math.max(1, http.getMaxIdleConnections()),
                        Math.max(1L, http.getKeepAliveDurationMillis()),
                        TimeUnit.MILLISECONDS))
                .connectTimeout(Math.max(0, http.getConnectTimeoutMillis()), TimeUnit.MILLISECONDS)
                .readTimeout(Math.max(0, http.getReadTimeoutMillis()), TimeUnit.MILLISECONDS)
                .writeTimeout(Math.max(0, http.getWriteTimeoutMillis()), TimeUnit.MILLISECONDS)
                .callTimeout(Math.max(0, http.getCallTimeoutMillis()), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(http.isRetryOnConnectionFailure());
        if (!http.isVerifySsl()) {
            providerBuilder.hostnameVerifier((hostname, session) -> true);
        }
        return providerBuilder.build();
    }

    /**
     * 在应用没有 Jackson 配置时提供兼容性兜底。
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper hermesObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

}
