import getConfig from "../config";
import axiosInstance from "./axios-instance";

const config = getConfig();

export interface DashboardListItem{
    id: string;
    name: string;
}

export const getDashboards = async (resourceGroupId: string, agentId: string) : Promise<DashboardListItem[]> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/dashboards`,);

        if (response.status !== 200) {
            throw new Error('Failed to get dashboards');
        }

        return response.data.dashboards;
    } catch (error) {
        console.error("Failed to dashboards", error);
        throw error;
    }
}

export interface Dashboard {
    name: string;
    graphs: Graph[];
}

export interface Graph{
    index: number;
    sensorName: string;
    metricName: string;
    metricType: string;
    lineColor: string;
    unit: string;
}

export const getDashboard = async (resourceGroupId: string, agentId: string, dashboardId: string) : Promise<Dashboard> => {
    try {
        const response = await axiosInstance
            .get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/dashboards/${dashboardId}`,);

        if (response.status !== 200) {
            throw new Error('Failed to get dashboard');
        }

        return response.data;
    } catch (error) {
        console.error("Failed to get dashboard", error);
        throw error;
    }
}
export interface DataPoint {
    time: string;
    value: number;
}

export const getGraphData = async (resourceGroupId: string, agentId: string, dashboardId: string, from: string, to: string, graphIndex: number, maxDataPoints: number, aggregationType: string) : Promise<DataPoint[]> => {
    try {
        const response = await axiosInstance
            .get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/dashboards/${dashboardId}/graphs/${graphIndex}/data`, {params:{
                from,
                to,
                maxDataPoints: maxDataPoints,
                aggregationType: aggregationType
            }});

        if (response.status !== 200) {
            throw new Error('Failed to get graph data');
        }

        return response.data.dataPoints;
    } catch (error) {
        console.error("Failed to get graph data", error);
        throw error;
    }
}
export interface CreateDashboardRequest {
    name: string;
    graphs: Graph[];
}

export const createDashboard = async (resourceGroupId: string, agentId: string, request: CreateDashboardRequest) => {
    try {
        const response = await axiosInstance.post(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/dashboards`, request, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to create dashboard');
        }
        return;
    } catch (error) {
        console.error("Failed to create dashboard", error);
        throw error;
    }
}
