package kg.asugtk.api.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Топики для подписки клиентов (диспетчеры и водители)
        config.enableSimpleBroker("/topic");
        // Префикс для сообщений, отправляемых клиентами на сервер
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Эндпоинт подключения WebSocket с поддержкой CORS
        registry.addEndpoint("/ws-asu")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        registry.addEndpoint("/ws-asu-raw")
                .setAllowedOriginPatterns("*");
    }
}
