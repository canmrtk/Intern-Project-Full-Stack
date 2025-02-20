import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "../css/EmployeeDetails.css";

const EmployeeDetails = () => {
  const { id } = useParams();
  const [employee, setEmployee] = useState(null);
  const [leaveHistory, setLeaveHistory] = useState([]);

  useEffect(() => {
    fetchEmployee();
    fetchLeaveHistory();
  }, []);

  const fetchEmployee = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/employees/${id}`);
      setEmployee(response.data);
    } catch (error) {
      console.error("Çalışan bilgisi alınamadı:", error);
    }
  };

  const fetchLeaveHistory = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/leave-requests/employee/${id}`);
      setLeaveHistory(response.data);
    } catch (error) {
      console.error("İzin geçmişi alınamadı:", error);
    }
  };

  if (!employee) {
    return <p>Yükleniyor...</p>;
  }

  return (
    <div className="employee-details-container">
      <h1 className="employee-details-title">Çalışan Detayları</h1>
      <p><strong>Ad Soyad:</strong> {employee.name} {employee.surname}</p>
      <p><strong>E-posta:</strong> {employee.email}</p>
      <p><strong>Departman:</strong> {employee.department}</p>
      <p><strong>Kalan İzin Günü:</strong> {employee.leaveDays} gün</p>

      <h2>📌 İzin Geçmişi</h2>
      {leaveHistory.length > 0 ? (
        <table className="leave-history-table">
          <thead>
            <tr>
              <th>Talep Edilen Gün</th>
              <th>Durum</th>
            </tr>
          </thead>
          <tbody>
            {leaveHistory.map((leave) => (
              <tr key={leave.id}>
                <td>{leave.leaveDaysRequested} gün</td>
                <td>
                  {leave.status === "APPROVED" ? (
                    <span className="status-approved">Onaylandı</span>
                  ) : (
                    <span className="status-pending">Bekliyor</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>İzin geçmişi bulunamadı.</p>
      )}
    </div>
  );
};

export default EmployeeDetails;
