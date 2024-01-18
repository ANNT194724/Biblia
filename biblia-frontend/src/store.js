import { createStore, combineReducers } from 'redux';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';

import authReducer from './redux/reducers/authReducer';
import profileReducer from './redux/reducers/profileReducer';
import sidebarReducer from './redux/reducers/sidebarReducer';

const rootReducer = combineReducers({
  auth: authReducer,
  profile: profileReducer,
  sidebar: sidebarReducer,
});

const persistConfig = {
  key: 'root',
  storage,
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

const store = createStore(persistedReducer);
const persistor = persistStore(store);

export { store, persistor };
