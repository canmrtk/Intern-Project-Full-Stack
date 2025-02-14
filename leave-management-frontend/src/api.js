import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:9090/api",
  headers: { "Content-Type": "application/json" }
});

// API Hata Yönetimi
const handleError = (error) => {
  if (error.response) {
    if (error.response.status === 404) {
      return "Aradığınız kayıt bulunamadı.";
    }
    if (error.response.status === 400) {
      return "Eksik veya hatalı bilgi girdiniz.";
    }
    if (error.response.status === 500) {
      return "Sunucu hatası! Lütfen daha sonra tekrar deneyin.";
    }
    return error.response.data;
  } else if (error.request) {
    return "Sunucuya ulaşılamıyor. Lütfen internet bağlantınızı kontrol edin.";
  } else {
    return "Bilinmeyen bir hata oluştu.";
  }
};

// GET Çalışan Listesi
export const getEmployees = async () => {
  try {
    const response = await api.get("/employees");
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
};

// POST Çalışan Ekle
export const addEmployee = async (employeeData) => {
  try {
    const response = await api.post("/employees", employeeData);
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
};

// PUT Çalışan Güncelle
export const updateEmployee = async (id, employeeData) => {
  try {
    const response = await api.put(`/employees/${id}`, employeeData);
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
};

// GET Tek Bir Çalışanı Getir
export const getEmployeeById = async (id) => {
  try {
    const response = await api.get(`/employees/${id}`);
    return response.data;
  } catch (error) {
    throw handleError(error);
  }
};
