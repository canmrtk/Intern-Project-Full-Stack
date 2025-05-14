import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:9090/api", // Backend API adresiniz
  headers: { "Content-Type": "application/json" }
});

// API Hata Yönetimi (Bu fonksiyonu kullanacağız)
const handleError = (error) => {
  if (error.response) {
    // Backend'den gelen bir hata (örn: 400, 404, 500)
    console.error("API Error Response Data:", error.response.data);
    console.error("API Error Response Status:", error.response.status);
    if (typeof error.response.data === 'string') {
      return error.response.data;
    } else if (error.response.data && error.response.data.message) {
      return error.response.data.message; // { message: "..." } yapısı için
    } else if (typeof error.response.data === 'object' && Object.keys(error.response.data).length > 0) {
      // Spring @Valid hataları Map<String, String> olarak gelebilir
      const messages = Object.values(error.response.data);
      return messages.join(", ");
    }
    return `Sunucu hatası: ${error.response.status}`;
  } else if (error.request) {
    // İstek yapıldı ama cevap alınamadı (network error)
    console.error("API Error Request:", error.request);
    return "Sunucuya ulaşılamıyor. Network bağlantınızı kontrol edin.";
  } else {
    // İsteği hazırlarken bir hata oluştu
    console.error("API Error Message:", error.message);
    return `Bir hata oluştu: ${error.message}`;
  }
};

// GET Çalışan Listesi
export const getEmployees = async () => {
  try {
    const response = await api.get("/employees");
    return response.data; // Başarılı durumda veriyi dön
  } catch (error) {
    throw handleError(error); // Hata durumunda işlenmiş hata mesajını fırlat
  }
};

// POST Çalışan Ekle
export const addEmployee = async (employeeData) => {
  try {
    const response = await api.post("/employees", employeeData);
    return response.data; // Başarılı durumda veriyi dön
  } catch (error) {
    throw handleError(error); // Hata durumunda işlenmiş hata mesajını fırlat
  }
};

// Diğer API fonksiyonları buraya eklenebilir (getEmployeeById, updateEmployee, deleteEmployee vb.)
// Örneğin:
export const getEmployeeById = async (id) => {
  try {
    const response = await api.get(`/employees/${id}`);
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
};

export const updateEmployee = async (id, employeeData) => {
  try {
    const response = await api.put(`/employees/${id}`, employeeData);
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
}; 

export const markNotificationsAsSeen = async (userId) => {
  try {

    const response = await api.post(`/notifications/${userId}/seen`);
    return response.data; // Backend'den dönen mesaj ("Tüm bildirimler okundu olarak işaretlendi.") 
  } catch (error) {
    throw handleError(error); 
  }
};

export const createLeaveRequest = async (leaveRequestData) => {
  // leaveRequestData: { employeeEmail, leaveDaysRequested, leaveType }
  try {
    const response = await api.post("/leave-requests/request", leaveRequestData);
    
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
};