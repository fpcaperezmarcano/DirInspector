package procesos;

public class Resultado {

    public static final int SIN_CODIGO = -1;

    private String ruta;
    private String ficheroLog;
    private Estado estado;
    private int codigoSalida;

    public Resultado(String ruta, String ficheroLog, Estado estado, int codigoSalida) {
        this.ruta = ruta;
        this.ficheroLog = ficheroLog;
        this.estado = estado;
        this.codigoSalida = codigoSalida;
    }

    public String getRuta() {
        return ruta;
    }

    public String getFicheroLog() {
        return ficheroLog;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getCodigoSalidaTexto() {
        if (codigoSalida == SIN_CODIGO) {
            return "-";
        } else {
            return String.valueOf(codigoSalida);
        }
    }
}
