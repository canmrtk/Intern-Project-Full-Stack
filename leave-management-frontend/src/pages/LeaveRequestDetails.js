import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "../css/LeaveRequest.css";

const LeaveRequestDetails = () => {
  const { employeeId } = useParams();
  const [leaveRequests, setLeaveRequests] = useState([]);

  useEffect(() => {
    fetchEmployeeLeaveRequests();
  }, []);

  const fetchEmployeeLeaveRequests = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/leave-requests/${employeeId}`);
      setLeaveRequests(response.data);
    } catch (error) {
      console.error("İzin geçmişi alınırken hata oluştu:", error);
    }
  };

  return (
    <div className="leave-requests-container">
      <h2 className="leave-requests-title">İzin Geçmişi</h2>

      <table className="leave-requests-table">
        <thead>
          <tr>
            <th>Talep Edilen Gün</th>
            <th>Talep Tarihi</th>
          </tr>
        </thead>
        <tbody>
          {leaveRequests.length > 0 ? (
            leaveRequests.map((request) => (
              <tr key={request.id}>
                <td>{request.leaveDaysRequested} Gün</td>
                <td>{new Date(request.requestDate).toLocaleDateString()}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="2">İzin geçmişi bulunamadı.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default LeaveRequestDetails;
