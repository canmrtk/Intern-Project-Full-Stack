import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "../css/UpdateEmployee.css";

const UpdateEmployee = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    department: "",
  });

  const [message, setMessage] = useState("");

  useEffect(() => {
    fetchEmployee();
  }, []);

  const fetchEmployee = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/employees/${id}`);
      const { name, surname, email, department } = response.data;
      setFormData({ name, surname, email, department });
    } catch (error) {
      console.error("Çalışan bilgisi alınamadı:", error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      await axios.put(`http://localhost:9090/api/employees/${id}`, formData);
      setMessage("Çalışan bilgileri başarıyla güncellendi.");
      
      // Güncelleme sonrası çalışan listesine yönlendir
      setTimeout(() => navigate("/employees"), 2000);
    } catch (error) {
      setMessage("Çalışan güncellenirken hata oluştu!");
      console.error("Çalışan güncelleme hatası:", error);
    }
  };

  return (
    <div className="update-employee-container">
      <h1>Çalışan Bilgilerini Güncelle</h1>
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

        <button type="submit">Güncelle</button>
      </form>

      {message && <p className="message">{message}</p>}
    </div>
  );
};

export default UpdateEmployee;
