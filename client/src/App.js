import React, {Component} from 'react';
import './App.css';
import {Layout, notification} from "antd";
import AppHeader from "./AppHeader";
import Login from "./user/Login/Login";
import {Route, Switch, withRouter} from "react-router-dom";
import {getCurrentUser} from "./util/APIUtils";
import SignUp from "./user/SignUp/SignUp";
import {ACCESS_TOKEN} from "./config/index";
import EditProfile from "./user/EditProfile/EditProfile";
import UserList from "./admin/UserList/UserList";

class App extends Component {

    state = {
        currentUser: null,
        isAuthenticated: false,
        isLoading: false
    };

    componentDidMount() {
        this.loadCurrentUser();
    }

    handleLogin = () => {
        this.loadCurrentUser(() => {
            notification.success({
                message: "You're successfully logged in.",
                description: `Welcome in Inspector Budget, ${this.state.currentUser.name}!`
            });
            this.props.history.push("/");
        });
    };

    handleLogout = () => {
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
    };

    loadCurrentUser = (onSuccessCallback) => {
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
    };

    render() {
        return (
            <Layout className="app-container">
                <AppHeader
                    isAuthenticated={this.state.isAuthenticated}
                    currentUser={this.state.currentUser}
                    onLogout={this.handleLogout}
                />
                <Layout className="app-content">
                    <Switch>
                        <Route exact path="/"/>
                        <Route path="/login" render={(props) =>
                            <Login onLogin={this.handleLogin} {...props} />
                        }/>
                        <Route path="/signup" render={(props) =>
                            <SignUp/>
                        }/>
                        <Route path="/profile" render={(props) =>
                            <EditProfile currentUser={this.state.currentUser}
                                         onProfileEdit={this.loadCurrentUser}/>
                        }/>
                        <Route path="/admin/users" render={(props) =>
                            <UserList/>
                        }/>
                    </Switch>
                </Layout>
            </Layout>
        );
    }
}

export default withRouter(App);
