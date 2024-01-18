import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  CButton,
  CCard,
  CCardBody,
  CCardGroup,
  CCol,
  CContainer,
  CForm,
  CFormInput,
  CInputGroup,
  CInputGroupText,
  CRow,
} from '@coreui/react';
import axios from 'axios';
import CIcon from '@coreui/icons-react';
import { cilLockLocked, cilUser } from '@coreui/icons';
import { useDispatch } from 'react-redux';
import { loginSuccess } from 'src/redux/actions/authAction';
import ErrorModal from 'src/components/modal/ErrorModal';

const Login = () => {
  const [login_id, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleLogin = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = 'http://localhost:8080/auth/signin';

    const data = {
      login_id,
      password,
    };

    if (login_id === '' || password === '') {
      setError('Hãy điền đầy đủ các trường thông tin');
      setLoading(false);
      return;
    }

    axios
      .post(apiEndpoint, data)
      .then((response) => {
        setLoading(false);
        dispatch(loginSuccess(response.data.user, response.data.token));
        localStorage.setItem('refreshToken', JSON.stringify(response.data.refresh_token));
        localStorage.setItem('userRole', response.data.user.role_code);
        navigate('/');
      })
      .catch((err) => {
        setLoading(false);
        if (err.response.status === 401) {
          setError('Sai mật khẩu hoặc tên đăng nhập');
        } else {
          setError(err.message);
        }
      });
  };

  return (
    <div className="bg-light min-vh-100 d-flex flex-row align-items-center">
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={8}>
            <CCardGroup>
              <CCard className="p-4">
                <CCardBody>
                  <CForm>
                    <h1>Đăng nhập</h1>
                    <p className="text-medium-emphasis">Đăng nhập vào tài khoản</p>
                    <CInputGroup className="mb-3">
                      <CInputGroupText>
                        <CIcon icon={cilUser} />
                      </CInputGroupText>
                      <CFormInput
                        placeholder="Tên người dùng"
                        autoComplete="username"
                        value={login_id}
                        required={true}
                        onChange={(e) => setLoginId(e.target.value)}
                      />
                    </CInputGroup>
                    <CInputGroup className="mb-4">
                      <CInputGroupText>
                        <CIcon icon={cilLockLocked} />
                      </CInputGroupText>
                      <CFormInput
                        type="password"
                        placeholder="Mật khẩu"
                        autoComplete="new-password"
                        value={password}
                        required={true}
                        onChange={(e) => setPassword(e.target.value)}
                      />
                    </CInputGroup>
                    <CRow>
                      <CCol xs={6}>
                        <CButton
                          color="primary"
                          className="px-4"
                          onClick={handleLogin}
                          disabled={loading}
                        >
                          {loading ? 'Đăng nhập...' : 'Đăng nhập'}
                        </CButton>
                      </CCol>
                      <CCol xs={6} className="text-right">
                        <CButton
                          color="link"
                          className="px-0"
                          onClick={() => navigate('/forgot-password')}
                        >
                          Quên mật khẩu ?
                        </CButton>
                      </CCol>
                    </CRow>
                  </CForm>
                  {error && (
                    <ErrorModal
                      visible={error !== null}
                      onHide={() => setError(null)}
                      message={error}
                    />
                  )}
                </CCardBody>
              </CCard>
              <CCard className="text-white bg-primary py-5" style={{ width: '44%' }}>
                <CCardBody className="text-center">
                  <div>
                    <h1>Đăng ký</h1>
                    <br></br>
                    <h4>Bạn chưa có tài khoản?</h4>
                    <Link to="/register">
                      <CButton color="primary" className="mt-4" active tabIndex={-1}>
                        <h5>Đăng ký ngay !</h5>
                      </CButton>
                    </Link>
                  </div>
                </CCardBody>
              </CCard>
            </CCardGroup>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  );
};

export default Login;
