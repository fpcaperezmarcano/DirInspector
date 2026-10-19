package procesos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class GestorProcesos {

    public static final long TIMEOUT_SEGUNDOS = 5;


    private Set<String> nombresUsados = new HashSet<>();


    public List<Resultado> inspeccionar(List<String> rutas, boolean concurrente) {
        if (concurrente) {
            return inspeccionarConcurrente(rutas);
        } else {
            return inspeccionarSecuencial(rutas);
        }
    }


    private List<Resultado> inspeccionarSecuencial(List<String> rutas) {
        List<Resultado> resultados = new ArrayList<>();

        for (String ruta : rutas) {
            File log = generarNombreLog(ruta);
            Process proceso = lanzarProceso(ruta, log);
            Resultado resultado = esperarProceso(ruta, log, proceso);
            resultados.add(resultado);
        }

        return resultados;
    }

    private List<Resultado> inspeccionarConcurrente(List<String> rutas) {
        List<File> logs = new ArrayList<>();
        List<Process> procesos = new ArrayList<>();

        for (String ruta : rutas) {
            File log = generarNombreLog(ruta);
            Process proceso = lanzarProceso(ruta, log);
            logs.add(log);
            procesos.add(proceso);
        }

        List<Resultado> resultados = new ArrayList<>();
        for (int i = 0; i < procesos.size(); i++) {
            Resultado resultado = esperarProceso(rutas.get(i), logs.get(i), procesos.get(i));
            resultados.add(resultado);
        }

        return resultados;
    }


    private Process lanzarProceso(String ruta, File log) {
        ProcessBuilder pb = new ProcessBuilder(comandoListado());
        pb.directory(new File(ruta));
        pb.redirectOutput(log);
        pb.redirectError(ProcessBuilder.Redirect.appendTo(log));

        try {
            return pb.start();
        } catch (IOException e) {
            System.err.println("No se pudo lanzar el proceso en " + ruta + ": " + e.getMessage());
            return null;
        }
    }


    private Resultado esperarProceso(String ruta, File log, Process proceso) {
        if (proceso == null) {
            return new Resultado(ruta, log.getName(), Estado.ERROR, Resultado.SIN_CODIGO);
        }

        try {
            boolean terminado = proceso.waitFor(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);

            if (!terminado) {
                proceso.destroyForcibly();
                return new Resultado(ruta, log.getName(), Estado.TIMEOUT, Resultado.SIN_CODIGO);
            }

            int codigo = proceso.exitValue();
            if (codigo == 0) {
                return new Resultado(ruta, log.getName(), Estado.OK, codigo);
            } else {
                return new Resultado(ruta, log.getName(), Estado.ERROR, codigo);
            }

        } catch (InterruptedException e) {
            proceso.destroyForcibly();
            return new Resultado(ruta, log.getName(), Estado.ERROR, Resultado.SIN_CODIGO);
        }
    }


    private List<String> comandoListado() {
        String sistema = System.getProperty("os.name").toLowerCase();

        if (sistema.contains("win")) {
            return List.of("cmd.exe", "/c", "dir", ".");
        } else {
            return List.of("ls", "-la", ".");
        }
    }

    private File generarNombreLog(String ruta) {
        String nombre = new File(ruta).getAbsoluteFile().getName();
        if (nombre.isEmpty()) {
            nombre = "raiz";
        }

        String nombreLog = "log_" + nombre + ".log";
        int numero = 2;
        while (nombresUsados.contains(nombreLog)) {
            nombreLog = "log_" + nombre + "_" + numero + ".log";
            numero++;
        }
        nombresUsados.add(nombreLog);

        return new File(nombreLog);
    }
}
