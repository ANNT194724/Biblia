import React from 'react';
import PropTypes from 'prop-types';
import Review from './Review';

const ReviewList = (props) => {
  const reviews = props.reviews;

  return (
    <>
      {reviews.map((review) => {
        return <Review review={review} key={review.review_id}></Review>;
      })}
    </>
  );
};

ReviewList.propsType = {
  reviews: PropTypes.array,
};

export default ReviewList;
