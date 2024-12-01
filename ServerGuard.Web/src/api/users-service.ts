import dayjs from "dayjs";
import getConfig from "../config";
import axios from 'axios';
import axiosInstance from "./axios-instance";

const config = getConfig();

async function login(credentials: { email: string; password: string }): Promise<string> {
    try {
        const response = await axios.post(`${config.apiUrl}/users/login`, credentials, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Login failed');
        }

        return response.data.token;
    } catch (error) {
        console.error('Login request failed:', error);
        throw error;
    }
}

export async function loginUser(email: string, password: string): Promise<boolean> {
    try {
        const token = await login({ email, password });
        localStorage.setItem('authToken', token);
        return true;
    } catch (error) {
        console.error('Login failed:', error);
        return false;
    }
}

interface RegisterRequest {
    email: string;
    password: string;
}

export async function register(registerRequest: RegisterRequest): Promise<boolean> {
    try {
        const response = await axios.post(`${config.apiUrl}/users/register`, registerRequest, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Registration failed');
        }

        return true;
    } catch (error) {
        console.error('Registration failed:', error);
        return false;
    }
}

export async function forgotPassword(email: string): Promise<boolean> {
    try {
        const timeZoneId = dayjs.tz.guess() ?? "UTC";
        const response = await axios.post(`${config.apiUrl}/users/reset-password-link`, { email, timeZoneId }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Forgot password failed');
        }

        return true;
    } catch (error) {
        console.error('Forgot password failed:', error);
        return false;
    }
}

export async function resetPassword(token: string, newPassword: string): Promise<boolean> {
    try {
        const response = await axios.post(`${config.apiUrl}/users/reset-password`, { token, newPassword }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Reset password failed');
        }

        return true;
    } catch (error) {
        console.error('Reset password failed:', error);
        return false;
    }
}

export interface UserProfile {
    id: string;
    email: string;
    resourceGroupPermissions: ResoureGroupPermission[];
}

export interface ResoureGroupPermission {
    id: string;
    role: string;
}

export async function getUserProfile(): Promise<UserProfile> {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/users/profile`);

        if (response.status !== 200) {
            throw new Error('Failed to get user profile');
        }

        return response.data;
    } catch (error) {
        console.error('Failed to get profile', error);
        throw error;
    }
}