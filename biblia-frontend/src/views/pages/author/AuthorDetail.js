import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  BASE_URL,
  DEFAULT_PAGE,
  DEFAULT_PAGE_SIZE,
  SORT_BY,
  SORT_DIRECTION,
  USER_ROLE,
} from 'src/Constants';
import { Link, useParams } from 'react-router-dom';
import BookList from 'src/components/book/BookList';
import AuthorImg from 'src/assets/images/author.png';
import { useSelector } from 'react-redux';
import UpdateAuthorModal from 'src/components/modal/UpdateAuthorModal';
const {
  CPagination,
  CPaginationItem,
  CCard,
  CContainer,
  CRow,
  CCol,
  CImage,
  CCardBody,
  CCardTitle,
  CCardText,
  CCardSubtitle,
  CButton,
  CSpinner,
  CModal,
  CModalHeader,
  CModalBody,
} = require('@coreui/react');

const AuthorDetail = () => {
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [books, setBooks] = useState([]);
  const [author, setAuthor] = useState(null);
  const [showPreview, setShowPreview] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const userData = useSelector((state) => state.auth.userData);
  const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR];
  const [loadingAuthor, setLoadingAuthor] = useState(true);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const { author_id } = useParams();

  const getAuthor = () => {
    axios.get(`${BASE_URL}/author/${author_id}`).then(function (response) {
      setAuthor(response.data);
      setLoadingAuthor(false);
    });
  };

  useEffect(() => {
    getAuthor();
  }, [author_id]);

  const getBooks = () => {
    const size = DEFAULT_PAGE_SIZE;
    const sort_by = SORT_BY.TITLE;
    const sort_direction = SORT_DIRECTION.ASC;
    const params = {
      page,
      size,
      author_id,
      sort_by,
      sort_direction,
    };

    axios.get(`${BASE_URL}/book`, { params }).then(function (response) {
      setBooks(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
      setLoadingBooks(false);
    });
  };

  useEffect(() => {
    getBooks();
  }, [page, author_id]);

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  if (loadingAuthor || loadingBooks) {
    return (
      <>
        <CSpinner color="primary"></CSpinner>
      </>
    );
  }

  return (
    <>
      <CContainer>
        <CRow className="mb-3">
          <CCol xs={3}>
            <CImage
              rounded
              thumbnail
              src={author.photo ? author.photo : AuthorImg}
              style={{ width: '100%' }}
              onClick={() => {
                if (author.photo) setShowPreview(!showPreview);
              }}
            ></CImage>
          </CCol>
          <CCol>
            <CCard style={{ width: '100%' }}>
              <CCardBody>
                <CCardTitle className="mb-2">
                  <h3>{author.name}</h3>
                </CCardTitle>
                <CCardSubtitle className="mb-4 text-medium-emphasis">
                  {author.alias && `Bút danh khác: ${author.alias}`}
                </CCardSubtitle>
                <CCardText>
                  {author.born && (
                    <CRow className="mb-3">
                      <CCol>{`Ngày sinh: ${author.born}`}</CCol>
                    </CRow>
                  )}
                  {author.died && (
                    <CRow className="mb-3">
                      <CCol>{`Ngày mất: ${author.died}`}</CCol>
                    </CRow>
                  )}
                  {author.website && (
                    <CRow className="mb-3">
                      <CCol>
                        Website: <Link to={author.website}>{`${author.website}`}</Link>
                      </CCol>
                    </CRow>
                  )}
                  {userData && roles.includes(userData.role_code) && (
                    <CRow className="mb-2">
                      <CCol>
                        <CButton onClick={() => setShowModal(true)}>Chỉnh sửa</CButton>
                      </CCol>
                    </CRow>
                  )}
                </CCardText>
                <CCardText dangerouslySetInnerHTML={{ __html: author.description }}></CCardText>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
        {author.photo && (
          <CModal visible={showPreview} onClose={() => setShowPreview(!showPreview)}>
            <CModalHeader closeButton>
              <h5>Ảnh</h5>
            </CModalHeader>
            <CModalBody>
              <CImage src={author.photo} style={{ width: '100%' }}></CImage>
            </CModalBody>
          </CModal>
        )}
        <span>
          <strong>{`${totalElements} tác phẩm:`}</strong>
        </span>
        <BookList books={books}></BookList>
        <div className="d-flex justify-content-center">
          <CPagination>
            <CPaginationItem
              aria-label="Previous"
              onClick={() => setPage(page - 1)}
              disabled={page === DEFAULT_PAGE}
            >
              <span aria-hidden="true">&laquo;</span>
            </CPaginationItem>
            {pageNumbers.map((pageNumber) => (
              <CPaginationItem
                key={pageNumber}
                active={pageNumber === page}
                onClick={() => setPage(pageNumber)}
              >
                {pageNumber}
              </CPaginationItem>
            ))}
            <CPaginationItem
              aria-label="Next"
              onClick={() => setPage(page + 1)}
              disabled={page === totalPages}
            >
              <span aria-hidden="true">&raquo;</span>
            </CPaginationItem>
          </CPagination>
        </div>
      </CContainer>
      {userData && roles.includes(userData.role_code) && (
        <UpdateAuthorModal visible={showModal} onHide={() => setShowModal(false)} author={author} />
      )}
    </>
  );
};

export default AuthorDetail;
