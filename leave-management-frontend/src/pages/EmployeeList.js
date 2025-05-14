import React, { useState, useEffect } from "react";

import { getEmployees } from "../api"; // axios yerine api.js'den import 

import { Link } from "react-router-dom";
import "../css/EmployeeList.css"; 

const EmployeeList = () => {
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentFilter, setDepartmentFilter] = useState("");
  
  const [error, setError] = useState(""); // Hata mesajları için state
 

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    
    setError(""); // Her fetch öncesi hatayı temizle
    try {
      const data = await getEmployees(); // api.js'deki fonksiyon
      setEmployees(data.reverse()); 
    } catch (err) {
      console.error("Çalışanları getirirken hata oluştu (EmployeeList):", err);
      setError(err); 
      setEmployees([]); // Hata durumunda listeyi boşalt
    }
    
  };

  const filteredEmployees = employees.filter((employee) => {
    return (
      (searchTerm === "" ||
        (employee.name && employee.name.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (employee.surname && employee.surname.toLowerCase().includes(searchTerm.toLowerCase()))) &&
      (departmentFilter === "" || (employee.department && employee.department.toLowerCase() === departmentFilter.toLowerCase()))
    );
  });

  return (
    <div className="employee-list-container">
      <h1 className="employee-list-title">Çalışan Listesi</h1>

    
      {error && <p className="error-message" style={{ color: "red" }}>Hata: {error}</p>}
     

      <input
        type="text"
        className="search-input"
        placeholder="Çalışan Adı veya Soyadıyla Ara..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />

      <select
        className="department-filter"
        value={departmentFilter}
        onChange={(e) => setDepartmentFilter(e.target.value)}
      >
        <option value="">Tüm Departmanlar</option>
        <option value="Human Resources">İnsan Kaynakları</option>
        <option value="Software Development">Yazılım Geliştirme</option>
        <option value="Graphic Designer">Grafik Tasarımcı</option>
        <option value="Project Manager">Proje Yöneticisi</option>
        <option value="Java Developer">Java Geliştirici</option>
      </select>

      <ul>
        {filteredEmployees.length > 0 ? (
          filteredEmployees.map((employee) => (
            <li key={employee.id} className="employee-item">
              <div className="employee-info">
                <p><strong>{employee.name} {employee.surname}</strong></p>
                <p>{employee.department}</p>
              </div>
              <div>
                <Link to={`/employee-details/${employee.id}`}>
                  <button className="detail-button-list">Detay</button>
                </Link>
                <Link to={`/update-employee/${employee.id}`}>
                  <button className="update-button">Güncelle</button>
                </Link>
              </div>
            </li>
          ))
        ) : (
          !error && <p className="employee-info">Çalışan bulunamadı veya listelenecek çalışan yok.</p> // Hata yoksa bu mesajı göster
        )}
      </ul>
    </div>
  );
};

export default EmployeeList;