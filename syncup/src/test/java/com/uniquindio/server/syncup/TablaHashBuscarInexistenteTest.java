package com.uniquindio.server.syncup;

import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablaHashBuscarInexistenteTest {

    //Buscar un usuario que no existe retorna null

    @Test
    void testBuscarUsuarioInexistente() {

        // Crear tabla con capacidad fija
        TablaHashUsuarios tabla = new TablaHashUsuarios(10);

        // No se agregan usuarios

        // Intentar buscar un usuario que no debe existir
        assertNull(tabla.buscar("noExiste"));
        assertNull(tabla.buscarUsuario("noExiste"));
        assertFalse(tabla.existeUsuario("noExiste"));
    }
}
