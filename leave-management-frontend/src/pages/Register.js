import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";


const Register = () => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    password: "",
    role: "EMPLOYEE", // Varsayılan olarak çalışan rolü atanıyor
  });

  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      await axios.post("http://localhost:9090/api/auth/register", formData);
      setMessage("Kayıt başarılı! Giriş sayfasına yönlendiriliyorsunuz...");
      setTimeout(() => navigate("/"), 2000);
    } catch (error) {
      setMessage("Kayıt sırasında hata oluştu: " + (error.response?.data || "Bilinmeyen hata."));
    }
  };

  return (
    <div className="register-container">
      <h1>Kullanıcı Kaydı</h1>
      <form onSubmit={handleSubmit}>
        <label>Ad:</label>
        <input type="text" name="name" value={formData.name} onChange={handleChange} required />

        <label>Soyad:</label>
        <input type="text" name="surname" value={formData.surname} onChange={handleChange} required />

        <label>E-posta:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />

        <label>Şifre:</label>
        <input type="password" name="password" value={formData.password} onChange={handleChange} required />

        <label>Rol Seçin:</label>
        <select name="role" value={formData.role} onChange={handleChange}>
          <option value="EMPLOYEE">Çalışan</option>
          <option value="MANAGER">Yönetici</option>
        </select>

        <button type="submit">Kayıt Ol</button>
      </form>

      {message && <p className="message">{message}</p>}
    </div>
  );
};

export default Register;
