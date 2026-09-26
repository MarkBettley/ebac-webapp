package com.ebac.modulo45.configuration;

import com.ebac.modulo45.entity.UsuarioLogin;
import com.ebac.modulo45.repository.UsuarioLoginRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginDataInitializer {

    @Bean
    CommandLineRunner inicializarUsuarioLogin(
            UsuarioLoginRepository repository) {

        return args -> {
            if (repository.findByUsername("marco").isEmpty()) {
                UsuarioLogin usuario = new UsuarioLogin();
                usuario.setUsername("marco");
                usuario.setPassword("123");
                repository.save(usuario);
            }
        };
    }
}
