package by.testtask.balancehub.utils;

import lombok.experimental.UtilityClass;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@UtilityClass
public class Elasticsearch {

    public static final GenericContainer<?> container =
            new GenericContainer<>(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.6.0"))
                    .withExposedPorts(9200)
                    .withEnv("discovery.type", "single-node")
                    .withEnv("ELASTIC_USERNAME", "elastic")
                    .withEnv("ELASTIC_PASSWORD", "password")
                    .withEnv("ES_JAVA_OPTS", "-Xms2g -Xmx2g")
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("xpack.security.http.ssl.enabled", "false")
                    .waitingFor(Wait.forHttp("/").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(5)));


    static {
        container.start();
    }

    public static String getElasticsearchUrl() {
        return "http://" + container.getHost() + ":" + container.getMappedPort(9200);
    }
}