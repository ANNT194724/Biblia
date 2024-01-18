import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  CButton,
  CCard,
  CCardBody,
  CCardTitle,
  CCol,
  CContainer,
  CForm,
  CFormInput,
  CFormLabel,
  CRow,
  CToast,
  CToastBody,
  CToastClose,
  CToaster,
} from '@coreui/react';
import axios from 'axios';
import ErrorModal from 'src/components/modal/ErrorModal';
import { BASE_URL } from 'src/Constants';

const ForgotPassword = () => {
  const [login_id, setLoginId] = useState('');
  const [new_password, setNewPassword] = useState('');
  const [token, setToken] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [emailSent, setEmailSent] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    const apiEndpoint = BASE_URL + '/user/password/reset';
    if (!emailSent) {
      const params = { login_id };

      if (!login_id) {
        setError('Hãy nhập email');
        setLoading(false);
        return;
      }

      await axios
        .get(apiEndpoint, { params })
        .then(() => {
          setLoading(false);
          setEmailSent(true);
        })
        .catch((err) => {
          setLoading(false);
          setError(err.message);
        });
    } else {
      const data = {
        token,
        new_password,
      };

      if (new_password !== confirmPassword) {
        setError('Mật khẩu không khớp');
        setLoading(false);
        return;
      }

      if (!new_password || new_password.length < 8) {
        setError('Mật khẩu cần ít nhất 8 kí tự');
        setLoading(false);
        return;
      }

      await axios
        .post(apiEndpoint, data)
        .then(() => {
          setLoading(false);
          setEmailSent(false);
          navigate('/login');
        })
        .catch((err) => {
          setLoading(false);
          setError(err.message);
        });
    }
  };

  return (
    <div className="bg-light min-vh-100 d-flex flex-row align-items-center">
      <CContainer>
        <CToaster placement="top-end">
          <CToast animation={false} autohide={false} visible={emailSent}>
            <div className="d-flex">
              <CToastBody>
                Một mã xác nhận với thời hạn 15 phút đã được gửi đến email của bạn
              </CToastBody>
              <CToastClose className="me-2 m-auto" />
            </div>
          </CToast>
        </CToaster>
        <CRow className="justify-content-center">
          <CCol md={6}>
            <CCard className="p-4">
              <CCardTitle>Quên mật khẩu</CCardTitle>
              <CCardBody>
                <CForm>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Email</CFormLabel>
                    <CCol>
                      <CFormInput
                        type="text"
                        disabled={emailSent}
                        onChange={(e) => setLoginId(e.target.value)}
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Mã xác nhận</CFormLabel>
                    <CCol>
                      <CFormInput
                        type="text"
                        disabled={!emailSent}
                        onChange={(e) => setToken(e.target.value)}
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Mật khẩu mới</CFormLabel>
                    <CCol>
                      <CFormInput
                        type="password"
                        disabled={!emailSent}
                        onChange={(e) => setNewPassword(e.target.value)}
                      />
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CFormLabel className="col-sm-3 col-form-label">Nhập lại mật khẩu</CFormLabel>
                    <CCol>
                      <CFormInput
                        type="password"
                        disabled={!emailSent}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                      />
                    </CCol>
                  </CRow>
                  <CRow>
                    <CCol>
                      <CButton disabled={loading} onClick={handleSubmit}>
                        Xác nhận
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
          </CCol>
        </CRow>
      </CContainer>
    </div>
  );
};

export default ForgotPassword;
