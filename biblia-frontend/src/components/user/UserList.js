import React from 'react';
import PropTypes from 'prop-types';
import User from './User';

const UserList = (props) => {
  const users = props.users;

  return (
    <>
      {users.map((user) => {
        return <User user={user} key={user.user_id}></User>;
      })}
    </>
  );
};

UserList.propsType = {
  users: PropTypes.array,
};

export default UserList;
