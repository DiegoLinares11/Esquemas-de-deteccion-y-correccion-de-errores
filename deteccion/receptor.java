import java.util.Scanner;

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

        int modulo = 255;
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
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Receptor Fletcher Checksum ===\n");

        while (true) {
            System.out.print("Ingrese la trama recibida (solo 0s y 1s): ");
            String trama = sc.nextLine().trim();

            if (!esBinario(trama)) {
                System.out.println("Error: la trama solo puede contener 0s y 1s.\n");
                continue;
            }

            System.out.print("Tamaño del bloque (8, 16 o 32): ");
            int bloque;
            try {
                bloque = Integer.parseInt(sc.nextLine());
                if (bloque != 8 && bloque != 16 && bloque != 32) {
                    System.out.println("Error: solo se permiten tamaños de bloque 8, 16 o 32.\n");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar un número entero.\n");
                continue;
            }

            verificarTrama(trama, bloque);

            System.out.print("\n¿Desea verificar otra trama? (s/n): ");
            String respuesta = sc.nextLine().trim().toLowerCase();
            if (!respuesta.equals("s")) {
                System.out.println("Finalizando receptor.");
                break;
            }

            System.out.println();
        }

        sc.close();
    }
}
