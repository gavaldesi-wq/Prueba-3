package com.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usuarios.model.Rol;
public interface RolRepository extends JpaRepository<Rol,Long>{
     
  
}
