package io.github.easy4j.hermes.spring.boot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.hermes.HermesClient;
import io.github.easy4j.hermes.HermesOkHttpClientFactory;
import okhttp3.OkHttpClient;
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
     * 创建 HermesClient Bean，并复用容器中的 ObjectMapper 与 OkHttpClient。
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public HermesClient hermesClient(HermesProperties properties,
                                     ObjectMapper objectMapper,
                                     OkHttpClient okHttpClient) {
        return new HermesClient(properties.getHttp(), properties.getCli(), objectMapper, okHttpClient);
    }

    /** 创建容器共享的高并发 OkHttpClient；若应用已提供客户端则保持原实例。 */
    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient hermesOkHttpClient(HermesProperties properties) {
        return HermesOkHttpClientFactory.create(properties.getHttp());
    }

    /** 在应用没有 Jackson 配置时提供兼容性兜底。 */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper hermesObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

}
