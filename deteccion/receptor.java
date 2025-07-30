import java.io.*;
import java.net.*;

public class receptor {

    public static boolean esBinario(String cadena) {
        return cadena.matches("[01]+");
    }

    public static void verificarTrama(String trama, int bloqueBits) {
        int longitudChecksum = bloqueBits * 2;

        if (trama.length() <= longitudChecksum) {
            System.out.println("Error: la trama es demasiado corta para contener datos + checksum.");
            return;
        }

        String datos = trama.substring(0, trama.length() - longitudChecksum);
        String checksumRecibido = trama.substring(trama.length() - longitudChecksum);

        while (datos.length() % bloqueBits != 0) {
            datos += "0";
        }

        int[] bloques = new int[datos.length() / bloqueBits];
        for (int i = 0; i < bloques.length; i++) {
            String bloque = datos.substring(i * bloqueBits, (i + 1) * bloqueBits);
            bloques[i] = Integer.parseInt(bloque, 2);
        }

        int modulo = (int) Math.pow(2, bloqueBits) - 1;
        int sum1 = 0, sum2 = 0;
        for (int val : bloques) {
            sum1 = (sum1 + val) % modulo;
            sum2 = (sum2 + sum1) % modulo;
        }

        String sum1Bin = String.format("%" + bloqueBits + "s", Integer.toBinaryString(sum1)).replace(" ", "0");
        String sum2Bin = String.format("%" + bloqueBits + "s", Integer.toBinaryString(sum2)).replace(" ", "0");
        String checksumCalculado = sum1Bin + sum2Bin;

        System.out.println("\nChecksum recibido:  " + checksumRecibido);
        System.out.println("Checksum calculado: " + checksumCalculado);

        if (checksumRecibido.equals(checksumCalculado)) {
            System.out.println("Trama recibida correctamente: " + datos);
        } else {
            System.out.println("Error detectado. Trama descartada.");
        }
    }

    public static void main(String[] args) {
    System.out.println("=== Receptor Fletcher Checksum ===\n");

    try (ServerSocket serverSocket = new ServerSocket(6544)) {
        System.out.println("Esperando mensaje a puerto 6544...");

        try (Socket socket = serverSocket.accept()) {
            System.out.println("Conexión recibida de " + socket.getInetAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String recibido = reader.readLine();

            if (recibido == null || !recibido.startsWith("FLE:")) {
                System.out.println("Trama no reconocida o sin prefijo FLE:");
                return;
            }

            String[] partes = recibido.split(":", 3); // FLE:bloqueBits:trama

            if (partes.length != 3) {
                System.out.println("Error: formato de trama no válido.");
                return;
            }

            int bloqueBits;
            String trama;

            try {
                bloqueBits = Integer.parseInt(partes[1].trim());
                trama = partes[2].trim();
            } catch (NumberFormatException e) {
                System.out.println("Error: el valor del bloque recibido no es válido.");
                return;
            }

            System.out.println("Bloque de verificación recibido: " + bloqueBits + " bits");

            if (!esBinario(trama)) {
                System.out.println("Error: la trama contiene caracteres no binarios.");
                return;
            }

            verificarTrama(trama, bloqueBits);
        }

    } catch (IOException e) {
        System.err.println("Error en el servidor receptor: " + e.getMessage());
    }
}

}
