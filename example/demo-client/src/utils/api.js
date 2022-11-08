import axios from 'axios';

export function getBaseURL(path = "") {
    return `/api/${path}`;
}

export const api = axios.create({
    baseURL: getBaseURL(),
})