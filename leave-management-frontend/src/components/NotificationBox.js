// ./src/components/NotificationBox.js
import React, { useEffect, useState } from "react"; // useState'i import ettiğinizden emin olun
import axios from "axios";
import "./NotificationBox.css"; // CSS dosyanızın yolu doğruysa

const NotificationBox = () => {
  const [notifications, setNotifications] = useState([]);
  // ---- DEĞİŞİKLİK BAŞLANGICI ----
  const [errorMessage, setErrorMessage] = useState(""); // Hata mesajları için state

  // Hata mesajını ayarlamak için yardımcı fonksiyon
  const setMessageStateOnError = (message) => {
    setErrorMessage(message);
  };
  // ---- DEĞİŞİKLİK SONU ----

  useEffect(() => {
    const fetchNotifications = async () => {
      const userId = localStorage.getItem("userId");
      console.log("NotificationBox - Attempting to fetch notifications. Raw userId from localStorage:", userId);

      // ---- ÖNCEKİ CEVAPTAN GELEN KONTROL ----
      if (!userId || userId === "undefined" || userId === "null") {
        console.warn("NotificationBox: Kullanıcı ID bulunamadı veya geçersiz (localStorage değeri: '" + userId + "'). Istek yapılmayacak.");
        setNotifications([]);
        // ---- DEĞİŞİKLİK: Hata mesajını burada da ayarlayabiliriz ----
        setMessageStateOnError("Bildirimleri almak için kullanıcı ID bulunamadı.");
        // ---- DEĞİŞİKLİK SONU ----
        return;
      }
      // ---- KONTROL SONU ----

      // ---- DEĞİŞİKLİK BAŞLANGICI: Her istek öncesi eski hata mesajını temizle ----
      setErrorMessage("");
      // ---- DEĞİŞİKLİK SONU ----

      try {
        const apiUrl = `http://localhost:9090/api/notifications/${userId}`;
        console.log("NotificationBox - Fetching notifications from URL:", apiUrl);
        const response = await axios.get(apiUrl);
        console.log("NotificationBox - API response:", response);

        if (response.data) {
          setNotifications(response.data);
        } else {
          setNotifications([]);
          console.warn("NotificationBox - API'den boş veya tanımsız veri geldi.");
          // İsteğe bağlı: setMessageStateOnError("Bildirimler alınamadı veya boş geldi.");
        }
      } catch (error) {
        console.error("NotificationBox - Bildirimler alınırken HATA oluştu:", error);
        if (error.response) {
          console.error("Error response data:", error.response.data);
          console.error("Error response status:", error.response.status);
          console.error("Error response headers:", error.response.headers);
          setMessageStateOnError(typeof error.response.data === 'string' ? error.response.data : `Sunucu hatası: ${error.response.status}`);
        } else if (error.request) {
          console.error("Error request:", error.request);
          setMessageStateOnError("Sunucuya ulaşılamadı. Network bağlantınızı kontrol edin.");
        } else {
          console.error("Error message:", error.message);
          setMessageStateOnError(`Bir hata oluştu: ${error.message}`);
        }
        setNotifications([]); // Hata durumunda bildirimleri temizle
      }
    };

    fetchNotifications();
    const interval = setInterval(fetchNotifications, 10000); // 10 saniyede bir
    return () => clearInterval(interval);
  }, []); // Bağımlılık dizisi boş

  return (
    <div className="notification-box">
      <h4><span role="img" aria-label="notification">🔔</span> Bildirimler</h4>
      {/* ---- DEĞİŞİKLİK BAŞLANGICI: Hata mesajını göster ---- */}
      {errorMessage && <p className="error-message-notification" style={{ color: 'red', fontSize: '12px' }}>{errorMessage}</p>}
      {/* ---- DEĞİŞİKLİK SONU ---- */}
      <ul>
        {notifications.length === 0 && !errorMessage && (
          <li>Okunmamış bildiriminiz yok.</li>
        )}
        {notifications.map((notificationItem, index) => ( // msg yerine notificationItem kullandım, daha açıklayıcı
          <li key={notificationItem.id || index}> {/* Backend'den ID geliyorsa onu kullanmak daha iyi */}
            {typeof notificationItem === "string" ? notificationItem : notificationItem.message}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NotificationBox;