import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
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
import PublisherImg from 'src/assets/images/publisher.png';
import CIcon from '@coreui/icons-react';
import { cibFacebook } from '@coreui/icons';
import UpdatePublisherModal from 'src/components/modal/UpdatePublisherModal';
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
  CButton,
  CSpinner,
  CModal,
  CModalHeader,
  CModalBody,
} = require('@coreui/react');

const PublisherDetail = () => {
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [books, setBooks] = useState([]);
  const [publisher, setPublisher] = useState(null);
  const [showPreview, setShowPreview] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const userData = useSelector((state) => state.auth.userData);
  const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR];
  const [loadingPublisher, setLoadingPublisher] = useState(true);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const { publisher_id } = useParams();

  const getPublisher = () => {
    axios.get(`${BASE_URL}/publisher/${publisher_id}`).then(function (response) {
      setPublisher(response.data);
      setLoadingPublisher(false);
    });
  };

  useEffect(() => {
    getPublisher();
  }, [publisher_id]);

  const getBooks = () => {
    const size = DEFAULT_PAGE_SIZE;
    const sort_by = SORT_BY.RATING;
    const sort_direction = SORT_DIRECTION.DESC;
    const params = {
      page,
      size,
      publisher: publisher_id,
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
  }, [page, publisher_id]);

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  if (loadingPublisher || loadingBooks) {
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
              src={publisher.logo_url ? publisher.logo_url : PublisherImg}
              style={{ width: '100%' }}
              onClick={() => {
                if (publisher.logo_url) setShowPreview(!showPreview);
              }}
            ></CImage>
          </CCol>
          <CCol>
            <CCard style={{ width: '100%' }}>
              <CCardBody>
                <CCardTitle className="mb-2">
                  <h3>{publisher.name}</h3>
                </CCardTitle>
                <CCardText>
                  {publisher.address && (
                    <CRow className="mb-3">
                      <CCol>{`Địa chỉ: ${publisher.address}`}</CCol>
                    </CRow>
                  )}
                  {publisher.email && (
                    <CRow className="mb-3">
                      <CCol>{`Email: ${publisher.email}`}</CCol>
                    </CRow>
                  )}
                  {publisher.phone_number && (
                    <CRow className="mb-3">
                      <CCol>{`Số điện thoại: ${publisher.phone_number}`}</CCol>
                    </CRow>
                  )}
                  {publisher.website && (
                    <CRow className="mb-3">
                      <CCol>
                        Website: <Link to={publisher.website}>{`${publisher.website}`}</Link>
                      </CCol>
                    </CRow>
                  )}
                  {publisher.facebook && (
                    <CRow className="mb-3">
                      <CCol>
                        <CButton
                          href={publisher.facebook}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <CIcon icon={cibFacebook} />
                          {` Facebook`}
                        </CButton>
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
                <CCardText dangerouslySetInnerHTML={{ __html: publisher.description }}></CCardText>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
        {publisher.logo_url && (
          <CModal visible={showPreview} onClose={() => setShowPreview(!showPreview)}>
            <CModalHeader closeButton>
              <h5>Ảnh</h5>
            </CModalHeader>
            <CModalBody>
              <CImage src={publisher.logo_url} style={{ width: '100%' }}></CImage>
            </CModalBody>
          </CModal>
        )}
        {userData && roles.includes(userData.role_code) && (
          <UpdatePublisherModal
            visible={showModal}
            onHide={() => setShowModal(false)}
            publisher={publisher}
          />
        )}
        <span>
          <strong>{`${totalElements} đầu sách:`}</strong>
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
    </>
  );
};

export default PublisherDetail;
