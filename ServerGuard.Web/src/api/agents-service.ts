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

export interface AgentDetails extends Agent{
    config: AgentConfig;
}

export interface AgentConfig {
    collectEverySeconds: number
    apiKey: string
    motherboardEnabled: boolean
    controllerEnabled: boolean
    memoryEnabled: boolean
    gpuEnabled: boolean
    networkEnabled: boolean
    cpuEnabled: boolean
    storageEnabled: boolean
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
    agentConfig: CreateAgentConfig;
}
interface CreateAgentConfig {
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
    types: MetricType[];
}

export interface MetricType{
    name: string;
    unit: string;
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

export const updateAgent = async (resourceGroupId: string, agentId: string, request: CreateAgentRequest) => {
    try {
        const response = await axiosInstance.put(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}`, request, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to update agent');
        }
        return;
    } catch (error) {
        console.error("Failed to update agent", error);
        throw error;
    }
}

export const getAgent = async (resourceGroupId: string, agentId: string) : Promise<AgentDetails> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}`);

        if (response.status !== 200) {
            throw new Error('Failed to get agent');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get agent", error);
        throw error;
    }
}


export const deleteAgent = async (resourceGroupId: string, agentId: string) => {
    try {
        const response = await axiosInstance.delete(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}`, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to delete agent');
        }
        return;
    } catch (error) {
        console.error("Failed to delete agent", error);
        throw error;
    }
}