import React, { useEffect } from "react";
import "./App.css";
import { Route, Routes, useLocation, useNavigate } from "react-router-dom";
import "./index.css";
// PAGES
import AdminHome from "../src/pages/admin/AdminHome.jsx";
import LoginAdmin from "../src/pages/admin/LoginAdmin.jsx";
import BotWeb from "../src/pages/bot-web/BotWeb.jsx";
import Add from "./pages/admin/add/Add.jsx";
import Mission from "./pages/admin/mission/Mission.jsx";
import User from "./pages/admin/users/User.jsx";
import DailyReward from "./pages/dailyReward/DailyReward.jsx";
import BottomNavigation from "./components/bottomNavigation/BottomNavigation.jsx";
import Ordinary from "./pages/admin/mission/ordinary/Ordinary.jsx";
import Level from "./pages/admin/mission/level/Level.jsx";
import Cinema from "./pages/admin/mission/cinema/Cinema.jsx";
import Special from "./pages/admin/mission/special/Special.jsx";
import Ref from "./pages/admin/mission/REF/Ref.jsx";
import Tasks from "./pages/tasks/Tasks.jsx";
import CinemaTask from "./pages/tasks/CinemaTask.jsx";

const App = () => {
  const blockedPages = ["/dashboard", "/app"];
  const navigate = useNavigate();
  const location = useLocation();
  useEffect(() => {
    // checkSecurity();
  }, [blockedPages, location.pathname, navigate]);
  async function checkSecurity() {
    if (
      blockedPages.some((blockedPage) =>
        location.pathname.startsWith(blockedPage)
      )
    ) {
      let accessToken = localStorage.getItem("access_token");
      const res = await ApiCall("/api/v1/security", "GET");
      if (res?.data == 401) {
        navigate("/admin/login");
      }
      if (accessToken !== null) {
        if (res?.data !== 401 && res?.error) {
          console.log("Hello");
          if (res?.data[0]?.name !== "ROLE_ADMIN") {
            navigate("/404");
          }
        }
      } else {
        navigate("/admin/login");
      }
    }
  }

  return (
    <div className="min-h-screen overflow-hidden flex flex-col relative">
      <Routes>
        <Route path={"/"} element={<BotWeb />} />
        <Route path={"/admin/login"} element={<LoginAdmin />} />
        <Route path={"/dashboard"} element={<AdminHome />} />
        <Route path={"/dashboard/add"} element={<Add />} />
        <Route path={"/dashboard/user"} element={<User />} />
        <Route path={"/dashboard/mission"} element={<Mission />} />
        <Route path={"/dashboard/mission/ordinary"} element={<Ordinary />} />
        <Route path={"/dashboard/mission/level"} element={<Level />} />
        <Route path={"/dashboard/mission/cinema"} element={<Cinema />} />
        <Route path={"/dashboard/mission/special"} element={<Special />} />
        <Route path={"/dashboard/mission/ref"} element={<Ref />} />
        {/* adminlar uchun */}
        {/* bot uchun */}
        <Route path={"/daily-reward"} element={<DailyReward />} />
        <Route path={"/tasks"} element={<Tasks />}>
          <Route index element={<CinemaTask />} />
          <Route path={"cinema-task"} element={<CinemaTask />} />
        </Route>
      </Routes>
      {!location.pathname.startsWith("/dashboard") && <BottomNavigation />}
    </div>
  );
};

export default App;
