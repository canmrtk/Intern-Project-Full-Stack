import React from "react";
import { Link } from "react-router-dom";
import "../css/ManagerDashboard.css";
import { useEffect, useState } from "react";
import axios from "axios";
import "../css/Notification.css";


const ManagerDashboard = ({ user }) => {
  const [notifications, setNotifications] = useState([]);

useEffect(() => {
  const interval = setInterval(() => {
    axios.get("http://localhost:9090/api/notifications")
      .then(res => {
        if (res.data.length > 0) {
          setNotifications(prev => [...prev, ...res.data]);
        }
      })
      .catch(err => console.error("Bildirim alınamadı", err));
  }, 5000); // 5 saniyede bir kontrol et

  return () => clearInterval(interval);
}, []);


const ManagerDashboard = () => {
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    const interval = setInterval(() => {
      axios.get("http://localhost:9090/api/notifications")
        .then(res => {
          setNotifications(prev => [...prev, ...res.data]);
        })
        .catch(err => console.error("Bildirim alınamadı", err));
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="manager-dashboard">
      {/* Bildirim kutusu */}
      <div className="notification-wrapper">
        {notifications.map((msg, idx) => (
          <div key={idx} className="notification-item">
            🔔 {msg}
          </div>
        ))}
      </div>

      {/* Sayfanın geri kalanı */}
      <h1>Yönetici Paneli</h1>
      {/* diğer içerikler */}
    </div>
  );
};




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
