package com.it_surmann.springify.multiconfig;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Locale;


/**
 * Implementation of ApplicationContextInitializer for loading YAML files with the name
 * application-mc-*.yml into the application context. Files are loaded from all jars in the
 * classpath corresponding with their order in the dependency graph.
 */
@Slf4j
public class MultiConfigContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    @SneakyThrows
    public void initialize(final ConfigurableApplicationContext context) {
        log.info("Searching for application-mc-*.yml/yaml/properties");

        final Resource[] resources = context.getResources("classpath*:/application-mc-*.{yml,yaml,properties}");

        for (final Resource resource : resources) {
            log.debug("Found file: {}", resource.getFilename());

            final PropertySourceLoader loader = resource.getFilename().toLowerCase(Locale.ROOT).endsWith("properties") ?
                    new PropertiesPropertySourceLoader() : new YamlPropertySourceLoader();
            final List<PropertySource<?>> sources = loader.load(resource.getFilename(), resource);

            if (sources == null || sources.isEmpty()) {
                log.warn("No source found for file: {}", resource.getFilename());
                continue;
            }

            for (final PropertySource<?> source : sources) {
                log.info("Adding file with lowest precedence: {}", resource.getFilename());
                context.getEnvironment().getPropertySources().addLast(source);
            }
        }
    }

}
