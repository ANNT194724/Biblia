import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginSuccess } from 'src/redux/actions/authAction';
import {
  CContainer,
  CRow,
  CCol,
  CCard,
  CCardBody,
  CForm,
  CFormLabel,
  CFormInput,
  CButton,
  CButtonGroup,
  CAlert,
  CAvatar,
  CSpinner,
} from '@coreui/react';
import axios from 'axios';
import moment from 'moment/moment';
import storage from 'src/firebase';

const Profile = () => {
  const [username, setUsername] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [edit, setEdit] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingUserData, setLoadingUserData] = useState(true);
  const [userData, setUserData] = useState(null);
  const [rerender, setRerender] = useState(false);
  const token = useSelector((state) => state.auth.token);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleClick = () => {
    let userDataReset = {
      token: '',
      id: 0,
      email: '',
      username: '',
    };
    dispatch(loginSuccess(userDataReset));
    navigate('/login');
  };

  const getProfile = () => {
    fetch(`http://localhost:8080/user/profile`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setUserData(data);
        setUsername(data.username);
        setPhoneNumber(data.phone_number);
        setLoadingUserData(false);
        dispatch(loginSuccess(data, token));
      });
  };

  useEffect(() => {
    if (token !== null) {
      getProfile();
    }
  }, [token, rerender]);

  const handleAvatarUpload = (event) => {
    setLoading(true);
    const file = event.target.files[0];
    if (file) {
      const storageRef = storage.ref();
      const avatarRef = storageRef.child(`avatars/${userData.user_id}/${file.name}`);

      avatarRef.put(file).then(() => {
        avatarRef.getDownloadURL().then((url) => {
          setAvatarUrl(url);
          setLoading(false);
          setRerender(!rerender);
        });
      });
    }
  };

  const handleUpdate = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = 'http://localhost:8080/user/profile';

    const data = {
      username,
      avatar_url: avatarUrl,
      phone_number: phoneNumber,
    };

    if (username === '') {
      setError('Username không được bỏ trống !');
      setLoading(false);
      return;
    }

    axios
      .put(apiEndpoint, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setRerender(!rerender);
        setEdit(false);
        setLoading(false);
      })
      .catch((err) => {
        setLoading(false);
        console.error('Registration error:', err);
        setError(err.message);
      });
  };

  if (loadingUserData) {
    return (
      <div className="bg-light min-vh-100 d-flex flex-row align-items-center">
        <CContainer className="d-flex justify-content-center">
          <CSpinner
            color="primary"
            className="center"
            size="sm"
            style={{ width: '5rem', height: '5rem' }}
          />
        </CContainer>
      </div>
    );
  }

  return (
    <div className="bg-light min-vh-100 d-flex flex-row align-items-center">
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={12} lg={7} xl={6}>
            <CCard className="mx-3">
              <CCardBody className="p-3">
                <CForm>
                  <h2 className="mb-4 text-center">Thông tin tài khoản</h2>
                  <CRow className="mb-5">
                    <CCol></CCol>
                    {userData.avatar_url != null ? (
                      <CAvatar
                        src={userData.avatar_url}
                        size="xl"
                        style={{ width: '10rem', height: '10rem', overflow: 'hidden' }}
                      />
                    ) : (
                      <CAvatar
                        color="secondary"
                        size="xl"
                        style={{ width: '5rem', height: '5rem' }}
                      >
                        <span style={{ fontSize: '2rem' }}>{userData.username.slice(0, 2)}</span>
                      </CAvatar>
                    )}
                    <CCol></CCol>
                  </CRow>
                  {/* <p className="text-medium-emphasis">Đăng ký tài khoản</p> */}
                  {edit && (
                    <CRow className="mb-3">
                      <CFormLabel className="col-sm-3 col-form-label">Ảnh đại diện</CFormLabel>
                      <CCol sm={9}>
                        <CFormInput type="file" accept="image/*" onChange={handleAvatarUpload} />
                      </CCol>
                    </CRow>
                  )}
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Username</CFormLabel>
                    <CCol sm={9}>
                      <CFormInput
                        type="text"
                        name="username"
                        defaultValue={userData.username}
                        readOnly={!edit}
                        plainText={!edit}
                        onChange={(e) => setUsername(e.target.value)}
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel htmlFor="staticEmail" className="col-sm-3 col-form-label">
                      Email
                    </CFormLabel>
                    <CCol sm={9}>
                      <CFormInput type="text" defaultValue={userData.login_id} plainText readOnly />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Số điện thoại</CFormLabel>
                    <CCol sm={9}>
                      <CFormInput
                        type="text"
                        defaultValue={userData.phone_number}
                        readOnly={!edit}
                        plainText={!edit}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Phân quyền</CFormLabel>
                    <CCol sm={9}>
                      <CFormInput
                        type="text"
                        defaultValue={userData.roles[0].description}
                        plainText
                        readOnly
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Tham gia từ</CFormLabel>
                    <CCol sm={9}>
                      <CFormInput
                        type="text"
                        defaultValue={moment(userData.created_time).format('DD-MM-YYYY hh:mm:ss')}
                        plainText
                        readOnly
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Cập nhật</CFormLabel>
                    <CCol sm={9}>
                      <CFormInput
                        type="text"
                        defaultValue={moment(userData.updated_time).format('DD-MM-YYYY hh:mm:ss')}
                        plainText
                        readOnly
                      />
                    </CCol>
                  </CRow>
                  {!edit && (
                    <div className="d-grid">
                      <CButtonGroup role="group" aria-label="Basic mixed styles example">
                        <CButton
                          color="primary"
                          onClick={() => {
                            setEdit(true);
                            setError(null);
                          }}
                        >
                          {'Sửa'}
                        </CButton>
                        <CButton color="danger" onClick={handleClick}>
                          {'Đăng xuất'}
                        </CButton>
                      </CButtonGroup>
                    </div>
                  )}
                  {edit && (
                    <div className="d-grid">
                      <CButtonGroup role="group" aria-label="Basic mixed styles example">
                        <CButton color="success" onClick={handleUpdate} disabled={loading}>
                          {loading && <CSpinner component="span" size="sm" aria-hidden="true" />}
                          {' Lưu'}
                        </CButton>
                        <CButton color="primary" onClick={() => setEdit(false)} disabled={loading}>
                          {'Hủy'}
                        </CButton>
                      </CButtonGroup>
                    </div>
                  )}
                </CForm>
              </CCardBody>
              {error && edit === true && (
                <div>
                  <CAlert color="danger">{error}</CAlert>
                </div>
              )}
            </CCard>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  );
};

export default Profile;
