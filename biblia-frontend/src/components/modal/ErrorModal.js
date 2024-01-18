import React from 'react';
import PropTypes from 'prop-types';
const { CModal, CModalHeader, CModalBody } = require('@coreui/react');

const ErrorModal = (props) => {
  return (
    <>
      <CModal visible={props.visible} onClose={props.onHide}>
        <CModalHeader closeButton>
          <h5>Error</h5>
        </CModalHeader>
        <CModalBody>
          <p>{props.message}</p>
        </CModalBody>
      </CModal>
    </>
  );
};

ErrorModal.propTypes = {
  visible: PropTypes.bool,
  onHide: PropTypes.func,
  message: PropTypes.string,
};

export default ErrorModal;
