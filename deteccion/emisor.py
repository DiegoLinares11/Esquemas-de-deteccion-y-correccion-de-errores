import sys

def fletcher_checksum_emisor(trama_binaria: str, bloque_bits: int = 8) -> str:
    # Padding automático
    while len(trama_binaria) % bloque_bits != 0:
        trama_binaria += '0'

    # Dividir en bloques binarios y convertir a decimal
    bloques = [
        int(trama_binaria[i:i + bloque_bits], 2)
        for i in range(0, len(trama_binaria), bloque_bits)
    ]

    # Ajuste dinámico del módulo según bloque
    modulo = (2 ** bloque_bits) - 1
    sum1 = 0
    sum2 = 0

    for byte in bloques:
        sum1 = (sum1 + byte) % modulo
        sum2 = (sum2 + sum1) % modulo

    # Checksum final en binario
    checksum_bin = format(sum1, f'0{bloque_bits}b') + format(sum2, f'0{bloque_bits}b')
    trama_con_checksum = trama_binaria + checksum_bin

    # Salida formateada como en el emisorHamming.java
    print("\n=== Emisor Fletcher Checksum ===")
    print(f"sum1: {sum1} | sum2: {sum2}")
    print(f"Trama con checksum final: {trama_con_checksum}")

    return trama_con_checksum


def es_binario(cadena: str) -> bool:
    return all(c in '01' for c in cadena)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Error: no se proporcionó la trama binaria desde Java.")
        sys.exit(1)

    trama = sys.argv[1]

    if not es_binario(trama):
        print("Error: la trama solo puede contener 0s y 1s.")
        sys.exit(1)

    print("Seleccione el tamaño del bloque:")
    print("1. 8 bits")
    print("2. 16 bits")
    print("3. 32 bits")
    print("Opcion (1/2/3): ")
    opcion = input("").strip()

    if opcion == "1":
        bloque = 8
    elif opcion == "2":
        bloque = 16
    elif opcion == "3":
        bloque = 32
    else:
        print("Opcion invalida. Finalizando.")
        sys.exit(1)
        
    print(f"Bloque usado: {bloque} bits")

    fletcher_checksum_emisor(trama, bloque)
