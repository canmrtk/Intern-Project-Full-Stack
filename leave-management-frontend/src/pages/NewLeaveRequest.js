import React, { useState } from "react";
import axios from "axios";
import "../css/NewLeaveRequest.css";

const NewLeaveRequest = ({ user }) => {
  const [leaveDaysRequested, setLeaveDaysRequested] = useState("");
  const [leaveType, setLeaveType] = useState(""); // YENİ

  const [message, setMessage] = useState("");

  const handleDaysChange = (e) => {
    const { value } = e.target;
    setLeaveDaysRequested(value < 1 ? 1 : value);
  };

  const handleTypeChange = (e) => {
    setLeaveType(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!leaveDaysRequested || leaveDaysRequested < 1 || !leaveType) {
      setMessage({
        text: "Lütfen geçerli bir izin günü ve izin türü seçin!",
        type: "error",
      });
      return;
    }

    try {
      const response = await axios.post("http://localhost:9090/api/leave-requests/request", {
        employeeEmail: user.email,
        leaveDaysRequested: parseInt(leaveDaysRequested, 10),
        leaveType: leaveType,
      });

      setMessage({ text: response.data, type: "success" });
    } catch (error) {
      setMessage({
        text: error.response?.data || "Bilinmeyen hata oluştu!",
        type: "error",
      });
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
          onChange={handleDaysChange}
          min="1"
          required
        />

        <label>İzin Türü:</label>
        <select value={leaveType} onChange={handleTypeChange} required>
          <option value="">İzin türünü seçiniz</option>
          <option value="ANNUAL">Yıllık İzin</option>
          <option value="SICK">Hastalık İzni</option>
          <option value="UNPAID">Ücretsiz İzin</option>
          <option value="MATERNITY">Doğum İzni</option>
        </select>

        <button type="submit">Talep Gönder</button>
      </form>
    </div>
  );
};

export default NewLeaveRequest;
