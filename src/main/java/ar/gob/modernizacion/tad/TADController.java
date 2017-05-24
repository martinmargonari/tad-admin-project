package ar.gob.modernizacion.tad;

import ar.gob.modernizacion.tad.managers.ConnectionManager;
import ar.gob.modernizacion.tad.managers.DocumentoManager;
import ar.gob.modernizacion.tad.managers.TramiteManager;
import ar.gob.modernizacion.tad.model.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinm on 19/05/17.
 */

@Controller
public class TADController {

    private static List<Tag> tags = new ArrayList<Tag>();
    private static List<String> tagsStr = new ArrayList<String>();

    static {
        tags.add(new Tag("Tag 1"));
        tags.add(new Tag("Tag 2"));
        tags.add(new Tag("Tag 3"));
    }

    static {
        tagsStr.add("Tag 1");
        tagsStr.add("Tag 2");
        tagsStr.add("Tag 3");
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @RequestMapping("/tramite_nuevo")
    public String tramite_nuevo(
            @RequestParam(value="tag", required=false, defaultValue="TAG1") String tag,
                    Model model) {
        for (Tag tagA: tags) {
            System.out.println (tagA.getTag());
        }
        model.addAttribute("tag",tag);
        model.addAttribute("tags",tagsStr);
        return "tramite_nuevo";
    }

    @RequestMapping("/post_tramite_nuevo")
    public void post_tramite_nuevo(
            @RequestParam (value="descripcion", required=true) String descripcion,
            @RequestParam (value="trata", required=true) String trata,
            @RequestParam (value="usuario", required = true) String usuario,
            @RequestParam (value="reparticion", required = true) String reparticion,
            @RequestParam (value="sector", required = true) String sector,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="etiquetas", required = true) String etiquetas,
            @RequestParam (value="descripcion_html", required = true) String descripcion_html,
            @RequestParam (value="tiene_pago", required = true) String tiene_pago,
            @RequestParam (value="id_sir", required = false, defaultValue = "") String id_sir,
            @RequestParam (value="tiene_prevalidacion", required = true) String tiene_prevalidacion) {

            Connection connection = ConnectionManager.connect("mmargonari","orl174A");

            ArrayList<String> tags = new ArrayList<>();
            tags.add("Ministerio de Agroindustria");
            tags.add("Actualizaciones");

            String id_tramite_configuracion = "1";
            String pago = "0";

            if (tiene_pago.contentEquals("SI")) {
                id_tramite_configuracion = "2";
                pago ="1";
            }

            String prevalidacion = "0";
            if (tiene_prevalidacion.contentEquals("SI")) {
                prevalidacion = "1";
            }

            if (connection != null) {
                try {
                    TramiteManager.insertTramite(connection, descripcion, id_tramite_configuracion, trata, usuario, reparticion, sector, nombre, tags, pago, id_sir, descripcion_html, prevalidacion);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                ConnectionManager.disconnect(connection);
            }



    }

    @RequestMapping("/documento_nuevo")
    public String documento_nuevo() {
        return "documento_nuevo";
    }

    @RequestMapping("/post_documento_nuevo")
    public void post_documento_nuevo(
            @RequestParam (value="acronimo_gedo", required=true) String acronimo_gedo,
            @RequestParam (value="acronimo_tad", required=true) String acronimo_tad,
            @RequestParam (value="nombre", required = true) String nombre,
            @RequestParam (value="descripcion", required = true) String descripcion,
            @RequestParam (value="es_embebido", required = true) String es_embebido) {

        Connection connection = ConnectionManager.connect("mmargonari","orl174A");

        String embebido = "0";

        if (es_embebido.contentEquals("SI")) {
            embebido = "1";
        }

        if (connection != null) {
            try {
                DocumentoManager.insertDocumento(connection,acronimo_gedo,acronimo_tad,nombre,descripcion,embebido);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ConnectionManager.disconnect(connection);
        }



    }
}
