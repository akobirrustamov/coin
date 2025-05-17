import React from "react";
import Info from "../../icons/Info";
import Settings from "../../icons/Settings";
import Mine from "../../icons/Mine";
import Friends from "../../icons/Friends";
function BottomNavigation() {
  return (
    <div>
      <div className="flex justify-around items-center bg-gradient-to-r from-purple-600 to-blue-500 rounded-t-3xl p-4 shadow-lg z-20">
        {[Mine, Friends, Info, Settings].map((Icon, idx) => (
          <button
            key={idx}
            className="p-2 rounded-full hover:bg-white hover:bg-opacity-20 transition-colors"
          >
            <Icon className="w-6 h-6 text-white" />
          </button>
        ))}
      </div>
    </div>
  );
}

export default BottomNavigation;
