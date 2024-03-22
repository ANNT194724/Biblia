import { cilArrowCircleRight, cilSearch } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { BASE_URL, DEFAULT_PAGE } from 'src/Constants';
import HouseImg from 'src/assets/images/house.jpg';
import { useNavigate } from 'react-router-dom';
import CreateIssuingHouseModal from 'src/components/modal/CreateIssuingHouseModal';
import ResponsivePagination from 'react-responsive-pagination';
const {
  CRow,
  CForm,
  CCol,
  CFormInput,
  CButton,
  CCard,
  CCardTitle,
  CCardBody,
  CContainer,
  CAvatar,
} = require('@coreui/react');

const IssuingHouseList = () => {
  const [name, setName] = useState('');
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [issuingHouses, setIssuingHouses] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const size = 12;
  const userData = useSelector((state) => state.auth.userData);
  const navigate = useNavigate();

  const getIssuingHouses = () => {
    const params = {
      page,
      size,
      name,
    };

    axios.get(`${BASE_URL}/issuing-house`, { params }).then(function (response) {
      setIssuingHouses(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
    });
  };

  useEffect(() => {
    getIssuingHouses();
  }, [page]);

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      setPage(DEFAULT_PAGE);
      getIssuingHouses();
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
        <CCol sm={7}>
          <CForm>
            <CFormInput
              type="text"
              placeholder="Tìm nhà phát hành"
              onChange={(e) => setName(e.target.value)}
              onKeyDown={handleKeyPress}
            ></CFormInput>
          </CForm>
        </CCol>
        <CCol>
          {userData && (userData.role_code === 'MODERATOR' || userData.role_code === 'ADMIN') && (
            <CButton onClick={() => setShowModal(true)}>Thêm nhà phát hành</CButton>
          )}
        </CCol>
      </CRow>
      <CreateIssuingHouseModal visible={showModal} onHide={() => setShowModal(false)} />
      <h6 className="mb-3">{`${totalElements} kết quả`}</h6>
      <CContainer className="mt-3 mb-3">
        <CRow>
          {issuingHouses.map((issuingHouse) => {
            return (
              <CCard
                className="col-lg-3 col-md-4 col-sm-6 col-12 g-3 mb-3 text-center"
                key={issuingHouse.issuing_house_id}
              >
                <div>
                  <CAvatar
                    size="xl"
                    className="mb-2 mt-3"
                    style={{ width: '8rem', height: '8rem', overflow: 'hidden' }}
                    alt={`Ảnh ${issuingHouse.name}`}
                    src={issuingHouse.logo_url ? issuingHouse.logo_url : HouseImg}
                  />
                </div>
                <CCardBody>
                  <CCardTitle className="mb-3">{issuingHouse.name}</CCardTitle>
                  <CButton
                    onClick={() => navigate(`/issuing-house/${issuingHouse.issuing_house_id}`)}
                  >
                    {`Xem Chi tiết `}
                    <CIcon icon={cilArrowCircleRight} />
                  </CButton>
                </CCardBody>
              </CCard>
            );
          })}
        </CRow>
      </CContainer>
      <ResponsivePagination
        total={totalPages}
        current={page}
        onPageChange={(page) => setPage(page)}
      />
    </>
  );
};

export default IssuingHouseList;
