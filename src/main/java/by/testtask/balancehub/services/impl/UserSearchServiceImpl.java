package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.dto.common.UserDTO;
import by.testtask.balancehub.services.UserSearchService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<UserDTO> searchByAll(String name, String email, String phone,
                                     LocalDate dateOfBirth, int page, int size) throws IOException {
        List<Query> queries = new ArrayList<>();

        if (Objects.nonNull(name)) queries.addAll(addNameQuery(name));

        if (Objects.nonNull(phone)) queries.addAll(addPhoneQuery(phone));

        if (Objects.nonNull(dateOfBirth)) queries.addAll(addDateOfBirthQuery(dateOfBirth));

        return createRequest(queries, page, size);
    }

    @Override
    public List<UserDTO> searchByName(String name, int page, int size) throws IOException {
        List<Query> queries = new ArrayList<>(addNameQuery(name));

        return createRequest(queries, page, size);
    }

    @Override
    public List<UserDTO> searchByEmail(String email, int page, int size) throws IOException {
        List<Query> queries = new ArrayList<>(addEmailQuery(email));

        return createRequest(queries, page, size);
    }

    @Override
    public List<UserDTO> searchByDateOfBirthday(LocalDate dateOfBirth, int page, int size) throws IOException {
        List<Query> queries = new ArrayList<>(addDateOfBirthQuery(dateOfBirth));

        return createRequest(queries, page, size);
    }

    private List<UserDTO> createRequest(List<Query> queries, Integer page, Integer size) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("users")
                .query(q -> q.bool(b -> b.must(queries)))
                .from(page * size)
                .size(size)
        );

        SearchResponse<UserDTO> response = elasticsearchClient.search(searchRequest, UserDTO.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    private List<Query> addNameQuery(String name) {
        List<Query> queries = new ArrayList<>();

        queries.add(MatchQuery.of(m -> m
                .field("name")
                .query(name)
        )._toQuery());

        return queries;
    }

    private List<Query> addEmailQuery(String email) {

        List<Query> queries = new ArrayList<>();
        queries.add(TermQuery.of(t -> t
                .field("emails.email.keyword") // ".keyword" для точного совпадения
                .value(email)
        )._toQuery());

        return queries;
    }

    private List<Query> addPhoneQuery(String phone) {
        List<Query> queries = new ArrayList<>();
        queries.add(TermQuery.of(t -> t
                .field("phones.phoneNumber.keyword")
                .value(phone)
        )._toQuery());

        return queries;
    }

    private List<Query> addDateOfBirthQuery(LocalDate dateOfBirth) {
        List<Query> queries = new ArrayList<>();

        queries.add(Query.of(q -> q
                .range(r -> r
                        .date(d -> d
                                .field("dateOfBirthday")
                                .gt(dateOfBirth.toString())
                        )
                )
        ));

        return queries;
    }
}
