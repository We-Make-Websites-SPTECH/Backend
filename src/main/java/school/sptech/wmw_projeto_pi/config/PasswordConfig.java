package school.sptech.wmw_projeto_pi.config;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        String algoritmo = "pbkdf2@SpringSecurity_v5_8";
        return new DelegatingPasswordEncoder(algoritmo,
                Map.of(algoritmo, Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()));
    }
}