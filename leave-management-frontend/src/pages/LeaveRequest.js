import React, { useState } from "react";
import axios from "axios";

const LeaveRequest = () => {
  const [employeeId, setEmployeeId] = useState("");
  const [leaveDays, setLeaveDays] = useState("");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:9090/api/leave/request", {
        employeeId: parseInt(employeeId),
        leaveDays: parseInt(leaveDays)
      });
      setMessage(response.data);
    } catch (error) {
      setMessage("İzin talebi başarısız oldu!");
      console.error("İzin talebi sırasında hata oluştu:", error);
    }
  };

  return (
    <div>
      <h1>İzin Talebi</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Çalışan ID:
          <input type="number" value={employeeId} onChange={(e) => setEmployeeId(e.target.value)} required />
        </label>
        <br />
        <label>
          İzin Gün Sayısı:
          <input type="number" value={leaveDays} onChange={(e) => setLeaveDays(e.target.value)} required />
        </label>
        <br />
        <button type="submit">İzin Talep Et</button>
      </form>
      {message && <p>{message}</p>}
    </div>
  );
};

export default LeaveRequest;
