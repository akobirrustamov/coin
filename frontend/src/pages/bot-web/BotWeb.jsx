import React, { useState, useEffect, lazy } from "react";
import ApiCall, { baseUrl } from "../config/index";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import Hamster from "../../icons/Hamster";
import {
  binanceLogo,
  dailyCipher,
  dailyCombo,
  dailyReward,
  dollarCoin,
  // mainCharacter,
} from "../../images";
import mainCharacter from "../../images/mainCharacter.png";
import mainBg from "../../images/mainBg.png";
import { useNavigate, useParams } from "react-router-dom";
import InitialLoading from "../../components/Loadings/InitialLoading/InitialLoading";
import Loading from "../../components/Loadings/ScreenLoading/ScreenLoading";
import BottomNavigation from "../../components/bottomNavigation/BottomNavigation";
const FirstLoginRewardModal = lazy(() =>
  import("../../components/FirstLoginRewardModal")
);

const BotWeb = () => {
  const { id } = useParams();
  const [newFormData, setNewFormData] = useState([]);
  const [telegramUser, setTelegramUser] = useState(null);
  const [levelIndex, setLevelIndex] = useState(6);
  const [points, setPoints] = useState(0);
  const [clicks, setClicks] = useState([]);
  const pointsToAdd = 1;
  const navigate = useNavigate();
  const [showIntroLoading, setShowIntroLoading] = useState(false);
  const [isUserLoading, setIsUserLoading] = useState(true);
  const [fetchedCoin, setFetchedCoin] = useState(null);
  const [fetchedEnergy, setFetchedEnergy] = useState(0);
  const [dailyRewardTimeLeft, setDailyRewardTimeLeft] = useState("00:00");
  const [dailyCipherTimeLeft, setDailyCipherTimeLeft] = useState("00:00");
  const [dailyComboTimeLeft, setDailyComboTimeLeft] = useState("00:00");

  const [energy, setEnergy] = useState(() => {
    return parseInt(localStorage.getItem("energy") || "1000");
  });
  const [localClicks, setLocalClicks] = useState(() => {
    return parseInt(localStorage.getItem("clicks") || "0");
  });
  const [showFirstLoginReward, setShowFirstLoginReward] = useState(false);
  const [isFirstTime, setIsFirstTime] = useState(false);

  useEffect(() => {
    fetchUser();
    fetchAdd();
  }, []);
  const fetchAdd = async () => {
    try {
      const response = await ApiCall("/api/v1/news", "GET", null, null, true);
      setNewFormData(response.data);
    } catch (error) {
      console.error("Error fetching news:", error);
      setNewFormData([]);
    }
  };
  const fetchUser = async () => {
    try {
      const response = await ApiCall("/api/v1/app/telegram-user/" + id, "GET");
      console.log("Telegram User:", response.data);
      if (!response.error) {
        const userData = response.data;
        setFetchedCoin(userData.availableCoin || 0);
        setFetchedEnergy(userData.energy || 0);
        setTelegramUser(userData);

        setIsFirstTime(userData.isFirstTime === true);
        if (userData.isFirstTime === true) {
          setShowIntroLoading(true);
        }
      }
    } catch (error) {
      console.error("Error fetching telegram-user:", error);
      setTelegramUser(null);
    } finally {
      setIsUserLoading(false);
    }
  };
  const handleIntroLoaded = () => {
    if (isFirstTime) {
      setShowFirstLoginReward(true);
    }
    setShowIntroLoading(false);
  };

  useEffect(() => {
    const interval = setInterval(async () => {
      const storedClicks = parseInt(localStorage.getItem("clicks") || "0");
      const storedEnergy = parseInt(localStorage.getItem("energy") || "1000");

      const obj = {
        userId: id,
        amount: storedClicks,
        energy: storedEnergy,
        type: 1,
        timestamp: Date.now(),
      };

      if (storedClicks > 0) {
        try {
          const res = await ApiCall("/api/v1/user-coin/" + id, "POST", obj);
          console.log("post:", res.data);
          if (!res.error) {
            localStorage.setItem("clicks", "0");
            setLocalClicks(0);
          }
          const updatedUser = await ApiCall(
            "/api/v1/app/telegram-user/" + id,
            "GET"
          );
          console.log("Updated User:", updatedUser.data);
          if (!updatedUser.error) {
            const user = updatedUser.data;
            console.log("Updated User:", user);
            console.log("Updated User Coin:", user.availableCoin);

            setFetchedCoin(user.availableCoin);
            setFetchedEnergy(user.energy);
          }
        } catch (err) {
          console.error("❌ Ошибка при отправке user-coin:", err);
        }
      }
    }, 30000); //  сек

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const maxEnergy = 1000;
    const regenInterval = 10000; // 10 сек = 10_000 мс

    const initEnergy = () => {
      const storedEnergy = parseInt(localStorage.getItem("energy") || "1000");
      const lastRegen = parseInt(
        localStorage.getItem("lastEnergyTimestamp") || Date.now().toString()
      );
      const now = Date.now();

      const elapsed = now - lastRegen;
      const regenSteps = Math.floor(elapsed / regenInterval);

      if (regenSteps > 0 && storedEnergy < maxEnergy) {
        const newEnergy = Math.min(maxEnergy, storedEnergy + regenSteps);
        const newTimestamp = lastRegen + regenSteps * regenInterval;

        localStorage.setItem("energy", newEnergy.toString());
        localStorage.setItem("lastEnergyTimestamp", newTimestamp.toString());
        setEnergy(newEnergy);
      } else {
        setEnergy(storedEnergy);
      }
    };

    initEnergy(); // запускаем при загрузке

    const interval = setInterval(() => {
      const storedEnergy = parseInt(localStorage.getItem("energy") || "1000");

      if (storedEnergy < maxEnergy) {
        const updated = storedEnergy + 1;
        localStorage.setItem("energy", updated.toString());
        localStorage.setItem("lastEnergyTimestamp", Date.now().toString());
        setEnergy(updated);
      }
    }, regenInterval);

    return () => clearInterval(interval);
  }, []);

  const handleCardClick = (e) => {
    if (energy <= 0) return;

    const card = e.currentTarget;
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left - rect.width / 2;
    const y = e.clientY - rect.top - rect.height / 2;

    card.style.transform = `perspective(1000px) rotateX(${
      -y / 15
    }deg) rotateY(${x / 15}deg) scale(0.98)`;
    card.style.boxShadow = `${x / 10}px ${y / 10}px 20px rgba(0,0,0,0.2)`;
    setTimeout(() => {
      card.style.transform = "";
      card.style.boxShadow = "";
    }, 300);

    setPoints((prev) => prev + pointsToAdd);
    setClicks((prev) => [
      ...prev,
      {
        id: Date.now(),
        x: e.pageX,
        y: e.pageY,
        value: "+1",
      },
    ]);

    setLocalClicks((prev) => {
      const updated = prev + 1;
      localStorage.setItem("clicks", updated.toString());
      return updated;
    });

    setEnergy((prev) => {
      const updated = Math.max(0, prev - 1);
      localStorage.setItem("energy", updated.toString());
      return updated;
    });
  };

  const handleAnimationEnd = (id) => {
    setClicks((prevClicks) => prevClicks.filter((click) => click.id !== id));
  };

  const calculateTimeLeft = (duration) => {
    const now = Date.now();
    const targetTime = now + duration;
    const diff = targetTime - now;
    if (diff <= 0) return "00:00";
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    return `${hours.toString().padStart(2, "0")}:${minutes
      .toString()
      .padStart(2, "0")}`;
  };

  useEffect(() => {
    const dailyDuration = 24 * 60 * 60 * 1000;
    const updateCountdowns = () => {
      setDailyRewardTimeLeft(calculateTimeLeft(dailyDuration));
      setDailyCipherTimeLeft(calculateTimeLeft(dailyDuration));
      setDailyComboTimeLeft(calculateTimeLeft(dailyDuration));
    };
    updateCountdowns();
    const interval = setInterval(updateCountdowns, 60000);
    return () => clearInterval(interval);
  }, []);

  const sliderSettings = {
    dots: false,
    infinite: true,
    speed: 800,
    slidesToShow: 2,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 2000,
    arrows: false,
    pauseOnHover: false,
    cssEase: "linear",
    centerMode: true,
    centerPadding: "10px",
    responsive: [
      {
        breakpoint: 1024,
        settings: { slidesToShow: 1, centerPadding: "0px" },
      },
      { breakpoint: 768, settings: { slidesToShow: 1, centerPadding: "0px" } },
      { breakpoint: 480, settings: { slidesToShow: 1, centerPadding: "0px" } },
    ],
  };
  const totalCoins = fetchedCoin === null ? 0 : fetchedCoin + localClicks;
  const totalEnergy = fetchedEnergy + (energy - fetchedEnergy);
  if (isUserLoading) {
    return (
      <div className="bg-gradient-to-b from-gray-100 to-gray-200 flex justify-center items-center min-h-0 h-screen overflow-hidden">
        <Loading />
      </div>
    );
  }

  if (showIntroLoading) {
    return <InitialLoading onLoaded={handleIntroLoaded} />;
  }
  return (
    <div className="bg-black flex justify-center min-h-0 h-screen overflow-hidden">
      <div className="w-full max-w-xl flex flex-col pb-20">
        {/* Header */}
        {telegramUser && (
          <div
            key={telegramUser.telegramId}
            className="px-4 py-3 bg-gradient-to-r from-purple-600 to-blue-500 rounded-b-3xl shadow-lg z-10"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <div className="p-2 rounded-xl bg-blue-950 bg-opacity-20 backdrop-blur-sm shadow-md">
                  <Hamster size={28} className="text-white" />
                </div>
                <div className="">
                  <p className="text-white font-semibold">
                    {telegramUser?.fullName}
                  </p>
                  <p className="text-white text-opacity-80 text-xs">
                    Level {telegramUser?.level}
                  </p>
                </div>
              </div>
              <div className="bg-white bg-opacity-20 backdrop-blur-sm rounded-xl px-3 py-2 shadow-md space-y-1">
                <div className="flex items-center space-x-1">
                  <img src={dollarCoin} alt="Dollar Coin" className="w-3 h-3" />
                  <p className="text-white text-lg">
                    Сoin: <strong>{totalCoins}</strong>
                  </p>
                </div>
                <div className="text-white text-sm">
                  ⚡ Energy: <strong>{totalEnergy}</strong>
                </div>
              </div>
            </div>

            {/* Slider */}
            <div className="mt-4">
              <div className="max-w-md mx-auto">
                <Slider {...sliderSettings}>
                  {newFormData &&
                    newFormData.map((newsItem) => (
                      <div key={newsItem.id} className="px-1 ">
                        <div className="flex gap-6 overflow-hidden mx-1">
                          <div className="relative h-10">
                            <img
                              src={`${baseUrl}/api/v1/file/getFile/${newsItem.mainPhoto.id}`}
                              alt={newsItem.title}
                              className="w-full h-8 object-cover rounded"
                            />
                          </div>
                          <div className="">
                            <h3 className="text-sm text-white font-semibold line-clamp-1">
                              {newsItem.title}
                            </h3>
                            <p className="text-fuchsia-100 text-xs line-clamp-2">
                              {newsItem.description}
                            </p>
                          </div>
                        </div>
                      </div>
                    ))}
                </Slider>
              </div>
            </div>
          </div>
        )}
        {/* Main Content */}
        <div style={{backgroundImage: `url(${mainBg})`}} className="flex-grow bg-cover flex flex-col px-6 bg-gradient-to-b from-blue-900 to-gray-800 rounded-3xl shadow-inner z-0">
          {/* Daily Activities */}
          <div className="grid grid-cols-3 gap-3 w-full py-4">
            {[
              {
                img: dailyReward,
                label: "Topshiriq",
                time: dailyRewardTimeLeft,
                color: "from-blue-400 to-blue-600",
                route: "/tasks",
              },
              {
                img: dailyCipher,
                label: "Kunlik mukofot",
                time: dailyCipherTimeLeft,
                color: "from-purple-400 to-purple-600",
                route: "/daily-reward",
              },
              {
                img: dailyCombo,
                label: "Boshqotirma",
                time: dailyComboTimeLeft,
                color: "from-green-400 to-green-600",
                route: "/puzzle",
              },
            ].map((item, idx) => (
              <div
                key={idx}
                className={`bg-gradient-to-br ${item.color} rounded-xl p-3 text-center shadow-md relative overflow-hidden cursor-pointer`}
                onClick={() => item.route && navigate(item.route)}
              >
                <div className="absolute -top-2 -right-2 w-16 h-16 bg-white bg-opacity-10 rounded-full"></div>
                <div className="relative z-10">
                  <img
                    src={item.img}
                    alt={item.label}
                    className="mx-auto w-12 h-12 drop-shadow-md"
                  />
                  <p className="text-white text-xs font-semibold mt-2">
                    {item.label}
                  </p>
                  <p className="text-white text-opacity-90 text-xs mt-1">
                    {item.time}
                  </p>
                </div>
              </div>
            ))}
          </div>

          <div className="flex-grow flex items-center justify-center">
            <div className="relative flex justify-center items-center">
              <div
                className="click-card w-80 h-80 rounded-full  shadow-xl flex items-center justify-center cursor-pointer transition-transform duration-300 relative"
                onClick={handleCardClick}
              >
                <div className="absolute inset-0 rounded-full bg-white bg-opacity-20 backdrop-blur-sm border-4 border-white border-opacity-30 z-0"></div>
                <img
                  src={mainCharacter}
                  alt="Main Character"
                  className="max-w-52 h-auto transform hover:scale-110 transition-transform duration-300 z-10"
                />
                <div className="absolute inset-0 rounded-full shadow-inner z-0"></div>
              </div>
              {clicks.map((click) => (
                <div
                  key={click.id}
                  className="absolute z-50 text-yellow-500 font-bold text-xl animate-float-up pointer-events-none"
                  onAnimationEnd={() => handleAnimationEnd(click.id)}
                  style={{
                    left: `${click.x}px`,
                    top: `${click.y}px`,
                    transform: "translate(-50%, -50%)",
                  }}
                >
                  {click.value}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
      {showFirstLoginReward && telegramUser && (
        <FirstLoginRewardModal
          telegramUser={telegramUser}
          onRewardClaimed={(amount) => {
            setFetchedCoin((prev) => (prev || 0) + amount);
          }}
          onClose={() => setShowFirstLoginReward(false)}
        />
      )}
      <BottomNavigation />
    </div>
  );
};

export default BotWeb;
