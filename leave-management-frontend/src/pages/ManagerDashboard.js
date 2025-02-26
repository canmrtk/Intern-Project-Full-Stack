import React from "react";
import { Link } from "react-router-dom";
import "../css/ManagerDashboard.css";

const ManagerDashboard = ({ user }) => {
  return (
    <div className="dashboard-container-manager">
      <h1>Hoş geldin, {user?.name}!</h1>
      <p>Buradan çalışanları ve izin taleplerini yönetebilirsiniz.</p>

      <div className="dashboard-sections-manager">
        {/* Çalışan Listesi */}
        <div className="dashboard-section-manager">
          <h2>Çalışan Yönetimi</h2>
          <Link to="/employees">
            <button className="button-list-manager">Çalışan Listesi</button>
          </Link>
          <Link to="/add-employee">
            <button className="dashboard-button-manager">Çalışan Ekle</button>
          </Link>
        </div>

        {/* Genel İzin Geçmişi */}
        <div className="dashboard-section-manager">
          <h2>İzin Yönetimi</h2>
          <Link to="/leave-request">
            <button className="dashboard-button-manager">Genel İzin Geçmişi</button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ManagerDashboard;
