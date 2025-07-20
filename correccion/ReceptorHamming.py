def calcular_sindrome(bits):
    s1 = bits[1] ^ bits[3] ^ bits[5] ^ bits[7] ^ bits[9] ^ bits[11]
    s2 = bits[2] ^ bits[3] ^ bits[6] ^ bits[7] ^ bits[10] ^ bits[11]
    s4 = bits[4] ^ bits[5] ^ bits[6] ^ bits[7]
    s8 = bits[8] ^ bits[9] ^ bits[10] ^ bits[11]
    return s8 << 3 | s4 << 2 | s2 << 1 | s1

def extraer_datos(bits):
    return [bits[3], bits[5], bits[6], bits[7], bits[9], bits[10], bits[11]]

def main():
    mensaje = input("Ingrese el mensaje recibido (11 bits): ").strip()
    if len(mensaje) != 11 or not all(c in '01' for c in mensaje):
        print("Mensaje inválido.")
        return

    bits = [0]  # posición 0 no se usa
    bits.extend(int(b) for b in mensaje)

    sindrome = calcular_sindrome(bits)

    if sindrome == 0:
        print("No se detectaron errores.")
        print("Datos originales:", ''.join(str(b) for b in extraer_datos(bits)))
    elif 1 <= sindrome <= 11:
        print(f"Error detectado en la posición {sindrome}. Corrigiendo...")
        bits[sindrome] ^= 1
        print("Trama corregida:", ''.join(str(bits[i]) for i in range(1, 12)))
        print("Datos originales:", ''.join(str(b) for b in extraer_datos(bits)))
    else:
        print("Error detectado pero no corregible.")

if __name__ == "__main__":
    main()
