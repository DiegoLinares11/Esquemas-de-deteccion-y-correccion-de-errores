def fletcher_checksum_emisor(trama_binaria: str, bloque_bits: int = 8) -> str:
    while len(trama_binaria) % bloque_bits != 0:
        trama_binaria += '0'

    bloques = [
        int(trama_binaria[i:i + bloque_bits], 2)
        for i in range(0, len(trama_binaria), bloque_bits)
    ]

    modulo = 255
    sum1 = 0
    sum2 = 0

    for byte in bloques:
        sum1 = (sum1 + byte) % modulo
        sum2 = (sum2 + sum1) % modulo

    checksum_bin = format(sum1, f'0{bloque_bits}b') + format(sum2, f'0{bloque_bits}b')
    trama_con_checksum = trama_binaria + checksum_bin

    print(f"\nTrama original (con padding si aplica): {trama_binaria}")
    print(f"Checksum binario: {checksum_bin}")
    print(f"Trama a enviar: {trama_con_checksum}\n")
    print("\nBloques de datos en decimal:", bloques)
    print(f"sum1: {sum1} | sum2: {sum2}")

    return trama_con_checksum


def es_binario(cadena: str) -> bool:
    return all(c in '01' for c in cadena)


if __name__ == "__main__":
    print("=== Emisor Fletcher Checksum ===")
    while True:
        trama = input("Ingrese la trama binaria a enviar: ")
        if not es_binario(trama):
            print("Error: la trama solo puede contener 0s y 1s.\n")
            continue

        try:
            bloque = int(input("Tamaño del bloque (8, 16 o 32): "))
            if bloque not in (8, 16, 32):
                print("Error: tamaño de bloque no válido. Solo se permiten 8, 16 o 32.\n")
                continue
        except ValueError:
            print("Error: ingrese un número entero para el tamaño de bloque.\n")
            continue

        # Si ambas entradas son válidas, calcular y mostrar resultado
        fletcher_checksum_emisor(trama, bloque)

        # Preguntar si desea enviar otra trama
        otra = input("¿Desea enviar otra trama? (s/n): ").strip().lower()
        if otra != 's':
            print("Finalizando emisor.")
            break
