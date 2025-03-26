import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

import Navbar from "./components/Navbar";
import NotificationBox from "./components/NotificationBox";

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
import Register from "./pages/Register";

function App() {
  const [user, setUser] = useState(() => {
    return JSON.parse(localStorage.getItem("user")) || null;
  });

  useEffect(() => {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  return (
    <Router>
      {user && <Navbar setUser={setUser} />}
      {user && <NotificationBox />} {/* 🔔 Bildirim kutusu sadece giriş yapanlar için */}

      <Routes>
        <Route path="/" element={<Login setUser={setUser} />} />
        <Route path="/register" element={<Register />} />

        <Route
          path="/employee-dashboard"
          element={user && user.role === "EMPLOYEE" ? <EmployeeDashboard user={user} /> : <Navigate to="/" />}
        />
        <Route
          path="/manager-dashboard"
          element={user && user.role === "MANAGER" ? <ManagerDashboard user={user} /> : <Navigate to="/" />}
        />

        <Route path="/employees" element={<EmployeeList />} />
        <Route path="/leave-request" element={<LeaveRequest />} />
        <Route path="/add-employee" element={<AddEmployee />} />
        <Route path="/update-employee/:id" element={<UpdateEmployee />} />
        <Route path="/employee-details/:id" element={<EmployeeDetails />} />
        <Route path="/leave-requests/:employeeId" element={<LeaveRequestDetails />} />
        <Route path="/new-leave-request" element={<NewLeaveRequest user={user} />} />
      </Routes>
    </Router>
  );
}

export default App;
