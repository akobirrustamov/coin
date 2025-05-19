// Tasks.jsx
import React from "react";
import { Outlet } from "react-router-dom";
import TaskTabs from "./TaskTabs";

const Tasks = () => {
  return (
    <div className="p-4">
      {/* Вкладки */}
      <TaskTabs />

      {/* Контент ниже */}
      <div className="mt-4">
        <Outlet />
      </div>
    </div>
  );
};

export default Tasks;
