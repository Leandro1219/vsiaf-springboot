package com.sis414.sis_414_parcial.repository;

import com.sis414.sis_414_parcial.entity.UnidadAdmin;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadAdminRepository extends JpaRepository<UnidadAdmin, Long> {
    /** Serializa altas/modificaciones de oficinas de una misma unidad. Requiere @Transactional. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UnidadAdmin u where u.id = :id")
    Optional<UnidadAdmin> bloquearPorId(@Param("id") Long id);
}
