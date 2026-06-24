package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesCliConfig;
import io.github.hiwepy.hermes.cli.availability.HermesCliAvailabilityChecker;
import io.github.hiwepy.hermes.cli.availability.HermesCliAvailabilityReport;
import io.github.hiwepy.hermes.exception.HermesCliStartupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;

/**
 * 应用启动时探测本机 {@code hermes} CLI 是否可用。
 */
@Slf4j
@RequiredArgsConstructor
public class HermesCliStartupChecker implements ApplicationRunner {

    private final HermesCliConfig cliConfig;
    private final HermesProperties hermesProperties;
    private final HermesCliAvailabilityChecker availabilityChecker;
    private final Environment environment;

    /**
     * 启动阶段执行 {@code hermes --version} 探测。
     */
    @Override
    public void run(ApplicationArguments args) {
        HermesCliAvailabilityReport report = availabilityChecker.check(cliConfig);
        String configSnapshot = buildEffectiveConfigSnapshot();

        if (report.isAvailable()) {
            log.info(
                    "Hermes CLI ready: {} effectiveConfig={}",
                    report.toDiagnosticMessage(),
                    configSnapshot);
            return;
        }

        String message = report.toDiagnosticMessage()
                + "。请确认 hermes.cli.executable 指向可执行的 hermes（如 /usr/local/bin/hermes）。"
                + " effectiveConfig={" + configSnapshot + "}";
        if (hermesProperties.getCli().isFailFastOnUnavailable()) {
            throw new HermesCliStartupException(message, report);
        }
        log.warn("Hermes CLI startup check failed (fail-fast disabled): {}", message);
    }

    private String buildEffectiveConfigSnapshot() {
        String profiles = environment.getProperty("spring.profiles.active", "(unset)");
        HermesProperties.HermesCli cli = hermesProperties.getCli();
        return "profiles=" + profiles
                + ", hermes.enabled=" + hermesProperties.isEnabled()
                + ", hermes.cli.executable=" + cli.getExecutable()
                + ", hermes.cli.startup-check-enabled=" + cli.isStartupCheckEnabled()
                + ", hermes.cli.fail-fast-on-unavailable=" + cli.isFailFastOnUnavailable()
                + ", hermesStarterOnClasspath=" + isHermesStarterOnClasspath();
    }

    private static boolean isHermesStarterOnClasspath() {
        try {
            Class.forName("io.github.hiwepy.hermes.spring.boot.HermesAutoConfiguration");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
