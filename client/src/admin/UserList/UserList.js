import {Component} from "react";
import {getRoles, getUsers} from "../../util/APIUtils";
import {Avatar, Col, Row, Table, Tag} from "antd";
import React from "react";
import {getInitials} from "../../util/Utils";
import {API_BASE_URL} from "../../config";
import './UserList.css';

const getRoleFilters = function () {

    let roleFilters = [];

    getRoles().then(response => {
        response.forEach((role) => {
            roleFilters.push({
                text: role,
                value: role
            })
        })
    });

    return roleFilters;
};

const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
        className: 'column-id',
        sorter: true
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
        sorter: true,
    },
    {
        title: 'Username',
        dataIndex: 'userName',
        key: 'userName',
        className: 'column-username',
        sorter: true,
    },
    {
        title: 'Email',
        dataIndex: 'email',
        key: 'email',
        className: 'column-email',
        sorter: true,
    },
    {
        title: 'Roles',
        dataIndex: 'roles',
        key: 'roles',
        className: 'column-roles',
        filters: getRoleFilters(),
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
        this.loadUsers(0);
    }

    loadUsers = (page, sortField, sortOrder, filters, onSuccessCallback) => {
        this.setState({
            isLoading: true
        });
        getUsers(page, sortField, sortOrder, filters).then(response => {
            this.setState({
                page: response,
                isLoading: false
            });
            onSuccessCallback();
        }).catch(error => {
            this.setState({
                isLoading: false
            })
        })
    };

    handleTableChange = (pagination, filters, sorter) => {
        this.loadUsers(pagination.current - 1, sorter.field, sorter.order, filters);
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
                               pageSize: this.state.page.pageable.pageSize,
                               total: this.state.page.totalElements,
                           }}
                           onChange={this.handleTableChange}
                    />
                </Col>
            </Row>
        )
    }
}

export default UserList;