import React from "react";
import Sidebar from "../Sidebar";

function User() {
  return (
    <div className="flex min-h-screen p-6">
      <Sidebar />
      <div className="ml-64">User</div>
    </div>
  );
}

export default User;
