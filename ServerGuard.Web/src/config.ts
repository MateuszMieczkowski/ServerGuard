
interface Config {
  apiUrl: string;
}

const config: Config = {
  apiUrl: "http://localhost:8080/api",
};

const getConfig = () => config;

export default getConfig;