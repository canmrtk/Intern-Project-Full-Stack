import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Employees from "./pages/Employees";
import LeaveRequest from "./pages/LeaveRequest";
import AddEmployee from "./pages/AddEmployee";
import UpdateEmployee from "./pages/UpdateEmployee";



function App() {
  return (
    <Router>
      
      <nav>
        <ul>
          <li><Link to="/employees">Çalışan Listesi</Link></li>
          <li><Link to="/leave-request">İzin Talebi</Link></li>
          <li><Link to="/add-employee">Çalışan Ekle</Link></li>
        </ul>
      </nav>
      <Routes>
        <Route path="/employees" element={<Employees />} />
        <Route path="/leave-request" element={<LeaveRequest />} />
        <Route path="/add-employee" element={<AddEmployee />} />
        <Route path="/update-employee/:id" element={<UpdateEmployee />} />
      </Routes>
    </Router>
  );
}

export default App;
