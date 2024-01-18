const initialState = {
  userData: null,
};

const profileReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET_PROFILE_DATA':
      return {
        ...state,
        userData: action.payload.data,
      };
    default:
      return state;
  }
};

export default profileReducer;
