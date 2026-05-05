# Pixel Platformer 🎮

Простая пиксельная платформенная игра на Java + Swing.

## Структура
src/
├── main.Game.java # Точка входа, окно
├── core.GamePanel.java # Игровой цикл, отрисовка
├── entities.Player.java # Игрок (движение, прыжки, гравитация)
├── entities.Platform.java # Платформы
├── entities.Coin.java # Монетки
└── core.InputHandler.java # Обработка клавиш

resources/
└── sprites/
└── player_sprite.png # Спрайт игрока (опционально)


## Управление

| Клавиша      | Действие |
|--------------|----------|
| ← / A        | Движение влево |
| → / D        | Движение вправо |
| Пробел / W   | Прыжок |

## Запуск
javac -d out src/.java
cp -r resources/ out/
java -cp out main.Game

## Принципы

Код следует **SOLID** (особенно **S** — единственная ответственность):

| Класс | Что делает |
|-------|------------|
| `main.Game` | Окно и запуск |
| `core.GamePanel` | Цикл, отрисовка, связь компонентов |
| `entities.Player` | Только логика игрока |
| `entities.Platform` | Только платформа |
| `entities.Coin` | Только монетка |
| `core.InputHandler` | Только клавиши |