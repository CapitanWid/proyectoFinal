package com.uniquindio.server.syncup;

import com.uniquindio.server.syncup.datastructures.TablaHashUsuarios;
import com.uniquindio.server.syncup.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TablaHashInsertarUsuarioTest {

    @Test
    void testInsertarUsuario() {

        TablaHashUsuarios tabla = new TablaHashUsuarios(10);

        Usuario u = new Usuario(
                "user1",
                "Juan",
                "Perez",
                "correo@example.com",
                "pwd123"
        );

        tabla.agregarUsuario(u);

        Usuario resultado = tabla.buscar("user1");

        assertNotNull(resultado);
        assertEquals("user1", resultado.getUsuario());
    }
}
