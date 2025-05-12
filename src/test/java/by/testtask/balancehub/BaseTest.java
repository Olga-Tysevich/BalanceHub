package by.testtask.balancehub;

import by.testtask.balancehub.utils.Elasticsearch;
import by.testtask.balancehub.utils.PostgresSQL;
import com.redis.testcontainers.RedisContainer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.validation.constraints.NotBlank;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.DockerImageName;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class BaseTest {
    @Autowired
    private UserDetailsService userDetailsService;
    private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("""
            redis:latest"""))
            .withExposedPorts(6379);

    @BeforeAll
    static void initContainers() {
        PostgresSQL.container.start();
        Elasticsearch.container.start();
        redisContainer.start();
    }

    @BeforeEach
    public void testSomethingUsingLettuce() {
        // Retrieve the Redis URI from the container
        String redisURI = redisContainer.getRedisURI();
        RedisClient client = RedisClient.create(redisURI);
        try (StatefulRedisConnection<String, String> connection = client.connect()) {
            RedisCommands<String, String> commands = connection.sync();
            Assertions.assertEquals("PONG", commands.ping());
        }
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.datasource.url", PostgresSQL.container::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresSQL.container::getUsername);
        registry.add("spring.datasource.password", PostgresSQL.container::getPassword);
        registry.add("spring.data.elasticsearch.uris", Elasticsearch::getElasticsearchUrl);
        registry.add("spring.data.elasticsearch.username", () -> "elastic");
        registry.add("spring.data.elasticsearch.password", () -> "password");
        registry.add("spring.redis.host", () -> redisContainer.getHost());
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("REDIS_HOST", () -> redisContainer.getHost());
        registry.add("REDIS_PORT", () -> redisContainer.getMappedPort(6379));
    }

    protected void setAuthentication(@NotBlank String username, @NotBlank String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void test() {}

}
