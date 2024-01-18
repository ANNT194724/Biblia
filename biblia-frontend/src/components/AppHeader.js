import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import {
  CContainer,
  CHeader,
  CHeaderBrand,
  CHeaderDivider,
  CHeaderNav,
  CHeaderToggler,
  CNavLink,
  CNavItem,
  CButton,
} from '@coreui/react';
import CIcon from '@coreui/icons-react';
import { cilBell, cilBook, cilMenu } from '@coreui/icons';

import { AppBreadcrumb } from './index';
import { AppHeaderDropdown } from './header/index';
import { setSidebar } from 'src/redux/actions/sidebarAction';

const AppHeader = () => {
  const token = useSelector((state) => state.auth.token);
  const sidebarShow = useSelector((state) => state.sidebar.show);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  return (
    <CHeader position="sticky" className="mb-4">
      <CContainer fluid>
        <CHeaderToggler className="ps-1" onClick={() => dispatch(setSidebar(!sidebarShow))}>
          <CIcon icon={cilMenu} size="lg" />
        </CHeaderToggler>
        <CHeaderBrand className="mx-auto d-md-none" to="/">
          <CIcon className="sidebar-brand-full" icon={cilBook} height={35} />
          <font size="6">
            <strong>BIBLIA</strong>
          </font>
        </CHeaderBrand>
        <CHeaderNav className="d-none d-md-flex me-auto"></CHeaderNav>
        <CHeaderNav>
          <CNavItem>
            <CNavLink href="#">
              <CIcon icon={cilBell} size="lg" />
            </CNavLink>
          </CNavItem>
        </CHeaderNav>
        <CHeaderNav className="ms-3">
          {token && token !== '' ? (
            <AppHeaderDropdown />
          ) : (
            <CButton
              color="success"
              onClick={() => {
                navigate('/login');
              }}
            >
              Login
            </CButton>
          )}
        </CHeaderNav>
      </CContainer>
      <CHeaderDivider />
      <CContainer fluid>
        <AppBreadcrumb />
      </CContainer>
    </CHeader>
  );
};

export default AppHeader;
