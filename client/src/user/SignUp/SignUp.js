import React, {Component} from 'react';
import './SignUp.css';
import {signUp, checkUsernameAvailability, checkEmailAvailability} from '../../util/APIUtils';
import {Link, withRouter} from 'react-router-dom';
import {
    REACT_APP_RECAPTCHA_SITE_KEY,
    NAME_MIN_LENGTH, NAME_MAX_LENGTH,
    USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH,
    EMAIL_MAX_LENGTH,
    PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH, API_BASE_URL
} from '../../config/index';
import {Form, Input, Button, notification, Col, Row} from 'antd';
import AvatarInput from "../AvatarInput/AvatarInput";
import ReCAPTCHA from "react-google-recaptcha";

const FormItem = Form.Item;

class SignUp extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: {
                value: ''
            },
            userName: {
                value: ''
            },
            email: {
                value: ''
            },
            password: {
                value: ''
            },
            reCAPTCHA: {
                response: null
            }
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.onReCAPTCHAChange = this.onReCAPTCHAChange.bind(this);
        this.validateUsernameAvailability = this.validateUsernameAvailability.bind(this);
        this.validateEmailAvailability = this.validateEmailAvailability.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
        this.handleAvatarUpload = this.handleAvatarUpload.bind(this);
    }

    handleInputChange(event, validationFun) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: {
                value: inputValue,
                ...validationFun(inputValue)
            }
        });
    }

    handleSubmit(event) {
        event.preventDefault();

        const signupRequest = {
            name: this.state.name.value,
            email: this.state.email.value,
            userName: this.state.userName.value,
            password: this.state.password.value,
            avatarId: this.state.avatarId,
            reCaptchaResponse: this.state.reCAPTCHA.response
        };
        signUp(signupRequest)
            .then(response => {
                notification.success({
                    message: 'Inspector Budget',
                    description: "Thank you! You're successfully registered. Please login to continue!",
                });
                this.props.history.push("/login");
            }).catch(error => {
            notification.error({
                message: 'Inspector Budget',
                description: error.message || 'Sorry! Something went wrong. Please try again!'
            });
        });
    }

    onReCAPTCHAChange(response, validationFun) {
        this.setState({
            reCAPTCHA: {
                value: response,
                ...validationFun(response)
            }
        });
    }

    validateReCAPTCHA = (response) => {
        if (response !== null) {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        } else {
            return {
                validateStatus: 'error',
                errorMsg: 'Please validate that you\'re a human!'
            }
        }
    };

    isFormInvalid() {
        return !(this.state.name.validateStatus === 'success' &&
            this.state.userName.validateStatus === 'success' &&
            this.state.email.validateStatus === 'success' &&
            this.state.password.validateStatus === 'success' &&
            this.state.reCAPTCHA.validateStatus === 'success'
        );
    }

    handleAvatarUpload(avatarId) {
        this.setState({
            avatarId: avatarId
        })
    }

    render() {
        return (
            <Row className="content-container">
                <Col xl={{span: 8, offset: 8}}
                     md={{span: 12, offset: 6}}
                     sm={{span: 20, offset: 2}}
                     xs={{span: 24}}
                     className="sign-up-box">
                    <h1 className="page-title">Sign up</h1>
                    <div className="sign-up-content">
                        <Form onSubmit={this.handleSubmit} className="sign-up-form">
                            <FormItem
                                label="Full name"
                                validateStatus={this.state.name.validateStatus}
                                help={this.state.name.errorMsg}>
                                <Input
                                    size="large"
                                    name="name"
                                    autoComplete="off"
                                    placeholder="Your full name"
                                    value={this.state.name.value}
                                    onChange={(event) => this.handleInputChange(event, this.validateName)}/>
                            </FormItem>
                            <FormItem label="Username"
                                      hasFeedback
                                      validateStatus={this.state.userName.validateStatus}
                                      help={this.state.userName.errorMsg}>
                                <Input
                                    size="large"
                                    name="userName"
                                    autoComplete="off"
                                    placeholder="A unique username"
                                    value={this.state.userName.value}
                                    onBlur={this.validateUsernameAvailability}
                                    onChange={(event) => this.handleInputChange(event, this.validateUsername)}/>
                            </FormItem>
                            <FormItem
                                label="Email"
                                hasFeedback
                                validateStatus={this.state.email.validateStatus}
                                help={this.state.email.errorMsg}>
                                <Input
                                    size="large"
                                    name="email"
                                    type="email"
                                    autoComplete="off"
                                    placeholder="Your email"
                                    value={this.state.email.value}
                                    onBlur={this.validateEmailAvailability}
                                    onChange={(event) => this.handleInputChange(event, this.validateEmail)}/>
                            </FormItem>
                            <FormItem
                                label="Password"
                                validateStatus={this.state.password.validateStatus}
                                help={this.state.password.errorMsg}>
                                <Input
                                    size="large"
                                    name="password"
                                    type="password"
                                    autoComplete="off"
                                    placeholder="A password between 6 to 20 characters"
                                    value={this.state.password.value}
                                    onChange={(event) => this.handleInputChange(event, this.validatePassword)}/>
                            </FormItem>
                            <FormItem>
                                <AvatarInput action={API_BASE_URL + "/user/avatar"}
                                             onChange={this.handleAvatarUpload}/>
                            </FormItem>
                            <FormItem
                                validateStatus={this.state.reCAPTCHA.validateStatus}
                                help={this.state.reCAPTCHA.errorMsg}>
                                <ReCAPTCHA
                                    ref="recaptcha"
                                    sitekey={REACT_APP_RECAPTCHA_SITE_KEY}
                                    onChange={(response) => this.onReCAPTCHAChange(response, this.validateReCAPTCHA)}
                                />
                            </FormItem>
                            <FormItem>
                                <Button type="primary"
                                        htmlType="submit"
                                        size="large"
                                        className="sign-up-form-button"
                                        disabled={this.isFormInvalid()}>
                                    Sign up
                                </Button>
                                Already registered? <Link to="/login">Login now!</Link>
                            </FormItem>
                        </Form>
                    </div>
                </Col>
            </Row>
        );
    }

    validateName = (name) => {
        if (name.length < NAME_MIN_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Name is too short (Minimum ${NAME_MIN_LENGTH} characters needed.)`
            }
        } else if (name.length > NAME_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Name is too long (Maximum ${NAME_MAX_LENGTH} characters allowed.)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
    };

    validateEmail = (email) => {
        if (!email) {
            return {
                validateStatus: 'error',
                errorMsg: 'Email may not be empty'
            }
        }

        const EMAIL_REGEX = RegExp('[^@ ]+@[^@ ]+\\.[^@ ]+');
        if (!EMAIL_REGEX.test(email)) {
            return {
                validateStatus: 'error',
                errorMsg: 'Email not valid'
            }
        }

        if (email.length > EMAIL_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Email is too long (Maximum ${EMAIL_MAX_LENGTH} characters allowed)`
            }
        }

        return {
            validateStatus: null,
            errorMsg: null
        }
    };

    validateUsername = (userName) => {
        if (userName.length < USERNAME_MIN_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Username is too short (Minimum ${USERNAME_MIN_LENGTH} characters needed.)`
            }
        } else if (userName.length > USERNAME_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Username is too long (Maximum ${USERNAME_MAX_LENGTH} characters allowed.)`
            }
        } else {
            return {
                validateStatus: null,
                errorMsg: null
            }
        }
    };

    validateUsernameAvailability = () => {
        const userNameValue = this.state.userName.value;
        const userNameValidation = this.validateUsername(userNameValue);

        if (userNameValidation.validateStatus === 'error') {
            this.setState({
                userName: {
                    value: userNameValue,
                    ...userNameValidation
                }
            });
            return;
        }

        this.setState({
            userName: {
                value: userNameValue,
                validateStatus: 'validating',
                errorMsg: null
            }
        });

        checkUsernameAvailability(userNameValue)
            .then(response => {
                if (response.available) {
                    this.setState({
                        userName: {
                            value: userNameValue,
                            validateStatus: 'success',
                            errorMsg: null
                        }
                    });
                } else {
                    this.setState({
                        userName: {
                            value: userNameValue,
                            validateStatus: 'error',
                            errorMsg: 'This username is already taken'
                        }
                    });
                }
            }).catch(error => {
            this.setState({
                userName: {
                    value: userNameValue,
                    validateStatus: 'error',
                    errorMsg: 'Can\'t check username availability!'
                }
            });
            notification.error({
                message: 'Inspector Budget',
                description: error.message || 'Sorry! Something went wrong. Please try again!'
            });
        });
    };

    validateEmailAvailability() {
        const emailValue = this.state.email.value;
        const emailValidation = this.validateEmail(emailValue);

        if (emailValidation.validateStatus === 'error') {
            this.setState({
                email: {
                    value: emailValue,
                    ...emailValidation
                }
            });
            return;
        }

        this.setState({
            email: {
                value: emailValue,
                validateStatus: 'validating',
                errorMsg: null
            }
        });

        checkEmailAvailability(emailValue)
            .then(response => {
                if (response.available) {
                    this.setState({
                        email: {
                            value: emailValue,
                            validateStatus: 'success',
                            errorMsg: null
                        }
                    });
                } else {
                    this.setState({
                        email: {
                            value: emailValue,
                            validateStatus: 'error',
                            errorMsg: 'This Email is already registered'
                        }
                    });
                }
            }).catch(error => {
            this.setState({
                email: {
                    value: emailValue,
                    validateStatus: 'error',
                    errorMsg: 'Can\'t check email availability!'
                }
            });
            notification.error({
                message: 'Inspector Budget',
                description: error.message || 'Sorry! Something went wrong. Please try again!'
            });
        });
    }

    validatePassword = (password) => {
        if (password.length < PASSWORD_MIN_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Password is too short (Minimum ${PASSWORD_MIN_LENGTH} characters needed.)`
            }
        } else if (password.length > PASSWORD_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Password is too long (Maximum ${PASSWORD_MAX_LENGTH} characters allowed.)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
    }
}

export default withRouter(SignUp);