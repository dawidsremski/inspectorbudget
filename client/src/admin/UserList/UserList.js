import {Component} from "react";
import {getUsers} from "../../util/APIUtils";
import {Avatar, Col, Row, Table, Tag} from "antd";
import React from "react";
import {getInitials} from "../../util/Utils";
import {API_BASE_URL} from "../../config";
import './UserList.css';

const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
        className: 'column-id',
        sortOrder: 'ascend',
        defaultSortOrder: 'ascend',
        selectable: true
    },
    {
        title: 'Avatar',
        dataIndex: 'avatarId',
        key: 'avatarId',
        className: 'column-avatar',
        render: (text, record) =>
            <Avatar src={(text !== null) ? `${API_BASE_URL}/user/avatar?id=${text}` : ''}>
                {getInitials(record.name)}
            </Avatar>,
    },
    {
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        className: 'column-name',
        hideOnSmall: true
    },
    {
        title: 'Username',
        dataIndex: 'userName',
        key: 'userName',
        className: 'column-username',
    },
    {
        title: 'Email',
        dataIndex: 'email',
        key: 'email',
        className: 'column-email',
    },
    {
        title: 'Roles',
        dataIndex: 'roles',
        key: 'roles',
        className: 'column-roles',
        render: roles =>
            <span>
                {roles.map(role =>
                    <Tag color={(role === 'ROLE_ADMIN') ? 'volcano' : 'geekblue'} key={role}>
                        {role.split('_')[1]}
                    </Tag>
                )}
                </span>
    }
];

class UserList extends Component {

    state = {
        page: {
            pageable: {}
        },
        currentPage: 0,
        isLoading: false
    };


    componentDidMount() {
        this.loadUsers();
    }

    loadUsers = (onSuccessCallback) => {
        this.setState({
            isLoading: true
        });
        getUsers(this.state.currentPage).then(response => {
            this.setState({
                page: response,
                isLoading: false
            });
            console.log(this.state);
            onSuccessCallback();
        }).catch(error => {
            this.setState({
                isLoading: false
            });
        });
    };

    handlePageChange = (page) => {
        this.setState({
            currentPage: page
        }, () => this.loadUsers());
    };

    render() {
        return (
            <Row className="content-container">
                <Col xl={{span: 20, offset: 2}}
                     sm={{span: 24, offset: 0}}
                     className="sign-up-box">
                    <h1 className="page-title">Users</h1>
                    <Table columns={columns}
                           rowKey='id'
                           dataSource={this.state.page.content}
                           pagination={{
                               current: this.state.currentPage + 1,
                               pageSize: this.state.page.pageable.pageSize,
                               total: this.state.page.totalElements,
                               onChange: page => this.handlePageChange(page - 1)
                           }}
                    />
                    }
                </Col>
            </Row>
        )
            ;
    }
}

export default UserList;