import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "../css/EmployeeDetails.css";

const EmployeeDetails = () => {
  const { id } = useParams(); 
  const [employee, setEmployee] = useState(null);

  useEffect(() => {
    fetchEmployee();
  }, []);

  const fetchEmployee = async () => {
    try {
      const response = await axios.get(`http://localhost:9090/api/employees/${id}`);
      setEmployee(response.data);
    } catch (error) {
      console.error("Çalışan bilgisi alınamadı:", error);
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
      <p><strong>Kalan İzin Günü:</strong> {employee.leaveDays} Gün</p>
     
    </div>
  );
};

export default EmployeeDetails;
