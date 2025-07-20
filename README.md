### Correcion de errores 

Emisor

El emisor toma como entrada un mensaje binario de 7 bits ingresado por el usuario, por ejemplo:
```bash
1100110
```
Para proteger el mensaje contra errores, el emisor utiliza el algoritmo de Hamming (11,7), que consiste en:
1. Reservar 4 posiciones en la trama para bits de paridad (posiciones 1, 2, 4 y 8).
2. Colocar los 7 bits de datos originales en las otras posiciones (3, 5, 6, 7, 9, 10, 11).
3. Calcular los bits de paridad de forma que, si durante la transmisión un solo bit cambia, la combinación de las paridades permitirá al receptor localizar y corregir el error.

Por ejemplo, para el mensaje 1100110, el emisor genera la trama codificada:
```bash
10111000110
```
Que contiene los datos originales más los bits de paridad calculados.
