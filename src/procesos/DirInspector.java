package procesos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirInspector {

    public static void main(String[] args) {
        boolean concurrente = false;

        List<String> rutas = new ArrayList<>();
        for (String arg : args) {
            if (arg.equals("-c")) {
                concurrente = true;
            } else {
                rutas.add(arg);
            }
        }

        if (rutas.isEmpty()) {
            System.err.println("Error: debe indicar al menos una ruta de directorio.");
            System.err.println("Uso: java procesos.DirInspector [-c] ruta1 [ruta2 ...]");
            System.exit(1);
        }


        List<String> validas = new ArrayList<>();
        for (String ruta : rutas) {
            if (esDirectorioValido(ruta)) {
                validas.add(ruta);
            }
        }

        if (concurrente) {
            System.out.println("Modo de ejecución: CONCURRENTE");
        } else {
            System.out.println("Modo de ejecución: SECUENCIAL");
        }

        GestorProcesos gestor = new GestorProcesos();
        List<Resultado> inspeccionados = gestor.inspeccionar(validas, concurrente);

        List<Resultado> resultados = new ArrayList<>();
        int posicion = 0;
        for (String ruta : rutas) {
            if (validas.contains(ruta)) {
                resultados.add(inspeccionados.get(posicion));
                posicion++;
            } else {
                resultados.add(new Resultado(ruta, "-", Estado.NO_EXISTE, Resultado.SIN_CODIGO));
            }
        }

        InformeResultados.imprimir(resultados);
    }

    private static boolean esDirectorioValido(String ruta) {
        File f = new File(ruta);
        if (!f.exists()) {
            System.err.println("La ruta no existe y se descarta: " + ruta);
            return false;
        }
        if (!f.isDirectory()) {
            System.err.println("La ruta no es un directorio y se descarta: " + ruta);
            return false;
        }
        return true;
    }
}
