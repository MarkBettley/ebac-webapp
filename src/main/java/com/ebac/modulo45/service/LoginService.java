package com.ebac.modulo45.service;

import com.ebac.modulo45.entity.UsuarioLogin;
import com.ebac.modulo45.repository.UsuarioLoginRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UsuarioLoginRepository usuarioLoginRepository;

    public LoginService(UsuarioLoginRepository usuarioLoginRepository) {
        this.usuarioLoginRepository = usuarioLoginRepository;
    }

    public boolean validarCredenciales(String username, String password) {
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return false;
        }

        Optional<UsuarioLogin> usuario =
                usuarioLoginRepository.findByUsername(username);

        return usuario.isPresent()
                && usuario.get().getPassword().equals(password);
    }
}
