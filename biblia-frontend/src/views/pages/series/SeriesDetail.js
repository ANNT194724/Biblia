import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import {
  BASE_URL,
  DEFAULT_PAGE,
  DEFAULT_PAGE_SIZE,
  SERIES_STATUS,
  SORT_BY,
  SORT_DIRECTION,
  USER_ROLE,
} from 'src/Constants';
import { Link, useParams } from 'react-router-dom';
import BookList from 'src/components/book/BookList';
import ResponsivePagination from 'react-responsive-pagination';
import UpdateSeriesModal from 'src/components/modal/UpdateSeriesModal';
const {
  CCard,
  CContainer,
  CRow,
  CCol,
  CCardBody,
  CCardTitle,
  CCardText,
  CSpinner,
  CBadge,
} = require('@coreui/react');

const SeriesDetail = () => {
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [books, setBooks] = useState([]);
  const [series, setseries] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const userData = useSelector((state) => state.auth.userData);
  const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR];
  const [loadingseries, setLoadingseries] = useState(true);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const { series_id } = useParams();

  const getseries = () => {
    axios.get(`${BASE_URL}/series/${series_id}`).then(function (response) {
      setseries(response.data);
      setLoadingseries(false);
    });
  };

  useEffect(() => {
    getseries();
  }, [series_id]);

  const getBooks = () => {
    const size = DEFAULT_PAGE_SIZE;
    const sort_by = SORT_BY.TITLE;
    const sort_direction = SORT_DIRECTION.ASC;
    const params = {
      page,
      size,
      series_id,
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

  const printAuthors = (authors) => {
    return authors.map((author) => {
      return (
        <li key={author.author_id}>
          <Link to={`/author/${author.author_id}`}>{author.name}</Link>
        </li>
      );
    });
  };

  useEffect(() => {
    getBooks();
  }, [page, series_id]);

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  if (loadingseries || loadingBooks) {
    return (
      <>
        <CSpinner color="primary"></CSpinner>
      </>
    );
  }

  return (
    <>
      <CContainer>
        <CCard style={{ width: '100%' }}>
          <CCardBody>
            <CCardTitle className="mb-2">
              <h3>
                {`${series.title} `}
                {series.status === SERIES_STATUS.COMPLETE && (
                  <CBadge color="success">Đã hoàn thành</CBadge>
                )}
                {series.status === SERIES_STATUS.ONGOING && (
                  <CBadge color="warning">Đang phát hành</CBadge>
                )}
                {series.status === SERIES_STATUS.ON_HOLD && (
                  <CBadge color="danger">Tạm dừng</CBadge>
                )}
              </h3>
            </CCardTitle>
            <CCardText className="mb-4 text-medium-emphasis">
              <CRow className="mb-3">
                <CCol xs={1}>Tác giả: </CCol>
                <CCol>{printAuthors(series.authors)}</CCol>
              </CRow>
            </CCardText>
            <CCardText>
              {series.alias && (
                <CRow className="mb-3">
                  <CCol>{`Tên khác: ${series.alias}`}</CCol>
                </CRow>
              )}
            </CCardText>
            <CCardText>
              <CRow className="mb-3">
                <CCol>
                  Nhà phát hành:{' '}
                  <Link to={`/issuing-house/${series.issuing_house_id}`}>
                    {series.issuing_house}
                  </Link>
                </CCol>
              </CRow>
            </CCardText>
            {/* {userData && roles.includes(userData.role_code) && (
              <CRow className="mb-2">
                <CCol>
                  <CButton onClick={() => setShowModal(true)}>Chỉnh sửa</CButton>
                </CCol>
              </CRow>
            )} */}
            <strong>
              Tóm tắt nội dung:<br></br>
            </strong>
            <CCardText dangerouslySetInnerHTML={{ __html: series.description }}></CCardText>
          </CCardBody>
        </CCard>
        {userData && roles.includes(userData.role_code) && (
          <UpdateSeriesModal
            visible={showModal}
            onHide={() => setShowModal(false)}
            series={series}
          />
        )}
        <br></br>
        <span>
          <strong>{`${totalElements} tập:`}</strong>
        </span>
        <BookList books={books}></BookList>
        <ResponsivePagination
          total={totalPages}
          current={page}
          onPageChange={(page) => setPage(page)}
        />
      </CContainer>
    </>
  );
};

export default SeriesDetail;
