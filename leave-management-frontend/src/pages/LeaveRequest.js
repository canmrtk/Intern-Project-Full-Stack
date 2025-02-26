import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "../css/LeaveRequest.css";

const LeaveRequests = () => {
  const [leaveRequests, setLeaveRequests] = useState([]);
  const [message, setMessage] = useState({ text: "", type: "" });

  useEffect(() => {
    fetchLeaveRequests();
  }, []);

  const fetchLeaveRequests = async () => {
    try {
      const response = await axios.get("http://localhost:9090/api/leave-requests");
      setLeaveRequests(response.data.reverse());
    } catch (error) {
      console.error("İzin talepleri alınırken hata oluştu:", error);
    }
  };

  const approveLeaveRequest = async (id) => {
    if (!window.confirm("Bu izin talebini onaylamak istediğine emin misin?")) return;

    try {
      await axios.put(`http://localhost:9090/api/leave-requests/${id}/approve`);
      setLeaveRequests(leaveRequests.map(request => 
        request.id === id ? { ...request, status: "APPROVED" } : request
      ));
      setMessage({ text: "İzin talebi onaylandı.", type: "success" });
    } catch (error) {
      setMessage({ text: error.response?.data || "Onay sırasında hata oluştu.", type: "error" });
    }
  };

  const rejectLeaveRequest = async (id) => {
    if (!window.confirm("Bu izin talebini reddetmek istediğine emin misin?")) return;

    try {
      await axios.put(`http://localhost:9090/api/leave-requests/${id}/reject`);
      setLeaveRequests(leaveRequests.filter(request => request.id !== id));
      setMessage({ text: "İzin talebi reddedildi.", type: "success" });
    } catch (error) {
      setMessage({ text: error.response?.data || "Reddetme sırasında hata oluştu.", type: "error" });
    }
  };

  return (
    <div className="leave-requests-container">
      <h1 className="leave-requests-title">İzin Talepleri</h1>
      
      {message.text && (
        <p className={message.type === "success" ? "success-message" : "error-message"}>
          {message.text}
        </p>
      )}

      <table className="leave-requests-table">
        <thead>
          <tr>
            <th>Çalışan</th>
            <th>E-posta</th>
            <th>Departman</th>
            <th>Talep Edilen Gün</th>
            <th>Kalan İzin</th>
            <th>Durum</th>
            <th>Detay</th>
            <th>Onayla</th>
            <th>Reddet</th>
          </tr>
        </thead>
        <tbody>
          {leaveRequests.length > 0 ? (
            leaveRequests.map((request) => (
              <tr key={request.id}>
                <td>{request.employee.name} {request.employee.surname}</td>
                <td>{request.employee.email}</td>
                <td>{request.employee.department}</td>
                <td>{request.leaveDaysRequested} Gün</td>
                <td>{request.employee.leaveDays} Gün</td>
                <td>
                  <span className={request.status === "APPROVED" ? "status-approved" : "status-pending"}>
                    {request.status === "APPROVED" ? "Onaylandı" : "Bekliyor"}
                  </span>
                </td>
                <td>
                  <Link to={`/leave-requests/${request.employee.id}`}>
                    <button className="detail-button">Detay</button>
                    
                  </Link>
                </td>
                <td>
                  {request.status !== "APPROVED" && (
                    <button className="approve-button" onClick={() => approveLeaveRequest(request.id)}>Onayla</button>
                  )}
                </td>
                <td>
                  {request.status !== "APPROVED" && (
                    <button className="reject-button" onClick={() => rejectLeaveRequest(request.id)}>Reddet</button>
                  )}
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="9">İzin talebi bulunamadı.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default LeaveRequests;
