package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.HermesClient;
import io.github.easy4j.hermes.HermesCliConfig;
import io.github.easy4j.hermes.HermesHttpClientConfig;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HermesAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(HermesAutoConfiguration.class)
            .withPropertyValues(
                    "hermes.http.startup-check-enabled=false",
                    "hermes.cli.startup-check-enabled=false");

    @Test
    void shouldCreateHighConcurrencyDefaults() {
        contextRunner.run(context -> {
            OkHttpClient client = context.getBean(OkHttpClient.class);
            assertEquals(2_000, client.connectTimeoutMillis());
            assertEquals(10_000, client.writeTimeoutMillis());
            assertEquals(120_000, client.readTimeoutMillis());
            assertEquals(128, client.dispatcher().getMaxRequests());
            assertEquals(64, client.dispatcher().getMaxRequestsPerHost());
            assertEquals(2_000, context.getBean(HermesHttpClientConfig.class).getConnectTimeoutMillis());
            HermesHttpClientConfig http = context.getBean(HermesHttpClientConfig.class);
            assertEquals("http://127.0.0.1:8642", http.requireBaseUrl());
            assertThrows(IllegalArgumentException.class,
                    () -> http.getEndpointPolicy().require("http://127.0.0.1:8643"));
            assertThrows(IllegalArgumentException.class,
                    () -> http.getEndpointPolicy().require("https://1.1.1.1"));
            assertEquals(300, context.getBean(HermesCliConfig.class).getTimeout());
            assertSame(client, context.getBean(HermesClient.class).getOkHttpClient());
        });
    }

    @Test
    void shouldKeepStrictPublicPolicyForExplicitRemoteEndpoint() {
        contextRunner.withPropertyValues("hermes.http.base-url=https://1.1.1.1")
                .run(context -> {
                    HermesHttpClientConfig config = context.getBean(HermesHttpClientConfig.class);
                    assertEquals("https://1.1.1.1", config.requireBaseUrl());
                    assertThrows(IllegalArgumentException.class,
                            () -> config.getEndpointPolicy().require("http://127.0.0.1:8642"));
                });
    }

    @Test
    void shouldKeepExplicitPerHostConcurrency() {
        contextRunner.withPropertyValues("hermes.http.max-requests-per-host=73")
                .run(context -> assertEquals(73,
                        context.getBean(OkHttpClient.class).dispatcher().getMaxRequestsPerHost()));
    }

    @Test
    void shouldKeepExternallyProvidedClientInstance() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(73);
        OkHttpClient external = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(17, 7, TimeUnit.MINUTES))
                .build();

        contextRunner.withBean("hermesOkHttpClient", OkHttpClient.class, () -> external).run(context -> {
            assertSame(external, context.getBean("hermesOkHttpClient", OkHttpClient.class));
            assertSame(external, context.getBean(HermesClient.class).getOkHttpClient());
        });
        assertEquals(73, external.dispatcher().getMaxRequests());
        external.dispatcher().executorService().shutdown();
        external.connectionPool().evictAll();
    }
}
