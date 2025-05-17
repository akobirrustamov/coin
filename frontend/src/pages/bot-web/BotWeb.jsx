import React, { useState, useEffect } from "react";
import ApiCall, { baseUrl } from "../config/index";
import { Link } from "react-router-dom";
import "../../App.css";
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
  hamsterCoin,
  mainCharacter,
} from "../../images";
import Info from "../../icons/Info";
import Settings from "../../icons/Settings";
import Mine from "../../icons/Mine";
import Friends from "../../icons/Friends";
import Coins from "../../icons/Coins";

const BotWeb = () => {
  const [newFormData, setNewFormData] = useState([]);
  const sliderSettings = {
    dots: false,
    infinite: true,
    speed: 800,
    slidesToShow: 2, //bir martada nechtasini kursatish uchunligi
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 2000,
    arrows: false,
    pauseOnHover: false,
    cssEase: "linear",
    centerMode: true, // Helps with spacing
    centerPadding: "10px", // Adjust this for tighter spacing
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 1,
          centerPadding: "10px",
        },
      },
      {
        breakpoint: 768,
        settings: {
          slidesToShow: 1, // Show fewer on mobile
          centerPadding: "5px",
        },
      },
      {
        breakpoint: 480,
        settings: {
          slidesToShow: 1,
          centerPadding: "0px",
        },
      },
    ],
  };

  const fetchAdd = async () => {
    try {
      const response = await ApiCall("/api/v1/news", "GET", null, null, true);
      setNewFormData(response.data);
    } catch (error) {
      console.error("Error fetching news:", error);
      setNewFormData([]);
    }
  };

  const [levelIndex, setLevelIndex] = useState(6);
  const [points, setPoints] = useState(0);
  const [clicks, setClicks] = useState([]);
  const pointsToAdd = 10;

  const [dailyRewardTimeLeft, setDailyRewardTimeLeft] = useState("00:00");
  const [dailyCipherTimeLeft, setDailyCipherTimeLeft] = useState("00:00");
  const [dailyComboTimeLeft, setDailyComboTimeLeft] = useState("00:00");

  const dailyRewardDuration = 24 * 60 * 60 * 1000;
  const dailyCipherDuration = 24 * 60 * 60 * 1000;
  const dailyComboDuration = 24 * 60 * 60 * 1000;

  const calculateTimeLeft = (duration) => {
    const now = new Date().getTime();
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
    const updateCountdowns = () => {
      setDailyRewardTimeLeft(calculateTimeLeft(dailyRewardDuration));
      setDailyCipherTimeLeft(calculateTimeLeft(dailyCipherDuration));
      setDailyComboTimeLeft(calculateTimeLeft(dailyComboDuration));
    };

    updateCountdowns();
    const interval = setInterval(updateCountdowns, 60000);
    return () => clearInterval(interval);
  }, []);

  const handleCardClick = (e) => {
    const card = e.currentTarget;
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left - rect.width / 2;
    const y = e.clientY - rect.top - rect.height / 2;

    // Enhanced 3D tilt effect
    card.style.transform = `perspective(1000px) rotateX(${
      -y / 15
    }deg) rotateY(${x / 15}deg) scale(0.98)`;
    card.style.boxShadow = `${x / 10}px ${y / 10}px 20px rgba(0,0,0,0.2)`;

    setTimeout(() => {
      card.style.transform = "";
      card.style.boxShadow = "";
    }, 300);

    setPoints(points + pointsToAdd);
    setClicks([...clicks, { id: Date.now(), x: e.pageX, y: e.pageY }]);

    // Add a subtle pulse effect to the points display
    const pointsDisplay = document.querySelector(".points-display");
    if (pointsDisplay) {
      pointsDisplay.classList.add("points-pulse");
      setTimeout(() => {
        pointsDisplay.classList.remove("points-pulse");
      }, 300);
    }
  };

  const handleAnimationEnd = (id) => {
    setClicks((prevClicks) => prevClicks.filter((click) => click.id !== id));
  };

  useEffect(() => {
    fetchAdd();
  }, []);

  const formatProfitPerHour = (profit) => {
    if (profit >= 1000000000) return `+${(profit / 1000000000).toFixed(2)}B`;
    if (profit >= 1000000) return `+${(profit / 1000000).toFixed(2)}M`;
    if (profit >= 1000) return `+${(profit / 1000).toFixed(2)}K`;
    return `+${profit}`;
  };

  const profitPerHour = points * 10;

  return (
    <div className="bg-gradient-to-b from-gray-100 to-gray-200 flex justify-center min-h-screen">
      <div className="w-full max-w-xl flex flex-col">
        {/* Header Section */}
        <div className="px-6 pt-2 bg-gradient-to-r from-purple-600 to-blue-500 rounded-b-3xl shadow-lg z-10">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="p-2 rounded-xl bg-white bg-opacity-20 backdrop-blur-sm shadow-md">
                <Hamster size={28} className="text-white" />
              </div>
              <div>
                <p className="text-white text-lg font-semibold">Wahhab (CEO)</p>
                <p className="text-white text-opacity-80 text-xs">
                  Level {levelIndex + 1}
                </p>
              </div>
            </div>

            <div className="flex items-center bg-white bg-opacity-20 backdrop-blur-sm rounded-full px-4 py-2 shadow-md">
              <img src={binanceLogo} alt="Exchange" className="w-6 h-6" />
              <div className="h-6 w-px bg-white bg-opacity-40 mx-3"></div>
              <div className="text-center">
                <p className="text-white text-opacity-80 text-xs">
                  Total Points
                </p>
                <div className="flex items-center justify-center space-x-1">
                  <img src={dollarCoin} alt="Dollar Coin" className="w-4 h-4" />
                  <p className="text-white text-base font-bold">
                    {points.toLocaleString()}
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Level Progress */}
          <div className="mt-4">
            <div className="flex gap-8 items-center">
              <div className="max-w-md mx-auto">
                <Slider {...sliderSettings}>
                  {newFormData.map((newsItem) => (
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
        </div>

        {/* Main Content */}
        <div className="flex-grow px-6 pt-6 bg-gray-100 rounded-3xl shadow-inner relative -my-4 z-0">
          {/* Daily Activities */}
          <div className="grid grid-cols-3 gap-3 mb-2">
            {[
              {
                img: dailyReward,
                label: "Daily Reward",
                time: dailyRewardTimeLeft,
                color: "from-blue-400 to-blue-600",
              },
              {
                img: dailyCipher,
                label: "Daily Cipher",
                time: dailyCipherTimeLeft,
                color: "from-purple-400 to-purple-600",
              },
              {
                img: dailyCombo,
                label: "Daily Combo",
                time: dailyComboTimeLeft,
                color: "from-green-400 to-green-600",
              },
            ].map((item, idx) => (
              <div
                key={idx}
                className={`bg-gradient-to-br ${item.color} rounded-xl p-3 text-center shadow-md relative overflow-hidden`}
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

          {/* Clickable Card */}
          <div className="relative mb-2 flex justify-center my-auto">
            <div
              className="click-card w-64 h-64 rounded-full bg-gradient-to-br from-yellow-200 to-yellow-400 shadow-xl flex items-center justify-center cursor-pointer transition-transform duration-300 mx-auto"
              onClick={handleCardClick}
            >
              <div className="absolute inset-0 rounded-full bg-white bg-opacity-20 backdrop-blur-sm border-4 border-white border-opacity-30"></div>
              <img
                src={mainCharacter}
                alt="Main Character"
                className="w-40 h-40 transform hover:scale-110 transition-transform duration-300 z-10"
                style={{ display: "block", margin: "0 auto" }}
              />
              <div className="absolute inset-0 rounded-full shadow-inner"></div>
            </div>

            {/* Click Animations */}
            {clicks.map((click) => (
              <div
                key={click.id}
                className="coin-fall absolute w-6 h-6 bg-yellow-400 rounded-full shadow-md z-30"
                onAnimationEnd={() => handleAnimationEnd(click.id)}
                style={{
                  left: `${click.x}px`,
                  top: `${click.y}px`,
                  background:
                    "radial-gradient(circle at 30% 30%, #fff, #fbbf24)",
                }}
              ></div>
            ))}
          </div>

          {/* Points Display */}
          {/* <div className="points-display bg-white rounded-xl shadow-md p-4 mb-6 flex items-center justify-center transition-all duration-300">
            <div className="flex items-center space-x-3">
              <div className="p-2 bg-yellow-100 rounded-lg">
                <Coins className="w-6 h-6 text-yellow-600" />
              </div>
              <div>
                <p className="text-gray-500 text-xs">Total Points</p>
                <p className="text-2xl font-bold text-gray-800">
                  {points.toLocaleString()}
                </p>
              </div>
            </div>
          </div> */}
        </div>
        {/* Bottom Navigation */}
        <div className="flex justify-around items-center bg-gradient-to-r from-purple-600 to-blue-500 rounded-t-3xl p-4 shadow-lg z-20">
          <button className="p-2 rounded-full hover:bg-white hover:bg-opacity-20 transition-colors">
            <Mine className="w-6 h-6 text-white" />
          </button>
          <button className="p-2 rounded-full hover:bg-white hover:bg-opacity-20 transition-colors">
            <Friends className="w-6 h-6 text-white" />
          </button>
          <button className="p-2 rounded-full hover:bg-white hover:bg-opacity-20 transition-colors">
            <Info className="w-6 h-6 text-white" />
          </button>
          <button className="p-2 rounded-full hover:bg-white hover:bg-opacity-20 transition-colors">
            <Settings className="w-6 h-6 text-white" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default BotWeb;
