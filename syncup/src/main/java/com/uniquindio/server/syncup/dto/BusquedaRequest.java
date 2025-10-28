package com.uniquindio.server.syncup.dto;

import java.util.List;

public class BusquedaRequest {
    private String titulo;
    private List<FiltroRequest> filtros;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<FiltroRequest> getFiltros() { return filtros; }
    public void setFiltros(List<FiltroRequest> filtros) { this.filtros = filtros; }
}
