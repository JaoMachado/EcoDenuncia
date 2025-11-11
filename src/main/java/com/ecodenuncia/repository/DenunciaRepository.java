/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.ecodenuncia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecodenuncia.model.Denuncia;
import java.util.List;
import org.springframework.stereotype.Repository;
/**
 *
 * @author gabri
 */

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long>{
}
