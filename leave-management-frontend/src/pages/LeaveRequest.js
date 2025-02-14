import React, { useState } from "react";
import axios from "axios";

const LeaveRequest = () => {
  const [email, setEmail] = useState("");
  const [leaveDays, setLeaveDays] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    if (!email || !leaveDays) {
      setError("E-posta ve izin gün sayısı zorunludur!");
      return;
    }

    try {
      const response = await axios.post("http://localhost:9090/api/leave/request", {
        email: email,
        leaveDays: parseInt(leaveDays),
      });

      setMessage(response.data);
    } catch (error) {
      setError("İzin talebi başarısız oldu! " + (error.response?.data?.message || "Bilinmeyen hata."));
      console.error("İzin talebi sırasında hata oluştu:", error);
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "auto", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
      <h2>İzin Talebi</h2>
      <form onSubmit={handleSubmit}>
        <label>
          E-Posta:
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <br />
        <label>
          İzin Gün Sayısı:
          <input type="number" value={leaveDays} onChange={(e) => setLeaveDays(e.target.value)} required />
        </label>
        <br />
        <button type="submit">İzin Talep Et</button>
      </form>
      {message && <p style={{ color: "green" }}>{message}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default LeaveRequest;
