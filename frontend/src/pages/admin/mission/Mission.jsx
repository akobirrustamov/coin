import React from "react";
import { Link } from "react-router-dom";
import Sidebar from "../Sidebar";

function Mission() {
  const files = [
    {
      link: "/dashboard/mission/cinema",
      name: "Video ko'rish",
    },
    {
      link: "/dashboard/mission/level",
      name: "Darajalar",
    },
    {
      link: "/dashboard/mission/special",
      name: "Obuna",
    },
    {
      link: "/dashboard/mission/ref",
      name: "Taklif",
    },
    {
      link: "/dashboard/mission/ordinary",
      name: "Maxsus",
    },
  ];

  return (
    <div className="flex min-h-screen p-6">
      <Sidebar />
      <div className="ml-64 p-8 w-full min-h-screen">
        <div className="max-w-6xl mx-auto">
          <h1 className="text-4xl font-bold mb-8 text-center text-gray-800">
            Topshiriqlar
          </h1>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {files.map((file) => (
              <Link
                key={file.link}
                to={file.link}
                className="block bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
              >
                <div className="p-4">
                  <div className="flex items-center">
                    <div className="p-3 rounded-lg bg-blue-100 text-blue-600 mr-4">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-6 w-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                        />
                      </svg>
                    </div>
                    <div>
                      <h3 className="text-xl font-semibold text-gray-800">
                        {file.name}
                      </h3>
                      <button className="text-blue-600 hover:text-blue-800 font-medium">
                        Yaratish →
                      </button>
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Mission;
