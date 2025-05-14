import React, { useState } from "react";

import { addEmployee } from "../api"; // axios yerine api.js'den import

import { useNavigate } from "react-router-dom";
import "../css/AddEmployee.css"; 

const AddEmployee = () => {
  const [formData, setFormData] = useState({
    name: "",
    surname: "",
    email: "",
    department: "",
    role: "EMPLOYEE",
   
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

   
    if (!formData.name || !formData.surname || !formData.email || !formData.department || !formData.role) {
        setError("Lütfen tüm zorunlu alanları doldurun.");
        return;
    }
 
    try {
 
      const newEmployeeData = {
        name: formData.name,
        surname: formData.surname,
        email: formData.email,
        department: formData.department,
        role: formData.role,
        
      };

      const responseData = await addEmployee(newEmployeeData); // api.js'deki fonksiyon
      setMessage(`Çalışan başarıyla eklendi! ID: ${responseData.id}`);
      setFormData({ name: "", surname: "", email: "", department: "", role: "EMPLOYEE" }); 
      setTimeout(() => navigate("/employees"), 2000);
    } catch (err) {
      console.error("Çalışan ekleme hatası (AddEmployee):", err);
      setError(err); 
    }
    
  };

  return (
    <div style={{ maxWidth: "500px", margin: "auto", padding: "70px", border: "1px solid #ccc", borderRadius: "16px" }}>
      <h1>Yeni Çalışan Ekle</h1>
      <form onSubmit={handleSubmit}>
       
        <label htmlFor="name_add_emp">Ad:</label>
        <input id="name_add_emp" type="text" name="name" value={formData.name} onChange={handleChange} required />

        <label htmlFor="surname_add_emp">Soyad:</label>
        <input id="surname_add_emp" type="text" name="surname" value={formData.surname} onChange={handleChange} required />

        <label htmlFor="email_add_emp">E-posta:</label>
        <input id="email_add_emp" type="email" name="email" value={formData.email} onChange={handleChange} required />
        
        <label htmlFor="department_add_emp">Departman:</label>
        <select id="department_add_emp" name="department" value={formData.department} onChange={handleChange} required>
          <option value="">Departman Seç</option>
          <option value="Human Resources">İnsan Kaynakları</option>
          <option value="Software Development">Yazılım Geliştirme</option>
          <option value="Graphic Designer">Grafik Tasarımcı</option>
          <option value="Project Manager">Proje Yöneticisi</option>
          <option value="Java Developer">Java Geliştirici</option>
        </select>

        <label htmlFor="role_add_emp">Rol:</label>
        <select id="role_add_emp" name="role" value={formData.role} onChange={handleChange} required>
          <option value="EMPLOYEE">Çalışan</option>
          <option value="MANAGER">Yönetici</option>
        </select>

        

        <button type="submit">Çalışan Ekle</button>
      </form>

      {message && <p style={{ color: "green", marginTop: "10px" }}>{message}</p>}
      {error && <p style={{ color: "red", marginTop: "10px" }}>Hata: {error}</p>}
    </div>
  );
};

export default AddEmployee;