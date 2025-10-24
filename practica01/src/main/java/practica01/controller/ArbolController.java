package practica01.controller;

import practica01.domain.Arbol;
import practica01.services.ArbolService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/arbol")
public class ArbolController {

    @Autowired
    private ArbolService arbolService;
    
    @Autowired
    private MessageSource messageSource;
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var arboles = arbolService.getArboles();
        model.addAttribute("arboles", arboles);
        model.addAttribute("totalArboles", arboles.size());
        return "/arbol/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("arbol", new Arbol());
        return "/arbol/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Arbol arbol, @RequestParam("imagenFile") MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        arbolService.save(arbol, imagenFile);        
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/arbol/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idArbol, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            arbolService.delete(idArbol);          
        } catch (IllegalArgumentException e) {            
            titulo = "error";
            detalle = "arbol.error01";
        } catch (Exception e) {            
            titulo = "error";
            detalle = "arbol.error02";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/arbol/listado";
    }

    @GetMapping("/modificar/{idArbol}")    
    public String modificar(@PathVariable("idArbol") Integer idArbol, Model model, RedirectAttributes redirectAttributes) {
        Optional<Arbol> arbolOpt = arbolService.getArbol(idArbol);
        if (arbolOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("arbol.error01", null, Locale.getDefault()));
            return "redirect:/arbol/listado";
        }
        model.addAttribute("arbol", arbolOpt.get());
        return "arbol/modifica";
    }

}