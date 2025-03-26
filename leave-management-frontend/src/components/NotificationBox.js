import React, { useEffect, useState } from "react";
import axios from "axios";
import "./NotificationBox.css";

const NotificationBox = () => {
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await axios.get("http://localhost:9090/api/notifications");
        setNotifications(response.data);
      } catch (error) {
        console.error("Bildirim alınamadı:", error);
      }
    };

    // İlk çağırma
    fetchNotifications();

    // Her 10 saniyede bir kontrol et
    const interval = setInterval(fetchNotifications, 10000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="notification-box">
      <h4>🔔 Bildirimler</h4>
      <ul>
        {notifications.map((msg, index) => (
          <li key={index}>{msg}</li>
        ))}
      </ul>
    </div>
  );
};

export default NotificationBox;
