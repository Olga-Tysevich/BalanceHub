package by.testtask.balancehub.events.listeners;

import by.testtask.balancehub.dto.common.UserDTO;
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
        UserDTO dto = event.dto();

        try {
            elasticsearchClient.index(i -> i
                    .index("users")
                    .id(dto.getId().toString())
                    .document(dto));
        } catch (IOException e) {
            // TODO логи
            throw new RuntimeException("Failed to index dto in Elasticsearch", e);
        }
    }
}
