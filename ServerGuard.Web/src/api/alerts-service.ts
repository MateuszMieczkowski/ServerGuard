import getConfig from "../config";
import axiosInstance from "./axios-instance";
import { Page } from "./Page";

const config = getConfig();


export interface GetAlertsPageResponse {
    alerts: Page<Alert>;
}

export interface Alert {
    id: string;
    name: string;
    sensorName: string;
    metricName: string;
    metricType: string;
    threshold: number;
    operator: string;
    duration: string;
    groupBy: string;
}

export const getAlertsPage = async (resourceGroupId: string, agentId: string, pageNumber: number, pageSize: number) : Promise<Page<Alert>> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/alerts?pageNumber=${pageNumber}&pageSize=${pageSize}`,);

        if (response.status !== 200) {
            throw new Error('Failed to get alerts');
        }

        return response.data.alerts;
    } catch (error) {
        console.error("Failed to get alerts", error);
        throw error;
    }
}

export interface GetAlertLogsPageResponse {
    alertLogs: Page<AlertLog>;
}

export interface AlertLog {
    id: string; 
    alertId: string;
    name: string;
    sensorName: string;
    metricName: string;
    metricType: string;
    threshold: number; 
    operator: string;
    duration: string;
    groupBy: string;
    triggeredAt: Date;
    triggeredByValue: number;
}

export const getAlertLogsPage = async (resourceGroupId: string, agentId: string, pageNumber: number, pageSize: number) : Promise<Page<AlertLog>> => {
    try {
        const response = await axiosInstance.get(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/alerts/logs?pageNumber=${pageNumber}&pageSize=${pageSize}`,);

        if (response.status !== 200) {
            throw new Error('Failed to get alerts');
        }

        return response.data.alertLogs;
    } catch (error) {
        console.error("Failed to get alerts", error);
        throw error;
    }
}

export const deleteAlert = async (resourceGroupId: string, agentId: string, alertId: string) => {
    try {
        const response = await axiosInstance.delete(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/alerts/${alertId}`);

        if (response.status !== 200) {
            throw new Error('Failed to delete alert');
        }
    } catch (error) {
        console.error("Failed to delete alert", error);
        throw error;
    }
}

export interface Metric{
    sensorName: string;
    metricName: string;
    type: string;
}

export interface CreateAlertRequest {
    name: string;
    metric: Metric;
    threshold: number;
    operator: string;
    duration: string;
    groupBy: string;
}

export const createAlert = async (resourceGroupId: string, agentId: string, request :CreateAlertRequest) => {
    try {
        const response = await axiosInstance.post(`${config.apiUrl}/resourceGroups/${resourceGroupId}/agents/${agentId}/alerts`, request , {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.status !== 200) {
            throw new Error('Failed to create alert');
        }
        return;
    } catch (error) {
        console.error("Failed to create alert", error);
        throw error;
    }
}