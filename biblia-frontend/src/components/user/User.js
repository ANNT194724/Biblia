import React, { useState } from 'react';
import PropTypes from 'prop-types';
import UpdateUserModal from '../modal/UpdateUserModal';
const { CRow, CCard, CCardBody, CCardText, CCol, CAvatar, CButton } = require('@coreui/react');

const User = (props) => {
  const [showModal, setShowModal] = useState(false);
  const user = props.user;

  return (
    <>
      <CCard className="mb-3" style={{ maxHeight: '200px', maxWidth: '980px' }}>
        <CRow className="g-0">
          <CCardBody>
            <CRow className="mb-1">
              <CCol xs={2} className="align-self-center">
                {user.avatar_url != null ? (
                  <CAvatar
                    src={user.avatar_url}
                    size="xl"
                    style={{ width: '5rem', height: '5rem', overflow: 'hidden' }}
                  />
                ) : (
                  <CAvatar color="secondary" size="xl" style={{ width: '5rem', height: '5rem' }}>
                    <span style={{ fontSize: '2rem' }}>{user.username.slice(0, 2)}</span>
                  </CAvatar>
                )}
              </CCol>
              <CCol className="align-self-center">
                <CCardText>Tên người dùng: {user.username}</CCardText>
                <CCardText>Phân quyền: {user.role_code}</CCardText>
              </CCol>
              <CCol className="align-self-center" xs={4}>
                <CButton onClick={() => setShowModal(true)}>Thông tin người dùng</CButton>
              </CCol>
            </CRow>
          </CCardBody>
        </CRow>
      </CCard>
      <UpdateUserModal visible={showModal} onHide={() => setShowModal(false)} user={user} />
    </>
  );
};

User.propsType = {
  user: PropTypes.object,
};

export default User;
