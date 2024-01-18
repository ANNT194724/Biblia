const initialState = {
  unfoldable: false,
  show: true,
};

const sidebarReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'SET_SIDEBAR':
      return {
        ...state,
        show: action.payload,
      };
    case 'SET_UNFOLDABLE':
      return {
        ...state,
        unfoldable: action.payload,
      };
    default:
      return state;
  }
};

export default sidebarReducer;
