import React, {Component} from 'react';
import {withRouter} from 'react-router-dom';
import './EditProfile.css';
import {checkEmailAvailability, editProfile} from '../../util/APIUtils';
import {Form, Input, Button, notification, Col, Row} from 'antd';
import AvatarInput from "../AvatarInput/AvatarInput";
import {
    NAME_MIN_LENGTH, NAME_MAX_LENGTH,
    EMAIL_MAX_LENGTH,
    PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH, API_BASE_URL
} from '../../config/index';

const FormItem = Form.Item;

class EditProfile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: {
                value: '',
                validateStatus: 'success'
            },
            userName: {
                value: ''
            },
            email: {
                value: '',
                validateStatus: 'success'
            },
            password: {
                value: '',
                validateStatus: 'success'
            },
            repeatedPassword: {
                value: '',
                validateStatus: 'success'
            },
            avatarId: {
                value: null
            }
        };
        this.fullfillForm = this.fullfillForm.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.validateEmailAvailability = this.validateEmailAvailability.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
        this.isFormModified = this.isFormModified.bind(this);
        this.handleAvatarUpload = this.handleAvatarUpload.bind(this);
        this.handleAvatarRemove = this.handleAvatarRemove.bind(this);
    }

    componentDidMount() {
        if (this.props.currentUser !== null) {
            this.fullfillForm();
        }
    }

    componentDidUpdate(prevProps) {
        if (prevProps.currentUser !== this.props.currentUser) {
            this.fullfillForm();
        }
    }

    fullfillForm = () => {
        this.setState({
                name: {
                    value: this.props.currentUser.name,
                    validateStatus: 'success'
                },
                userName: {
                    value: this.props.currentUser.userName
                },
                email: {
                    value: this.props.currentUser.email,
                    validateStatus: 'success'
                },
                avatarId: {
                    value: this.props.currentUser.avatarId,
                    validateStatus: 'success'
                }
            }
        );
    };

    validateName = (name) => {
        if (name.length < NAME_MIN_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Name is too short (minimum ${NAME_MIN_LENGTH} characters needed)`
            }
        } else if (name.length > NAME_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Name is too long (maximum ${NAME_MAX_LENGTH} characters allowed)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
    };

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

        const profileEditRequest = {
            name: this.state.name.value,
            email: this.state.email.value,
            password: this.state.password.value,
            repeatedPassword: this.state.repeatedPassword.value,
            avatarId: this.state.avatarId.value
        };
        editProfile(profileEditRequest)
            .then(response => {
                notification.success({
                    message: 'Inspector Budget',
                    description: "Changes successfully saved!",
                });
                this.props.onProfileEdit();
            }).catch(error => {
            notification.error({
                message: 'Inspector Budget',
                description: error.message || 'Sorry! Something went wrong. Please try again!'
            });
        });
    }

    handleAvatarUpload(avatarId) {
        this.setState({
            avatarId: {
                value: avatarId,
                validateStatus: 'success'
            }
        });
    }

    handleAvatarRemove() {
        this.setState({
            avatarId: {
                value: null
            }
        });
    }

    isFormInvalid() {
        return !(this.state.name.validateStatus === 'success' &&
            this.state.email.validateStatus === 'success' &&
            this.state.password.validateStatus === 'success' &&
            this.state.repeatedPassword.validateStatus === 'success'
        );
    }

    isFormModified() {
        if (this.props.currentUser !== null) {
            return (this.state.name.value !== this.props.currentUser.name ||
                this.state.userName.value !== this.props.currentUser.userName ||
                this.state.email.value !== this.props.currentUser.email ||
                this.state.avatarId.value !== this.props.currentUser.avatarId
            )
        } else return false;
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
                errorMsg: 'Email is not valid'
            }
        }

        if (email.length > EMAIL_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Email is too long (maximum ${EMAIL_MAX_LENGTH} characters allowed)`
            }
        }

        return {
            validateStatus: null,
            errorMsg: null
        }
    };

    validateEmailAvailability() {
        const emailValue = this.state.email.value;
        const emailValidation = this.validateEmail(emailValue);

        if (emailValue === this.props.currentUser.email) {
            this.setState({
                email: {
                    value: emailValue,
                    validateStatus: 'success',
                    errorMsg: null
                }
            });
            return;
        }

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
                            errorMsg: 'This email is already registered'
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
        let result = {};
        if (password.length < PASSWORD_MIN_LENGTH) {
            result = {
                validateStatus: 'error',
                errorMsg: `Password is too short (minimum ${PASSWORD_MIN_LENGTH} characters needed)`
            }
        } else if (password.length > PASSWORD_MAX_LENGTH) {
            result = {
                validateStatus: 'error',
                errorMsg: `Password is too long (maximum ${PASSWORD_MAX_LENGTH} characters allowed)`
            }
        } else {
            result = {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
        if (this.state.repeatedPassword.value !== '' && this.state.repeatedPassword.value !== password) {
            this.setState({
                repeatedPassword: {
                    validateStatus: 'error',
                    errorMsg: 'The passwords you entered don\'t match!',
                    value: this.state.repeatedPassword.value
                }
            });
        } else {
            this.setState({
                repeatedPassword: {
                    validateStatus: 'success',
                    errorMsg: null,
                    value: this.state.repeatedPassword.value
                }
            });
        }
        return result;
    };

    validateRepeatedPassword = (repeatedPassword) => {
        if (this.state.password.value !== repeatedPassword) {
            return {
                validateStatus: 'error',
                errorMsg: 'The passwords you entered don\'t match!'
            }
        } else return {
            validateStatus: 'success',
            errorMsg: null
        }
    };

    render() {
        return (
            <Row className="content-container">
                <Col xl={{span: 8, offset: 8}}
                     md={{span: 12, offset: 6}}
                     sm={{span: 20, offset: 2}}
                     xs={{span: 24}}
                     className="sign-up-box">
                    <h1 className="page-title">Edit profile</h1>
                    <div className="sign-up-content">
                        <Form onSubmit={this.handleSubmit} className="sign-up-form">
                            <FormItem
                                required
                                label="Full name"
                                hasFeedback
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
                            <FormItem
                                required
                                label="Username">
                                <Input
                                    disabled
                                    size="large"
                                    name="userName"
                                    value={this.state.userName.value}/>
                            </FormItem>
                            <FormItem
                                required
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
                            {/*<FormItem*/}
                            {/*label="Password"*/}
                            {/*validateStatus={this.state.password.validateStatus}*/}
                            {/*help={this.state.password.errorMsg}>*/}
                            {/*<Input*/}
                            {/*size="large"*/}
                            {/*name="password"*/}
                            {/*type="password"*/}
                            {/*autoComplete="off"*/}
                            {/*placeholder={`A password between ${PASSWORD_MIN_LENGTH} to ${PASSWORD_MAX_LENGTH} characters`}*/}
                            {/*value={this.state.password.value}*/}
                            {/*onChange={(event) => this.handleInputChange(event, this.validatePassword)}/>*/}
                            {/*</FormItem>*/}
                            {/*<FormItem*/}
                            {/*className="repeated-password-form-item"*/}
                            {/*label="Repeat password"*/}
                            {/*validateStatus={this.state.repeatedPassword.validateStatus}*/}
                            {/*help={this.state.repeatedPassword.errorMsg}>*/}
                            {/*<Input*/}
                            {/*size="large"*/}
                            {/*name="repeatedPassword"*/}
                            {/*type="password"*/}
                            {/*autoComplete="off"*/}
                            {/*placeholder="Repeated password"*/}
                            {/*value={this.state.repeatedPassword.value}*/}
                            {/*onChange={(event) => this.handleInputChange(event, this.validateRepeatedPassword)}/>*/}
                            {/*</FormItem>*/}
                            <FormItem>
                                <AvatarInput action={API_BASE_URL + "/user/avatar"}
                                             onChange={this.handleAvatarUpload}
                                             onRemove={this.handleAvatarRemove}
                                             currentId={this.state.avatarId.value}/>
                            </FormItem>
                            <FormItem>
                                <Button type="primary"
                                        htmlType="submit"
                                        size="large"
                                        className="sign-up-form-button"
                                        disabled={this.isFormInvalid() || !this.isFormModified()}>
                                    Save
                                </Button>
                                <Button
                                    size="large"
                                    onClick={() => this.fullfillForm()}
                                    disabled={!this.isFormModified()}>
                                    Reset
                                </Button>
                            </FormItem>
                        </Form>
                    </div>
                </Col>
            </Row>
        );
    }
}

export default withRouter(EditProfile);