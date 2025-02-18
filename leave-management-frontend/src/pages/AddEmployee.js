import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../css/AddEmployee.css";  

const AddEmployee = () => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    department: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await axios.post("http://localhost:9090/api/employees", formData);
      setMessage("Çalışan başarıyla eklendi!");
      setTimeout(() => navigate("/employees"), 2000);
    } catch (error) {
      setError("Çalışan eklenirken hata oluştu! " + (error.response?.data?.message || "Bilinmeyen hata."));
      console.error("Çalışan ekleme hatası:", error);
    }
  };

  return (
    <div className="add-employee-container">
      <h1 className="employee-add-title">Yeni Çalışan Ekle</h1>
      <form onSubmit={handleSubmit}>
        <label>Ad:</label>
        <input type="text" name="name" value={formData.name} onChange={handleChange} required />

        <label>Soyad:</label>
        <input type="text" name="surname" value={formData.surname} onChange={handleChange} required />

        <label>E-posta:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />

        <label>Departman:</label>
        <select name="department" value={formData.department} onChange={handleChange} required>
          <option value="">Departman Seç</option>
          <option value="Human Resources">İnsan Kaynakları</option>
          <option value="Software Development">Yazılım Geliştirme</option>
          <option value="Graphic Designer">Grafik Tasarımcı</option>
          <option value="Project Manager">Proje Yöneticisi</option>
          <option value="Java Developer">Java Geliştirici</option>
        </select>

        <button type="submit">Çalışan Ekle</button>
      </form>

      {message && <p className="success-message">{message}</p>}
      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default AddEmployee;
