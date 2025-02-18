import React, { useState } from "react";
import axios from "axios";
import "../css/NewLeaveRequest.css";

const NewLeaveRequest = () => {
  const [formData, setFormData] = useState({
    employeeEmail: "",
    leaveDaysRequested: "",
  });

  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.employeeEmail || formData.leaveDaysRequested < 1) {
      setMessage({ text: "Lütfen geçerli bir e-posta adresi ve izin günü girin!", type: "error" });
      return;
    }

    try {
      const response = await axios.post("http://localhost:9090/api/leave-requests/request", {
        employee: { email: formData.employeeEmail }, 
        leaveDaysRequested: formData.leaveDaysRequested,
      });

      setMessage({ text: "İzin talebiniz başarıyla oluşturuldu ve onay bekliyor.", type: "success" });
    } catch (error) {
      setMessage({ text: "İzin talebi oluşturulamadı: " + error.response.data, type: "error" });
    }
  };

  return (
    <div className="leave-request-container">
      <h1 className="leave-request-title">Yeni İzin Talebi</h1>
      {message && (
        <p className={message.type === "success" ? "success-message" : "error-message"}>
          {message.text}
        </p>
      )}
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          name="employeeEmail"
          placeholder="Çalışan e-mail"
          value={formData.employeeEmail}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="leaveDaysRequested"
          placeholder="Kaç gün izin?"
          value={formData.leaveDaysRequested}
          onChange={handleChange}
          min="1" 
          required
        />
        <button type="submit">Talep Gönder</button>
      </form>
    </div>
  );
};

export default NewLeaveRequest;
