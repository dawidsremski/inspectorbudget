import React, {Component} from 'react';
import './App.css';
import {Layout, notification} from "antd";
import AppHeader from "./AppHeader";
import Login from "./Login/Login";
import {Route, Switch, withRouter} from "react-router-dom";
import {getCurrentUser} from "./APIUtils";
import SignUp from "./SignUp/SignUp";
import {ACCESS_TOKEN} from "./config/index";
const { Content } = Layout;

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: false
        };
        this.handleLogin = this.handleLogin.bind(this);
        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
    }

    componentDidMount() {
        this.loadCurrentUser();
    }

    handleLogin() {
        this.loadCurrentUser(() => {
            notification.success({
                message: "You're successfully logged in.",
                description: `Welcome in Inspector Budget, ${this.state.currentUser.name}!`
            });
            this.props.history.push("/");
        });
    }

    handleLogout() {
        localStorage.removeItem(ACCESS_TOKEN);
        this.setState({
            currentUser: null,
            isAuthenticated: false
        });
        this.props.history.push("/");
        notification.success({
            message: "You're successfully logged out.",
            description: "Good bye!",
        });
    }

    loadCurrentUser(onSuccessCallback) {
        this.setState({
            isLoading: true
        });
        getCurrentUser().then(response => {
            this.setState({
                currentUser: response,
                isAuthenticated: true,
                isLoading: false
            });
            onSuccessCallback();
        }).catch(error => {
            this.setState({
                isLoading: false
            });
        });
    }

    render() {
        return (
            <Layout className="app-container">
                <AppHeader
                    isAuthenticated={this.state.isAuthenticated}
                    currentUser={this.state.currentUser}
                    onLogout={this.handleLogout}
                />
                <Content className="app-content">
                        <Switch>
                            <Route path="/login" render={(props) =>
                                <Login onLogin={this.handleLogin} {...props} />}
                            />
                        </Switch>
                        <Switch>
                            <Route path="/signup" render={(props) =>
                                <SignUp/>
                            }/>
                        </Switch>
                </Content>
            </Layout>
        );
    }
}

export default withRouter(App);
