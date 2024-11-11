import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const axiosInstance = axios.create();

axiosInstance.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      console.log('Unauthenticated, logging out ...');
      localStorage.removeItem('authToken');
      window.dispatchEvent(new Event('authTokenRemoved'));
    }
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.request.use(
    config => {
      const token = localStorage.getItem('authToken');
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    },
    error => {
      return Promise.reject(error);
    }
  );

export default axiosInstance;