import getConfig from "../config";
import axiosInstance from "./axios-instance";

const config = getConfig();

export interface GetAgentsResponse {
    agents: Agent[];
    pageNumber: number;
    pageSize: number;
    totalPages: number;
}

export interface Agent{
    id: string;
    name: string;
    lastContactAt: string | null;
}

export const getAgents = async (resourceGroupId: string, pageNumber: number, pageSize: number) : Promise<GetAgentsResponse> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents?pageNumber=${pageNumber}&pageSize=${pageSize}`,);

        if (response.status !== 200) {
            throw new Error('Failed to get agents');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get agents", error);
        throw error;
    }
}

export interface CreateAgentRequest {
    name: string;
    agentConfig: AgentConfig;
}
interface AgentConfig {
    collectEverySeconds: number;
    isCpuEnabled: boolean;
    isGpuEnabled: boolean;
    isMemoryEnabled: boolean;
    isNetworkEnabled: boolean;
    isControllerEnabled: boolean;
    isMotherboardEnabled: boolean;
    isStorageEnabled: boolean;
}
export const createAgent = async (resourceGroupId: string, request :CreateAgentRequest) => {
    try {
        const response = await axiosInstance.post(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents`, request , {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to create agent');
        }
        return;
    } catch (error) {
        console.error("Failed to create agent", error);
        throw error;
    }
}
export interface GetAgentAvailableMetricsResponse{
    sensors: Sensor[];
}
export interface Sensor{
    name: string;
    metrics: Metric[];
}
export interface Metric{
    name: string;
    types: string[];
}

export const getAgentAvailableMetrics = async (resourceGroupId: string, agentId: string) : Promise<GetAgentAvailableMetricsResponse> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/metrics/availableMetrics`);

        if (response.status !== 200) {
            throw new Error('Failed to get agent available metrics');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get agent available metrics", error);
        throw error;
    }
}