package modelo.comprobantes;

public class ResultadoValidacion {
    private boolean valido;
    private String mensaje;

    public ResultadoValidacion(boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
    }

    public boolean isValido() { return valido; }
    public String getMensaje() { return mensaje; }
}