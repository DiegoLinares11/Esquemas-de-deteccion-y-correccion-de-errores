import socket

def calcular_sindrome(bits):
    s1 = bits[1] ^ bits[3] ^ bits[5] ^ bits[7] ^ bits[9] ^ bits[11]
    s2 = bits[2] ^ bits[3] ^ bits[6] ^ bits[7] ^ bits[10] ^ bits[11]
    s4 = bits[4] ^ bits[5] ^ bits[6] ^ bits[7]
    s8 = bits[8] ^ bits[9] ^ bits[10] ^ bits[11]
    return s8 << 3 | s4 << 2 | s2 << 1 | s1

def extraer_datos(bits):
    return [bits[3], bits[5], bits[6], bits[7], bits[9], bits[10], bits[11]]

def binario_a_texto(binario):
    # Asegurar que la longitud sea múltiplo de 8 para decodificar ASCII
    if len(binario) % 8 != 0:
        binario = binario[:-(len(binario) % 8)]
    texto = ""
    for i in range(0, len(binario), 8):
        byte = binario[i:i+8]
        texto += chr(int(byte, 2))
    return texto

def procesar_trama(trama):
    """Procesa una trama Hamming concatenada, separándola en bloques de 11 bits."""
    mensaje_final = []
    bloques = [trama[i:i+11] for i in range(0, len(trama), 11)]

    for i, mensaje in enumerate(bloques, 1):
        if len(mensaje) != 11 or not all(c in '01' for c in mensaje):
            print(f"Bloque {i} inválido. Se omite.")
            continue

        bits = [0]
        bits.extend(int(b) for b in mensaje)

        sindrome = calcular_sindrome(bits)

        if sindrome == 0:
            print(f"[Bloque {i}] No se detectaron errores.")
        elif 1 <= sindrome <= 11:
            print(f"[Bloque {i}] Error detectado en posición {sindrome}. Corrigiendo...")
            bits[sindrome] ^= 1
            print("Trama corregida:", ''.join(str(bits[i]) for i in range(1, 12)))
        else:
            print(f"[Bloque {i}] Error no corregible. Se omite.")
            continue

        datos = extraer_datos(bits)
        print("Datos originales:", ''.join(str(b) for b in datos))
        mensaje_final.extend(datos)

    if mensaje_final:
        binario_final = ''.join(str(b) for b in mensaje_final)
        print("\n Mensaje reconstruido completo (binario):")
        print(binario_final)

        # Convertir a texto
        texto = binario_a_texto(binario_final)
        print("\n Mensaje decodificado:")
        print(texto)
    else:
        print("\n No se pudo reconstruir el mensaje.")


def escuchar_socket():
    HOST = '127.0.0.1'
    PORT = 6543
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen(1)
        print(f"[RECEPTOR] Esperando conexión en {HOST}:{PORT}...")
        conn, addr = s.accept()
        with conn:
            print(f"[RECEPTOR] Conectado desde {addr}")
            data = conn.recv(4096).decode()
            print(f"[RECEPTOR] Trama recibida: {data}")

            # Verificar prefijo
            if data.startswith("HAM:"):
                procesar_trama(data[4:])
            else:
                print("[INFO] Trama sin prefijo o no es Hamming. No procesada.")



if __name__ == "__main__":
    escuchar_socket()
