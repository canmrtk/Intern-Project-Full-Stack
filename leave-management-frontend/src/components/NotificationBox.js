import React, { useEffect, useState } from "react";
import axios from "axios";
import "./NotificationBox.css";

const NotificationBox = () => {
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const userId = localStorage.getItem("userId");
        const response = await axios.get(`http://localhost:9090/api/notifications/${userId}`);
        setNotifications(response.data);
      } catch (error) {
        console.error("Bildirim alınamadı:", error);
      }
    };
  
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 10000);
    return () => clearInterval(interval);
  }, []);
  

  return (
    <div className="notification-box">
      <h4>🔔 Bildirimler</h4>
      <ul>
        {notifications.map((msg, index) => (
          <li key={index}>{typeof msg === "string" ? msg : msg.message}</li>
        ))}
      </ul>
    </div>
  );
};

export default NotificationBox;
