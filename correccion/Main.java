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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // CAPA APLICACIÓN
        System.out.println(" CAPA APLICACION");
        System.out.print("Ingrese el mensaje a enviar: ");
        String mensaje = sc.nextLine();

        // CAPA PRESENTACIÓN
        System.out.println("\n CAPA PRESENTACION");
        String binario = textoABinario(mensaje);
        System.out.println("Texto en ASCII binario: " + binario);

        // Agrupar en bloques de 7 bits
        List<String> bloques7 = new ArrayList<>();
        for (int i = 0; i < binario.length(); i += 7) {
            String bloque = binario.substring(i, Math.min(i + 7, binario.length()));
            if (bloque.length() < 7) {
                bloque = String.format("%-7s", bloque).replace(' ', '0'); // rellenar con ceros
            }
            bloques7.add(bloque);
        }

        // CAPA ENLACE: aplicar Hamming a cada bloque
        System.out.println("\nCAPA ENLACE (Hamming 11,7)");
        List<String> bloquesCodificados = new ArrayList<>();
        for (String bloque : bloques7) {
            String codificado = EmisorHamming.codificarBloque(bloque);
            System.out.println("Bloque 7 bits: " + bloque + " → Codificado (11 bits): " + codificado);
            bloquesCodificados.add(codificado);
        }

        // Unir todos los bloques codificados
        StringBuilder tramaTotal = new StringBuilder();
        for (String codificado : bloquesCodificados) {
            tramaTotal.append(codificado);
        }

        // CAPA RUIDO: aplicar probabilidad de error
        System.out.println("\n CAPA RUIDO");
        System.out.print("Ingrese probabilidad de error por bit (ej. 0.01): ");
        double probabilidadError = sc.nextDouble();

        String tramaConRuido = aplicarRuido(tramaTotal.toString(), probabilidadError);
        System.out.println("Trama original:      " + tramaTotal);
        System.out.println("Trama con ruido:     " + tramaConRuido);

        // Aqui es lo que se va a implementar despuesss.
    }
}
