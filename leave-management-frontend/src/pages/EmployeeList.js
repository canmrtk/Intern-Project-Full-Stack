import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "../css/EmployeeList.css";

const EmployeeList = () => {
  const [employees, setEmployees] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [departmentFilter, setDepartmentFilter] = useState("");

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      const response = await axios.get("http://localhost:9090/api/employees");
      setEmployees(response.data.reverse()); 
    } catch (error) {
      console.error("Çalışanları getirirken hata oluştu:", error);
    }
  };

  const filteredEmployees = employees.filter((employee) => {
    return (
      (searchTerm === "" || employee.name.toLowerCase().includes(searchTerm.toLowerCase())) &&
      (departmentFilter === "" || employee.department.toLowerCase() === departmentFilter.toLowerCase())
    );
  });

  return (
    <div className="employee-list-container">
      <h1 className="employee-list-title">Çalışan Listesi</h1>

      {/* Arama Kutusu */}
      <input
        type="text"
        className="search-input"
        placeholder="Çalışan Ara..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />

      {/* Departman Filtreleme */}
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

      {/* Çalışan Listesi */}
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
                  <button className="detail-button">Detay</button>
                </Link>
                <Link to={`/update-employee/${employee.id}`}>
                  <button className="update-button">Güncelle</button>
                </Link>
              </div>
            </li>
          ))
        ) : (
          <p className="employee-info">Çalışan bulunamadı.</p>
        )}
      </ul>
    </div>
  );
};

export default EmployeeList;
