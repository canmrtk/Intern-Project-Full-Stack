import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

const UpdateEmployee = () => {
  const { id } = useParams(); // URL'den ID'yi al
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    department: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  // Sayfa açıldığında, çalışanın bilgilerini getir
  useEffect(() => {
    axios.get(`http://localhost:9090/api/employees/${id}`)
      .then((response) => {
        setFormData(response.data);
      })
      .catch((error) => {
        setError("Çalışan bilgileri alınamadı.");
        console.error("Çalışan verisi getirme hatası:", error);
      });
  }, [id]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      await axios.put(`http://localhost:9090/api/employees/${id}`, formData);
      setMessage("Çalışan başarıyla güncellendi!");
      setTimeout(() => navigate("/employees"), 2000);
    } catch (error) {
      setError("Çalışan güncellenirken hata oluştu! " + (error.response?.data?.message || "Bilinmeyen hata."));
      console.error("Çalışan güncelleme hatası:", error);
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "auto", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
      <h2>Çalışan Güncelle</h2>
      <form onSubmit={handleSubmit}>
        <label>Ad:</label>
        <input type="text" name="name" value={formData.name} onChange={handleChange} required />

        <label>Soyad:</label>
        <input type="text" name="surname" value={formData.surname} onChange={handleChange} required />

        <label>E-posta:</label>
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />

        <label>Departman:</label>
        <input type="text" name="department" value={formData.department} onChange={handleChange} required />

        <button type="submit">Güncelle</button>
      </form>

      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default UpdateEmployee;
