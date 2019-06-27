import {ACCESS_TOKEN, API_BASE_URL} from "../config";

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    });

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
        .then(response =>
            response.json().then(json => {
                if (!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
        );
};

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function getCurrentUser() {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}

export function getUsers(page = 1, sortField, sortOrder, filters) {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    let sortDirection = (sortOrder === 'ascend') ? 'asc' : 'desc';

    let requestURL = `${API_BASE_URL}/user?`;

    if (!isNaN(page)) {
        requestURL = requestURL.concat(`&page=${page}`)
    }

    if (sortField !== undefined && sortField !== null) {
        requestURL = requestURL.concat(`&sort=${sortField}`);
        if (sortOrder !== undefined && sortOrder !== null) {
            requestURL = requestURL.concat(`,${sortDirection}`);
        }
    }

    if (filters !== undefined && filters !== null) {
        if (filters.roles !== undefined && filters.roles !== null) {
            for (let i = 0; i < filters.roles.length; i++) {
                requestURL = requestURL.concat(
                    (i === 0) ? `&role=${filters.roles[i]}` : `,${filters.roles[i]}`
                )
            }
        }
    }

    return request({
        url: requestURL,
        method: 'GET'
    });
}

export function signUp(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    })
}

export function editProfile(editProfileRequest) {
    return request({
        url: API_BASE_URL + "/user",
        method: 'PATCH',
        body: JSON.stringify(editProfileRequest)
    });
}

export function checkUsernameAvailability(username) {
    return request({
        url: API_BASE_URL + "/user/checkUsernameAvailability?username=" + username,
        method: 'GET'
    });
}

export function checkEmailAvailability(email) {
    return request({
        url: API_BASE_URL + "/user/checkEmailAvailability?email=" + email,
        method: 'GET'
    });
}

export function getRoles() {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/role",
        method: 'GET'
    });
}