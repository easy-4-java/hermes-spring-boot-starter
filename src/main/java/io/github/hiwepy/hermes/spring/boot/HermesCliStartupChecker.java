package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesCliConfig;
import io.github.hiwepy.hermes.cli.HermesCliExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 启动时探测 Hermes CLI 是否可用。
 */
@Slf4j
public class HermesCliStartupChecker implements ApplicationRunner {

    private final HermesCliConfig config;
    private final boolean failFast;

    public HermesCliStartupChecker(HermesCliConfig config, boolean failFast) {
        this.config = config;
        this.failFast = failFast;
    }

    @Override
    public void run(ApplicationArguments args) {
        HermesCliExecutor executor = new HermesCliExecutor(config);
        boolean available = executor.probe();

        if (available) {
            log.info("Hermes CLI is available: {}", config.getExecutable());
        } else {
            String message = "Hermes CLI is NOT available: " + config.getExecutable();
            if (failFast) {
                throw new HermesCliUnavailableException(message);
            } else {
                log.warn(message + " (continuing without CLI support)");
            }
        }
    }

    public static class HermesCliUnavailableException extends RuntimeException {
        public HermesCliUnavailableException(String message) {
            super(message);
        }
    }
}
