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
import IssuingHouseImg from 'src/assets/images/house.jpg';
import CIcon from '@coreui/icons-react';
import { cibFacebook } from '@coreui/icons';
import UpdateIssuingHouseModal from 'src/components/modal/UpdateIssuingHouseModal';
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

const IssuingHouseDetail = () => {
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [books, setBooks] = useState([]);
  const [issuingHouse, setIssuingHouse] = useState(null);
  const [showPreview, setShowPreview] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const userData = useSelector((state) => state.auth.userData);
  const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR];
  const [loadingIssuingHouse, setLoadingIssuingHouse] = useState(true);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const { issuing_house_id } = useParams();

  const getIssuingHouse = () => {
    axios.get(`${BASE_URL}/issuing-house/${issuing_house_id}`).then(function (response) {
      setIssuingHouse(response.data);
      setLoadingIssuingHouse(false);
    });
  };

  useEffect(() => {
    getIssuingHouse();
  }, [issuing_house_id]);

  const getBooks = () => {
    const size = DEFAULT_PAGE_SIZE;
    const sort_by = SORT_BY.TITLE;
    const sort_direction = SORT_DIRECTION.ASC;
    const params = {
      page,
      size,
      issuing_house: issuing_house_id,
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
  }, [page, issuing_house_id]);

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  if (loadingIssuingHouse || loadingBooks) {
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
              src={issuingHouse.logo_url ? issuingHouse.logo_url : IssuingHouseImg}
              style={{ width: '100%' }}
              onClick={() => {
                if (issuingHouse.logo_url) setShowPreview(!showPreview);
              }}
            ></CImage>
          </CCol>
          <CCol>
            <CCard style={{ width: '100%' }}>
              <CCardBody>
                <CCardTitle className="mb-2">
                  <h3>{issuingHouse.name}</h3>
                </CCardTitle>
                <CCardText>
                  {issuingHouse.address && (
                    <CRow className="mb-3">
                      <CCol>{`Địa chỉ: ${issuingHouse.address}`}</CCol>
                    </CRow>
                  )}
                  {issuingHouse.email && (
                    <CRow className="mb-3">
                      <CCol>{`Email: ${issuingHouse.email}`}</CCol>
                    </CRow>
                  )}
                  {issuingHouse.phone_number && (
                    <CRow className="mb-3">
                      <CCol>{`Số điện thoại: ${issuingHouse.phone_number}`}</CCol>
                    </CRow>
                  )}
                  {issuingHouse.website && (
                    <CRow className="mb-3">
                      <CCol>
                        Website: <Link to={issuingHouse.website}>{`${issuingHouse.website}`}</Link>
                      </CCol>
                    </CRow>
                  )}
                  {issuingHouse.facebook && (
                    <CRow className="mb-3">
                      <CCol>
                        <CButton
                          href={issuingHouse.facebook}
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
                <CCardText
                  dangerouslySetInnerHTML={{ __html: issuingHouse.description }}
                ></CCardText>
              </CCardBody>
            </CCard>
          </CCol>
        </CRow>
        {issuingHouse.logo_url && (
          <CModal visible={showPreview} onClose={() => setShowPreview(!showPreview)}>
            <CModalHeader closeButton>
              <h5>Ảnh</h5>
            </CModalHeader>
            <CModalBody>
              <CImage src={issuingHouse.logo_url} style={{ width: '100%' }}></CImage>
            </CModalBody>
          </CModal>
        )}
        {userData && roles.includes(userData.role_code) && (
          <UpdateIssuingHouseModal
            visible={showModal}
            onHide={() => setShowModal(false)}
            issuingHouse={issuingHouse}
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

export default IssuingHouseDetail;
