import Sidebar from "../../Sidebar";
import React from "react";

function Ordinary() {
  return (
    <div className="flex min-h-screen p-6">
      <Sidebar />
      <div className="ml-64 p-8 w-full min-h-screen">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-blue-500">
            Video Topshiriqlar
          </h1>
          <button
            onClick={() => {
              setIsModalOpen(true);
              setEditingId(null);
              setFormData({
                title: "",
                description: "",
                mainPhoto: null,
              
              });
            }}
            className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md transition"
          >
            + Video Topshiriq Qo'shish
          </button>
        </div>
      </div>
    </div>
  );
}

export default Ordinary;
