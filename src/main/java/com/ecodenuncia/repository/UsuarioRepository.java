/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecodenuncia.repository;
import com.ecodenuncia.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gabri
 */

//Avisa ao Spring que é um repositorio
@Repository
//cria a interface pegando a classe modelo (usuario) e o id dela (tipo long)
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
    // OSpring Data JPA entende o nome "findByEmail" e automaticamente cria
    // uma consulta "SELECT * FROM tb_usuarios WHERE email = ?"
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByCpfCnpj(String cpfCnpj);
    
}
