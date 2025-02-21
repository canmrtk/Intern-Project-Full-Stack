import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import EmployeeList from "./pages/EmployeeList";
import LeaveRequest from "./pages/LeaveRequest";
import AddEmployee from "./pages/AddEmployee";
import UpdateEmployee from "./pages/UpdateEmployee";

import EmployeeDetails from "./pages/EmployeeDetails";

import LeaveRequestDetails from "./pages/LeaveRequestDetails";

import NewLeaveRequest from "./pages/NewLeaveRequest";

import Login from "./pages/Login";
import EmployeeDashboard from "./pages/EmployeeDashboard";
import ManagerDashboard from "./pages/ManagerDashboard";






function App() {
  return (
    <Router>
      {/* Navbar */}
      <nav>
        <ul>
          <li><Link to="/employees">Çalışan Listesi</Link></li>
          <li><Link to="/leave-request">Geçmiş İzin Listesi</Link></li>
          <li><Link to="/add-employee">Çalışan Ekle</Link></li>
          <li><Link to="/new-leave-request">İzin Talep et</Link></li>
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
        <Route path="/new-leave-request" element={<NewLeaveRequest />} />
        <Route path="/" element={<Login setUser={setUser} />} />
        {user && user.role === "EMPLOYEE" && <Route path="/employee-dashboard" element={<EmployeeDashboard />} />}
        {user && user.role === "MANAGER" && <Route path="/manager-dashboard" element={<ManagerDashboard />} />}
      
      </Routes>
    </Router>
  );
}

export default App;
