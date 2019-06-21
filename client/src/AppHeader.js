import React, {Component} from 'react';
import {Avatar, Layout, Menu} from "antd";
import {Link, withRouter} from "react-router-dom";
import './AppHeader.css';
import SubMenu from "antd/lib/menu/SubMenu";
import {API_BASE_URL} from "./config";

const Header = Layout.Header;

class AppHeader extends Component {

    render() {
        let menuItems;
        if (this.props.currentUser) {
            menuItems = [
                <Menu.Item key="/">
                    <Link to="/">Home</Link>
                </Menu.Item>,
                <SubMenu className="menu-item-right"
                         key="profile-menu"
                         trigger={['click']}
                         title={
                             <span className="submenu-title-wrapper">
                                 <Avatar icon="user"
                                         src={(this.props.currentUser.avatarURL !== null) ? API_BASE_URL + this.props.currentUser.avatarURL : ""}/>
                                    Profile
                             </span>
                         }>
                    <li className="profile-dropdown-info">
                        <p className="info-name">
                            {this.props.currentUser.name}
                        </p>
                        <p className="info-username">
                            @{this.props.currentUser.userName}
                        </p>
                    </li>
                    <Menu.Divider/>
                    <Menu.Item key="/profile">
                        <Link to="/profile">Profile</Link>
                    </Menu.Item>
                    <Menu.Item key="logout" onClick={this.props.onLogout}>
                        Logout
                    </Menu.Item>
                </SubMenu>
            ];
        } else {
            menuItems = [
                <Menu.Item key="/login">
                    <Link to="/login">Login</Link>
                </Menu.Item>,
                <Menu.Item key="/signup">
                    <Link to="/signup">Sign up</Link>
                </Menu.Item>
            ];
        }

        return (
            <Header className="app-header" style={{background: '#fff'}}>
                <div className="container">
                    <Menu
                        theme="light"
                        mode="horizontal"
                        className="app-menu"
                        selectedKeys={[this.props.location.pathname]}
                        style={{lineHeight: '64px'}}>
                        {menuItems}
                    </Menu>
                </div>
            </Header>
        );
    }
}

export default withRouter(AppHeader);