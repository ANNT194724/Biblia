import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { BASE_URL, USER_ROLE } from 'src/Constants';
import ErrorModal from './ErrorModal';
import CIcon from '@coreui/icons-react';
import Select from 'react-select';
import { cilLockLocked, cilLockUnlocked, cilTrash } from '@coreui/icons';
const {
  CRow,
  CForm,
  CCol,
  CFormInput,
  CButton,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CFormLabel,
  CModalFooter,
  CImage,
} = require('@coreui/react');

const UpdateUserModal = (props) => {
  const user = props.user;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [data, setData] = useState({
    user_id: user.user_id,
    role_code: user.role_code,
    status: user.status,
    delete_flag: user.delete_flag,
  });

  const token = useSelector((state) => state.auth.token);

  const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR, USER_ROLE.USER].map((role) => ({
    value: role,
    label: role,
  }));

  useEffect(() => {
    setData({
      user_id: user.user_id,
      role_code: user.role_code,
      status: user.status,
      delete_flag: 1,
    });
  }, [props]);

  const updateUser = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = `${BASE_URL}/admin/user`;

    axios
      .put(apiEndpoint, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setLoading(false);
        props.onHide();
        window.location.reload();
      })
      .catch((err) => {
        setLoading(false);
        setError(err.message);
      });
  };

  return (
    <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
      <CModalHeader>
        <CModalTitle>Thông tin người dùng</CModalTitle>
      </CModalHeader>
      <CModalBody>
        {data.logo_url && (
          <CImage
            thumbnail
            className="mb-3"
            align="center"
            src={data.logo_url}
            width={200}
          ></CImage>
        )}
        <CForm>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên</CFormLabel>
            <CCol>
              <CFormInput type="text" defaultValue={user.username} readOnly plainText />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Email</CFormLabel>
            <CCol>
              <CFormInput type="text" defaultValue={user.login_id} readOnly plainText />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Số điện thoại</CFormLabel>
            <CCol>
              <CFormInput type="text" defaultValue={user.phone_number} readOnly plainText />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Vai trò</CFormLabel>
            <CCol sm={3}>
              <Select
                defaultValue={{ value: user.role_code, label: user.role_code }}
                options={roles}
                onChange={(e) => setData({ ...data, role_code: e.value })}
              />
            </CCol>
          </CRow>
          <CRow className="justify-content-evenly">
            <CCol xs={3}>
              <CButton
                color={data.status === 1 ? 'danger' : 'success'}
                onClick={() => setData({ ...data, status: Number(!data.status) })}
              >
                <CIcon icon={data.status === 1 ? cilLockLocked : cilLockUnlocked} />
                {data.status === 1 ? 'Khoá tài khoản' : 'Mở khóa tài khoản'}
              </CButton>
            </CCol>
            <CCol xs={3}>
              <CButton color="danger" onClick={() => setData({ ...data, delete_flag: 0 })}>
                <CIcon icon={cilTrash} /> Xóa tài khoản
              </CButton>
            </CCol>
          </CRow>
        </CForm>
        {error && (
          <ErrorModal visible={error !== null} onHide={() => setError(null)} message={error} />
        )}
      </CModalBody>
      <CModalFooter>
        <CButton color="secondary" onClick={props.onHide}>
          Đóng
        </CButton>
        <CButton color="primary" onClick={updateUser} disabled={loading}>
          Lưu
        </CButton>
      </CModalFooter>
    </CModal>
  );
};

UpdateUserModal.propsType = {
  onHide: PropTypes.func,
  visible: PropTypes.bool,
  user: PropTypes.object,
};

export default UpdateUserModal;
