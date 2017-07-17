package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.managers.*;
import ar.gob.modernizacion.tad.model.Documento;
import ar.gob.modernizacion.tad.model.Tag;
import ar.gob.modernizacion.tad.model.Tramite;
import ar.gob.modernizacion.tad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.websocket.server.PathParam;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by martinm on 19/05/17.
 */

@Controller
@RequestMapping("/tramites")
public class TramitesController {

    private static HashMap<String, List<Tag>> etiquetas;

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model,
                             @RequestParam(value="username") String username,
                             @RequestParam(value = "password") String password) {
        User user = null;
        try {
            user = new User(username,Encrypter.decrypt(password));
            TramiteManager.loadTramites(user);
            EtiquetaManager.loadEtiquetas(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Tag> allTags = new ArrayList<>();
        Iterator it = Application.etiquetas.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            allTags.addAll((ArrayList<Tag>)pair.getValue());
        }

        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute("tags", allTags);
        model.addAttribute("tratas_existentes", Application.tratasExistentes);
        model.addAttribute(user);

        return "tramite/tramite_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@ModelAttribute User user,
        @RequestParam (value="descripcion", required=true) String descripcion,
        @RequestParam (value="usuario_creacion", required = true) String usuario_creacion,
        @RequestParam (value="trata", required=true) String trata,
        @RequestParam (value="usuario_iniciador", required = true) String usuario_iniciador,
        @RequestParam (value="reparticion", required = true) String reparticion,
        @RequestParam (value="sector", required = true) String sector,
        @RequestParam (value="nombre", required = true) String nombre,
        @RequestParam (value="selectable_tags", required = true) String selected,
        @RequestParam (value="descripcion_html", required = true) String descripcion_html,
        @RequestParam (value="descripcion_corta", required = true) String descripcion_corta,
        @RequestParam (value="tiene_pago", required = true) String tiene_pago,
        @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
        @RequestParam (value="obligatorio_interviniente", required = true) String obligatorio_interviniente,
        @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion,
        @RequestParam (value="visible", required = true) String visible_text, Model model) {

        String tags="{\"tags\":[";
        String tagsArr[] = selected.split(",");
        for (String tag: tagsArr) {
            tags += "\"" + tag + "\",";
        }

        tags = tags.substring(0,tags.length() - 1);
        tags += "]}";

        System.out.println("SELECTED::: " + tags);


        byte id_tramite_configuracion = 1;
        byte pago = 0;

        if (tiene_pago.contentEquals("SI")) {
            id_tramite_configuracion = 2;
            pago = 1;
        }

        byte prevalidacion = 0;
        if (tiene_prevalidacion.contentEquals("SI")) {
            prevalidacion = 1;
        }

        byte obligatorio = 0;
        if (obligatorio_interviniente.contentEquals("SI")) {
            obligatorio = 1;
        }

        byte visible = 0;
        if (visible_text.contentEquals("SI")) {
            visible = 1;
        }

        user.setPassword(Encrypter.decrypt(user.getPassword()));

        Tramite tramite = new Tramite(0,descripcion,id_tramite_configuracion,usuario_creacion,trata,usuario_iniciador,reparticion,sector,nombre,tags,pago,id_sir,descripcion_html,descripcion_corta,obligatorio,prevalidacion,visible);

        boolean success = true;
        try {
            TramiteManager.insertTramite(tramite, user);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
            }

        model.addAttribute("success", success);
        model.addAttribute("id", String.valueOf(tramite.getId()));
        user.setPassword(Encrypter.encrypt(user.getPassword()));

        return "post_tramite_nuevo";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showTramitesModificaciones(Model model,
                                             @RequestParam(value="username") String username,
                                             @RequestParam(value = "password") String password) {

        User user = null;
        try {
            user = new User(username,Encrypter.decrypt(password));
            TramiteManager.loadTramites(user);
            EtiquetaManager.loadEtiquetas(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("tramites", Application.tramites);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "tramite/tramites_modificaciones";
    }

    @RequestMapping(path = "/modificaciones/tramite", method = RequestMethod.GET)
    public String modificar(@RequestParam(value="selectable_tramites", required = true) int id, Model model,
                            @ModelAttribute User user, RedirectAttributes ra) {
        ra.addFlashAttribute("user", user);

        return "redirect:/tramites/modificaciones/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/modificaciones/tramite/{id}", method = RequestMethod.GET)
    public String getTramiteModificacion(@PathVariable("id") int id, Model model) {
        User user = (User) model.asMap().get("user");
        Tramite tramite = Application.tramites.get(id);
        String etiquetas = tramite.getEtiquetas();
        int i = etiquetas.indexOf("[") + 1; int f = etiquetas.indexOf("]");
        etiquetas = etiquetas.substring(i,f);

        String tagsArr[] = etiquetas.split(",");

        ArrayList<Tag> allTags = new ArrayList<>();
        Iterator it = Application.etiquetas.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            allTags.addAll((ArrayList<Tag>)pair.getValue());
        }

        for(Tag tag: allTags) {
            for (String tagSelected: tagsArr) {
                if (tag.getTag().compareTo(tagSelected.substring(1,tagSelected.length()-1)) == 0)
                    tag.setSelected(true);
            }
        }


        model.addAttribute("tramite",tramite);
        model.addAttribute("etiquetas",etiquetas);
        model.addAttribute("tags", allTags);
        model.addAttribute("tratas_existentes", Application.tratasExistentes);
        model.addAttribute(user);

        return "tramite/tramite_modificar";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.POST)
    public String modify(Model model, @ModelAttribute User user,
            @RequestParam (value="id", required = true) int id,
            @RequestParam (value="descripcion", required=true) String descripcion,
            @RequestParam (value="usuario_modificador", required = true) String usuario_modificador,
            @RequestParam (value="trata", required=true) String trata,
            @RequestParam (value="usuario_iniciador", required = true) String usuario_iniciador,
            @RequestParam (value="reparticion", required = true) String reparticion,
            @RequestParam (value="sector", required = true) String sector,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="selectable_tags", required = true) String selected,
            @RequestParam (value="descripcion_html", required = true) String descripcion_html,
            @RequestParam (value="descripcion_corta", required = true) String descripcion_corta,
            @RequestParam (value="tiene_pago", required = true) String tiene_pago,
            @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
            @RequestParam (value="obligatorio_interviniente", required = true) String obligatorio_interviniente,
            @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion,
            @RequestParam (value="visible", required = true) String visible_text) {

        Tramite tramite = Application.tramites.get(id);
        tramite.setDescripcion(descripcion);
        tramite.setTrata(trata);
        tramite.setUsuarioIniciador(usuario_iniciador);
        tramite.setReparticion(reparticion);
        tramite.setSector(sector);
        tramite.setNombre(nombre);
        tramite.setDescripcionCorta(descripcion_corta);
        tramite.setDescripcionHtml(descripcion_html);

        String tags="{\"tags\":[";
        String tagsArr[] = selected.split(",");
        for (String tag: tagsArr) {
            tags += "\"" + tag + "\",";
        }

        tags = tags.substring(0,tags.length() - 1);
        tags += "]}";

        System.out.println("SELECTED::: " + tags);

        Iterator it = Application.etiquetas.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            for (Tag tag: (ArrayList<Tag>)pair.getValue()) {
                tag.setSelected(false);
            }
        }

        tramite.setEtiquetas(tags);

        byte id_tramite_configuracion = 1;
        byte pago = 0;
        if (tiene_pago.contentEquals("SI")) {
            id_tramite_configuracion = 2;
            pago = 1;
        }
        tramite.setPago(pago);
        tramite.setIdTipoTramiteSir(id_sir);
        tramite.setIdTramiteConfiguracion(id_tramite_configuracion);

        byte prevalidacion = 0;
        if (tiene_prevalidacion.contentEquals("SI")) {
            prevalidacion = 1;
        }
        tramite.setPrevalidacion(prevalidacion);

        byte obligatorio = 0;
        if (obligatorio_interviniente.contentEquals("SI")) {
            obligatorio = 1;
        }
        tramite.setObligatorioInterviniente(obligatorio);

        byte visible = 0;
        if (visible_text.contentEquals("SI")) {
            visible = 1;
        }
        tramite.setVisible(visible);

        user.setPassword(Encrypter.decrypt(user.getPassword()));
        boolean success = true;
        try {
            TramiteManager.updateTramite(tramite, usuario_modificador, user);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        model.addAttribute("success", success);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "post_modificacion";
    }

    @RequestMapping(path = "/relaciones", method = RequestMethod.GET)
    public String showTramitesRelaciones(Model model,
                                         @RequestParam(value="username") String username,
                                         @RequestParam(value = "password") String password) {

        User user = null;
        try {
            user = new User(username,Encrypter.decrypt(password));
            TramiteManager.loadTramites(user);
            DocumentoManager.loadDocumentos(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("tramites", Application.tramites);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "tramite/tramites_relaciones";
    }

    @RequestMapping(path = "/relaciones/tramite", method = RequestMethod.GET)
    public String relacionar(@RequestParam(value="selectable_tramites", required = true) int id, Model model,
                             @ModelAttribute User user, RedirectAttributes ra) {

        ra.addFlashAttribute("user", user);
        return "redirect:/tramites/relaciones/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/relaciones/tramite/{id}", method = RequestMethod.GET)
    public String getTramiteRelacion(@PathVariable("id") int id, Model model) {
        User user = (User) model.asMap().get("user");
        Tramite tramite = Application.tramites.get(id);
        ArrayList<Integer> docsId = null;
        user.setPassword(Encrypter.decrypt(user.getPassword()));
        try {
            Iterator it = Application.documentos.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                Documento documento = (Documento) pair.getValue();
                documento.setRelacionado((byte)0);
            }

            docsId = RelacionesManager.getDocumentosRelacionados(id, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String documentos_relacionados = "";
        for (int doc: docsId) {
            documentos_relacionados += doc + ",";
        }

        if (documentos_relacionados.length() > 0)
            documentos_relacionados = documentos_relacionados.substring(0, documentos_relacionados.length() - 1);

        model.addAttribute("tramite",tramite);
        model.addAttribute("documentos",Application.documentos);
        model.addAttribute("documentos_relacionados",documentos_relacionados);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "tramite/tramite_relaciones_configuracion";
    }

    @RequestMapping(path = "/relaciones/tramite", method = RequestMethod.POST)
    public String addTramiteRelacion(@RequestParam("tramite_id") int id, Model model, @ModelAttribute User user,
                                     @RequestParam("usuario") String usuario,
                                     @RequestParam("documentos_relacionados") String documentosRelacionados,
                                     @RequestParam("documentos_configuracion") String documentosConfigurados){


        String listaDocumentosRelacionados[] = documentosRelacionados.split(",");
        for (String docId: listaDocumentosRelacionados) {
            if (docId.compareTo("") != 0) {
                Documento documento = Application.documentos.get(Integer.parseInt(docId));
                documento.setRelacionado((byte)0);
                documento.relacion = null;
            }
        }

        user.setPassword(Encrypter.decrypt(user.getPassword()));
        boolean success = true;
        try {
            RelacionesManager.updateRelaciones(id, documentosConfigurados,user);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "post_relacion_documentos";
    }

    @RequestMapping(path = "/prevalidaciones", method = RequestMethod.GET)
    public String showTramitesPrevalidaciones(Model model,
                                              @RequestParam(value="username") String username,
                                              @RequestParam(value = "password") String password) {

        User user = null;
        try {
            user = new User(username, Encrypter.decrypt(password));
            TramiteManager.loadTramites(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HashMap<Integer,Tramite> tramitesConPrevalidacion = new HashMap<>();
        Iterator it = Application.tramites.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if ( ((Tramite) pair.getValue()).getPrevalidacion() == 1) {
                tramitesConPrevalidacion.put((Integer) pair.getKey(), (Tramite) pair.getValue());
            }
        }




        model.addAttribute("tramites", tramitesConPrevalidacion);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "tramite/tramites_prevalidaciones";
    }

    @RequestMapping(path = "/prevalidaciones/tramite", method = RequestMethod.GET)
    public String prevalidar(@RequestParam(value="selectable_tramites", required = true) int id, Model model,
                             @ModelAttribute User user, RedirectAttributes ra) {

        ra.addFlashAttribute("user",user);
        return "redirect:/tramites/prevalidaciones/tramite/"+Integer.toString(id);
    }

    @RequestMapping(path = "/prevalidaciones/tramite/{id}", method = RequestMethod.GET)
    public String getTramitePrevalidacion(@PathVariable("id") int id, Model model) {
        User user = (User) model.asMap().get("user");
        Tramite tramite = Application.tramites.get(id);
        ArrayList<String> cuits = null;
        user.setPassword(Encrypter.decrypt(user.getPassword()));
        try {
            cuits = TramiteManager.getPrevalidaciones(id, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
            String cuits_prevalidados = "";
            for (String cuit: cuits) {
                cuits_prevalidados += cuit + ",";
            }

            if (cuits_prevalidados.length() > 0)
                cuits_prevalidados = cuits_prevalidados.substring(0, cuits_prevalidados.length() - 1);

        model.addAttribute("tramite",tramite);
        model.addAttribute("cuits",cuits);
        model.addAttribute("cuits_prevalidados",cuits_prevalidados);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "tramite/tramites_prevalidaciones_configuracion";
    }

    @RequestMapping(path = "/prevalidaciones/tramite", method = RequestMethod.POST)
    public String addTramitePrevalidacion(@RequestParam("tramite_id") int id, Model model,
                                          @RequestParam("cuits_configuracion") String cuitsConfiguracion,
                                          @ModelAttribute User user){

        user.setPassword(Encrypter.decrypt(user.getPassword()));

        boolean success = true;
        try {
            TramiteManager.updatePrevalidaciones(id, cuitsConfiguracion, user);
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        model.addAttribute("success", success);
        user.setPassword(Encrypter.encrypt(user.getPassword()));
        model.addAttribute(user);

        return "post_tramite_prevalidacion";
    }
}
