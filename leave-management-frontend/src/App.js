import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Employees from "./pages/Employees";
import LeaveRequest from "./pages/LeaveRequest";

function App() {
  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li><a href="/employees">Çalışan Listesi</a></li>
            <li><a href="/leave-request">İzin Talebi</a></li>
          </ul>
        </nav>
        <Routes>
          <Route path="/employees" element={<Employees />} />
          <Route path="/leave-request" element={<LeaveRequest />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
