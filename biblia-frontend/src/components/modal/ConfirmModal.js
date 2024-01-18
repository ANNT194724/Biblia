import React from 'react';
import PropTypes from 'prop-types';
const { CModal, CModalHeader, CModalBody, CModalFooter, CButton } = require('@coreui/react');

const ConfirmModal = (props) => {
  return (
    <>
      <CModal visible={props.visible} onClose={props.onHide}>
        <CModalHeader closeButton>
          <h5>Error</h5>
        </CModalHeader>
        <CModalBody>
          <p>{props.message}</p>
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={props.onHide}>
            Hủy
          </CButton>
          <CButton color="primary" onClick={props.onConfirm}>
            Đồng ý
          </CButton>
        </CModalFooter>
      </CModal>
    </>
  );
};

ConfirmModal.propTypes = {
  visible: PropTypes.bool,
  onHide: PropTypes.func,
  message: PropTypes.string,
  onConfirm: PropTypes.func,
};

export default ConfirmModal;
