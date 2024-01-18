import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment/moment';
const {
  CRow,
  CCard,
  CCardTitle,
  CCardBody,
  CCardText,
  CCardSubtitle,
  CCol,
} = require('@coreui/react');

const Review = (props) => {
  const review = props.review;

  return (
    <>
      <CCard className="mb-3" style={{ maxHeight: '200px', maxWidth: '980px' }}>
        <CRow className="g-0">
          <CCardBody>
            <CCardTitle>{review.username}</CCardTitle>
            <CRow className="mb-1">
              <CCol>
                <CCardSubtitle>{`Điểm: ${review.rating}`}</CCardSubtitle>
              </CCol>
              <CCol className="text-end">
                <CCardText>{moment(review.created_time).format('DD-MM-YYYY hh:mm:ss')}</CCardText>
              </CCol>
            </CRow>
            {review.content && <CCardText>{review.content}</CCardText>}
          </CCardBody>
        </CRow>
      </CCard>
    </>
  );
};

Review.propsType = {
  review: PropTypes.object,
};

export default Review;
