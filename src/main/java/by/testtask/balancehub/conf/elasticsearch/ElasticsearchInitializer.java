package by.testtask.balancehub.conf.elasticsearch;
import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.repos.UserRepo;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElasticsearchInitializer {
    private final ElasticsearchClient elasticsearchClient;
    private final UserMapper userMapper;
    private final UserRepo userRepo;
    private final TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() throws IOException {
        createIndexIfNotExists();
        syncAllUsersToElasticsearch();
    }

    private void createIndexIfNotExists() throws IOException {
        boolean exists = elasticsearchClient.indices().exists(e -> e.index("users")).value();
        if (!exists) {
            elasticsearchClient.indices().create(c -> c
                    .index("users")
                    .mappings(m -> m
                            .properties("name", p -> p.text(t -> t))
                            .properties("emails", p -> p.keyword(k -> k))
                            .properties("phones", p -> p.keyword(k -> k))
                            .properties("dateOfBirthday", p -> p.date(d -> d))
                    )
            );
        }
    }

    private void syncAllUsersToElasticsearch() {
        Set<UserDTO> users = transactionTemplate.execute(status -> getAllUsers());


        try {
            List<BulkOperation> operations = users.stream()
                    .map(user -> BulkOperation.of(op -> op
                            .index(IndexOperation.of(i -> i
                                    .index("users")
                                    .id(user.getId().toString())
                                    .document(user)
                            ))
                    ))
                    .toList();

            elasticsearchClient.bulk(b -> b
                    .index("users")
                    .operations(operations)
            );

            //TODO логи
            System.out.println("Синхронизировано пользователей в Elasticsearch: " + users.size());
        } catch (IOException e) {
            System.err.println("Ошибка синхронизации пользователей в Elasticsearch: " + e.getMessage());
            throw new RuntimeException("Failed to sync users to Elasticsearch", e);
        }
    }

    private Set<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());
    }
}
