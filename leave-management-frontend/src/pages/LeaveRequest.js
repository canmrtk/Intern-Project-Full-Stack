import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "../css/LeaveRequest.css";

const LeaveRequests = () => {
  const [leaveRequests, setLeaveRequests] = useState([]);

  useEffect(() => {
    fetchLeaveRequests();
  }, []);

  const fetchLeaveRequests = async () => {
    try {
      const response = await axios.get("http://localhost:9090/api/leave-requests");
      setLeaveRequests(response.data);
    } catch (error) {
      console.error("İzin talepleri alınırken hata oluştu:", error);
    }
  };

  return (
    <div className="leave-requests-container">
      <h2 className="leave-requests-title">İzin Talepleri</h2>
      
      <table className="leave-requests-table">
        <thead>
          <tr>
            <th>Çalışan</th>
            <th>E-posta</th>
            <th>Departman</th>
            <th>Talep Edilen Gün</th>
            <th>Kalan İzin</th>
            <th>Detay</th>
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
                  <Link to={`/leave-requests/${request.employee.id}`}>
                    <button className="detail-button">Detay</button>
                  </Link>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="6">İzin talebi bulunamadı.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default LeaveRequests;
