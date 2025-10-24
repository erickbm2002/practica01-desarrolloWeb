package practica01.repository;

import practica01.domain.Arbol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArbolRepository extends JpaRepository<Arbol, Integer> {
    
    // Buscar árboles por nombre (parcial, sin importar mayúsculas/minúsculas)
    public List<Arbol> findByNombreArbolContainingIgnoreCase(String nombreArbol);
    
    // Buscar árboles por tipo de flor
    public List<Arbol> findByTipoFlorContainingIgnoreCase(String tipoFlor);

}