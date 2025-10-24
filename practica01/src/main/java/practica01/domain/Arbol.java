package practica01.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name="arbol")
public class Arbol implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_arbol")
    private Integer idArbol;
    
    @Column(name="nombre_arbol", nullable=false, length=100)
    @NotNull
    @Size(max=100)
    private String nombreArbol;
    
    @Column(name="tipo_flor", length=100)
    @Size(max=100)
    private String tipoFlor;
    
    @Column(name="dureza_madera", length=100)
    @Size(max=100)
    private String durezaMadera;
    
    @Column(name="altura_promedio", precision=5, scale=2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal alturaPromedio;
    
    @Column(name="ruta_imagen", length=255)
    @Size(max=255)
    private String rutaImagen;

}