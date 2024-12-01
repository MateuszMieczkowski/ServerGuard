import { InvalidTokenError } from "jwt-decode";
import getConfig from "../config";
import axiosInstance from "./axios-instance";
import { Page } from "./Page";

const config = getConfig();


export const createResourceGroup = async (name: string) =>{
    try {
        const response = await axiosInstance.post(`${config.apiUrl}/resourceGroups`, {name}, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to create resource group');
        }
        return;
    } catch (error) {
        console.error("Failed to create resource group", error);
        throw error;
    }
}
export interface ResourceGroup {
    id: string;
    name: string;
}

export interface GetResourceGroupsResponse {
    resourceGroups: ResourceGroup[];
    pageNumber: number;
    pageSize: number;
    totalPages: number;
}

export const getResourceGroups = async (pageNumber: number, pageSize: number) : Promise<GetResourceGroupsResponse> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups?pageNumber=${pageNumber}&pageSize=${pageSize}`,);

        if (response.status !== 200) {
            throw new Error('Failed to get resource groups');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get resource groups", error);
        throw error;
    }
}

export const getResourceGroup = async (resourceGroupId: string) : Promise<ResourceGroup> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}`,);

        if (response.status !== 200) {
            throw new Error('Failed to get resource group');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get resource group", error);
        throw error;
    }
}

export const updateResourceGroup = async (resourceGroupId: string, name: string) => {
    try {
        const response = await axiosInstance.put(`${config.apiUrl}/resourceGroups/${resourceGroupId}`, {name}, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to update resource group');
        }
        return;
    } catch (error) {
        console.error("Failed to update resource group", error);
        throw error;
    }
}

export interface User {
    id: string;
    email: string;
    role: string;
}

export const getUsers = async (resourceGroupId: string, pageNumber: number, pageSize: number) : Promise<Page<User>> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/users?pageNumber=${pageNumber}&pageSize=${pageSize}`);

        if (response.status !== 200) {
            throw new Error('Failed to get users');
        }

        return response.data.users;
    } catch (error) {
        console.error("Failed to get users", error);
        throw error;
    }
 }

 export const deleteUser = async (resourceGroupId: string, userId: string) => {
    try {
        const response = await axiosInstance.delete(`${config.apiUrl}/resourceGroups/${resourceGroupId}/users/${userId}`);

        if (response.status !== 200) {
            throw new Error('Failed to delete user');
        }
        return;
    } catch (error) {
        console.error("Failed to delete user", error);
        throw error;
    }
 }

 export const inviteUser = async (resourceGroupId: string, email: string, role: string) => {
    try {
        const response = await axiosInstance.post(`${config.apiUrl}/resourceGroups/${resourceGroupId}/invitations`, {email, role}, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to invite user');
        }
        return;
    } catch (error) {
        console.error("Failed to invite user", error);
        throw error;
    }
 }

 export interface Invitation {
    email: string;
    role: string;
    resourceGroupName: string;
 }

 export const getInvitation = async (resourceGroupId: string, token: string) : Promise<Invitation> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/invitations/${token}`);

        if (response.status !== 200) {
            throw new Error('Failed to get invitation');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get invitation", error);
        throw error;
    }
 }

 export const acceptInvitation = async (resourceGroupId: string, token: string) => {
    try {
        const response = await axiosInstance.post(`${config.apiUrl}/resourceGroups/${resourceGroupId}/invitations/${token}`);

        if (response.status !== 200) {
            throw new Error('Failed to accept invitation');
        }
        return;
    } catch (error) {
        console.error("Failed to accept invitation", error);
        throw error;
    }
 }