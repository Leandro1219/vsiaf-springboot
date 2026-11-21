package com.sis414.sis_414_parcial.repository;

import com.sis414.sis_414_parcial.entity.Oficina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    // LEFT JOIN: las oficinas creadas sin unidad_id son validas. Para datos
    // anteriores a la correccion se permite recuperar ENTIDAD/UNIDAD por FK.
    @Query("""
        select count(o) > 0 from Oficina o
        left join o.unidad u left join u.entidad e
        where coalesce(o.entidadCodigo, e.codigo) = :entidad
          and upper(trim(o.nomOfic)) = upper(trim(:nombre))
        """)
    boolean existeNombre(@Param("entidad") String entidad, @Param("nombre") String nombre);

    @Query("""
        select count(o) > 0 from Oficina o
        left join o.unidad u left join u.entidad e
        where coalesce(o.entidadCodigo, e.codigo) = :entidad
          and upper(trim(o.nomOfic)) = upper(trim(:nombre)) and o.id <> :id
        """)
    boolean existeNombreExcepto(@Param("entidad") String entidad, @Param("nombre") String nombre,
                               @Param("id") Long id);

    @Query("""
        select coalesce(max(o.codOfic), 0) from Oficina o
        left join o.unidad u left join u.entidad e
        where coalesce(o.entidadCodigo, e.codigo) = :entidad
          and coalesce(o.unidadCodigo, u.unidad) = :unidad
        """)
    Integer mayorCodigo(@Param("entidad") String entidad, @Param("unidad") String unidad);

    /** Identificador funcional de OFICINA.DBF (CODOFIC) en la entidad/unidad actual. */
    @Query("""
        select o from Oficina o
        left join o.unidad u left join u.entidad e
        where coalesce(o.entidadCodigo, e.codigo) = :entidad
          and coalesce(o.unidadCodigo, u.unidad) = :unidad
          and o.codOfic = :codigo
        """)
    List<Oficina> buscarPorCodigo(@Param("entidad") String entidad,
                                  @Param("unidad") String unidad,
                                  @Param("codigo") Integer codigo);
}
