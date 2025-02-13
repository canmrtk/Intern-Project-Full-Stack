import React, { useState, useEffect } from "react";
import axios from "axios";

const Employees = () => {
  const [employees, setEmployees] = useState([]);

  useEffect(() => {
    axios.get("http://localhost:9090/api/employees")
      .then(response => {
        setEmployees(response.data);
      })
      .catch(error => {
        console.error("API hatası:", error);
      });
  }, []);

  return (
    <div>
      <h2>Çalışan Listesi</h2>
      <ul>
        {employees.map(employee => (
          <li key={employee.id}>
            {employee.name} {employee.surname} - {employee.department} (Kalan izin: {employee.leaveDays} gün)
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Employees;
