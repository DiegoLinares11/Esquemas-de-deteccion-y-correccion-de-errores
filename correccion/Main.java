import java.io.*;
import java.util.*;

public class Main {

    // Capa de presentación: convierte texto a binario ASCII
    public static String textoABinario(String texto) {
        StringBuilder binario = new StringBuilder();
        for (char c : texto.toCharArray()) {
            binario.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binario.toString();
    }

    // Capa de ruido: aplica flip aleatorio con cierta probabilidad
    public static String aplicarRuido(String trama, double probabilidad) {
        Random random = new Random();
        StringBuilder ruidoso = new StringBuilder();
        for (char bit : trama.toCharArray()) {
            if (random.nextDouble() < probabilidad) {
                ruidoso.append(bit == '0' ? '1' : '0'); // flip
            } else {
                ruidoso.append(bit);
            }
        }
        return ruidoso.toString();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        // ─── CAPA APLICACIÓN ──────────────────────────────
        System.out.println(" CAPA APLICACION");
        System.out.print("Ingrese el mensaje a enviar: ");
        String mensaje = sc.nextLine();

        // ─── CAPA PRESENTACIÓN ────────────────────────────
        System.out.println("\n CAPA PRESENTACION");
        String binario = textoABinario(mensaje);
        System.out.println("Texto en ASCII binario: " + binario);

        // ─── CAPA ENLACE ─────────────────────────────────
        System.out.println("\n CAPA ENLACE");
        System.out.println("Seleccione el tipo de transmisión:");
        System.out.println("1. Corrección de errores (Hamming 11,7)");
        System.out.println("2. Detección de errores (Fletcher Checksum en Python)");
        System.out.print("Opción: ");
        int opcion = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        String tramaCodificada = "";

        if (opcion == 1) {
            // Hamming
            List<String> bloques7 = new ArrayList<>();
            for (int i = 0; i < binario.length(); i += 7) {
                String bloque = binario.substring(i, Math.min(i + 7, binario.length()));
                if (bloque.length() < 7) {
                    bloque = String.format("%-7s", bloque).replace(' ', '0');
                }
                bloques7.add(bloque);
            }

            System.out.println("\n[ENLACE] Hamming 11,7:");
            List<String> codificados = new ArrayList<>();
            for (String bloque : bloques7) {
                String codificado = EmisorHamming.codificarBloque(bloque);
                System.out.println("Bloque 7 bits: " + bloque + " → Codificado (11 bits): " + codificado);
                codificados.add(codificado);
            }

            StringBuilder sb = new StringBuilder();
            for (String bloque : codificados) {
                sb.append(bloque);
            }
            tramaCodificada = sb.toString();

        } else if (opcion == 2) {
            // Ejecutar emisor.py desde Java
            System.out.println("\n[ENLACE] Ejecutando emisor.py (Fletcher Checksum):");

            ProcessBuilder pb = new ProcessBuilder("python", "deteccion/emisor.py");
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process proceso = pb.start();
            proceso.waitFor();

            System.out.println("[Python finalizado]");

            System.out.println("NOTA: Este flujo es externo, la trama binaria con checksum se muestra en el script.");
            System.out.print("¿Desea continuar con la capa de ruido? (s/n): ");
            if (!sc.nextLine().trim().toLowerCase().equals("s")) {
                System.out.println("Finalizando...");
                return;
            }

            System.out.print("Pegue aquí la trama resultante del emisor.py: ");
            tramaCodificada = sc.nextLine().trim();
        } else {
            System.out.println("Opción no válida.");
            return;
        }

        // ─── CAPA RUIDO ─────────────────────────────────
        System.out.println("\n CAPA RUIDO");
        System.out.print("Ingrese probabilidad de error por bit (ej. 0.01): ");
        double probabilidadError = sc.nextDouble();

        String tramaConRuido = aplicarRuido(tramaCodificada, probabilidadError);
        System.out.println("Trama original:      " + tramaCodificada);
        System.out.println("Trama con ruido:     " + tramaConRuido);

        // ─── CAPA TRANSMISION ─────────────────────────────────
        System.out.println("\n CAPA TRANSMISION");
        System.out.print("Ingrese el puerto para transmisión (ej. 6543): ");
        int puerto = sc.nextInt();

        try (java.net.Socket socket = new java.net.Socket("127.0.0.1", puerto);
                OutputStream out = socket.getOutputStream()) {

            // Agregamos un prefijo para que el receptor sepa que es Hamming
            String payload = "HAM:" + tramaConRuido;
            out.write(payload.getBytes());
            out.flush();

            System.out.println(" Trama enviada al receptor Python.");
        } catch (IOException e) {
            System.err.println(" Error al enviar la trama: " + e.getMessage());
        }

    }
}
