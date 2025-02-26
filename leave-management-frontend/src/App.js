import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from "react-router-dom";
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
  const [user, setUser] = useState(null);

  return (
    <Router>
      {/*<nav>
        <ul>
          <li><Link to="/">Ana Sayfa</Link></li>
          {user?.role === "MANAGER" && <li><Link to="/manager-dashboard">Yönetici Paneli</Link></li>}
          {user?.role === "EMPLOYEE" && <li><Link to="/employee-dashboard">Çalışan Paneli</Link></li>}
        </ul>
      </nav>*/}

      <Routes>
        <Route path="/" element={<Login setUser={setUser} />} />
        <Route path="/register" element={<Register />} />

        {/* Kullanıcı giriş yapmadıysa giriş ekranına yönlendirme yap */}
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
