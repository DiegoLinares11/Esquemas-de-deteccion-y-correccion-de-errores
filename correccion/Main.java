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
        int bloqueBits = -1;

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
                String codificado = emisorHamming.codificarBloque(bloque);
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

            // Ejecutar el script con la trama binaria como argumento
            ProcessBuilder pb = new ProcessBuilder("py", "../deteccion/emisor.py", binario.toString());
            pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectErrorStream(true);                         

            Process proceso = pb.start();

            // Capturar y leer la salida del script
            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            tramaCodificada = null;

            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);  

                if (linea.startsWith("Trama con checksum final: ")) {
                    tramaCodificada = linea.replace("Trama con checksum final: ", "").trim();
                }

                if (linea.startsWith("Bloque usado: ")) {  
                    try {
                        String valor = linea.replace("Bloque usado: ", "").replace(" bits", "").trim();
                        bloqueBits = Integer.parseInt(valor);
                        System.out.println("bloqueBits");
                    } catch (NumberFormatException e) {
                        System.out.println("Error al interpretar el bloque desde la salida del emisor.");
                        return;
                    }
                }
            }

            proceso.waitFor();

            System.out.println("[Python finalizado]");

            if (tramaCodificada == null || bloqueBits == -1) {
                System.out.println("Error: no se pudo capturar la trama codificada o el bloque desde emisor.py.");
                return;
            }

            System.out.println("Trama capturada desde emisor.py: " + tramaCodificada);
            System.out.println("Bloque capturado desde emisor.py: " + bloqueBits + " bits"); // 🆕 Confirmación
        }else {
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

        int puerto;
        String payload;

        if (opcion == 1) {
            puerto = 6543;
            payload = "HAM:" + tramaConRuido;
        } else if (opcion == 2) {
            puerto = 6544;
            payload = "FLE:" + bloqueBits + ":" + tramaConRuido;
        } else {
            System.out.println("Error: opción no válida para transmisión.");
            return;
        }

        try (java.net.Socket socket = new java.net.Socket("127.0.0.1", puerto);
            OutputStream out = socket.getOutputStream()) {
            
            out.write(payload.getBytes());
            out.flush();

            System.out.println(" Trama enviada al receptor en el puerto " + puerto + ".");
        } catch (IOException e) {
            System.err.println(" Error al enviar la trama: " + e.getMessage());
        }


    }
}
