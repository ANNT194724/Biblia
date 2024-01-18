const initialState = {
  userData: null,
  token: null,
};

const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        userData: action.payload.userData,
        token: action.payload.token,
      };
    case 'REFRESH_TOKEN':
      return {
        ...state,
        token: action.payload.token,
      };
    default:
      return state;
  }
};

export default authReducer;
