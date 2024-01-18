import React, { useState } from 'react';
import {
  CAlert,
  CButton,
  CCard,
  CCardBody,
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
import { useNavigate } from 'react-router-dom';
import ErrorModal from 'src/components/modal/ErrorModal';

const Register = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [repeatPassword, setRepeatPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleRegister = (event) => {
    const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i;

    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = 'http://localhost:8080/user/signup';

    const data = {
      username,
      email,
      password,
    };

    if (username === '' || email === '' || password === '') {
      setError('Hãy điền đầy đủ các trường thông tin');
      setLoading(false);
      return;
    }

    if (!email.match(emailRegex)) {
      setError('Email không hợp lệ');
      setLoading(false);
      return;
    }

    if (password.length < 8) {
      setError('Mật khẩu cần ít nhất 8 ký tự');
      setLoading(false);
      return;
    }

    if (password !== repeatPassword) {
      setError('Mật khẩu không khớp');
      setLoading(false);
      return;
    }

    axios
      .post(apiEndpoint, data)
      .then((response) => {
        setLoading(true);
        setTimeout(() => {
          navigate('/login');
        }, 3000);
      })
      .catch((err) => {
        setLoading(false);
        console.error('Registration error:', err);
        setError(err.message);
      });
  };

  return (
    <div className="bg-light min-vh-100 d-flex flex-row align-items-center">
      <CContainer>
        <CRow className="justify-content-center">
          <CCol md={9} lg={7} xl={6}>
            <CCard className="mx-4">
              <CCardBody className="p-4">
                <CForm>
                  <h1>Đăng ký</h1>
                  <p className="text-medium-emphasis">Đăng ký tài khoản</p>
                  <CInputGroup className="mb-3">
                    <CInputGroupText>
                      <CIcon icon={cilUser} />
                    </CInputGroupText>
                    <CFormInput
                      placeholder="Tên người dùng"
                      autoComplete="username"
                      value={username}
                      required={true}
                      onChange={(e) => setUsername(e.target.value)}
                    />
                  </CInputGroup>
                  <CInputGroup className="mb-3">
                    <CInputGroupText>@</CInputGroupText>
                    <CFormInput
                      placeholder="Email"
                      autoComplete="email"
                      value={email}
                      required={true}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </CInputGroup>
                  <CInputGroup className="mb-3">
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
                  <CInputGroup className="mb-4">
                    <CInputGroupText>
                      <CIcon icon={cilLockLocked} />
                    </CInputGroupText>
                    <CFormInput
                      type="password"
                      placeholder="Xác nhận mật khẩu"
                      autoComplete="new-password"
                      value={repeatPassword}
                      required={true}
                      onChange={(e) => setRepeatPassword(e.target.value)}
                    />
                  </CInputGroup>
                  <div className="d-grid">
                    <CButton color="success" onClick={handleRegister} disabled={loading}>
                      {loading ? 'Tạo tài khoản...' : 'Tạo tài khoản'}
                    </CButton>
                  </div>
                </CForm>
              </CCardBody>
              {error && (
                <ErrorModal
                  visible={error !== null}
                  onHide={() => setError(null)}
                  message={error}
                />
              )}
              {loading && (
                <div>
                  <CAlert color="success">
                    {'Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản'}
                  </CAlert>
                </div>
              )}
            </CCard>
          </CCol>
        </CRow>
      </CContainer>
    </div>
  );
};

export default Register;
