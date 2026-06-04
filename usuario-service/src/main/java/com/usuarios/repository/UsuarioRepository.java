package com.usuarios.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.usuarios.model.Usuarios;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {


    /*Esto es para buscar el usuario por el correo para el login */
    Optional<Usuarios> findByCorreo(String correo);
}
