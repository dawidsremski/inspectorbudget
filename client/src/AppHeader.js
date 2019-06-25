import React, {Component} from 'react';
import {Avatar, Layout, Menu} from "antd";
import {Link, withRouter} from "react-router-dom";
import './AppHeader.css';
import SubMenu from "antd/lib/menu/SubMenu";
import {API_BASE_URL} from "./config";
import {getInitials} from "./util/Utils";

const Header = Layout.Header;

class AppHeader extends Component {

    render() {
        let menuItems;
        if (this.props.currentUser) {
            menuItems = [
                <Menu.Item key="/">
                    <Link to="/">Home</Link>
                </Menu.Item>,
                this.adminMenu(),
                <SubMenu className="menu-item-right"
                         key="profile-menu"
                         trigger={['click']}
                         title={
                             <span className="submenu-title-wrapper">
                                 <Avatar
                                         src={(this.props.currentUser.avatarId !== null) ?
                                             API_BASE_URL
                                             + '/user/avatar?id='
                                             + this.props.currentUser.avatarId : ""}>
                                     {getInitials(this.props.currentUser.name)}
                                 </Avatar>

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
                        <Link to="/profile">Edit profile</Link>
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

    adminMenu = () => {
        if (this.props.currentUser.roles.includes("ROLE_ADMIN")) {
            return ([
                <Menu.Item key="/admin/users">
                    <Link to="/admin/users">Users</Link>
                </Menu.Item>,
                <Menu.Item key="/admin/settings">
                    <Link to="/admin/settings">Settings</Link>
                </Menu.Item>
            ])
        }
    };
}

export default withRouter(AppHeader);