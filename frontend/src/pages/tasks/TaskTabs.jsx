import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import clsx from "clsx";

const tabs = [
  { label: "Cinema", route: "/tasks/cinema-task" },
  { label: "Special", route: "/tasks/special-task" },
  { label: "Level", route: "/tasks/level-task" },
];

const TaskTabs = () => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <div className="sticky top-0 z-30 bg-white py-2 px-2">
      <div className="flex justify-around bg-gray-100 p-2 rounded-full shadow-inner">
        {tabs.map((tab) => {
          const isActive =
            location.pathname === tab.route ||
            (tab.route.endsWith("cinema-task") && location.pathname === "/tasks");

          return (
            <button
              key={tab.route}
              onClick={() => navigate(tab.route)}
              className={clsx(
                "flex-1 text-center py-2 px-4 rounded-full transition-all duration-300 font-semibold text-sm",
                isActive
                  ? "bg-gradient-to-r from-purple-500 to-blue-500 text-white shadow-lg"
                  : "bg-transparent text-gray-600 hover:bg-gray-200"
              )}
            >
              {tab.label}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default TaskTabs;
