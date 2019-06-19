require('dotenv').config();
export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
export const RECAPTCHA_SITE_KEY = process.env.REACT_APP_RECAPTCHA_SITE_KEY;
export const ACCESS_TOKEN = 'accessToken';
//Registration form
export const NAME_MIN_LENGTH = 5;
export const NAME_MAX_LENGTH = 40;
export const USERNAME_MIN_LENGTH = 5;
export const USERNAME_MAX_LENGTH = 15;
export const EMAIL_MAX_LENGTH = 40;
export const PASSWORD_MIN_LENGTH = 6;
export const PASSWORD_MAX_LENGTH = 20;
//Max avatar size in MB
export const MAX_AVATAR_UPLOAD_SIZE = 2;
