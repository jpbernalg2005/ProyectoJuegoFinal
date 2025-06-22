# 🐱 Atrapa al Gato

Proyecto desarrollado por **Jose Pablo Bernal** para el curso de Ingeniería de Software.  
El objetivo del juego es bloquear al gato antes de que escape del tablero.

---
## ▶️ Video 

https://youtu.be/kBf7dl14iAg

## 🎮 Descripción del Juego

"Atrapa al Gato" es un juego de estrategia donde el jugador bloquea celdas para atrapar a un gato que intenta escapar del tablero. El gato se mueve automáticamente usando una estrategia de búsqueda inteligente.

- El jugador bloquea una celda por turno.
- El gato se mueve luego de cada bloqueo.
- Si el gato llega al borde, el jugador pierde.
- Si el jugador lo rodea por completo, gana.

---

## 🧠 Lógica y Arquitectura

- **`HexGameService`**: lógica principal del juego.
- **`HexGameState`**: estado actual del tablero y del gato.
- **`HexGameBoard`**: representa las celdas y bloqueos.
- **`BFSCatMovement`**: movimiento automático del gato usando BFS.
- **`H2GameRepository`**: guarda las partidas en una base de datos H2 embebida.

---

## 🚀 Ejecución del Proyecto

### 🧱 Requisitos

- Java 17+
- Apache Maven

### ▶️ Instrucciones

```bash
git clone https://github.com/tu-usuario/atrapar-al-gato.git
cd atrapar-al-gato
mvn spring-boot:run
```

La aplicación se iniciará en `http://localhost:8080`.

---

## 🔧 Interacción

Puedes usar herramientas como Postman o Insomnia para interactuar con el juego.

### Endpoints útiles:

- `POST /games/new`: Crear una nueva partida
- `POST /games/{id}/move`: Bloquear una celda
- `GET /games/{id}`: Obtener estado del juego
- `GET /games/{id}/suggested`: Obtener movimiento sugerido del gato
- `GET /games/stats`: Ver estadísticas generales

---



## 🌟 Posibles Mejoras Futuras

- Mejorar la lógica de bloqueos por parte del jugador.
- Guardar y continuar partidas abandonadas.
- Visualización gráfica del tablero (JavaFX o web).
- Selección de dificultad y tamaño del tablero.
- Estadísticas más avanzadas desde la base de datos.

---

## 📁 Estructura del Proyecto

```
src/
├── base/
│   └── ... (interfaces y contratos)
├── impl/
│   ├── model/          # Tablero, posición, estado del juego
│   ├── strategy/       # Movimiento del gato (BFS)
│   ├── service/        # HexGameService (control central)
│   └── repository/     # Repositorio H2 para persistencia
```

---

## ✅ Autor

Jose Pablo Bernal – 2025  
Universidad Adolfo Ibáñez – Ingeniería
