import React, { useState, useEffect } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  useLocation,
  useNavigate,
} from "react-router-dom";

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
import UserProfile from "./pages/UserProfile";
import PrivateRoute from "./components/PrivateRoute";

function AppWrapper() {
  const [user, setUser] = useState(() => {
    return JSON.parse(localStorage.getItem("user")) || null;
  });

  const [redirected, setRedirected] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // Giriş yapan kullanıcıyı role göre yönlendir
  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser && !redirected) {
      const parsedUser = JSON.parse(storedUser);
      setUser(parsedUser);

      if (location.pathname === "/") {
        if (parsedUser.role === "MANAGER") {
          navigate("/manager-dashboard", { replace: true });
        } else if (parsedUser.role === "EMPLOYEE") {
          navigate("/employee-dashboard", { replace: true });
        }
        setRedirected(true);
      }
    }
  }, [navigate, location.pathname, redirected]);

  return (
    <>
      {user && <Navbar setUser={setUser} />}
      {user && <NotificationBox />}
      <Routes>
        <Route
          path="/"
          element={
            user ? (
              user.role === "EMPLOYEE" ? (
                <Navigate to="/employee-dashboard" />
              ) : (
                <Navigate to="/manager-dashboard" />
              )
            ) : (
              <Login setUser={setUser} />
            )
          }
        />
        <Route path="/register" element={<Register />} />
        <Route
          path="/employee-dashboard"
          element={
            <PrivateRoute user={user} allowedRoles={["EMPLOYEE"]}>
              <EmployeeDashboard user={user} />
            </PrivateRoute>
          }
        />
        <Route
          path="/manager-dashboard"
          element={
            <PrivateRoute user={user} allowedRoles={["MANAGER"]}>
              <ManagerDashboard user={user} />
            </PrivateRoute>
          }
        />
        <Route path="/employees" element={<EmployeeList />} />
        <Route path="/leave-request" element={<LeaveRequest />} />
        <Route path="/add-employee" element={<AddEmployee />} />
        <Route path="/update-employee/:id" element={<UpdateEmployee />} />
        <Route path="/employee-details/:id" element={<EmployeeDetails />} />
        <Route path="/leave-requests/:employeeId" element={<LeaveRequestDetails />} />
        <Route path="/new-leave-request" element={<NewLeaveRequest user={user} />} />
        <Route
          path="/user-profile"
          element={user ? <UserProfile user={user} /> : <Navigate to="/" />}
        />
      </Routes>
    </>
  );
}

function App() {
  return (
    <Router>
      <AppWrapper />
    </Router>
  );
}

export default App;
