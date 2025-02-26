import React from "react";
import { Link } from "react-router-dom";
import "../css/EmployeeDashboard.css";

const EmployeeDashboard = ({ user }) => {
  return (
    <div className="dashboard-container">
      <h1>Merhaba, {user?.name}!</h1>
      <p>Buradan izin talep edebilir ve geçmiş izinlerini görebilirsin.</p>

      <div className="dashboard-sections">
        {/* İzin Talep Etme */}
        <div className="dashboard-section">
          <h2>İzin Talepleri</h2>
          <Link to="/new-leave-request">
            <button className="button-izin" >Yeni İzin Talebi</button>
          </Link>
          <Link to={`/leave-requests/${user.id}`}>
            <button className="dashboard-button">Geçmiş İzin Taleplerim</button>
          </Link>
        </div>

        {/* Çalışan Detayları */}
        <div className="dashboard-section">
          <h2>Çalışan Bilgilerim</h2>
          <Link to={`/employee-details/${user.id}`}>
            <button className="dashboard-button">Detaylarımı Gör</button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default EmployeeDashboard;
