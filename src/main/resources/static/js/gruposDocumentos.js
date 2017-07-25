$(document).ready(function() {

    $(document).on("click", ".documento__add", function(e) {
        var docId = $('.selectpicker option:selected').val();
        var documentos_update = document.getElementById("documentos_update");
        var documentos_insert = document.getElementById("documentos_insert");
        var documentos_delete = document.getElementById("documentos_delete");

        if (documentos_update.value.includes(docId) || documentos_insert.value.includes(docId)) {
            alert("El documento ya fue seleccionado");
            return false;
        }

        if (documentos_delete.value.includes(docId)) {
            var n = documentos_delete.value.indexOf(docId);

            documentos_delete.value = documentos_delete.value.substring(0,n-1) + documentos_delete.value.substring(n + docId.length, documentos_delete.value.length);
            if (documentos_delete.value.startsWith(","))
                documentos_delete.value = documentos_delete.value.substring(1,documentos_delete.value.length);
        }

        if (documentos_insert.value.length > 0) {
            documentos_insert.value += ",";
        }
        documentos_insert.value += docId;

        var tableRow = document.createElement("tr");
        tableRow.setAttribute("th:id", "row-" + docId);

        var nombreDoc = $('.selectpicker option:selected').text();
        var celdaDocRequerido = document.createElement("td");
        var textDocRequerido = document.createTextNode(nombreDoc);
        celdaDocRequerido.appendChild(textDocRequerido);
        celdaDocRequerido.setAttribute("value",docId);
        celdaDocRequerido.style = "width:75%";
        celdaDocRequerido.setAttribute("align","left");
        celdaDocRequerido.setAttribute("id", "text-" + docId);

        var celdaObligatorio = document.createElement("td");
        var checkbox = document.createElement('input');
        checkbox.type = "checkbox";
        checkbox.title = "Obligatorio";
        celdaObligatorio.setAttribute("id", "obligatorio-" + docId);
        celdaObligatorio.appendChild(checkbox);
        celdaObligatorio.style = "width:10%";


        var celdaOrden = document.createElement("td");
        var ordenInput = document.createElement('input');
        ordenInput.type = "number";
        ordenInput.title = "Orden";
        ordenInput.value = 1;
        ordenInput.setAttribute("min", 1);
        ordenInput.setAttribute("id", "orden-" + docId);
        celdaOrden.appendChild(ordenInput);
        celdaOrden.style = "width:10%";

        var celdaDelete = document.createElement("td");
        celdaDelete.style="width:5%";
        var button = document.createElement("button");
        button.className = "documento__delete";
        button.type = "button";

        celdaDelete.appendChild(button);

        tableRow.appendChild(celdaDocRequerido);
        tableRow.appendChild(celdaObligatorio);
        tableRow.appendChild(celdaOrden);
        tableRow.appendChild(celdaDelete);

        var table = document.getElementById("table_body");
        table.appendChild(tableRow);

        return false;
    });

    $(document).on("click", ".documento__delete", function(e) {
        var that = this;

        var row = that.parentNode.parentNode;
        var i = row.rowIndex - 1;

        var docRequerido = row.cells.item(0).id.substring(5);
        var documentos_update = document.getElementById("documentos_update");
        var documentos_insert = document.getElementById("documentos_insert");
        var documentos_delete = document.getElementById("documentos_delete");
        var element;

        if (documentos_insert.value.includes(docRequerido))
            element = documentos_insert;
        else
            element = documentos_update;

        var n = element.value.indexOf(docRequerido);
        element.value = element.value.substring(0,n-1) + element.value.substring(n + docRequerido.length, element.value.length);
        if (element.value.startsWith(","))
            element.value = element.value.substring(1,element.value.length);

        if (documentos_delete.value.length > 0) {
            documentos_delete.value += ",";
        }

        if (element == documentos_update)
            documentos_delete.value += docRequerido;

        document.getElementById("table_body").deleteRow(i);
        return false;
    });

});
