import { useState } from "react";
import { motion } from "framer-motion";
import ApiCall from "../pages/config/index";
import { useParams } from "react-router-dom";

const FirstLoginRewardModal = ({ telegramUser, onRewardClaimed, onClose }) => {
  const { id } = useParams();
  const [isClaiming, setIsClaiming] = useState(false);
  const [error, setError] = useState(null);

  const handleClaim = async () => {
    setIsClaiming(true);
    setError(null);

    try {
      // 1. Выдаём награду
      await ApiCall("/api/v1/user-coin/" + id, "POST", {
        userId: id,
        amount: 500,
        type: 9,
        timestamp: Date.now(),
      });

      // 2. Обновляем isFirstTime
      await ApiCall("/api/v1/app/telegram-user/is-first-time/" + id, "PUT");

      if (onRewardClaimed) onRewardClaimed(500);
      if (onClose) onClose();
    } catch (err) {
      console.error("Ошибка при получении награды:", err);
      setError("Произошла ошибка. Попробуйте снова.");
    } finally {
      setIsClaiming(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-70 z-50 flex items-center justify-center">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="bg-white rounded-2xl p-6 w-4/5 text-center shadow-xl"
      >
        <h2 className="text-2xl font-bold mb-4 text-blue-600">
           Hush Kelibsiz!
        </h2>
        <p className="text-gray-800 text-lg mb-4">
          Siz <strong>500 coin</strong> birinchi kirganingiz uchun oldingiz 🎉
        </p>
        {error && <p className="text-red-500 text-sm mb-2">{error}</p>}
        <button
          onClick={handleClaim}
          disabled={isClaiming}
          className={`px-4 w-3/4 py-2 rounded-full text-white transition-all ${
            isClaiming
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-blue-500 hover:bg-blue-600"
          }`}
        >
          {isClaiming ? "Ozgina sabr..." : "Katta rahmat"}
        </button>
      </motion.div>
    </div>
  );
};

export default FirstLoginRewardModal;
