package com.ebac.modulo45.repository;

import com.ebac.modulo45.entity.UsuarioLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioLoginRepository
        extends JpaRepository<UsuarioLogin, Long> {

    Optional<UsuarioLogin> findByUsername(String username);
}
