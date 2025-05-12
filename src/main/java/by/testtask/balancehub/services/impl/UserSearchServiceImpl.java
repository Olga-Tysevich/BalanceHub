package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.dto.req.UserSearchReq;
import by.testtask.balancehub.dto.resp.UserPageResp;
import by.testtask.balancehub.mappers.UserMapper;
import by.testtask.balancehub.services.UserSearchService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {
    private final ElasticsearchClient elasticsearchClient;
    private final UserMapper userMapper;

    @Override
    public UserPageResp searchByAll(UserSearchReq req) {
        List<Query> queries = new ArrayList<>();

        if (Objects.nonNull(req.getName())) queries.addAll(addNameQuery(req.getName()));

        if (Objects.nonNull(req.getEmail())) queries.addAll(addEmailQuery(req.getEmail()));

        if (Objects.nonNull(req.getPhone())) queries.addAll(addPhoneQuery(req.getPhone()));

        if (Objects.nonNull(req.getDateOfBirth())) queries.addAll(addDateOfBirthQuery(req.getDateOfBirth()));

        return createRequest(queries, req.getPage(), req.getSize());
    }

    @Override
    public UserPageResp searchByName(String name, int page, int size) {
        List<Query> queries = new ArrayList<>(addNameQuery(name));

        return createRequest(queries, page, size);
    }

    @Override
    public UserPageResp searchByEmail(String email, int page, int size) {
        List<Query> queries = new ArrayList<>(addEmailQuery(email));

        return createRequest(queries, page, size);
    }

    @Override
    public UserPageResp searchByPhone(String phone, int page, int size) {
        List<Query> queries = new ArrayList<>(addPhoneQuery(phone));

        return createRequest(queries, page, size);
    }

    @Override
    public UserPageResp searchByDateOfBirth(LocalDate dateOfBirth, int page, int size) {
        List<Query> queries = new ArrayList<>(addDateOfBirthQuery(dateOfBirth));

        return createRequest(queries, page, size);
    }

    private UserPageResp createRequest(List<Query> queries, int page, int size) {

        log.info("Creating request for users with page: {}, size: {}", page, size);

        log.debug("Queries being used for search: {}", queries);

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("users")
                .query(q -> q.bool(b -> b.must(queries)))
                .from(page * size)
                .size(size)
        );

        try {
            log.info("Sending search request to Elasticsearch...");

            SearchResponse<UserIndexDTO> response = elasticsearchClient.search(searchRequest, UserIndexDTO.class);

            Set<UserIndexDTO> users = response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toSet());

            log.debug("Found {} users in the search result", users.size());

            Set<UserDTO> t = users.stream().map(userMapper::toUserDTO).collect(Collectors.toSet());

            int totalHits = Objects.nonNull(response.hits().total()) ? (int) response.hits().total().value() : 0;
            int totalPages = (int) Math.ceil((double) totalHits / size);

            log.info("Total hits: {}, Total pages: {}", totalHits, totalPages);

            return UserPageResp.builder()
                    .users(t)
                    .page(page)
                    .totalPages(totalPages)
                    .totalUsers(totalHits)
                    .build();
        } catch (IOException e) {
            log.error("Error occurred while querying Elasticsearch: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private List<Query> addNameQuery(String name) {
        List<Query> queries = new ArrayList<>();

        queries.add(Query.of(q -> q
                .matchPhrasePrefix(m -> m
                        .field("name")
                        .query(name)
                )
        ));

        return queries;
    }

    private List<Query> addEmailQuery(String email) {
        List<Query> queries = new ArrayList<>();
        queries.add(Query.of(q -> q
                .nested(n -> n
                        .path("emails")
                        .query(q2 -> q2
                                .term(t -> t
                                        .field("emails.email")
                                        .value(email)
                                )
                        )
                )
        ));
        return queries;
    }

    private List<Query> addPhoneQuery(String phone) {
        List<Query> queries = new ArrayList<>();
        queries.add(Query.of(q -> q
                .nested(n -> n
                        .path("phones")
                        .query(q2 -> q2
                                .term(t -> t
                                        .field("phones.phone")
                                        .value(phone)
                                )
                        )
                )
        ));
        return queries;
    }

    private List<Query> addDateOfBirthQuery(LocalDate dateOfBirth) {
        List<Query> queries = new ArrayList<>();

        queries.add(Query.of(q -> q
                .range(r -> r
                        .date(d -> d
                                .field("dateOfBirth")
                                .gt(dateOfBirth.toString())
                        )
                )
        ));

        return queries;
    }
}
