import React, {Component} from 'react';
import {Layout, Menu} from "antd";
import {Link, withRouter} from "react-router-dom";

const Header = Layout.Header;

class AppHeader extends Component {

    render() {
        let menuItems;
        if(this.props.currentUser) {
            menuItems = [
            <Menu.Item key="/" onClick={this.props.onLogout}>
                <Link to="/">Logout</Link>
            </Menu.Item>
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

        return(
          <Header className="app-header" style={{background: '#fff'}}>
              <div className="container">
                  <Menu
                      theme="light"
                      mode="horizontal"
                      className="app-menu"
                      selectedKeys={[this.props.location.pathname]}
                      style={{ lineHeight: '64px' }} >
                      {menuItems}
                  </Menu>
              </div>
          </Header>
        );
    }
}

export default withRouter(AppHeader);