import axios from 'axios';

export function uploadEntryImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  return axios.post('http://localhost:8080/api/ocr/entry', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}