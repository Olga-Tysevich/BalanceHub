package by.testtask.balancehub;

import by.testtask.balancehub.utils.Elasticsearch;
import by.testtask.balancehub.utils.PostgresSQL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PostgresSQL.Initializer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseTest {

    @BeforeAll
    static void initTestContainer() {
        PostgresSQL.container.start();
        Elasticsearch.container.start();
    }

}
