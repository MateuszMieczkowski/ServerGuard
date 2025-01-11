interface Config {
  apiUrl: string;
  webSocketUrl: string;
}

const config: Config = {
  apiUrl: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  webSocketUrl: import.meta.env.VITE_WEBSOCKET_URL || "ws://localhost:8080/ws",
};

const getConfig = () => config;

export default getConfig;