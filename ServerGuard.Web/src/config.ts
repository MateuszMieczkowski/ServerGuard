import { apiUrl, webSocketUrl } from '../config.json';

interface Config {
  apiUrl: string;
  webSocketUrl: string;
}

const config: Config = {
  apiUrl: apiUrl,
  webSocketUrl: webSocketUrl
};

const getConfig = () => config;

export default getConfig;