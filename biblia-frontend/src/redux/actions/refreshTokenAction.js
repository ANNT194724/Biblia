export const refreshTokenSuccess = (token) => {
  return {
    type: 'REFRESH_TOKEN',
    payload: { token },
  };
};
