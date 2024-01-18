import { cilArrowCircleRight } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { AUTHOR_ROLE, BASE_URL } from 'src/Constants';
import CoverImg from 'src/assets/images/cover.jpg';
import axios from 'axios';
import BookRequestModal from '../modal/BookRequestModal';
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

const BookRequestList = (props) => {
  const books = props.books;
  const [authors, setAuthors] = useState(null);
  const [request, setRequest] = useState(null);
  const [showModal, setShowModal] = useState(false);

  const printAuthors = (authors) => {
    if (!authors) {
      return '';
    }
    const sortedAuthors = authors.sort((a, b) => {
      if (a.role === AUTHOR_ROLE.AUTHOR) return -1;
      if (b.role !== AUTHOR_ROLE.AUTHOR) return 1;
      return 0;
    });
    let authorString = `${sortedAuthors[0].name}`;
    for (let i = 1; i < sortedAuthors.length; i++) {
      authorString += `, ${sortedAuthors[i].name} (${sortedAuthors[i].role})`;
    }
    return authorString;
  };

  const handleShowModal = async (book_id) => {
    const response = await axios.get(`${BASE_URL}/book/${book_id}`);
    setRequest(response.data.book);
    setAuthors(response.data.authors);
    setShowModal(true);
  };

  const onHide = () => {
    setRequest(null);
    setShowModal(false);
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
              <CCol className="align-self-center">
                <CCardBody>
                  <CButton onClick={() => handleShowModal(book.book_id)}>
                    {`Xem Chi tiết `}
                    <CIcon icon={cilArrowCircleRight} />
                  </CButton>
                </CCardBody>
              </CCol>
            </CRow>
          </CCard>
        );
      })}
      {request && (
        <BookRequestModal visible={showModal} onHide={onHide} book={request} authors={authors} />
      )}
    </>
  );
};

BookRequestList.propsType = {
  books: PropTypes.array,
};

export default BookRequestList;
