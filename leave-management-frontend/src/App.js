import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import EmployeeList from "./pages/EmployeeList";
import LeaveRequest from "./pages/LeaveRequest";
import AddEmployee from "./pages/AddEmployee";
import UpdateEmployee from "./pages/UpdateEmployee";

import EmployeeDetails from "./pages/EmployeeDetails";

import LeaveRequestDetails from "./pages/LeaveRequestDetails";






function App() {
  return (
    <Router>
      {/* Navbar */}
      <nav>
        <ul>
          <li><Link to="/employees">Çalışan Listesi</Link></li>
          <li><Link to="/leave-request">İzin Talebi</Link></li>
          <li><Link to="/add-employee">Çalışan Ekle</Link></li>
        </ul>
      </nav>

      {/* Rotalar */}
      <Routes>
        <Route path="/employees" element={<EmployeeList />} />
        <Route path="/leave-request" element={<LeaveRequest />} />
        <Route path="/add-employee" element={<AddEmployee />} />
        <Route path="/update-employee/:id" element={<UpdateEmployee />} />
        <Route path="/employee-details/:id" element={<EmployeeDetails />} />
        <Route path="/leave-requests/:employeeId" element={<LeaveRequestDetails />} />
      </Routes>
    </Router>
  );
}

export default App;
