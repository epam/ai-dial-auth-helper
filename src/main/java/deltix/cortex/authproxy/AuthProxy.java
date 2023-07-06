package deltix.cortex.authproxy;

import deltix.cortex.authproxy.utils.LoggingConfigurator;
import deltix.gflog.LogFactory;
import deltix.gflog.jul.JulBridge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.validation.constraints.NotNull;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@ComponentScan({"deltix.cortex.authproxy"})
public class AuthProxy {
    static {
        try {
            System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LoggingSystem.NONE);
            JulBridge.install();
            LoggingConfigurator.configure(AuthProxy.class, "logs/auth-proxy.log");
            LogFactory.getLog(AuthProxy.class).info().append("GFLog initialized").commit();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthProxy.class, args);
    }

    @Value("${scheduled.poolSize:2}")
    @NotNull
    private Integer poolSize;

    @SuppressWarnings("unused")
    @Bean
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler ret = new ThreadPoolTaskScheduler();
        ret.setPoolSize(poolSize);
        return ret;
    }
}