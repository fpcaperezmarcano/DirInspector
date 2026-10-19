package procesos;

import java.util.List;


public final class InformeResultados {

    private InformeResultados() {
    }

    public static void imprimir(List<Resultado> resultados) {
        String[] cabecera = {"Ruta", "Fichero de log", "Estado", "Exit Code"};


        int[] anchos = new int[cabecera.length];
        for (int i = 0; i < cabecera.length; i++) {
            anchos[i] = cabecera[i].length();
        }
        for (Resultado r : resultados) {
            String[] fila = fila(r);
            for (int i = 0; i < fila.length; i++) {
                anchos[i] = Math.max(anchos[i], fila[i].length());
            }
        }

        String separador = separador(anchos);
        System.out.println();
        System.out.println(separador);
        System.out.println(formatear(cabecera, anchos));
        System.out.println(separador);
        for (Resultado r : resultados) {
            System.out.println(formatear(fila(r), anchos));
        }
        System.out.println(separador);
    }

    private static String[] fila(Resultado r) {
        return new String[]{r.getRuta(), r.getFicheroLog(), r.getEstado().name(), r.getCodigoSalidaTexto()};
    }

    private static String formatear(String[] celdas, int[] anchos) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < celdas.length; i++) {
            sb.append(' ').append(String.format("%-" + anchos[i] + "s", celdas[i])).append(" |");
        }
        return sb.toString();
    }

    private static String separador(int[] anchos) {
        StringBuilder sb = new StringBuilder("+");
        for (int ancho : anchos) {
            sb.append("-".repeat(ancho + 2)).append('+');
        }
        return sb.toString();
    }
}
