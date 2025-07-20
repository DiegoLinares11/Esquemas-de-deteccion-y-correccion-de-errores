import java.util.Scanner;

public class EmisorHamming {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese un mensaje binario de 7 bits: ");
        String mensaje = sc.nextLine();

        if (mensaje.length() != 7 || !mensaje.matches("[01]+")) {
            System.out.println("Mensaje inválido.");
            return;
        }

        int[] bits = new int[12]; // posición 0 no se usa
        int j = 0;

        // colocar bits de datos en posiciones correctas
        for (int i = 1; i <= 11; i++) {
            if (i != 1 && i != 2 && i != 4 && i != 8) {
                bits[i] = mensaje.charAt(j++) - '0';
            }
        }

        // calcular bits de paridad
        bits[1] = bits[3] ^ bits[5] ^ bits[7] ^ bits[9] ^ bits[11];
        bits[2] = bits[3] ^ bits[6] ^ bits[7] ^ bits[10] ^ bits[11];
        bits[4] = bits[5] ^ bits[6] ^ bits[7];
        bits[8] = bits[9] ^ bits[10] ^ bits[11];

        System.out.print("Mensaje codificado (11 bits): ");
        for (int i = 1; i <= 11; i++) {
            System.out.print(bits[i]);
        }
        System.out.println();
    }
}
