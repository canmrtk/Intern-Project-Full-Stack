import React, { useState } from "react";
import axios from "axios";
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
      const response = await axios.post("http://localhost:9090/api/leave-requests/request", {
        employeeEmail: user.email,
        leaveDaysRequested: parseInt(leaveDaysRequested, 10),
        leaveType: leaveType,
      });

    
      if (response.status === 201 && response.data) { 
         setMessage({
          text: `İzin talebiniz başarıyla oluşturuldu. `, // Örnek mesaj
          type: "success"
        });
        // Formu sıfırla
        setLeaveDaysRequested("");
        setLeaveType("");
      } else {
        // Beklenmedik başarılı yanıt durumu
         setMessage({ text: response.data || "İzin talebi gönderildi ancak sunucudan beklenmedik bir yanıt alındı.", type: "success" });
      }
     

    } catch (error) {
      console.error("İzin talebi gönderme hatası:", error.response || error.message);
     
      let errorMessageText = "Bilinmeyen bir hata oluştu!";
      if (error.response && error.response.data) {
        if (typeof error.response.data === 'string') {
          errorMessageText = error.response.data;
        } else if (error.response.data.message) { 
          errorMessageText = error.response.data.message;
        } else if (typeof error.response.data === 'object') {
            
            const messages = Object.values(error.response.data);
            if(messages.length > 0) errorMessageText = messages.join(", ");
        }
      } else if (error.message) {
        errorMessageText = error.message;
      }
      setMessage({
        text: errorMessageText,
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
        <label htmlFor="leaveDaysRequested">Kaç gün izin almak istiyorsun?</label>
        <input
          id="leaveDaysRequested"
          type="number"
          name="leaveDaysRequested"
          placeholder="Kaç gün izin?"
          value={leaveDaysRequested}
          onChange={handleDaysChange}
          min="1"
          
        />

        <label htmlFor="leaveType">İzin Türü:</label>
        <select id="leaveType" name="leaveType" value={leaveType} onChange={handleTypeChange} > {/* required kaldırıldı */}
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