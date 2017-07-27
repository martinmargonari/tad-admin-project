package ar.gob.modernizacion.tad.controllers;

import ar.gob.modernizacion.tad.Application;
import ar.gob.modernizacion.tad.dao.*;
import ar.gob.modernizacion.tad.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private static final String YES = "SI";

    @Autowired
    private EtiquetaDAO etiquetaDAO;

    @Autowired
    private TramiteDAO tramiteDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @Autowired
    private DocumentoRequeridoDAO documentoRequeridoDAO;

    @Autowired
    private PrevalidacionDAO prevalidacionDAO;

    @RequestMapping(path = "/new", method = RequestMethod.GET)
    public String getNewForm(Model model,
                             @RequestParam(value="username") String username,
                             @RequestParam(value = "password") String password) {
        User user = new User(username,password);

        model.addAttribute("tags", etiquetaDAO.list(user));
        model.addAttribute("tratas_existentes", tramiteDAO.getTratas(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "tramite/tramite_nuevo";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String add(@ModelAttribute User user,
        @RequestParam (value="descripcion", required=true) String descripcion,
        @RequestParam (value="trata", required=true) String trata,
        @RequestParam (value="usuario_iniciador", required = true) String usuario_iniciador,
        @RequestParam (value="reparticion", required = true) String reparticion,
        @RequestParam (value="sector", required = true) String sector,
        @RequestParam (value="nombre", required = true) String nombre,
        @RequestParam (value="selected_tags", required = true) String selected,
        @RequestParam (value="descripcion_html", required = true) String descripcion_html,
        @RequestParam (value="descripcion_corta", required = true) String descripcion_corta,
        @RequestParam (value="tiene_pago", required = true) String tiene_pago,
        @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
        @RequestParam (value="obligatorio_interviniente", required = true) String obligatorio_interviniente,
        @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion,
        @RequestParam (value="tiene_firma_conjunta", required = true) String tiene_firma_conjunta,
        @RequestParam (value="visible", required = true) String visible_text, Model model) {

        Tramite tramite = generateTramite(descripcion, trata, usuario_iniciador, reparticion, sector, nombre, selected, descripcion_html,
                descripcion_corta, tiene_pago, id_sir, obligatorio_interviniente, tiene_prevalidacion, tiene_firma_conjunta, visible_text);

        user.decryptPassword();

        boolean success = true;
        try {
            tramiteDAO.insert(tramite,user);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        model.addAttribute("success", success);
        model.addAttribute("id", String.valueOf(tramite.getId()));
        user.encryptPassword();
        model.addAttribute(user);

        return "post_tramite_nuevo";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.GET)
    public String showTramitesModificaciones(Model model,
                                             @RequestParam(value="username") String username,
                                             @RequestParam(value = "password") String password) {

        User user = new User(username,password);

        model.addAttribute("tramites", tramiteDAO.list(user));
        user.encryptPassword();
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
        user.decryptPassword();
        Tramite tramite = tramiteDAO.get(id,user);
        String etiquetas = tramite.getEtiquetas();
        int i = etiquetas.indexOf("[") + 1; int f = etiquetas.indexOf("]");
        etiquetas = etiquetas.substring(i,f);

        String tagsArr[] = etiquetas.split(",");

        ArrayList<Tag> allTags = new ArrayList<>();
        HashMap<String, List<Tag>> etiquetasSelected = etiquetaDAO.list(user);
        Iterator it = etiquetasSelected.entrySet().iterator();
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
        model.addAttribute("tags", etiquetasSelected);
        model.addAttribute("tratas_existentes", tramiteDAO.getTratas(user));
        user.encryptPassword();
        model.addAttribute(user);

        return "tramite/tramite_modificar";
    }

    @RequestMapping(path = "/modificaciones", method = RequestMethod.POST)
    public String modify(Model model, @ModelAttribute User user,
            @RequestParam (value="id", required = true) int id,
            @RequestParam (value="descripcion", required=true) String descripcion,
            @RequestParam (value="trata", required=true) String trata,
            @RequestParam (value="usuario_iniciador", required = true) String usuario_iniciador,
            @RequestParam (value="reparticion", required = true) String reparticion,
            @RequestParam (value="sector", required = true) String sector,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="selected_tags", required = true) String selected,
            @RequestParam (value="descripcion_html", required = true) String descripcion_html,
            @RequestParam (value="descripcion_corta", required = true) String descripcion_corta,
            @RequestParam (value="tiene_pago", required = true) String tiene_pago,
            @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
            @RequestParam (value="obligatorio_interviniente", required = true) String obligatorio_interviniente,
            @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion,
            @RequestParam (value="tiene_firma_conjunta", required = true) String tiene_firma_conjunta,
            @RequestParam (value="visible", required = true) String visible_text) {

        user.decryptPassword();

        Tramite tramite = generateTramite(descripcion, trata, usuario_iniciador, reparticion, sector, nombre, selected, descripcion_html,
                descripcion_corta, tiene_pago, id_sir, obligatorio_interviniente, tiene_prevalidacion, tiene_firma_conjunta, visible_text);
        tramite.setId(id);

        HashMap<String, List<Tag>> etiquetasSelected = etiquetaDAO.list(user);
        Iterator it = etiquetasSelected.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            for (Tag tag: (ArrayList<Tag>)pair.getValue()) {
                tag.setSelected(false);
            }
        }

        boolean success = true;
        try {
            tramiteDAO.update(tramite, user);
        } catch (DataAccessException e) {
            success = false;
            e.printStackTrace();
        }

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_modificacion";
    }

    @RequestMapping(path = "/relaciones", method = RequestMethod.GET)
    public String showTramitesRelaciones(Model model,
                                         @RequestParam(value="username") String username,
                                         @RequestParam(value = "password") String password) {

        User user = new User(username,password);

        model.addAttribute("tramites", tramiteDAO.list(user));
        user.encryptPassword();
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
        user.decryptPassword();

        Tramite tramite = tramiteDAO.get(id,user);
        List<Documento> documentos = documentoDAO.list(user);
        List<DocumentoRequerido> documentosRequeridos = documentoRequeridoDAO.listPorTramite(id, user);

        String documentos_update = "";
        int idTipoDocumento;
        Documento documento;
        for (DocumentoRequerido documentoRequerido: documentosRequeridos) {
            idTipoDocumento = documentoRequerido.getIdTipoDocumento();
            documentos_update += idTipoDocumento + ",";
            documento = documentoDAO.get(idTipoDocumento, user);
            documentoRequerido.setAcronimoGedo(documento.getAcronimoGedo());
            documentoRequerido.setAcronimoTad(documento.getAcronimoTad());
            documentoRequerido.setNombre(documento.getNombre());
        }

        if (documentos_update.length() > 0)
            documentos_update = documentos_update.substring(0, documentos_update.length() - 1);

        model.addAttribute("tramite",tramite);
        model.addAttribute("documentos", documentos);
        model.addAttribute("documentos_requeridos", documentosRequeridos);
        model.addAttribute("documentos_update",documentos_update);
        user.encryptPassword();
        model.addAttribute(user);

        return "tramite/tramite_relaciones_configuracion";
    }

    @RequestMapping(path = "/relaciones/tramite", method = RequestMethod.POST)
    public String addTramiteRelacion(@RequestParam("tramite_id") int id, Model model, @ModelAttribute User user,
                                     @RequestParam("documentos_update") String documentosUpdate,
                                     @RequestParam("documentos_insert") String documentosInsert,
                                     @RequestParam("documentos_delete") String documentosDelete){

        user.decryptPassword();

        String listaDocumentosUpdate[] = documentosUpdate.split(";");
        for (String docRequerido: listaDocumentosUpdate) {
            if (docRequerido.isEmpty()) break;
            String parameters[] = docRequerido.split(",");
            DocumentoRequerido documentoRequerido = new DocumentoRequerido();
            documentoRequerido.setIdTipoTramite(id);
            documentoRequerido.setIdTipoDocumento(Integer.valueOf(parameters[0]));
            documentoRequerido.setObligatorio(Byte.valueOf(parameters[1]));
            documentoRequerido.setCantidad(Byte.valueOf(parameters[2]));
            documentoRequerido.setOrden(Byte.valueOf(parameters[3]));
            
            documentoRequeridoDAO.update(documentoRequerido, user);
        }

        String listaDocumentosInsert[] = documentosInsert.split(";");
        for (String docRequerido: listaDocumentosInsert) {
            if (docRequerido.isEmpty()) break;
            String parameters[] = docRequerido.split(",");
            DocumentoRequerido documentoRequerido = new DocumentoRequerido();
            documentoRequerido.setIdTipoTramite(id);
            documentoRequerido.setIdTipoDocumento(Integer.valueOf(parameters[0]));
            documentoRequerido.setObligatorio(Byte.valueOf(parameters[1]));
            documentoRequerido.setCantidad(Byte.valueOf(parameters[2]));
            documentoRequerido.setOrden(Byte.valueOf(parameters[3]));

            documentoRequeridoDAO.insert(documentoRequerido, user);
        }

        String listaDocumentosDelete[] = documentosDelete.split(",");
        for (String docRequerido: listaDocumentosDelete) {
            if (docRequerido.isEmpty()) break;

            int idTipoDocumento = Integer.valueOf(docRequerido);
            documentoRequeridoDAO.delete(idTipoDocumento, user);
        }
        
        boolean success = true;

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_relacion_documentos";
    }

    @RequestMapping(path = "/prevalidaciones", method = RequestMethod.GET)
    public String showTramitesPrevalidaciones(Model model,
                                              @RequestParam(value="username") String username,
                                              @RequestParam(value = "password") String password) {

        User user = new User(username, password);

        List<Tramite> tramites = tramiteDAO.list(user);
        List<Tramite> tramitesConPrevalidacion = new ArrayList<>();

        for(Tramite tramite: tramites) {
            if ( tramite.getPrevalidacion() == 1) {
                tramitesConPrevalidacion.add(tramite);
            }
        }


        model.addAttribute("tramites", tramitesConPrevalidacion);
        user.encryptPassword();
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
        user.decryptPassword();

        List<Prevalidacion> listPrevalidaciones = prevalidacionDAO.listPorTramite(id, user);

        ArrayList<String> cuits = new ArrayList<>();
        String cuitsPrevalidados = "";
        for (Prevalidacion prevalidacion: listPrevalidaciones) {
            cuitsPrevalidados += prevalidacion.getCuit() + ",";
            cuits.add(prevalidacion.getCuit());
        }

        if (cuitsPrevalidados.length() > 0)
            cuitsPrevalidados = cuitsPrevalidados.substring(0, cuitsPrevalidados.length() - 1);

        model.addAttribute("tramite", tramiteDAO.get(id, user));
        model.addAttribute("cuits",cuits);
        model.addAttribute("cuits_prevalidados",cuitsPrevalidados);
        user.encryptPassword();
        model.addAttribute(user);

        return "tramite/tramites_prevalidaciones_configuracion";
    }

    @RequestMapping(path = "/prevalidaciones/tramite", method = RequestMethod.POST)
    public String addTramitePrevalidacion(@RequestParam("tramite_id") int id, Model model,
                                          @RequestParam("cuits_configuracion") String cuitsConfiguracion,
                                          @ModelAttribute User user){

        user.decryptPassword();

        boolean success = true;
        for (String cuit: cuitsConfiguracion.split(",")) {
            if (cuit.isEmpty()) break;
            prevalidacionDAO.insert(new Prevalidacion(0,id,cuit),user);
        }

        model.addAttribute("success", success);
        user.encryptPassword();
        model.addAttribute(user);

        return "post_tramite_prevalidacion";
    }

    private String parseTags(String listOfTags) {
        String tags="{\"tags\":[";
        String tagsArr[] = listOfTags.split(",");
        for (String tag: tagsArr) {
            tags += "\"" + tag + "\",";
        }

        tags = tags.substring(0,tags.length() - 1);
        tags += "]}";

        return tags;
    }

    private byte parseYesNo(String option) {
        if (option.contentEquals(YES)) {
            return 1;
        }

        return 0;
    }

    private Tramite generateTramite(String descripcion, String trata, String usuario_iniciador, String reparticion, String sector, String nombre, String selected, String descripcion_html, String descripcion_corta, String tiene_pago, String id_sir, String obligatorio_interviniente, String tiene_prevalidacion, String tiene_firma_conjunta, String visible_text) {
        Tramite tramite = new Tramite();
        String tags = parseTags(selected);
        byte pago = parseYesNo(tiene_pago);
        if (pago == 0) id_sir = "";
        byte idTramiteConfiguracion = (byte) (pago + 1);
        byte obligatorio = parseYesNo(obligatorio_interviniente);
        byte prevalidacion = parseYesNo(tiene_prevalidacion);
        byte tieneFirmaConjunta = parseYesNo(tiene_firma_conjunta);
        if (tieneFirmaConjunta == 1) idTramiteConfiguracion = 6;
        byte visible = parseYesNo(visible_text);

        tramite.setId(0);
        tramite.setDescripcion(descripcion);
        tramite.setIdTramiteConfiguracion(idTramiteConfiguracion);
        tramite.setTrata(trata);
        tramite.setUsuarioIniciador(usuario_iniciador);
        tramite.setReparticion(reparticion);
        tramite.setSector(sector);
        tramite.setNombre(nombre);
        tramite.setEtiquetas(tags);
        tramite.setPago(pago);
        tramite.setIdTipoTramiteSir(id_sir);
        tramite.setDescripcionHtml(descripcion_html);
        tramite.setDescripcionCorta(descripcion_corta);
        tramite.setObligatorioInterviniente(obligatorio);
        tramite.setPrevalidacion(prevalidacion);
        tramite.setVisible(visible);

        return tramite;
    }
}
