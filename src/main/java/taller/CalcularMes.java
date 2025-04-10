package taller;


import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class CalcularMes extends Thread {

    private final File directorio;
    private double totalMes;
    private static final Semaphore semaphore = new Semaphore(1);

    public CalcularMes(File directorio) {
        this.directorio = directorio;
    }

    public double getTotalMes() {
        return totalMes;
    }

    public File getDirectorio() {
        return directorio;
    }

    @Override
    public void run() {
        if (!directorio.exists() || !directorio.isDirectory()) return;

        File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".ods"));
        if (archivos == null) return;

        for (File archivo : archivos) {
            try {
                semaphore.acquire();

                try {
                    procesarArchivo(archivo);

                } finally {
                    semaphore.release();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void procesarArchivo(File archivo) {
        try {

            SpreadSheet documento = SpreadSheet.createFromFile(archivo);
            Sheet hoja = documento.getSheet(0);

            Object valor = hoja.getValueAt(4, 45);
            if (valor instanceof Number) {
                double ingreso = ((Number) valor).doubleValue();
                totalMes += ingreso;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {

        }
    }
}
