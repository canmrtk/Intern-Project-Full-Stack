import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

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
    <div style={{ maxWidth: "400px", margin: "auto", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
      <h2>Yeni Çalışan Ekle</h2>
      <form onSubmit={handleSubmit}>
        <label>Ad:</label>
        <input type="text" name="name" value={formData.name} onChange={handleChange} required />

        <label>Soyad:</label>
        <input type="text" name="surname" value={formData.surname} onChange={handleChange} required />

        <label>E-posta:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />

        <label>Departman:</label>
        <input type="text" name="department" value={formData.department} onChange={handleChange} required />

        <button type="submit">Çalışan Ekle</button>
      </form>

      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default AddEmployee;
