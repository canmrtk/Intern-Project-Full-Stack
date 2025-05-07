import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../css/EmployeeDashboard.css";

const Login = ({ setUser }) => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const existingUser = localStorage.getItem("user");
    if (existingUser) {
      const parsedUser = JSON.parse(existingUser);
      if (parsedUser.role === "MANAGER") {
        navigate("/manager-dashboard");
      } else {
        navigate("/employee-dashboard");
      }
    }
  }, []);
  

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
  
    try {
      const response = await axios.post("http://localhost:9090/api/auth/login", formData);
      setUser(response.data);
      localStorage.setItem("user", JSON.stringify(response.data));
  
      
      localStorage.setItem("userId", response.data.id);  
  
      if (response.data.role === "MANAGER") {
        navigate("/manager-dashboard");
      } else {
        navigate("/employee-dashboard");
      }
    } catch (error) {
      setMessage("Giriş başarısız! " + (error.response?.data || "Bilinmeyen hata."));
    }
  };
  

  return (
    <div className="dashboard-container">
      <h1>Giriş Yap</h1>
      <form onSubmit={handleSubmit}>
        <label>E-posta:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />

        <label>Şifre:</label>
        <input type="password" name="password" value={formData.password} onChange={handleChange} required />

        <button type="submit" className="dashboard-button">Giriş Yap</button>
      </form>

      {message && <p className="error-message">{message}</p>}
    </div>
  );
};

export default Login;
