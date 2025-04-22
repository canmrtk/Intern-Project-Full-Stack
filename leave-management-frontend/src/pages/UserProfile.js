import React, { useEffect, useState } from "react";
import axios from "axios";
import "../css/UserProfile.css";

const UserProfile = ({ user }) => {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await axios.get(`http://localhost:9090/api/users/profile?email=${user.email}`);
        setProfile(response.data);
      } catch (err) {
        setError("Profil bilgileri alınamadı.");
      }
    };

    fetchProfile();
  }, [user.email]);

  if (error) {
    return <div className="profile-container"><p className="error">{error}</p></div>;
  }

  if (!profile) {
    return <div className="profile-container"><p>Yükleniyor...</p></div>;
  }

  return (
    <div className="profile-container">
      <h2>Kullanıcı Profilim</h2>
      <p><strong>Ad Soyad:</strong> {profile.name} {profile.surname}</p>
      <p><strong>E-posta:</strong> {profile.email}</p>
      <p><strong>Departman:</strong> {profile.department}</p>
      <p><strong>Rol:</strong> {profile.role}</p>
      <p><strong>Kalan İzin:</strong> {profile.leaveDays} gün</p>
    </div>
  );
};

export default UserProfile;
