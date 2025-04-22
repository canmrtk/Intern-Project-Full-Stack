import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../css/Navbar.css";

const Navbar = ({ setUser }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("user");
    setUser(null);
    navigate("/"); // Giriş sayfasına yönlendir
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <h2>Kafein Yazılım</h2>
      </div>

      <div className="navbar-center">
        <Link to="/user-profile" className="profile-link">Profilim</Link>
        <button className="logout-button" onClick={handleLogout}>
          Çıkış Yap
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
