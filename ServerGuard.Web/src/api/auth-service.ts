import getConfig from "../config";
import axios from 'axios';

const config = getConfig();

async function login(credentials: { email: string; password: string }): Promise<string> {
    try {
        const response = await axios.post(`${config.apiUrl}/auth/login`, credentials, {
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
        const response = await axios.post(`${config.apiUrl}/auth/register`, registerRequest, {
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