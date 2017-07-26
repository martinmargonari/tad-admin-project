$(document).ready(function() {

    $(document).on("click", ".grupo__add", function(e) {
        var grupoId = $('.selectpicker option:selected').val();
        var grupos_relacionados = document.getElementById("grupos_relacionados");
        var grupos_insert = document.getElementById("grupos_insert");

        if (grupos_relacionados.value.includes(grupoId) || grupos_insert.value.includes(grupoId)) {
            alert("El grupo ya fue ingresado");
            return false;
        }

        if (grupos_insert.value.length > 0) {
            grupos_insert.value += ",";
        }
        grupos_insert.value += grupoId;

        var tableRow = document.createElement("tr");
        tableRow.setAttribute("th:id", "row-" + grupoId);

        var nombreGrupo = $('.selectpicker option:selected').text();
        var celdaGrupo= document.createElement("td");
        var textGrupo = document.createTextNode(nombreGrupo);
        celdaGrupo.appendChild(textGrupo);
        celdaGrupo.setAttribute("value",grupoId);
        celdaGrupo.style = "width:90%";
        celdaGrupo.setAttribute("align","left");
        celdaGrupo.setAttribute("id", "text-" + grupoId);


        var celdaDelete = document.createElement("td");
        celdaDelete.style="width:10%";
        var button = document.createElement("button");
        button.className = "grupo__delete";
        button.type = "button";

        celdaDelete.appendChild(button);

        tableRow.appendChild(celdaGrupo);
        tableRow.appendChild(celdaDelete);

        var table = document.getElementById("table_body");
        table.appendChild(tableRow);

        return false;
    });

    $(document).on("click", ".grupo__delete", function(e) {
        var that = this;

        var row = that.parentNode.parentNode;
        var i = row.rowIndex - 1;

        var grupoEliminado = row.cells.item(0).id.substring(5);
        var grupos_relacionados = document.getElementById("grupos_relacionados");
        var grupos_insert = document.getElementById("grupos_insert");
        var grupos_delete = document.getElementById("grupos_delete");
        var element;

        if (grupos_insert.value.includes(grupoEliminado))
            element = grupos_insert;
        else
            element = grupos_relacionados;

        var n = element.value.indexOf(grupoEliminado);
        element.value = element.value.substring(0,n-1) + element.value.substring(n + grupoEliminado.length, element.value.length);
        if (element.value.startsWith(","))
            element.value = element.value.substring(1,element.value.length);

        if (element == grupos_relacionados) {
            if (grupos_delete.value.length > 0) {
                grupos_delete.value += ",";
            }
            grupos_delete.value += grupoEliminado;
        }
            
        document.getElementById("table_body").deleteRow(i);
        return false;
    });
    
    $(document).on("click", ".grupo__delete", function(e) {
        var that = this;

        var row = that.parentNode.parentNode;
        var i = row.rowIndex - 1;

        var grupo = row.cells.item(0).textContent;
        var grupos_configuracion = document.getElementById("grupos_configuracion");
        var n = grupos_configuracion.value.indexOf(grupo);

        grupos_configuracion.value = grupos_configuracion.value.substring(0,n-1) + grupos_configuracion.value.substring(n + grupo.length, grupos_configuracion.value.length);
        if (grupos_configuracion.value.startsWith(","))
            grupos_configuracion.value = grupos_configuracion.value.substring(1,grupos_configuracion.value.length);
        document.getElementById("table_body").deleteRow(i);

        return false;
    });

});
