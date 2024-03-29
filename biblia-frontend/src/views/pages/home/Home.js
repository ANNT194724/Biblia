import { cilSearch } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import CreateBookModal from 'src/components/modal/CreateBookModal';
import axios from 'axios';
import { BASE_URL, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, SORT_BY, SORT_DIRECTION } from 'src/Constants';
import BookList from 'src/components/book/BookList';
import ResponsivePagination from 'react-responsive-pagination';
const { CRow, CForm, CCol, CFormInput, CButton } = require('@coreui/react');

const Home = () => {
  const [keyword, setKeyword] = useState(localStorage.getItem('keyword') || '');
  const [page, setPage] = useState(Number(localStorage.getItem('page')) || DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [books, setBooks] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const size = DEFAULT_PAGE_SIZE;
  const sort_by = SORT_BY.RATING;
  const sort_direction = SORT_DIRECTION.DESC;
  const userData = useSelector((state) => state.auth.userData);

  const getBooks = () => {
    const params = {
      page,
      size,
      keyword,
      sort_by,
      sort_direction,
    };

    axios.get(`${BASE_URL}/book`, { params }).then(function (response) {
      setBooks(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
    });
  };

  useEffect(() => {
    getBooks();
    localStorage.setItem('page', page);
  }, [page]);

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      localStorage.setItem('keyword', keyword);
      localStorage.setItem('page', DEFAULT_PAGE);
      setPage(DEFAULT_PAGE);
      getBooks();
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
              placeholder="Tìm sách qua mã ISBN hoặc tiêu đề"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={handleKeyPress}
            ></CFormInput>
          </CForm>
        </CCol>
        <CCol>
          {userData && userData.role_code === 'USER' && (
            <CButton onClick={() => setShowModal(true)}>Yêu cầu thêm sách</CButton>
          )}
          {userData && (userData.role_code === 'MODERATOR' || userData.role_code === 'ADMIN') && (
            <CButton onClick={() => setShowModal(true)}>Thêm sách</CButton>
          )}
        </CCol>
      </CRow>
      <CreateBookModal
        role={userData ? userData.role_code : ''}
        visible={showModal}
        onHide={() => setShowModal(false)}
      />
      <h6 className="mb-3">{`${totalElements} kết quả`}</h6>
      <BookList books={books}></BookList>
      <ResponsivePagination
        total={totalPages}
        current={page}
        onPageChange={(page) => setPage(page)}
      />
    </>
  );
};

export default Home;
