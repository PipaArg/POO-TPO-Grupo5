import vistas.VistaGenerarOC;
import vistas.VistaConsultarCC;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] opciones = {
                "Generar Orden de Compra",
                "Consultar Cuenta Corriente"
            };

            int eleccion = JOptionPane.showOptionDialog(
                null,
                "Bienvenido a FarmaRed\n¿Qué desea hacer?",
                "FarmaRed - Sistema de Gestión",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            if (eleccion == 0) {
                new VistaGenerarOC().setVisible(true);
            } else if (eleccion == 1) {
                new VistaConsultarCC().setVisible(true);
            }
        });
    }
}
