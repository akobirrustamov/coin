import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Check, Gift } from "lucide-react";
import clsx from "clsx";
import BottomNavigation from "../../components/bottomNavigation/BottomNavigation";

const getDaysInMonth = (year, month) => {
  return new Date(year, month + 1, 0).getDate();
};

const DailyReward = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth(); // 0-indexed
  const daysInMonth = getDaysInMonth(year, month);

  const [claimedDays, setClaimedDays] = useState([1, 3, 5]); // пример уже полученных дней

  const handleClaim = (day) => {
    if (!claimedDays.includes(day)) {
      setClaimedDays((prev) => [...prev, day]);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4 text-center">Daily Rewards</h2>
      <div className="grid grid-cols-7 gap-2">
        {Array.from({ length: daysInMonth }, (_, index) => {
          const day = index + 1;
          const isClaimed = claimedDays.includes(day);

          return (
            <motion.div
              key={day}
              whileTap={{ scale: 0.9 }}
              onClick={() => handleClaim(day)}
              className={clsx(
                "rounded-lg p-2 h-20 flex flex-col items-center justify-center cursor-pointer transition-all duration-300",
                isClaimed ? "bg-green-400 text-white" : "bg-white shadow"
              )}
            >
              <p className="text-sm font-semibold">{day}</p>
              {isClaimed ? (
                <Check className="w-5 h-5 mt-1" />
              ) : (
                <Gift className="w-5 h-5 mt-1 text-yellow-500" />
              )}
            </motion.div>
          );
        })}
      </div>
      <BottomNavigation/>
    </div>
  );
};

export default DailyReward;
