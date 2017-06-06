package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.GruposManager;
import ar.gob.modernizacion.tad.model.Grupo;
import ar.gob.modernizacion.tad.model.Tramite;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by MMargonari on 06/06/2017.
 */

@Controller
@RequestMapping("/grupos")
public class GruposController {

    @RequestMapping(path = "/tramites", method = RequestMethod.GET)
    public String showGruposTramites(Model model) {
        model.addAttribute("tramites", Application.tramites);

        return "grupos_tramites";
    }

    @RequestMapping(path = "/tramites/grupo", method = RequestMethod.GET)
    public String showGruposTramitesRedirect(@RequestParam(value="selectable_tramites", required = true) int id, Model model) {

        return "redirect:/grupos/tramites/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/tramites/tramite/{id}", method = RequestMethod.GET)
    public String getGruposTramite(@PathVariable("id") int id, Model model) {
        Tramite tramite = Application.tramites.get(id);
        ArrayList<Integer> gruposId = null;
        try {
            gruposId = GruposManager.getGruposTramite(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String grupos_relacionados = ",";
        for (int grupo: gruposId) {
            Grupo grupo1 = Application.grupos.get(doc);
            documento.setRelacionado((byte)1);
            System.out.println(Application.documentos.get(doc).getRelacionado());
            documentos_relacionados += doc + ",";
        }

        model.addAttribute("tramite",tramite);
        model.addAttribute("documentos",Application.documentos);
        model.addAttribute("documentos_relacionados",documentos_relacionados);

        return "tramite_relaciones_configuracion";
    }


}
