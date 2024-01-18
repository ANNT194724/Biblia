import React, { useEffect } from 'react';

import { CSidebar, CSidebarBrand, CSidebarNav } from '@coreui/react';
import CIcon from '@coreui/icons-react';

import { AppSidebarNav } from './AppSidebarNav';

import { sygnet } from 'src/assets/brand/sygnet';

import SimpleBar from 'simplebar-react';
import 'simplebar/dist/simplebar.min.css';

// sidebar nav config
import navigation from '../_nav';
import { cilBook } from '@coreui/icons';
import { useDispatch, useSelector } from 'react-redux';
import { setSidebar } from 'src/redux/actions/sidebarAction';

const AppSidebar = () => {
  const dispatch = useDispatch();
  const sidebarShow = useSelector((state) => state.sidebar.show);
  const unfoldable = useSelector((state) => state.sidebar.unfoldable);
  const userData = useSelector((state) => state.auth.userData);

  useEffect(() => {}, [userData]);

  return (
    <CSidebar
      position="fixed"
      visible={sidebarShow}
      unfoldable={unfoldable}
      onVisibleChange={(visible) => {
        dispatch(setSidebar(visible));
      }}
    >
      <CSidebarBrand className="d-none d-md-flex" to="/">
        <CIcon className="sidebar-brand-full" icon={cilBook} height={35} />
        <font size="6">
          <strong>BIBLIA</strong>
        </font>
        <CIcon className="sidebar-brand-narrow" icon={sygnet} height={35} />
      </CSidebarBrand>
      <CSidebarNav>
        <SimpleBar>
          <AppSidebarNav items={navigation} />
        </SimpleBar>
      </CSidebarNav>
    </CSidebar>
  );
};

export default React.memo(AppSidebar);
