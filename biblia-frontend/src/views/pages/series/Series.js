import { cilSearch } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { BASE_URL, DEFAULT_PAGE } from 'src/Constants';
import ResponsivePagination from 'react-responsive-pagination';
import SeriesList from '../../../components/series/SeriesList';
import CreateSeriesModal from 'src/components/modal/CreateSeriesModal';
const { CRow, CForm, CCol, CFormInput, CButton } = require('@coreui/react');

const Series = () => {
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(Number(localStorage.getItem('page')) || DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [series, setSeries] = useState([]);
  const size = 12;
  const userData = useSelector((state) => state.auth.userData);

  const getSeries = () => {
    const params = {
      page,
      size,
      keyword,
    };

    axios.get(`${BASE_URL}/series`, { params }).then(function (response) {
      setSeries(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
    });
  };

  useEffect(() => {
    getSeries();
    localStorage.setItem('series_page', page);
  }, [page]);

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      localStorage.setItem('serise_page', DEFAULT_PAGE);
      setPage(DEFAULT_PAGE);
      getSeries();
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
        <CCol sm={9}>
          <CForm>
            <CFormInput
              type="text"
              placeholder="Tìm series"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={handleKeyPress}
            ></CFormInput>
          </CForm>
        </CCol>
        <CCol>
          {userData && (userData.role_code === 'MODERATOR' || userData.role_code === 'ADMIN') && (
            <CButton onClick={() => setShowModal(true)}>Thêm series</CButton>
          )}
        </CCol>
      </CRow>
      <CreateSeriesModal visible={showModal} onHide={() => setShowModal(false)} />
      <h6 className="mb-3">{`${totalElements} kết quả`}</h6>
      <SeriesList series={series}></SeriesList>
      <ResponsivePagination
        total={totalPages}
        current={page}
        onPageChange={(page) => setPage(page)}
      />
    </>
  );
};

export default Series;
