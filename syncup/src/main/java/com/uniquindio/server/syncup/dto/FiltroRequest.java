package com.uniquindio.server.syncup.dto;

public class FiltroRequest implements Comparable<FiltroRequest> {
    private String campo;
    private String valor;
    private String logica;

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public String getLogica() { return logica; }
    public void setLogica(String logica) { this.logica = logica; }

        @Override
    public int compareTo(FiltroRequest o) {
        return this.campo.compareTo(o.getCampo());
    }
}
