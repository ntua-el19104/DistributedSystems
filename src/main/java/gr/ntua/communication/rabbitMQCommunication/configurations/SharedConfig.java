package gr.ntua.communication.rabbitMQCommunication.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@Setter
@Getter
@NoArgsConstructor
public class SharedConfig {
    private boolean isBootstrap;
    private PublicKey publicKey;
}
