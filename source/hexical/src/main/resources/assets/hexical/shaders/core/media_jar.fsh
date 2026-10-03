#version 330 core

in vec2 fragUV;
in vec2 quadSize;
in vec3 offset;

uniform float GameTime;

out vec4 fragColor;

// Хэш для псевдослучайного распределения пузырьков
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

// Генерация пузырьков, движущихся строго вверх
float bubbles(vec2 uv, float time, float seed) {
    vec2 id = floor(uv);
    vec2 f = fract(uv) - 0.5;
    float n = hash(id + seed);

    // Скорость зависит от хэша, чтобы пузырьки двигались неравномерно
    float speed = 0.15 + n * 0.25;
    float y = fract(n + time * speed) - 0.5;
    float x = (n - 0.5) * 0.8; // Разброс по горизонтали

    vec2 pos = vec2(x, y);
    float dist = length(f - pos);

    // Размер и мягкость пузырька
    float radius = 0.04 + n * 0.03;
    float bubble = smoothstep(radius, radius * 0.2, dist);

    return bubble * step(0.3, n);
}

void main() {
    // Время в секундах (непрерывный поток)
    float time = GameTime * 1200.0;
    vec2 uv = fragUV;

    // Уникальный seed для каждой банки (чтобы они не были клонами)
    float seed = offset.x * 10.0 + offset.y * 5.0 + offset.z * 2.0;

    // === 1. СПЛОШНОЙ ЦВЕТ С ЛЁГКОЙ ПУЛЬСАЦИЕЙ ===
    vec3 baseColor = vec3(0.55, 0.35, 0.85); // Аметистовый фиолетовый

    // Лёгкая пульсация яркости (едва заметное "дыхание")
    float pulse = 0.95 + 0.05 * sin(time * 0.5 + seed);
    baseColor *= pulse;

    // === 2. ПОЛУПРОЗРАЧНОСТЬ ===
    // Сплошная прозрачность для всей жидкости
    float alpha = 0.45;

    // === 3. ПУЗЫРЬКИ ===
    // UV-координаты для пузырьков: растягиваем по X и движем по Y
    vec2 bubbleUV = vec2(uv.x * 6.0, uv.y * 3.0 - time * 0.3);
    float bub = bubbles(bubbleUV, time * 0.1, seed);

    // Пузырьки добавляют яркое свечение и делают жидкость в этом месте почти непрозрачной
    baseColor += vec3(0.9, 0.7, 1.0) * bub * 0.8;
    alpha = mix(alpha, 0.95, bub);

    // Финальная отдача цвета и прозрачности
    fragColor = vec4(baseColor, alpha);
}