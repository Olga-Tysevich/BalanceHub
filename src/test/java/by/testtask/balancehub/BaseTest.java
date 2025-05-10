package by.testtask.balancehub;

import by.testtask.balancehub.utils.Elasticsearch;
import by.testtask.balancehub.utils.PostgresSQL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseTest {
    @BeforeAll
    static void initContainers() {
        PostgresSQL.container.start();
        Elasticsearch.container.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresSQL.container::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresSQL.container::getUsername);
        registry.add("spring.datasource.password", PostgresSQL.container::getPassword);
        registry.add("spring.data.elasticsearch.uris", Elasticsearch::getElasticsearchUrl);
        registry.add("spring.data.elasticsearch.username", () -> "elastic");
        registry.add("spring.data.elasticsearch.password", () -> "password");
    }

}
