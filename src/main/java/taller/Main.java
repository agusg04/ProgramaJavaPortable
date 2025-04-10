package taller;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String directorioActual = System.getProperty("user.dir");

        File carpeta = new File(directorioActual);
        List<String> meses = new ArrayList<>(Arrays.asList(
                "enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
        );

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            return;
        }

        File[] subdirectorios = carpeta.listFiles(File::isDirectory);
        if (subdirectorios == null) {
            return;
        }

        List<CalcularMes> hilos = new ArrayList<>();
        double sumaTotal = 0.0;

        for (File directorio : subdirectorios) {
            if (meses.contains(directorio.getName().toLowerCase())) {
                CalcularMes hilo = new CalcularMes(directorio);
                hilos.add(hilo);
                hilo.start();
            }
        }

        for (CalcularMes hilo : hilos) {
            try {
                hilo.join();
                sumaTotal += hilo.getTotalMes();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        escribirResultados(hilos, sumaTotal, directorioActual);

    }

    private static void escribirResultados(List<CalcularMes> hilos, double sumaTotal, String ruta) {
        File salida = new File(ruta + File.separator + "Ingresos.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(salida))) {
            bw.write("Ingresos Totales Por Mes\n\n");

            for (CalcularMes hilo : hilos) {
                String nombre = hilo.getDirectorio().getName();
                double total = hilo.getTotalMes();
                bw.write(nombre + ": " + total + " €\n");
            }

            bw.write("\nSuma total: " + sumaTotal + "\n");

        } catch (IOException e) {}
    }
}