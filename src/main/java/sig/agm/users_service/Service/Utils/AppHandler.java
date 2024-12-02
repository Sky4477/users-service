package sig.agm.users_service.Service.Utils;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AppHandler {

    private final WebClient client;

    @Autowired
    public AppHandler(WebClient.Builder domaineClientBuilder) {
        this.client = domaineClientBuilder.baseUrl("http://localhost:######").build();
    }

    
}
