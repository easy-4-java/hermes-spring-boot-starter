package io.github.hiwepy.hermes.spring.boot;

import io.github.hiwepy.hermes.HermesClientConfig;
import io.github.hiwepy.hermes.cli.HermesCliExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 启动时探测 Hermes CLI 是否可用。
 */
public class HermesCliStartupChecker implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HermesCliStartupChecker.class);

    private final HermesClientConfig config;
    private final boolean failFast;

    public HermesCliStartupChecker(HermesClientConfig config, boolean failFast) {
        this.config = config;
        this.failFast = failFast;
    }

    @Override
    public void run(ApplicationArguments args) {
        HermesCliExecutor executor = new HermesCliExecutor(config);
        boolean available = executor.probe();

        if (available) {
            log.info("Hermes CLI is available: {}", config.getLocalExecutable());
        } else {
            String message = "Hermes CLI is NOT available: " + config.getLocalExecutable();
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
