import React from "react";
import hat from "../img/hat.png"; // Импортируем шляпу
export default function ScreenLoading() {
  return (
    <div className="w-20 h-20 perspective-1000 flex items-center justify-center">
      <div
        className="relative w-16 h-16 rounded-full bg-yellow-400 text-white text-5xl font-bold flex items-center justify-center animate-spinY"
        style={{ transformStyle: "preserve-3d" }}
      >
        {/* Лицевая сторона */}
        <div
          className="absolute inset-0 flex items-center justify-center backface-hidden"
          style={{ backfaceVisibility: "hidden" }}
        >
          <img src={hat} alt="Hat" className="w-16 h-16" />
        </div>
        {/* Обратная сторона — зеркальное отображение */}
        <div
          className="absolute inset-0 flex items-center justify-center rotateY-180 backface-hidden"
          style={{ transform: "rotateY(180deg)", backfaceVisibility: "hidden" }}
        >
          <img src={hat} alt="Hat" className="w-16 h-16" />
        </div>
      </div>
    </div>
  );
}
