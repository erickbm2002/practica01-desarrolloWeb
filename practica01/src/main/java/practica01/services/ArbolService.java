package practica01.services;
 
import practica01.domain.Arbol;
import practica01.repository.ArbolRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
 
@Service
public class ArbolService {
 
    @Autowired
    private ArbolRepository arbolRepository;
   
    // Directorio donde se guardarán las imágenes
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";
   
    @Transactional(readOnly=true)
    public List<Arbol> getArboles() {
        return arbolRepository.findAll();
    }
   
    @Transactional(readOnly = true)
    public Optional<Arbol> getArbol(Integer idArbol) {
        return arbolRepository.findById(idArbol);
    }
 
    @Transactional
    public void save(Arbol arbol, MultipartFile imagenFile) {
        // Primero guardamos el árbol para obtener el ID
        arbol = arbolRepository.save(arbol);
       
        // Si hay una imagen, la procesamos
        if (!imagenFile.isEmpty()) {
            try {
                String rutaImagen = guardarImagen(imagenFile, arbol.getIdArbol());
                arbol.setRutaImagen(rutaImagen);
                arbolRepository.save(arbol);
            } catch (IOException e) {
                // Manejo de error de imagen
                System.err.println("Error al guardar la imagen: " + e.getMessage());
            }
        }
    }
 
    @Transactional
    public void delete(Integer idArbol) {
        // Verifica si el árbol existe antes de intentar eliminarlo
        if (!arbolRepository.existsById(idArbol)) {
            throw new IllegalArgumentException("El árbol con ID " + idArbol + " no existe.");
        }
       
        // Obtener el árbol para eliminar su imagen
        Optional<Arbol> arbolOpt = arbolRepository.findById(idArbol);
        if (arbolOpt.isPresent() && arbolOpt.get().getRutaImagen() != null) {
            eliminarImagen(arbolOpt.get().getRutaImagen());
        }
       
        arbolRepository.deleteById(idArbol);
    }
   
    // Método para guardar la imagen en el servidor
    private String guardarImagen(MultipartFile file, Integer idArbol) throws IOException {
        // Crear directorio si no existe
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
       
        // Obtener extensión del archivo original
        String nombreOriginal = file.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
       
        // Crear nombre único para la imagen
        String nombreArchivo = "arbol_" + idArbol + "_" + UUID.randomUUID().toString() + extension;
       
        // Guardar archivo
        Path rutaDestino = Paths.get(UPLOAD_DIR + nombreArchivo);
        Files.copy(file.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
       
        // Retornar la ruta relativa para la BD
        return "images/" + nombreArchivo;
    }
   
    // Método para eliminar imagen física del servidor
    private void eliminarImagen(String rutaImagen) {
        try {
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                // Convertir ruta relativa a ruta absoluta
                String nombreArchivo = rutaImagen.replace("images/", "");
                Path rutaArchivo = Paths.get(UPLOAD_DIR + nombreArchivo);
                Files.deleteIfExists(rutaArchivo);
            }
        } catch (IOException e) {
            System.err.println("Error al eliminar la imagen: " + e.getMessage());
        }
    }
   
    // Métodos de búsqueda adicionales
    @Transactional(readOnly = true)
    public List<Arbol> buscarPorNombre(String nombre) {
        return arbolRepository.findByNombreArbolContainingIgnoreCase(nombre);
    }
   
    @Transactional(readOnly = true)
    public List<Arbol> buscarPorTipoFlor(String tipoFlor) {
        return arbolRepository.findByTipoFlorContainingIgnoreCase(tipoFlor);
    }
}
 