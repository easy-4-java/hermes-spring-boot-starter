package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.HermesClient;
import io.github.easy4j.hermes.HermesCliConfig;
import io.github.easy4j.hermes.HermesHttpClientConfig;
import io.github.easy4j.hermes.HttpResponseMode;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HermesAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(HermesAutoConfiguration.class)
            .withPropertyValues(
                    "hermes.http.startup-check-enabled=false",
                    "hermes.http.base-url=http://hermes.example:8642",
                    "hermes.http.mode=auto",
                    "hermes.http.stream-core-pool-size=7",
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
            HermesHttpClientConfig http = context.getBean(HermesHttpClientConfig.class);
            assertEquals(2_000, http.getConnectTimeoutMillis());
            assertEquals("http://hermes.example:8642", http.getBaseUrl());
            assertEquals(HttpResponseMode.AUTO, http.getMode());
            assertEquals(7, http.getStreamCorePoolSize());
            assertEquals(300, context.getBean(HermesCliConfig.class).getTimeout());
            assertSame(client, context.getBean(HermesClient.class).getOkHttpClient());
        });
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

    @Test
    void shouldExposeStarterProperties() {
        HermesProperties properties = new HermesProperties();
        assertTrue(properties.isEnabled());

        properties.setEnabled(false);

        assertFalse(properties.isEnabled());
        assertTrue(properties.equals(properties));
        assertTrue(properties.hashCode() != 0);
        assertTrue(properties.toString().contains("enabled=false"));
    }
}
