import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../css/Navbar.css";

const Navbar = ({ setUser }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("user"); // Kullanıcı oturum bilgilerini temizle
    setUser(null); // Uygulama içindeki state’i sıfırla
    navigate("/"); // Giriş sayfasına yönlendir
  };

  return (
    <nav className="navbar">
      <h2>Kafein Yazılım</h2>
      <button className="logout-button" onClick={handleLogout}>Çıkış Yap</button>
    </nav>
  );
};

export default Navbar;
