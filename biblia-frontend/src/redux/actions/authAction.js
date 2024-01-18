export const loginSuccess = (userData, token) => {
  return {
    type: 'LOGIN_SUCCESS',
    payload: { userData, token },
  };
};
