package com.sis414.sis_414_parcial.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "oficina")
public class Oficina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ENTIDAD C(4) y UNIDAD C(5) en OFICINA.DBF.
     * No son IDs ingresados por el operador: se resuelven del contexto institucional.
     */
    @Column(name = "entidad", length = 4)
    private String entidadCodigo;

    @Column(name = "unidad", length = 5)
    private String unidadCodigo;

    private Integer codOfic;

    @Column(length = 65)
    private String nomOfic;

    @Column(columnDefinition = "text")
    private String observ;

    private LocalDate feult;

    @Column(length = 8)
    private String usuar;

    private Integer apiEstado;

    // Referencia tecnica OPCIONAL: UNIDAD C(5) del DBF puede estar vacia.
    // No se inventa una fila en UNIDADADMIN solo para satisfacer la FK.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = true)
    private UnidadAdmin unidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntidadCodigo() { return entidadCodigo; }
    public void setEntidadCodigo(String entidadCodigo) { this.entidadCodigo = entidadCodigo; }

    public String getUnidadCodigo() { return unidadCodigo; }
    public void setUnidadCodigo(String unidadCodigo) { this.unidadCodigo = unidadCodigo; }

    public Integer getCodOfic() { return codOfic; }
    public void setCodOfic(Integer codOfic) { this.codOfic = codOfic; }

    public String getNomOfic() { return nomOfic; }
    public void setNomOfic(String nomOfic) { this.nomOfic = nomOfic; }

    public String getObserv() { return observ; }
    public void setObserv(String observ) { this.observ = observ; }

    public LocalDate getFeult() { return feult; }
    public void setFeult(LocalDate feult) { this.feult = feult; }

    public String getUsuar() { return usuar; }
    public void setUsuar(String usuar) { this.usuar = usuar; }

    public Integer getApiEstado() { return apiEstado; }
    public void setApiEstado(Integer apiEstado) { this.apiEstado = apiEstado; }

    public UnidadAdmin getUnidad() { return unidad; }
    public void setUnidad(UnidadAdmin unidad) { this.unidad = unidad; }
}
