package com.usuarios.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usuarios.dto.LoginRequest;
import com.usuarios.dto.UsuarioDTO;
import com.usuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor



public class UsuarioController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    /*pa mostrar los usuarios creados */
    @GetMapping
    public List<UsuarioDTO> getAll(){
        logger.info("GET /api/usuarios");
        List<UsuarioDTO> usuarios = usuarioService.getAll();
        logger.debug("Cantidad de usuarios obtenidos: {}", usuarios.size());
        return usuarios;
    }


    /*Pa buscar por id */
    @GetMapping("/{id}")    
    public ResponseEntity<?> getById(@PathVariable Long id) {
        logger.info("GET /api/usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.getById(id));
    }



    /*Pa guardar los usuarios */
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UsuarioDTO dto,BindingResult bindingResult) {
        
        logger.info("POST /api/usuarios - correo={}", dto.getCorreo());
        /*Esto es para ver cuando hay errores, el valid de arriba, revisa cada componente del DTO, compara los @email,
        los @notblank etc etc, guarda todos erroes en el objeto bindingResult  */
        if (bindingResult.hasErrors()) { /*Aca ve si hay almenos un error en el bindigResult */
        List<String> errors = bindingResult.getAllErrors() /*aca se crea una lista llamada errores que va a guardar los errores que se encontro  */
            .stream()
            .map(error -> error.getDefaultMessage()) /*Aca por cada error que haya, solo se saca el mensaje del error, mensajes creados en el dto */
            .collect(Collectors.toList());
        logger.warn("Errores de validación: {}", errors);
        return ResponseEntity.badRequest().body(errors);
        /*luego en el return se devuelve la lista con los mensajes de los errores encontrados, si hay error en el nombre
        o en el correo, se devolvera los 2 mensajes de errores */
    }

        UsuarioDTO guardado = usuarioService.save(dto);
        logger.info("Usuario creado exitosamente id={}", guardado.getId());
        return ResponseEntity.ok(guardado);
    }
    
    /*Pa actualizar los usuarios */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
         logger.info("PUT /api/usuarios/{} - correo={}", id, dto.getCorreo());
        UsuarioDTO actualizado = usuarioService.update(id, dto);
        logger.info("Usuario actualizado exitosamente id={}", id);
        return ResponseEntity.ok(actualizado);
    }
    
    /* Pa eliminar*/
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        logger.info("DELETE /api/usuarios/{}", id);
        usuarioService.delete(id);
        logger.info("Usuario eliminado exitosamente id={}", id);
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente"));
    }

    /*El loginn */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("POST /api/usuarios/login - correo={}", request.getCorreo());
        Object respuesta = usuarioService.login(
            request.getCorreo(),
            request.getPassword());

        logger.info("Login exitoso correo={}", request.getCorreo());
        return ResponseEntity.ok(respuesta);
    }

}


