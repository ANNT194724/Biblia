import React, { Suspense } from 'react';
import { HashRouter, Route, Routes } from 'react-router-dom';
import './scss/style.scss';
import { useDispatch } from 'react-redux';
import { loginSuccess } from './redux/actions/authAction';
import axios from 'axios';
import { BASE_URL } from './Constants';
import { refreshTokenSuccess } from './redux/actions/refreshTokenAction';
import ForgotPassword from './views/pages/login/ForgotPassword';

const loading = (
  <div className="pt-3 text-center">
    <div className="sk-spinner sk-spinner-pulse"></div>
  </div>
);

// Containers
const DefaultLayout = React.lazy(() => import('./layout/DefaultLayout'));

// Pages
const Login = React.lazy(() => import('./views/pages/login/Login'));
const Register = React.lazy(() => import('./views/pages/register/Register'));
const Page404 = React.lazy(() => import('./views/pages/page404/Page404'));
const Page500 = React.lazy(() => import('./views/pages/page500/Page500'));

const App = () => {
  const dispatch = useDispatch();

  const refreshTokenStr = localStorage.getItem('refreshToken');
  const refreshToken = JSON.parse(refreshTokenStr);

  const isTokenValid = () => {
    if (!refreshToken) {
      return false;
    }
    const expiryTime = new Date(refreshToken.expiry_time).getTime();
    const currentTime = new Date().getTime();
    return currentTime < expiryTime;
  };

  const logout = () => {
    let userDataReset = {
      token: '',
      id: 0,
      email: '',
      username: '',
    };
    dispatch(loginSuccess(userDataReset, null));
    localStorage.removeItem('userRole');
    localStorage.removeItem('refreshToken');
  };

  const getNewToken = async () => {
    const apiEndpoint = `${BASE_URL}/auth/refresh-token`;
    const data = {
      refresh_token: refreshToken.token,
    };
    await axios
      .post(apiEndpoint, data)
      .then((response) => {
        dispatch(refreshTokenSuccess(response.data.access_token));
      })
      .catch(() => {
        logout();
      });
  };

  if (!isTokenValid()) {
    localStorage.clear();
  } else {
    getNewToken();
  }

  return (
    <HashRouter>
      <Suspense fallback={loading}>
        <Routes>
          <Route exact path="/login" name="Login Page" element={<Login />} />
          <Route exact path="/register" name="Register Page" element={<Register />} />
          <Route exact path="/404" name="Page 404" element={<Page404 />} />
          <Route exact path="/500" name="Page 500" element={<Page500 />} />
          <Route
            exact
            path="/forgot-password"
            name="Reset Password Page"
            element={<ForgotPassword />}
          />
          <Route path="*" name="Home" element={<DefaultLayout />} />
        </Routes>
      </Suspense>
    </HashRouter>
  );
};

export default App;
