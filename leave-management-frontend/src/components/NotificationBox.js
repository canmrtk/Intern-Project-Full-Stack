import React, { useEffect, useState, useCallback } from "react";
import "./NotificationBox.css";
import { markNotificationsAsSeen } from "../api";


import axios from "axios"; 



const NotificationBox = () => {
  const [notifications, setNotifications] = useState([]);
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [userId, setUserId] = useState(localStorage.getItem("userId"));

  const setMessageStateOnError = (message) => {
    setErrorMessage(message);
  };

  const fetchNotifications = useCallback(async () => {
    // console.log("NotificationBox callback - userId:", userId); // userId'yi kontrol et
    if (!userId || userId === "undefined" || userId === "null") {
    
      return;
    }
    
    setErrorMessage("");
    setIsLoading(true);

    try {
      
      const response = await axios.get(`http://localhost:9090/api/notifications/${userId}`);
      
      
      if (response.data) {
        setNotifications(response.data);
      } else {
        setNotifications([]);
      }
    } catch (error) {
      console.error("NotificationBox - Error fetching notifications:", error);
      if (error.response) {
        setMessageStateOnError(typeof error.response.data === 'string' ? error.response.data : `Sunucu hatası: ${error.response.status}`);
      } else if (error.request) {
        setMessageStateOnError("Sunucuya ulaşılamadı.");
      } else {
        setMessageStateOnError(`Bir hata oluştu: ${error.message}`);
      }
      setNotifications([]);
    } finally {
      setIsLoading(false);
    }
  }, [userId]); // userId bağımlılığını koru

  useEffect(() => {
    const currentUserId = localStorage.getItem("userId");
    // console.log("NotificationBox useEffect - currentUserId from localStorage:", currentUserId, "state userId:", userId);
    if (currentUserId !== userId) {
        setUserId(currentUserId);
    }

    
    if (userId && userId !== "null" && userId !== "undefined") {
        fetchNotifications(); 
        const interval = setInterval(fetchNotifications, 10000);
        return () => clearInterval(interval);
    } else {
        
        setNotifications([]);
        
    }
  }, [userId, fetchNotifications]);


  const handleMarkAsSeen = async () => {
    if (!userId) {
      setMessageStateOnError("Kullanıcı ID bulunamadığı için bildirimler okundu olarak işaretlenemedi.");
      return;
    }
    setErrorMessage("");
    setIsLoading(true);
    try {
      const responseMessage = await markNotificationsAsSeen(userId);
      // console.log(responseMessage);
      setNotifications([]); 
      
      // setMessageStateOnError("Tüm bildirimler okundu olarak işaretlendi."); // Bunu farklı bir state ile yönetmek daha iyi olabilir (örn: successMessage)
    } catch (err) {
      console.error("Bildirimleri okundu işaretlerken hata:", err);
      setMessageStateOnError(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="notification-box">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '5px' }}>
        <h4><span role="img" aria-label="notification">🔔</span> Bildirimler</h4>
        {notifications.length > 0 && (
          <button 
            onClick={handleMarkAsSeen} 
            disabled={isLoading}
            style={{ fontSize: '10px', padding: '2px 5px', cursor: 'pointer', border: '1px solid #ccc', borderRadius: '3px' }}
            title="Tümünü okundu işaretle"
          >
            ✔️ Hepsini Oku
          </button>
        )}
      </div>

      {errorMessage && <p className="error-message-notification" style={{ color: "red", fontSize: '12px', margin: '5px 0' }}>{errorMessage}</p>}
      
      {isLoading && notifications.length === 0 && <p style={{fontSize: '12px', color: '#555'}}>Yükleniyor...</p>}

      <ul>
        {!isLoading && notifications.length === 0 && !errorMessage && (
          <li style={{fontSize: '12px', color: '#777', fontStyle: 'italic'}}>Okunmamış bildiriminiz yok.</li>
        )}
        {notifications.map((notificationItem) => (
          
          <li key={notificationItem.id ? notificationItem.id : notificationItem.message}> 
            {notificationItem.message}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NotificationBox;