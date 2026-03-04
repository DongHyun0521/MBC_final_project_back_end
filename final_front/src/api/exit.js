import axios from 'axios';

export function uploadExitImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  return axios.post('http://localhost:8080/api/ocr/exit', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}

export function requestPayment(paymentData) {
  return axios.post('http://localhost:8080/api/payment', paymentData);
}