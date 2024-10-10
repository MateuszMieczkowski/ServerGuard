import getConfig from "../config";

interface LoginRequest {
    email: string;
    password: string;
}

const config = getConfig();

async function login(credentials: LoginRequest): Promise<string> {
    const response = await fetch(`${config.apiUrl}/auth/login`, {
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

export async function loginUser(email: string, password: string): Promise<boolean> {
    try {
        const token = await login({ email: email, password });
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

export async function register(credentials: RegisterRequest): Promise<boolean> {
    try {
        const response = await fetch(`${config.apiUrl}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(credentials)
        });

        if (!response.ok) {
            throw new Error('Registration failed');
        }

        const data = await response.json();
        return data.success;
    } catch (error) {
        console.error('Registraion failed:', error);
        return false;
    }
}