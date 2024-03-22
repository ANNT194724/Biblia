import { cilArrowCircleRight, cilStar } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import React from 'react';
import PropTypes from 'prop-types';
import { AUTHOR_ROLE, SERIES_STATUS } from 'src/Constants';
import CoverImg from 'src/assets/images/cover.jpg';
import { Link, useNavigate } from 'react-router-dom';
const {
  CRow,
  CCol,
  CButton,
  CCard,
  CCardTitle,
  CCardBody,
  CCardText,
  CBadge,
  CContainer,
  CCarousel,
  CCarouselItem,
  CImage,
} = require('@coreui/react');

const SeriesList = (props) => {
  const series = props.series;
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

  const renderCovers = (covers) => {
    if (covers.length === 0) {
      covers = [CoverImg];
    }
    let id = 0;
    return (
      <CCarousel controls dark>
        {covers.map((cover) => {
          id++;
          return (
            <CCarouselItem key={id}>
              <CImage style={{ maxWidth: '80%' }} src={cover} alt={`cover-${id}`} />
            </CCarouselItem>
          );
        })}
      </CCarousel>
    );
  };

  return (
    <>
      <CContainer className="mt-3 mb-3">
        <CRow>
          {series.map((series) => {
            return (
              <CCard
                className="col-lg-4 col-md-4 col-sm-6 col-12 g-3 mb-3 text-center"
                key={series.book_id}
              >
                {renderCovers(series.covers)}
                <CRow className="g-0">
                  <CCardBody>
                    <CCardTitle>
                      {series.title} <br></br>
                      {series.status === SERIES_STATUS.COMPLETE && (
                        <CBadge color="success">Đã hoàn thành</CBadge>
                      )}
                      {series.status === SERIES_STATUS.ONGOING && (
                        <CBadge color="warning">Đang phát hành</CBadge>
                      )}
                      {series.status === SERIES_STATUS.ON_HOLD && (
                        <CBadge color="danger">Tạm dừng</CBadge>
                      )}
                    </CCardTitle>
                    <CRow>
                      <CCardText>{printAuthors(series.authors)}</CCardText>
                      <CCol>
                        <CCardText>
                          <strong>{`Số tập: ${series.vols}`}</strong> <br></br>
                          <CIcon icon={cilStar} />
                          {` Điểm TB: ${series.rating}/10`}
                        </CCardText>
                      </CCol>
                      <CCol>
                        <CButton onClick={() => navigate(`/series/${series.series_id}`)}>
                          {`Xem Chi tiết `}
                          <CIcon icon={cilArrowCircleRight} />
                        </CButton>
                      </CCol>
                    </CRow>
                  </CCardBody>
                </CRow>
              </CCard>
            );
          })}
        </CRow>
      </CContainer>
    </>
  );
};

SeriesList.propsType = {
  series: PropTypes.array,
};

export default SeriesList;
