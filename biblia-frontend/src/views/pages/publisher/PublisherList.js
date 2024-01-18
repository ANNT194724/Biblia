import { cilArrowCircleRight, cilSearch } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { BASE_URL, DEFAULT_PAGE } from 'src/Constants';
import PublisherImg from 'src/assets/images/publisher.png';
import { useNavigate } from 'react-router-dom';
import CreatePublisherModal from 'src/components/modal/CreatePublisherModal';
const {
  CRow,
  CForm,
  CCol,
  CFormInput,
  CButton,
  CCard,
  CCardTitle,
  CCardBody,
  CPagination,
  CPaginationItem,
  CContainer,
  CAvatar,
} = require('@coreui/react');

const PublisherList = () => {
  const [name, setName] = useState('');
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [publishers, setPublishers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const size = 12;
  const userData = useSelector((state) => state.auth.userData);
  const navigate = useNavigate();

  const getPublishers = () => {
    const params = {
      page,
      size,
      name,
    };

    axios.get(`${BASE_URL}/publisher`, { params }).then(function (response) {
      setPublishers(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
    });
  };

  useEffect(() => {
    getPublishers();
  }, [page]);

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      setPage(DEFAULT_PAGE);
      getPublishers();
    }
  };

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  return (
    <>
      <CRow className="mb-3">
        <CCol sx={1}>
          <CIcon icon={cilSearch}></CIcon> Tìm kiếm
        </CCol>
        <CCol sm={8}>
          <CForm>
            <CFormInput
              type="text"
              placeholder="Tìm nhà xuất bản"
              onChange={(e) => setName(e.target.value)}
              onKeyDown={handleKeyPress}
            ></CFormInput>
          </CForm>
        </CCol>
        <CCol>
          {userData && (userData.role_code === 'MODERATOR' || userData.role_code === 'ADMIN') && (
            <CButton onClick={() => setShowModal(true)}>Thêm nhà xuất bản</CButton>
          )}
        </CCol>
      </CRow>
      <CreatePublisherModal visible={showModal} onHide={() => setShowModal(false)} />
      <h6 className="mb-3">{`${totalElements} kết quả`}</h6>
      <CContainer className="mt-3 mb-3">
        <CRow>
          {publishers.map((publisher) => {
            return (
              <CCard
                className="col-lg-3 col-md-4 col-sm-6 col-12 g-3 mb-3 text-center"
                key={publisher.publisher_id}
              >
                <div>
                  <CAvatar
                    size="xl"
                    className="mb-2 mt-3"
                    style={{ width: '8rem', height: '8rem', overflow: 'hidden' }}
                    alt={`Ảnh ${publisher.name}`}
                    src={publisher.logo_url ? publisher.logo_url : PublisherImg}
                  />
                </div>
                <CCardBody>
                  <CCardTitle className="mb-3">{publisher.name}</CCardTitle>
                  <CButton onClick={() => navigate(`/publisher/${publisher.publisher_id}`)}>
                    {`Xem Chi tiết `}
                    <CIcon icon={cilArrowCircleRight} />
                  </CButton>
                </CCardBody>
              </CCard>
            );
          })}
        </CRow>
      </CContainer>
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
    </>
  );
};

export default PublisherList;
