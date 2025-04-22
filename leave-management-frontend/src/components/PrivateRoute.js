import React from "react";
import { Navigate } from "react-router-dom";

/**
 * Belirli kullanıcı rollerine göre route'u korur.
 * @param {object} user - Giriş yapan kullanıcı bilgisi
 * @param {array} allowedRoles - İzin verilen roller ["EMPLOYEE", "MANAGER"]
 * @param {ReactNode} children - İçeride render edilecek bileşen
 */
const PrivateRoute = ({ user, allowedRoles, children }) => {
  if (!user) {
    // Kullanıcı yoksa giriş sayfasına yönlendir
    return <Navigate to="/" replace />;
  }

  if (!allowedRoles.includes(user.role)) {
    // Rol yetkisi yoksa da yönlendir
    return <Navigate to="/" replace />;
  }

  return children; // Yetkiliyse devam et
};

export default PrivateRoute;
