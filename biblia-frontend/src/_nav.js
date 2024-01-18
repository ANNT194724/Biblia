import React from 'react';
import CIcon from '@coreui/icons-react';
import {
  cilAddressBook,
  cilGroup,
  cilHome,
  cilHouse,
  cilLibrary,
  cilLibraryBuilding,
  cilList,
  cilListRich,
  cilStar,
} from '@coreui/icons';
import { CNavGroup, CNavItem, CNavTitle } from '@coreui/react';
import { USER_ROLE } from './Constants';

const userRole = localStorage.getItem('userRole');
const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR];

let _nav = [
  {
    component: CNavItem,
    name: 'Home',
    to: '/',
    icon: <CIcon icon={cilHome} customClassName="nav-icon" />,
  },
  {
    component: CNavTitle,
    name: 'Browse',
  },
  {
    component: CNavItem,
    name: 'Tác giả',
    to: '/author',
    icon: <CIcon icon={cilGroup} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Thể loại',
    to: '/genre',
    icon: <CIcon icon={cilListRich} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Series',
    to: '/series',
    icon: <CIcon icon={cilLibrary} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Nhà phát hành',
    to: '/issuing-house',
    icon: <CIcon icon={cilHouse} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Nhà xuất bản',
    to: '/publisher',
    icon: <CIcon icon={cilLibraryBuilding} customClassName="nav-icon" />,
  },
];

if (userRole && roles.includes(userRole)) {
  _nav.push(
    {
      component: CNavTitle,
      name: 'Moderator',
    },
    {
      component: CNavItem,
      name: 'Yêu cầu thêm sách',
      to: '/book-request',
      icon: <CIcon icon={cilList} customClassName="nav-icon" />,
    },
  );
}

if (userRole === USER_ROLE.ADMIN) {
  _nav.push(
    {
      component: CNavTitle,
      name: 'Admin',
    },
    {
      component: CNavItem,
      name: 'Quản lý người dùng',
      to: '/user',
      icon: <CIcon icon={cilAddressBook} customClassName="nav-icon" />,
    },
  );
}

export default _nav;
