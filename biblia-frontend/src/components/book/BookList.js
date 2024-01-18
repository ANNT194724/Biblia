import { cilArrowCircleRight, cilStar } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React from 'react';
import PropTypes from 'prop-types';
import { AUTHOR_ROLE } from 'src/Constants';
import CoverImg from 'src/assets/images/cover.jpg';
import { Link, useNavigate } from 'react-router-dom';
const {
  CRow,
  CCol,
  CButton,
  CCard,
  CCardImage,
  CCardTitle,
  CCardBody,
  CCardText,
} = require('@coreui/react');

const BookList = (props) => {
  const books = props.books;
  const navigate = useNavigate();

  const printAuthors = (authors) => {
    const sortedAuthors = authors.sort((a, b) => {
      if (a.role === AUTHOR_ROLE.AUTHOR) return -1;
      if (b.role !== AUTHOR_ROLE.AUTHOR) return 1;
      return 0;
    });

    return sortedAuthors.map((author) => {
      if (author.role === AUTHOR_ROLE.AUTHOR) {
        return (
          <Link key={author.author_id} to={`/author/${author.author_id}`}>
            {`${author.name}`}
          </Link>
        );
      } else {
        return (
          <>
            <span>, </span>
            <Link key={author.author_id} to={`/author/${author.author_id}`}>
              {`${author.name} (${author.role})`}
            </Link>
          </>
        );
      }
    });
  };

  return (
    <>
      {books.map((book) => {
        return (
          <CCard
            className="mb-3"
            style={{ maxHeight: '200px', maxWidth: '980px' }}
            key={book.book_id}
          >
            <CRow className="g-0">
              <CCol xs={2}>
                <CCardImage
                  style={{ maxWidth: '120px' }}
                  alt={`Bìa ${book.title}`}
                  src={book.image_url ? book.image_url : CoverImg}
                />
              </CCol>
              <CCol xs={7}>
                <CCardBody>
                  <CCardTitle>{book.title}</CCardTitle>
                  <CCardText>{printAuthors(book.authors)}</CCardText>
                  {book.isbn && <CCardText>{`ISBN: ${book.isbn}`}</CCardText>}
                  <CCardText>{`Năm xuất bản: ${book.published_year}`}</CCardText>
                </CCardBody>
              </CCol>
              <CCol>
                <CCardBody>
                  <CCardText>
                    <CIcon icon={cilStar} />
                    {` Điểm: ${book.rating}/10`}
                  </CCardText>
                  <CButton onClick={() => navigate(`/book/${book.book_id}`)}>
                    {`Xem Chi tiết `}
                    <CIcon icon={cilArrowCircleRight} />
                  </CButton>
                </CCardBody>
              </CCol>
            </CRow>
          </CCard>
        );
      })}
    </>
  );
};

BookList.propsType = {
  books: PropTypes.array,
};

export default BookList;
