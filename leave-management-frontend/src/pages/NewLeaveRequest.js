import React, { useState } from "react";

import { createLeaveRequest } from "../api"; 

import "../css/NewLeaveRequest.css";

const NewLeaveRequest = ({ user }) => {
  const [leaveDaysRequested, setLeaveDaysRequested] = useState("");
  const [leaveType, setLeaveType] = useState("");
  const [message, setMessage] = useState({ text: "", type: "" });

  const handleDaysChange = (e) => {
    const { value } = e.target;
    setLeaveDaysRequested(value < 1 ? "" : value);
  };

  const handleTypeChange = (e) => {
    setLeaveType(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage({ text: "", type: "" });

    if (!leaveDaysRequested || parseInt(leaveDaysRequested, 10) < 1 || !leaveType) {
      setMessage({
        text: "Lütfen geçerli bir izin günü (en az 1) ve izin türü seçin!",
        type: "error",
      });
      return;
    }

    if (!user || !user.email) {
      setMessage({
        text: "Kullanıcı bilgileri bulunamadı. Lütfen tekrar giriş yapın.",
        type: "error",
      });
      return;
    }

    
    try {
      const leaveRequestData = {
        employeeEmail: user.email,
        leaveDaysRequested: parseInt(leaveDaysRequested, 10),
        leaveType: leaveType,
      };
      
      const responseData = await createLeaveRequest(leaveRequestData); // api.js'deki fonksiyon

      
      if (responseData && responseData.id) {
         setMessage({
          text: `İzin talebiniz başarıyla oluşturuldu. Talep ID: ${responseData.id}`,
          type: "success"
        });
        setLeaveDaysRequested("");
        setLeaveType("");
      } else {
         setMessage({ text: responseData || "İzin talebi gönderildi ancak sunucudan beklenmedik bir yanıt alındı.", type: "success" });
      }
    } catch (err) {
      console.error("İzin talebi gönderme hatası (NewLeaveRequest):", err);
    
      setMessage({
        text: err, 
        type: "error",
      });
    }
   
  };

  return (
  
    <div className="leave-request-container">
      <h1 className="leave-request-title">Yeni İzin Talebi</h1>
      {message.text && (
        <p className={message.type === "success" ? "success-message" : "error-message"}>
          {message.text}
        </p>
      )}
      <form onSubmit={handleSubmit}>
        <label htmlFor="leaveDaysRequested_new">Kaç gün izin almak istiyorsun?</label>
        <input
          id="leaveDaysRequested_new"
          type="number"
          name="leaveDaysRequested"
          placeholder="Kaç gün izin?"
          value={leaveDaysRequested}
          onChange={handleDaysChange}
          min="1"
        />

        <label htmlFor="leaveType_new">İzin Türü:</label>
        <select id="leaveType_new" name="leaveType" value={leaveType} onChange={handleTypeChange} >
          <option value="">İzin türünü seçiniz</option>
          <option value="ANNUAL">Yıllık İzin</option>
          <option value="SICK">Hastalık İzni</option>
          <option value="UNPAID">Ücretsiz İzin</option>
          <option value="MATERNITY">Doğum İzni (Kadın)</option>
          <option value="PATERNITY">Babalık İzni (Erkek)</option>
        </select>

        <button type="submit">Talep Gönder</button>
      </form>
    </div>
  );
};

export default NewLeaveRequest;