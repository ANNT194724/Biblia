import React from 'react';
import {
  CAvatar,
  CDropdown,
  CDropdownDivider,
  CDropdownHeader,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
} from '@coreui/react';
import { cilAccountLogout, cilSettings, cilUser } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginSuccess } from 'src/redux/actions/authAction';

const AppHeaderDropdown = () => {
  const userData = useSelector((state) => state.auth.userData);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleLogout = () => {
    let userDataReset = {
      token: '',
      id: 0,
      email: '',
      username: '',
    };
    dispatch(loginSuccess(userDataReset, null));
    localStorage.removeItem('userRole');
    localStorage.removeItem('refreshToken');
    navigate('/');
  };

  return (
    <CDropdown variant="nav-item">
      <CDropdownToggle placement="bottom-end" className="py-0" caret={false}>
        {userData.avatar_url !== null && userData !== null ? (
          <CAvatar
            src={userData.avatar_url}
            style={{ height: '2.5rem', width: '2.5rem', overflow: 'hidden' }}
          />
        ) : (
          <CAvatar color="secondary">{userData.username.slice(0, 2)}</CAvatar>
        )}
      </CDropdownToggle>
      <CDropdownMenu className="pt-0" placement="bottom-end">
        <CDropdownHeader className="bg-light fw-semibold py-2">Account</CDropdownHeader>
        <CDropdownItem
          onClick={() => {
            navigate('/profile');
          }}
        >
          <CIcon icon={cilUser} className="me-2" />
          Profile
        </CDropdownItem>
        <CDropdownItem href="#">
          <CIcon icon={cilSettings} className="me-2" />
          Settings
        </CDropdownItem>
        <CDropdownDivider />
        <CDropdownItem onClick={handleLogout}>
          <CIcon icon={cilAccountLogout} className="me-2" />
          Logout
        </CDropdownItem>
      </CDropdownMenu>
    </CDropdown>
  );
};

export default AppHeaderDropdown;
