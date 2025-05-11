package by.testtask.balancehub.events.listeners;

import by.testtask.balancehub.dto.elasticsearch.UserIndex;
import by.testtask.balancehub.events.Events;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserChangedEventListener {
    private final ElasticsearchClient elasticsearchClient;

    @EventListener
    public void onUserChanged(Events.UserChangedEvent event) {
        UserIndex index = event.index();

        try {
            elasticsearchClient.index(i -> i
                    .index("users")
                    .id(index.getId().toString())
                    .document(index));
        } catch (IOException e) {
            // TODO логи
            throw new RuntimeException("Failed to index index in Elasticsearch", e);
        }
    }
}
