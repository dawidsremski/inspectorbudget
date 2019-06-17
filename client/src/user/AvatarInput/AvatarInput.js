import React from "react";
import {Upload, Icon, message } from "antd";
import {API_BASE_URL} from "../../config";
import './AvatarInput.css';

function beforeUpload(file) {
    const isJPG = file.type === 'image/jpeg';
    if (!isJPG) {
        message.error('You can only use jpeg file as an avatar!');
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
        message.error('File must smaller than 2MB!');
    }
    return isJPG && isLt2M;
}

class AvatarInput extends React.Component {
    state = {
        loading: false,
    };

    handleChange = info => {
        if (info.file.status === 'uploading') {
            this.setState({loading: true});
            return;
        }
        if (info.file.status === 'done') {
            // Get this url from response in real world.
            console.log(info);
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
            <Upload
                name="file"
                listType="picture-card"
                className="avatar-uploader"
                showUploadList={false}
                action={this.props.action}
                beforeUpload={beforeUpload}
                onChange={this.handleChange}>
                {imageUrl ? <img className="avatar-img-circle" src={imageUrl} alt="avatar" /> : uploadButton}
            </Upload>
        );
    }
}

export default AvatarInput;