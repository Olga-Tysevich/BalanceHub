package by.testtask.balancehub.events.listeners;

import by.testtask.balancehub.dto.elasticsearch.UserIndexDTO;
import by.testtask.balancehub.events.Events;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserChangedEventListener {
    private final ElasticsearchClient elasticsearchClient;

    @EventListener
    public void onUserChanged(Events.UserChangedEvent event) {
        UserIndexDTO index = event.index();
        log.info("Received UserChangedEvent for user [id={}]", index.getId());

        try {
            elasticsearchClient.index(i -> i
                    .index("users")
                    .id(index.getId().toString())
                    .document(index));
            log.info("User index successfully created/updated in Elasticsearch [id={}]", index.getId());
        } catch (IOException e) {
            log.error("Failed to index user [id={}] in Elasticsearch: {}", index.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to index index in Elasticsearch", e);
        }
    }

}
