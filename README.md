### Correcion de errores 

#### Emisor

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

#### Receptor

El receptor toma como entrada una trama binaria de 11 bits (generada por el emisor), por ejemplo:
```bash
10111000111
```
Notar que aca se metio un error en la posicion 11 a proposito para que lo corrija.

El receptor:
1. Calcula nuevamente las paridades y genera un síndrome, que indica la posición del bit erróneo (si hay uno).
2. Si el síndrome indica una posición válida (1–11), el receptor corrige el bit en esa posición.
3. Extrae los 7 bits de datos originales y los muestra.

En el ejemplo anterior, el receptor detecta un error en la posición 11, lo corrige y recupera los datos originales:
```bash
1100110
```
