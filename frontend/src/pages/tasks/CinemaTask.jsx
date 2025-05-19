import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import ApiCall, { baseUrl } from "../config/index";

const LOCAL_STORAGE_KEY = "cinemaTaskState";

const CinemaTask = () => {
  const [selectedTask, setSelectedTask] = useState(null);
  const [startTime, setStartTime] = useState(null);
  const [completedTasks, setCompletedTasks] = useState([]);
  const [step, setStep] = useState("start");
  const [errorMessage, setErrorMessage] = useState("");
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const savedSelectedTaskIdRef = useRef(null);

  const navigate = useNavigate();

  // Загрузка состояния из localStorage
  useEffect(() => {
    const savedState = localStorage.getItem(LOCAL_STORAGE_KEY);
    if (savedState) {
      try {
        const { selectedTaskId, startTime, step, completedTasks } =
          JSON.parse(savedState);
        savedSelectedTaskIdRef.current = selectedTaskId;
        setStartTime(startTime || null);
        setStep(step || "start");
        setCompletedTasks(completedTasks || []);
      } catch {
        // ignore malformed data
      }
    }
  }, []);

  // Загрузка заданий и восстановление выбранного
  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const response = await ApiCall("/api/v1/task-cinema", "GET");

        if (!response.error) {
          const mappedTasks = response.data
            .filter((task) => task.status)
            .map((task) => ({
              id: task.id,
              title: task.title,
              description: task.description,
              reward: task.coinCount,
              videoUrl: task.url,
              duration: task.seconds,
              status: task.status,
              photo: task.mainPhoto
                ? `${baseUrl}/api/v1/file/getFile/${task.mainPhoto.id}`
                : "",
            }));

          setTasks(mappedTasks);

          if (savedSelectedTaskIdRef.current) {
            const foundTask = mappedTasks.find(
              (task) => task.id === savedSelectedTaskIdRef.current
            );
            if (foundTask) {
              setSelectedTask(foundTask);
            }
            savedSelectedTaskIdRef.current = null;
          }
        } else {
          console.error("Ошибка сервера:", response.data);
        }
      } catch (error) {
        console.error("Ошибка при загрузке заданий:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTasks();
  }, []);

  // Сохраняем состояние в localStorage
  useEffect(() => {
    const stateToSave = {
      selectedTaskId: selectedTask?.id || null,
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
      setErrorMessage("Пожалуйста, сначала нажмите 'Start'.");
      return;
    }

    const elapsed = (Date.now() - startTime) / 1000;
    const required = selectedTask?.duration || 0;

    if (elapsed >= required) {
      if (!completedTasks.includes(selectedTask.id)) {
        const updatedCompleted = [...completedTasks, selectedTask.id];
        setCompletedTasks(updatedCompleted);
        localStorage.setItem(
          LOCAL_STORAGE_KEY,
          JSON.stringify({
            selectedTaskId: null,
            startTime: null,
            step: "start",
            completedTasks: updatedCompleted,
          })
        );
      }

      setErrorMessage("");
      alert(`Награда ${selectedTask.reward} монет получена!`);
      setSelectedTask(null);
      setStep("start");
      setStartTime(null);
    } else {
      const left = Math.ceil(required - elapsed);
      setErrorMessage(`Нужно досмотреть ещё ${left} секунд.`);
    }
  };

  return (
    <div className="px-4 py-4 mb-20">
      {!selectedTask && (
        <div className="mb-6">
          <p className="text-center font-bold text-lg mb-2 text-gray-800">
            Награда за 10 просмотров
          </p>
          <div className="flex justify-center gap-2 mb-3">
            {Array.from({ length: 10 }).map((_, idx) => (
              <div
                key={idx}
                className={`w-6 h-6 rounded-sm ${
                  idx < completedTasks.length ? "bg-yellow-400" : "bg-gray-300"
                }`}
              ></div>
            ))}
          </div>
          <div className="flex justify-center items-center gap-2">
            <span className="text-yellow-600 font-semibold">10,000 монет</span>
          </div>
        </div>
      )}

      {!selectedTask && (
        <div className="space-y-4">
          {tasks.map((task, idx) => (
            <div
              key={task.id}
              className={`bg-white rounded-xl shadow p-4 cursor-pointer hover:shadow-md transition ${
                completedTasks.includes(task.id) ? "opacity-60" : ""
              }`}
              onClick={() => {
                setSelectedTask(task);
                setErrorMessage("");
                if (task.id !== savedSelectedTaskIdRef.current) {
                  setStep("start");
                  setStartTime(null);
                }
              }}
            >
              <p className="text-sm font-semibold text-gray-600 mb-1">
                Задание №{idx + 1}
              </p>
              {task.photo && (
                <img
                  src={task.photo}
                  alt="task"
                  className="w-full h-48 object-cover rounded-lg mb-2"
                />
              )}
              <h3 className="font-semibold text-lg text-gray-800">
                {task.title}
              </h3>
              <div className="flex justify-between items-center gap-2 mt-2">
                <span className="text-sm text-gray-700">
                  {task.reward} монет
                </span>
                {completedTasks.includes(task.id) && (
                  <span className="text-green-500">выполнено!</span>
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
            className="font-bold my-4 text-xl text-gray-500 hover:underline"
          >
            ← Назад
          </button>
          <h2 className="text-xl font-bold text-gray-800 mb-2">
            Задание №{tasks.findIndex((t) => t.id === selectedTask.id) + 1}:{" "}
            {selectedTask.title}
          </h2>
          {selectedTask.photo && (
            <img src={selectedTask.photo} alt="" className="mb-4" />
          )}
          <p className="text-gray-600 font-semibold mb-6">
            {selectedTask.description}
          </p>

          {step === "start" && (
            <button
              onClick={handleStart}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg shadow hover:bg-blue-700 transition"
            >
              Start
            </button>
          )}

          {step === "watch" && (
            <div className="flex items-center justify-between gap-2">
              <p>Смотрите видео</p>
              <button
                onClick={handleWatch}
                className="bg-yellow-500 text-white px-4 py-2 rounded-lg shadow hover:bg-yellow-600 transition"
              >
                Watch
              </button>
            </div>
          )}

          {step === "check" && (
            <>
              <div className="flex items-center justify-between gap-2">
                <p>Проверка просмотра</p>
                <button
                  onClick={handleCheck}
                  className="bg-yellow-500 text-white px-4 py-2 rounded-lg shadow hover:bg-yellow-600 transition"
                >
                  Check
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
