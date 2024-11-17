import { jwtDecode } from "jwt-decode";

export const isAuthenticated = () => {
    var jwtToken = localStorage.getItem("authToken");
    if(jwtToken === null) {
        return false;
    }
    var jwt = jwtDecode(jwtToken);
    if(jwt === null) {
        return false;
    }
    if(jwt.exp! < Date.now() / 1000) {
        return false;
    }
    return true;
}