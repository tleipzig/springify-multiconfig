package com.it_surmann.springify.multiconfig;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.util.List;


/**
 * Implementation of ApplicationContextInitializer for loading YAML files with the name
 * application-mc-*.yml into the application context. Files are loaded from all jars in the
 * classpath corresponding with their order in the depdency graph.
 */
@Slf4j
public class MultiConfigContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    @SneakyThrows
    public void initialize(final ConfigurableApplicationContext context) {
        log.info("Searching for application-mc-*.yml");

        final Resource[] resources = context.getResources("classpath*:/application-mc-*.yml");

        for (final Resource resource : resources) {
            log.debug("Found file: {}", resource.getFilename());

            final YamlPropertySourceLoader yamlSourceLoader = new YamlPropertySourceLoader();
            final List<PropertySource<?>> yamlSources = yamlSourceLoader.load(resource.getFilename(), resource);

            if (yamlSources == null || yamlSources.isEmpty()) {
                log.warn("No source found for file: {}", resource.getFilename());
                continue;
            }

            for (final PropertySource<?> yamlSource : yamlSources) {
                log.info("Adding file with lowest precedence: {}", resource.getFilename());
                context.getEnvironment().getPropertySources().addLast(yamlSource);
            }
        }
    }

}
