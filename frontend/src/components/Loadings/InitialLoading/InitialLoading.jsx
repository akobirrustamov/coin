import React, { useState, useEffect } from "react";
import bg2 from "../img/bg2.png"; // Фоновое изображение
import books from "../img/books.png"; // Книги
import hat from "../img/hat.png"; // Шляпа
import tasks from "../img/tasks.png"; // Задачи
import triangle from "../img/triangle.png"; // Треугольник

const loadingMessages = [
  "Собираем расписание...",
  "Загружаем лекции...",
  "Настраиваем сессии...",
  "Считаем баллы...",
  "Почти готово...",
];

const icons = [hat, books, tasks, triangle]; // Массив импортированных картинок

export default function InitialLoading({ onLoaded }) {
  const [progress, setProgress] = useState(0);
  const [frame, setFrame] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setProgress((prev) => {
        const next = prev + 1;
        if (next >= 100) {
          clearInterval(interval);
          if (onLoaded) onLoaded();
        }
        return next;
      });
    }, 40);
    return () => clearInterval(interval);
  }, [onLoaded]);

  useEffect(() => {
    const cycle = setInterval(() => {
      setFrame((prev) => (prev + 1) % icons.length);
    }, 1500);
    return () => clearInterval(cycle);
  }, []);

  const messageIndex = Math.min(
    Math.floor(progress / (100 / loadingMessages.length)),
    loadingMessages.length - 1
  );

  // Координаты для движения по квадрату с центром внизу по середине
  const positions = [
    { x: 0, y: 20 }, // нижняя (центр)
    { x: -60, y: -20 }, // левая верхняя
    { x: 0, y: -60 }, // верхняя (центр)
    { x: 60, y: -20 }, // правая верхняя
  ];

  return (
    <div
      style={{ backgroundImage: `url(${bg2})` }}
      className="bg-cover flex flex-col items-center justify-center h-screen bg-gradient-to-br from-gray-100 to-blue-100 font-sans overflow-hidden"
    >
      {/* Контейнер для иконок */}
      <div className="relative mb-8" style={{ width: 128, height: 128 }}>
        {icons.map((src, index) => {
          const relativeIndex = (index - frame + icons.length) % icons.length;
          const pos = positions[relativeIndex];
          const isActive = relativeIndex === 0;
          return (
            <img
              key={index}
              src={src}
              alt=""
              draggable={false}
              style={{
                position: "absolute",
                bottom: 0,
                left: "50%",
                transformOrigin: "center center",
                transform: `translate(calc(${pos.x}px - 50%), ${
                  pos.y
                }px) scale(${isActive ? 1 : 0.8})`,
                transition: "all 0.7s ease-in-out",
                width: isActive ? 64 : 52,
                height: "auto",
                opacity: isActive ? 1 : 0.6,
                zIndex: 10 - relativeIndex,
                userSelect: "none",
                pointerEvents: "none",
              }}
            />
          );
        })}
      </div>

      {/* Прогрессбар с градиентом */}
      <div className="w-4/5 h-5 bg-gray-100 rounded-full overflow-hidden mb-4 shadow-inner">
        <div
          className="h-full rounded-full transition-all duration-100 ease-linear bg-gradient-to-r from-blue-500 to-blue-900"
          style={{ width: `${progress}%` }}
        />
      </div>

      {/* Текст загрузки */}
      <div className="text-xl font-semibold text-white min-h-[24px] text-center">
        {loadingMessages[messageIndex]}
      </div>
    </div>
  );
}
