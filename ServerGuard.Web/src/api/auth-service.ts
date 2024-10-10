import getConfig from "../config";

export interface LoginRequest {
    username: string;
    password: string;
}

const config = getConfig();

async function login(credentials: LoginRequest): Promise<string> {
    const response = await fetch(`${config.apiUrl}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    });

    if (!response.ok) {
        throw new Error('Login failed');
    }

    const data = await response.json();
    const token = data.token;
    return token;
}

export async function loginUser(username: string, password: string): Promise<boolean> {
    try {
        const token = await login({ username, password });
        localStorage.setItem('authToken', token);
        return true;
    } catch (error) {
        console.error('Login failed:', error);
        return false;
    }
}