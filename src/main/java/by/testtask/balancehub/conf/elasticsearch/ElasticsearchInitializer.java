package by.testtask.balancehub.conf.elasticsearch;

import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.repos.UserRepo;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Checking if 'users' index exists in Elasticsearch...");
        boolean exists = elasticsearchClient.indices().exists(e -> e.index("users")).value();
        if (!exists) {
            elasticsearchClient.indices().create(c -> c
                    .index("users")
                    .mappings(m -> m
                            .properties("id", p -> p.long_(l -> l))
                            .properties("name", p -> p.text(t -> t))
                            .properties("emails", p -> p.nested(n -> n
                                    .properties("id", p2 -> p2.long_(l -> l))
                                    .properties("email", p2 -> p2.keyword(k -> k))
                            ))
                            .properties("phones", p -> p.nested(n -> n
                                    .properties("id", p2 -> p2.long_(l -> l))
                                    .properties("phone", p2 -> p2.keyword(k -> k))
                            ))
                            .properties("dateOfBirth", p -> p.date(d -> d))
                            .properties("account", p -> p.nested(n -> n
                                    .properties("id", p2 -> p2.long_(l -> l))
                                    .properties("balance", p2 -> p2.keyword(k -> k))
                            ))
                    )
            );
            log.info("'users' index created successfully.");
        }
    }


    //TODO логи
    private void syncAllUsersToElasticsearch() {
        Set<UserIndexDTO> users = transactionTemplate.execute(status -> getAllUsers());

        if (Objects.isNull(users) || users.isEmpty()) {
            log.warn("No users found to sync with Elasticsearch.");
            return;
        }

        try {
            log.info("Preparing bulk operations to sync users to Elasticsearch...");
            List<BulkOperation> operations = users.stream()
                    .map(index -> {
                        log.info("Synchronizing user with ID: {}", index.getId());
                        return BulkOperation.of(op -> op
                                .index(IndexOperation.of(i -> i
                                        .index("users")
                                        .id(index.getId().toString())
                                        .document(index)
                                ))
                        );
                    })
                    .toList();

            BulkResponse response = elasticsearchClient.bulk(b -> b
                    .operations(operations)
            );

            if (response.errors()) {
                log.error("Errors occurred while syncing users to Elasticsearch.");
                response.items().forEach(item -> {
                    if (item.error() != null) {
                        log.error("Error for ID {}: {}", item.id(), item.error().reason());
                    }
                });
            } else {
                log.info("Successfully synced {} users to Elasticsearch.", users.size());
            }
        } catch (IOException e) {
            log.error("Error occurred while syncing users to Elasticsearch: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync users to Elasticsearch", e);
        }
    }

    private Set<UserIndexDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toUserIndex)
                .collect(Collectors.toSet());
    }
}
