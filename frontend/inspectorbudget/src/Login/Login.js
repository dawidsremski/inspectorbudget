import React, {Component} from "react";
import './Login.css';
import {Form, Input, Button, Icon, notification, Row, Col} from 'antd';
import {Link} from "react-router-dom";
import {ACCESS_TOKEN} from "../config/index";
import {login} from "../APIUtils";

const FormItem = Form.Item;

class Login extends Component {
    render() {
        const AntWrappedLoginForm = Form.create()(LoginForm);
        return (
            <Row className="content-container">
                <Col xl={{span: 8, offset: 8}}
                     md={{span: 12, offset: 6}}
                     sm={{span: 20, offset: 2}}
                     xs={{span: 24}}
                     className="login-box">
                    <h1 className="page-title">Login</h1>
                    <div className="login-content">
                        <AntWrappedLoginForm onLogin={this.props.onLogin} />
                    </div>
                </Col>
            </Row>
        );
    }
}

class LoginForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            usernameOrEmail: {
                value: ''
            },
            password: {
                value: ''
            }
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        LoginForm.validateIfNotBlank = LoginForm.validateIfNotBlank.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const loginRequest = Object.assign({}, values);
                login(loginRequest).then(response => {
                    localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                    this.props.onLogin();
                }).catch(error => {
                    if(error.status === 401) {
                        notification.error({
                            message: 'Inspector Budget',
                            description: 'Your username or password is incorrect. Please try again!'
                        });
                    } else {
                        notification.error({
                            message: 'Inspector Budget',
                            description: error.message || 'Sorry! Something went wrong. Please try again!'
                        });
                    }
                })
                // .finally(() => {
                //     this.setState({
                //         usernameOrEmail: {
                //             value: ''
                //         },
                //         password: {
                //             value: ''
                //         }
                //     });
                //     this.props.form.setFieldsValue({
                //         usernameOrEmail: '',
                //         password: ''
                //     });
                // });
            }
        });
    }

    isFormInvalid() {
        return (this.state.usernameOrEmail.validateStatus !== 'success' || this.state.password.validateStatus !== 'success');
    }

    handleInputChange(event, validationFun) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName] : {
                value: inputValue,
                ...validationFun(inputValue)
            }
        });
    }
    
    static validateIfNotBlank(value) {
        return {
            validateStatus: (value !== null && value !== '')? 'success' : 'error' 
        }   
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator('usernameOrEmail', {
                        rules: [{ required: true, message: 'Please input your username or email!' }],
                        initialValue: this.state.usernameOrEmail.value
                    })(
                        <Input
                            prefix={<Icon type="user" />}
                            size="large"
                            name="usernameOrEmail"
                            placeholder="Username or Email"
                            onChange={(event) => this.handleInputChange(event, LoginForm.validateIfNotBlank)}
                        />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator('password', {
                        rules: [{ required: true, message: 'Please input your Password!' }],
                        initialValue: this.state.password.value
                    })(
                        <Input
                            prefix={<Icon type="lock" />}
                            size="large"
                            name="password"
                            type="password"
                            placeholder="Password"
                            onChange={(event) => this.handleInputChange(event, LoginForm.validateIfNotBlank)}
                        />
                    )}
                </FormItem>
                <FormItem>
                    <Button type="primary"
                            htmlType="submit"
                            size="large"
                            className="login-form-button"
                            disabled={this.isFormInvalid()}>
                        Login
                    </Button>
                    Or <Link to="/signup">register now!</Link>
                </FormItem>
            </Form>
        );
    }
}

export default Login;