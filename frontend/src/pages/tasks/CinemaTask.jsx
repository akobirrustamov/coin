import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const sampleTasks = [
  {
    id: 1,
    title: "Как работает блокчейн",
    description: "Обзор блокчейна и его ключевых компонентов.",
    reward: 500,
    videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4",
    duration: 10,
  },
  {
    id: 2,
    title: "Введение в криптовалюты",
    description: "Основы криптовалют и способы их использования.",
    reward: 500,
    videoUrl: "https://www.w3schools.com/html/movie.mp4",
    duration: 8,
  },
  {
    id: 3,
    title: "Введение в криптовалюты",
    description: "Основы криптовалют и способы их использования.",
    reward: 500,
    videoUrl: "https://www.w3schools.com/html/movie.mp4",
    duration: 8,
  },
];

const LOCAL_STORAGE_KEY = "cinemaTaskState";

const CinemaTask = () => {
  const [selectedTask, setSelectedTask] = useState(null);
  const [startTime, setStartTime] = useState(null);
  const [completedTasks, setCompletedTasks] = useState([]);
  const [step, setStep] = useState("start"); // start | watch | check
  const [errorMessage, setErrorMessage] = useState("");

  const navigate = useNavigate();

  // Загрузка состояния из localStorage при монтировании
  useEffect(() => {
    const savedState = localStorage.getItem(LOCAL_STORAGE_KEY);
    if (savedState) {
      try {
        const { selectedTaskId, startTime, step, completedTasks } =
          JSON.parse(savedState);

        if (selectedTaskId) {
          const task = sampleTasks.find((t) => t.id === selectedTaskId);
          if (task) {
            setSelectedTask(task);
          }
        }
        setStartTime(startTime || null);
        setStep(step || "start");
        setCompletedTasks(completedTasks || []);
      } catch {
        // Некорректные данные, игнорируем
      }
    }
  }, []);

  // Сохраняем состояние в localStorage при изменениях
  useEffect(() => {
    const stateToSave = {
      selectedTaskId: selectedTask ? selectedTask.id : null,
      startTime,
      step,
      completedTasks,
    };
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(stateToSave));
  }, [selectedTask, startTime, step, completedTasks]);

  const handleStart = () => {
    setStartTime(Date.now());
    setStep("watch");
  };

  const handleWatch = () => {
    if (selectedTask?.videoUrl) {
      window.open(selectedTask.videoUrl, "_blank");
      setStep("check");
    }
  };

  const handleCheck = () => {
    if (!startTime) {
      setErrorMessage("Пожалуйста, сначала нажмите 'start'.");
      return;
    }
    const elapsed = (Date.now() - startTime) / 1000;
    if (elapsed >= (selectedTask?.duration || 0)) {
      if (!completedTasks.includes(selectedTask.id)) {
        setCompletedTasks([...completedTasks, selectedTask.id]);
      }
      setErrorMessage("");
      alert(`Награда ${selectedTask.reward} монет получена!`);
      setSelectedTask(null);
      setStep("start");
      setStartTime(null);
    } else {
      setErrorMessage(
        "Видео должно быть просмотрено полностью на скорости 1.0x."
      );
    }
  };

  return (
    <div className="px-4 py-4">
      {!selectedTask && (
        <>
          <div className="mb-6">
            <p className="text-center font-bold text-lg mb-2 text-gray-800">
              Награда за 10 просмотров
            </p>
            <div className="flex justify-center gap-2 mb-3">
              {Array.from({ length: 10 }).map((_, idx) => (
                <div
                  key={idx}
                  className={`w-6 h-6 rounded-sm ${
                    idx < completedTasks.length
                      ? "bg-yellow-400"
                      : "bg-gray-300"
                  }`}
                ></div>
              ))}
            </div>
            <div className="flex justify-center items-center gap-2">
              <span className="text-yellow-600 font-semibold">
                10,000 монет
              </span>
            </div>
          </div>
        </>
      )}

      {!selectedTask && (
        <div className="space-y-4">
          {sampleTasks.map((task, idx) => (
            <div
              key={task.id}
              className={`bg-white rounded-xl shadow p-4 cursor-pointer hover:shadow-md transition ${
                completedTasks.includes(task.id) ? "opacity-60" : ""
              }`}
              onClick={() => setSelectedTask(task)}
            >
              <p className="text-sm text-gray-400 mb-1">Задание #{idx + 1}</p>
              <h3 className="font-semibold text-lg text-gray-800">
                {task.title}
              </h3>
              <div className="flex justify-between items-center gap-2 mt-2">
                <span className="text-sm text-gray-700">
                  {task.reward} монет
                </span>
                {completedTasks.includes(task.id) ? (
                  <span className="text-green-500">bajarildi!</span>
                ) : (
                  ""
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedTask && (
        <div className="bg-white rounded-xl shadow p-6">
          <button
            onClick={() => {
              setSelectedTask(null);
              setStep("start");
              setErrorMessage("");
              setStartTime(null);
            }}
            className="text-sm text-gray-500 hover:underline"
          >
            ←
          </button>
          <h2 className="text-xl font-bold text-gray-800 mb-2">
            Задание #
            {sampleTasks.findIndex((t) => t.id === selectedTask.id) + 1}:{" "}
            {selectedTask.title}
          </h2>
          <p className="text-gray-600 mb-6">{selectedTask.description}</p>

          {step === "start" && (
            <button
              onClick={handleStart}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg shadow hover:bg-blue-700 transition"
            >
              start
            </button>
          )}

          {step === "watch" && (
            <div className="flex items-center gap-2">
              <p>Videoni kuring</p>
              <button
                onClick={handleWatch}
                className="bg-yellow-500 text-white px-4 py-2 rounded-lg shadow hover:bg-yellow-600 transition"
              >
                watch
              </button>
            </div>
          )}

          {step === "check" && (
            <>
              <div className="flex items-center justify-between gap-2">
                <p>Videoni kuring</p>
                <button
                  onClick={handleCheck}
                  className="bg-yellow-500 text-white px-4 py-2 rounded-lg shadow hover:bg-yellow-600 transition"
                >
                  check
                </button>
              </div>
              {errorMessage && (
                <p className="text-red-500 text-sm mt-2">{errorMessage}</p>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default CinemaTask;
