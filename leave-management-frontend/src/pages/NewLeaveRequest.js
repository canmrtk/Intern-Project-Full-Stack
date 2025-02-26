import React, { useState } from "react";
import axios from "axios";
import "../css/NewLeaveRequest.css";

const NewLeaveRequest = ({ user }) => {
  const [leaveDaysRequested, setLeaveDaysRequested] = useState("");

  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    const { value } = e.target;
    
    // Kullanıcının 1 günden az izin almasını önlüyoruz
    if (value < 1) {
      setLeaveDaysRequested(1);
    } else {
      setLeaveDaysRequested(value);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!leaveDaysRequested || leaveDaysRequested < 1) {
      setMessage({ text: "Lütfen geçerli bir izin günü girin!", type: "error" });
      return;
    }

    try {
      const response = await axios.post("http://localhost:9090/api/leave-requests/request", {
        employeeEmail: user.email, // 📌 Giriş yapan kullanıcının e-postası backend’e gönderilecek
        leaveDaysRequested: parseInt(leaveDaysRequested, 10),
      });

      setMessage({ text: response.data, type: "success" });
    } catch (error) {
      setMessage({ text: error.response?.data || "Bilinmeyen hata oluştu!", type: "error" });
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
        <label>Kaç gün izin almak istiyorsun?</label>
        <input
          type="number"
          name="leaveDaysRequested"
          placeholder="Kaç gün izin?"
          value={leaveDaysRequested}
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
