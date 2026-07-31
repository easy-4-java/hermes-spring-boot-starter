package io.github.easy4j.hermes.spring.boot;

import io.github.easy4j.hermes.cli.availability.HermesCliAvailabilityChecker;
import io.github.easy4j.hermes.exception.HermesCliStartupException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link HermesCliStartupChecker} 行为测试。
 */
class HermesCliStartupCheckerTest {

    @Test
    void shouldFailFastWhenCliUnavailable() {
        HermesProperties properties = new HermesProperties();
        properties.getCli().setExecutable("/nonexistent/hermes-startup-test");
        properties.getCli().setFailFastOnUnavailable(true);
        HermesCliStartupChecker checker = new HermesCliStartupChecker(
                properties.getCli(), properties, new HermesCliAvailabilityChecker(), new MockEnvironment());

        assertThrows(HermesCliStartupException.class,
                () -> checker.run(new DefaultApplicationArguments(new String[0])));
    }

    @Test
    void shouldWarnOnlyWhenFailFastDisabled() {
        HermesProperties properties = new HermesProperties();
        properties.getCli().setExecutable("/nonexistent/hermes-startup-test");
        properties.getCli().setFailFastOnUnavailable(false);
        HermesCliStartupChecker checker = new HermesCliStartupChecker(
                properties.getCli(), properties, new HermesCliAvailabilityChecker(), new MockEnvironment());

        assertDoesNotThrow(() -> checker.run(new DefaultApplicationArguments(new String[0])));
    }
}
