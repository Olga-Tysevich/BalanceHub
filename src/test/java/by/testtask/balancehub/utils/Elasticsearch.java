package by.testtask.balancehub.utils;

import lombok.experimental.UtilityClass;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@UtilityClass
public class Elasticsearch {

    public static final GenericContainer<?> container =
            new GenericContainer<>(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:latest"))
            .withExposedPorts(9200)
            .withEnv("discovery.type", "single-node");

    static {
        container.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.elasticsearch.rest.uris=" + getElasticsearchUrl()
            ).applyTo(applicationContext);
        }
    }

    // Метод для получения URL Elasticsearch
    public static String getElasticsearchUrl() {
        return "http://" + container.getHost() + ":" + container.getMappedPort(9200);
    }

    // Метод для остановки контейнера (если необходимо)
    public static void stop() {
        container.stop();
    }
}