package com.usuarios.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.usuarios.repository.RolRepository;
import com.usuarios.repository.UsuarioRepository;
import com.usuarios.model.Usuarios;
import com.usuarios.dto.UsuarioDTO;
import com.usuarios.model.Rol;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j 
@Service
@RequiredArgsConstructor

public class UsuarioService {


    /*Aca tamos haciendo la inyeccion del repositori pa ingresar a la base de datos */
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;


    public List<UsuarioDTO> getAll(){
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll() // Aca se traen todos los usuarios de la base de datos
                .stream() // Se convierte la lista en stream para poder transformarla
                .map(UsuarioDTO::fromModel) // Aca se convierte cada entidad usuario en un DTO
                .collect(Collectors.toList()); // despues de transformar todo, se vuelve a convertir en una lista de DTO para devolverla
    }


    
    public UsuarioDTO getById(Long id) {
        log.info("Buscando usuario id={}", id);
        Usuarios u = usuarioRepository.findById(id) // Aca lo que hacemos es buscar el usuario por la id
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado id={}", id);
                    return new RuntimeException("Usuario no encontrado");
                }); 
                // Si el usuario no existe, sale un error el cual seria el mensaje de Usuario no encontrado
 
        log.info("Usuario encontrado id={}", id);
        return UsuarioDTO.fromModel(u); // luego aqui todo se convierte a dto pa devolverlo en el get
    }


        // ESTE ES PARA EL POST OSEA CREAR EL USUARIO
    public UsuarioDTO save(UsuarioDTO dto) {

        log.info("Creando usuario correo={}", dto.getCorreo());

        Usuarios usuario = dto.toModel(); 
        // Convierte el dto a una entidad pa guardarlo

        /*Esto es para encriptar la contraseña, para que no aparezca en texto plano */
         String passwordEncriptada = passwordEncoder.encode(dto.getPassword());
        usuario.setPassword(passwordEncriptada);

        /*Aca buscamos el rol en la base de datos */
        Rol rol = rolRepository.findById(dto.getRolId()).orElseThrow(()-> {
            log.warn("Rol no encontrado id={}", dto.getRolId());
            return new RuntimeException("Rol no encontrado");
        });

        /*Aca le asignamos el rol al usuario */
        usuario.setRol(rol);

        Usuarios guardado = usuarioRepository.save(usuario); 
        // Guarda en la base de datos

        log.info("Usuario creado exitosamente id={}", guardado.getId());
        return UsuarioDTO.fromModel(guardado); 
        // Convierte lo guardado a DTO y lo devuelve
    }


    //AHORA EL PUT PA ACTUALIZAR EL USUARIO

    public UsuarioDTO update(Long id, UsuarioDTO dto){
        log.info("Actualizando usuario id={}", id);
        Usuarios u = usuarioRepository.findById(id)
                .orElseThrow(()-> {
                    log.warn("Usuario no encontrado id={}", id);
                    return new RuntimeException("Usuario no encontrado");
                }); /*Aca tamos buscando el usuario para ver si existe, si no, sale el mensaje */

        u.setNombre(dto.getNombre()); 
        u.setCorreo(dto.getCorreo());
        // Aca estamos actualizando los datos

        /*Esto es por si viene una nueva contraseña, poder encriptarla denuevo */
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String passwordEncriptada = passwordEncoder.encode(dto.getPassword());
            u.setPassword(passwordEncriptada);
            log.debug("Contraseña actualizada para usuario: {}", u.getCorreo());
        }


        // esto es pa actualizar solo el rol si viene el rol id
        if (dto.getRolId() != null) {
            Rol rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> {
                        log.warn("Rol no encontrado id={}", dto.getRolId());
                        return new RuntimeException("Rol no encontrado");
                    });

            u.setRol(rol);
        }

        Usuarios actualizado = usuarioRepository.save(u);

        log.info("Usuario actualizado exitosamente id={}", id);
        return UsuarioDTO.fromModel(actualizado);
        // Se guarda todo y devuelve los cambios actualizados 
    
    }


    // EL MAS FACIL DE TODOS, EL DELETE
    public void delete(Long id) {
        log.info("Eliminando usuario id={}", id);
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado id={}", id);
                    return new RuntimeException("Usuario no encontrado");
                });
        usuarioRepository.delete(usuario);
        log.info("Usuario eliminado exitosamente id={}", id);
    }

    /*METODO PARA EL LOGIN PIPIPI */

    public UsuarioDTO login(String correo, String password){
        log.info("Intentando login correo={}", correo);
        /*Aca lo que se hace es preguntar al repository si encontro el correo, si lo encuentra guarda el usuario en "usuario", si no lo encuentra mostrara un mensaje de que no lo encontro po */
        Usuarios usuario = usuarioRepository.findByCorreo(correo).orElseThrow(()-> {
            log.warn("Usuario no encontrado por correo={}", correo);
            return new RuntimeException("No se ha encontrado el usuario por el correo");
        });

        /*Aca compara la contraseña guardada con la contraseña ingresada y si no son iguales, mostrara que esta incorrecta */
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            log.warn("Intento de login fallido para: {}", correo);
            throw new RuntimeException("Contraseña incorrecta");
        }


        /*Esto es para, si hay un login exitoso, que haya un mensaje confirmando eso */
        log.info("Login exitoso para: {}", correo);
        /*Si el usuario realmente existe, hacemos return UsuarioDTO para que asi mostremos los datos que queremos mostrar en el get*/
        return UsuarioDTO.fromModel(usuario);

    }

}

