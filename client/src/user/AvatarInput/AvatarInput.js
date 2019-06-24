import React, {Component} from "react";
import {Upload, Icon, message, Button} from "antd";
import {API_BASE_URL, MAX_AVATAR_UPLOAD_SIZE} from "../../config";
import './AvatarInput.css';

function beforeUpload(file) {
    const isJPG = file.type === 'image/jpeg';
    if (!isJPG) {
        message.error('You can only use jpeg file as an avatar!');
    }
    const isSizeCorrect = file.size / 1024 / 1024 < MAX_AVATAR_UPLOAD_SIZE;
    if (!isSizeCorrect) {
        message.error(`File must be smaller than ${MAX_AVATAR_UPLOAD_SIZE}MB!`);
    }
    return isJPG && isSizeCorrect;
}

class AvatarInput extends Component {
    state = {
        loading: false,
    };

    componentDidMount() {
        if (this.props.currentId !== null) {
            this.setState({
                imageUrl: API_BASE_URL + `/user/avatar?id=${this.props.currentId}`
            });
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.currentId !== prevProps.currentId) {
            this.setState({
                imageUrl: (this.props.currentId !== null) ?
                    API_BASE_URL + `/user/avatar?id=${this.props.currentId}` : ''
            });
        }
    }

    handleChange = info => {
        if (info.file.status === 'uploading') {
            this.setState({loading: true});
            return;
        }
        if (info.file.status === 'done') {
            this.setState({
                imageUrl: API_BASE_URL + info.file.response.avatarUrl,
                loading: false,
            });
            this.props.onChange(info.file.response.avatarId);
        }
    };

    render() {
        const uploadButton = (
            <div>
                <Icon type={this.state.loading ? 'loading' : 'plus'}/>
                <div className="ant-upload-text">Avatar</div>
            </div>
        );
        const imageUrl = this.state.imageUrl;
        return (
            <div style={{
                position: 'relative',
                height: '102px',
                width: '102px'
            }}>
                <Button className="remove-avatar-button"
                        type="danger"
                        shape="circle"
                        size="small"
                        onClick={() => this.props.onRemove()}
                        icon="close"
                        style={{
                            display: (this.props.currentId !== null)? 'block' : 'none'
                        }}>
                </Button>
                <Upload
                    name="file"
                    listType="picture-card"
                    className="avatar-uploader"
                    showUploadList={false}
                    action={this.props.action}
                    beforeUpload={beforeUpload}
                    onChange={this.handleChange}>
                    {imageUrl ? <img className="avatar-img-circle" src={imageUrl} alt="avatar"/> : uploadButton}
                </Upload>
            </div>
        );
    }
}

export default AvatarInput;