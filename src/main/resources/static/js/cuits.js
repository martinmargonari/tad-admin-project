$(document).ready(function() {

    $(document).on("click", ".cuit__add", function(e) {
        var cuit = document.getElementById("cuit").value;
        var cuits_prevalidados = document.getElementById("cuits_prevalidados");
        var cuits_configuracion = document.getElementById("cuits_configuracion");

        if (cuits_prevalidados.value.includes(cuit) || cuits_configuracion.value.includes(cuit)) {
            alert("El CUIT ya fue ingresado");
            document.getElementById("cuit").value = "";
            return false;
        }

        if (cuits_configuracion.value.length > 0) {
            cuits_configuracion.value += ",";
        }
        cuits_configuracion.value += cuit;

        var tableRow = document.createElement("tr");
        tableRow.setAttribute("th:id", "row-" + cuit);

        var celdaCuit = document.createElement("td");
        var textCuit = document.createTextNode(cuit);
        celdaCuit.appendChild(textCuit);
        celdaCuit.setAttribute("value",cuit);
        celdaCuit.style = "width:90%";
        celdaCuit.setAttribute("align","left");
        celdaCuit.setAttribute("id", "text-" + cuit);


        var celdaDelete = document.createElement("td");
        celdaDelete.style="width:10%";
        var button = document.createElement("button");
        button.className = "cuit__delete";
        button.type = "button";

        celdaDelete.appendChild(button);

        tableRow.appendChild(celdaCuit);
        tableRow.appendChild(celdaDelete);

        var table = document.getElementById("table_body");
        table.appendChild(tableRow);
        document.getElementById("cuit").value = "";

        return false;
    });

    $(document).on("click", ".cuit__delete", function(e) {
        var that = this;

        var row = that.parentNode.parentNode;
        var i = row.rowIndex - 1;

        var cuit = row.cells.item(0).textContent;
        var cuits_configuracion = document.getElementById("cuits_configuracion");
        var n = cuits_configuracion.value.indexOf(cuit);

        cuits_configuracion.value = cuits_configuracion.value.substring(0,n-1) + cuits_configuracion.value.substring(n + cuit.length, cuits_configuracion.value.length);
        if (cuits_configuracion.value.startsWith(","))
            cuits_configuracion.value = cuits_configuracion.value.substring(1,cuits_configuracion.value.length);
        document.getElementById("table_body").deleteRow(i);
        
        return false;
    });

});
