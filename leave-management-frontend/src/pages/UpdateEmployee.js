import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

const UpdateEmployee = () => {
  const { id } = useParams(); // URL'den çalışan ID'sini al
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    department: "",
  });

  useEffect(() => {
    fetchEmployee();
  }, []);

  const fetchEmployee = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/employees/${id}`);
      setFormData(response.data);
    } catch (error) {
      console.error("Çalışan bilgisi alınamadı:", error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`http://localhost:9090/api/employees/${id}`, formData);
      alert("Çalışan başarıyla güncellendi!");
      navigate("/employees");
    } catch (error) {
      console.error("Çalışan güncellenirken hata oluştu:", error);
    }
  };

  return (
    <div className="container">
      <h2>Çalışan Güncelle</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="name" value={formData.name} onChange={handleChange} required />
        <input type="text" name="surname" value={formData.surname} onChange={handleChange} required />
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />

        <select name="department" value={formData.department} onChange={handleChange} required>
          <option value="Human Resources">İnsan Kaynakları</option>
          <option value="Software Development">Yazılım Geliştirme</option>
          <option value="Graphic Designer">Grafik Tasarımcı</option>
          <option value="Project Manager">Proje Yöneticisi</option>
          <option value="Java Developer">Java Geliştirici</option>
        </select>

        <button type="submit">Güncelle</button>
      </form>
    </div>
  );
};

export default UpdateEmployee;
