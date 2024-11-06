import getConfig from "../config";
import axiosInstance from "./axios-instance";

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