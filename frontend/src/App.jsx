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
    <div>
      <Routes>
        <Route path={"/"} element={<BotWeb />} />
        <Route path={"/admin/login"} element={<LoginAdmin />} />
        <Route path={"/dashboard"} element={<AdminHome />} />
        <Route path={"/dashboard/add"} element={<Add />} />
        <Route path={"/dashboard/mission"} element={<Mission />} />
        <Route path={"/dashboard/user"} element={<User />} />
      </Routes>
    </div>
  );
};

export default App;
