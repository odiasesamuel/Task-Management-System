package com.prunny.notification;

import com.prunny.notification.config.ApplicationProperties;
import com.prunny.notification.config.CRLFLogConverter;
import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import tech.jhipster.config.DefaultProfileUtil;
import tech.jhipster.config.JHipsterConstants;

@SpringBootApplication
@EnableConfigurationProperties({ ApplicationProperties.class })
public class NotificationserviceApp {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationserviceApp.class);

    private final Environment env;

    public NotificationserviceApp(Environment env) {
        this.env = env;
    }


    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            LOG.error(
                "You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
            );
        }
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)
        ) {
            LOG.error(
                "You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
            );
        }
    }


    public static void main(String[] args) {
        try {
            // Workaround Hazelcast issue: https://github.com/hazelcast/hazelcast/issues/26361#issuecomment-2489778475
            Class.forName(
                "org.springframework.boot.devtools.autoconfigure.DevToolsProperties",
                false,
                SpringApplication.class.getClassLoader()
            );
            System.setProperty("spring.devtools.restart.enabled", "false");
            LOG.warn(
                "Spring Boot Developer Tools restart has been disabled using System property in order to prevent issues with Hazelcast"
            );
        } catch (Exception e) {
            // Devtools not found, ignore
        }

        SpringApplication app = new SpringApplication(NotificationserviceApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String applicationName = env.getProperty("spring.application.name");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path"))
            .filter(StringUtils::isNotBlank)
            .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOG.info(
            CRLFLogConverter.CRLF_SAFE_MARKER,
            """

            ----------------------------------------------------------
            \tApplication '{}' is running! Access URLs:
            \tLocal: \t\t{}://localhost:{}{}
            \tExternal: \t{}://{}:{}{}
            \tProfile(s): \t{}
            ----------------------------------------------------------""",
            applicationName,
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );

        String configServerStatus = env.getProperty("configserver.status");
        if (configServerStatus == null) {
            configServerStatus = "Not found or not setup for this application";
        }
        LOG.info(
            CRLFLogConverter.CRLF_SAFE_MARKER,
            "\n----------------------------------------------------------\n\t" +
            "Config Server: \t{}\n----------------------------------------------------------",
            configServerStatus
        );
    }
}
