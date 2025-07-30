import java.util.Scanner;

public class emisorHamming {

    public static String codificarBloque(String bloque7) {
        int[] bits = new int[12]; // posición 0 no se usa
        int j = 0;

        // colocar bits de datos en posiciones correctas
        for (int i = 1; i <= 11; i++) {
            if (i != 1 && i != 2 && i != 4 && i != 8) {
                bits[i] = bloque7.charAt(j++) - '0';
            }
        }

        // calcular bits de paridad
        bits[1] = bits[3] ^ bits[5] ^ bits[7] ^ bits[9] ^ bits[11];
        bits[2] = bits[3] ^ bits[6] ^ bits[7] ^ bits[10] ^ bits[11];
        bits[4] = bits[5] ^ bits[6] ^ bits[7];
        bits[8] = bits[9] ^ bits[10] ^ bits[11];

        // construir bloque codificado
        StringBuilder codificado = new StringBuilder();
        for (int i = 1; i <= 11; i++) {
            codificado.append(bits[i]);
        }
        return codificado.toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese un mensaje binario de cualquier longitud: ");
        String mensaje = sc.nextLine().trim();

        if (!mensaje.matches("[01]+")) {
            System.out.println("Mensaje inválido. Solo se permiten 0s y 1s.");
            return;
        }

        // Rellenar si no es múltiplo de 7
        int longitud = mensaje.length();
        int bloques = (int) Math.ceil(longitud / 7.0);

        // Procesar por bloques de 7 bits
        System.out.println("\nBloques codificados (11 bits por bloque):");
        for (int i = 0; i < bloques; i++) {
            int inicio = i * 7;
            int fin = Math.min(inicio + 7, mensaje.length());
            String bloque = mensaje.substring(inicio, fin);

            // rellenar con ceros si es menor a 7 bits
            if (bloque.length() < 7) {
                bloque = String.format("%-7s", bloque).replace(' ', '0');
            }

            String bloqueCodificado = codificarBloque(bloque);
            System.out.println("Bloque original: " + bloque + " -> Codificado: " + bloqueCodificado);
        }
    }
}
