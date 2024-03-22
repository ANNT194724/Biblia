import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import {
  AUTHOR_ROLE,
  BASE_URL,
  DEFAULT_PAGE,
  DEFAULT_PAGE_SIZE,
  HOME_URL,
  USER_ROLE,
} from 'src/Constants';
import { Link, useParams } from 'react-router-dom';
import {
  CButton,
  CCard,
  CCardBody,
  CCardLink,
  CCardSubtitle,
  CCardText,
  CCardTitle,
  CCol,
  CContainer,
  CForm,
  CFormLabel,
  CFormTextarea,
  CImage,
  CModal,
  CModalBody,
  CModalHeader,
  CPagination,
  CPaginationItem,
  CRow,
} from '@coreui/react';
import CoverImg from 'src/assets/images/cover.jpg';
import CIcon from '@coreui/icons-react';
import { cilStar } from '@coreui/icons';
import UpdateBookModal from 'src/components/modal/UpdateBookModal';
import Select from 'react-select';
import ErrorModal from 'src/components/modal/ErrorModal';
import Review from 'src/components/review/Review';
import ReviewList from 'src/components/review/ReviewList';
import Description from 'src/components/Description';

const BookDetail = () => {
  const [book, setBook] = useState('');
  const [authors, setAuthors] = useState([]);
  const [userReview, setUserReview] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [error, setError] = useState(null);
  const [rating, setRating] = useState(null);
  const [content, setContent] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [showPreview, setShowPreview] = useState(false);
  const [rerender, setRerender] = useState(false);
  const { book_id } = useParams();
  const userData = useSelector((state) => state.auth.userData);
  const token = useSelector((state) => state.auth.token);
  const roles = [USER_ROLE.ADMIN, USER_ROLE.MODERATOR];

  const getBook = () => {
    let params;
    if (userData) {
      params = {
        user_id: userData.user_id,
      };
    }
    axios.get(`${BASE_URL}/book/${book_id}`, { params }).then(function (response) {
      setBook(response.data.book);
      setAuthors(response.data.authors);
      if (userData) {
        setUserReview(response.data.user_review);
      }
    });
  };

  useEffect(() => {
    getBook();
    getReviews();
  }, [book_id, rerender]);

  const printAuthors = () => {
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

  const printGenres = () => {
    if (book.genres) {
      return book.genres.map((genre) => {
        return (
          <CCardLink key={genre.genre_id} href={`${HOME_URL}/genre/${genre.genre_id}`}>
            {genre.genre}
          </CCardLink>
        );
      });
    }
    return null;
  };

  const ratings = [
    {
      value: 10,
      label: '(10) Kiệt tác',
    },
    {
      value: 9,
      label: '(9) Tuyệt vời',
    },
    {
      value: 8,
      label: '(8) Rất hay',
    },
    {
      value: 7,
      label: '(7) Hay',
    },
    {
      value: 6,
      label: '(6) Ổn',
    },
    {
      value: 5,
      label: '(5) Trung bình',
    },
    {
      value: 4,
      label: '(4) Tệ',
    },
    {
      value: 3,
      label: '(3) Rất tệ',
    },
    {
      value: 2,
      label: '(2) Thất vọng',
    },
    {
      value: 1,
      label: '(1) Kinh khủng',
    },
  ];

  const createReview = () => {
    setError(null);
    const apiEndpoint = `${BASE_URL}/review`;
    const data = {
      book_id,
      rating,
      content,
    };
    axios
      .post(apiEndpoint, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setRerender(!rerender);
      })
      .catch((err) => {
        setError(err.message);
      });
  };

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  const getReviews = () => {
    const size = DEFAULT_PAGE_SIZE;
    const params = {
      page,
      size,
      book_id,
      user_id: userData ? userData.user_id : null,
    };
    axios.get(`${BASE_URL}/review`, { params }).then(function (response) {
      setReviews(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
    });
  };

  return (
    <>
      <CContainer>
        <CRow>
          <CCol xs={4}>
            <CImage
              rounded
              thumbnail
              src={book.image_url ? book.image_url : CoverImg}
              style={{ width: '100%' }}
              onClick={() => {
                if (book.image_url) setShowPreview(!showPreview);
              }}
            ></CImage>
          </CCol>
          <CCol>
            <CCard style={{ width: '100%' }}>
              <CCardBody>
                <CCardTitle className="mb-2">
                  <h3>{book.title}</h3>
                </CCardTitle>
                <CCardSubtitle className="mb-4 text-medium-emphasis">
                  {printAuthors()}
                </CCardSubtitle>
                <CCardText>
                  <CIcon icon={cilStar} />
                  {` Điểm: ${book.rating}/10`}
                </CCardText>
                <CCardText>
                  <CRow className="mb-3">
                    <CCol>ISBN: {book.isbn}</CCol>
                    <CCol>{book.pages_no} trang</CCol>
                    <CCol>Năm xuất bản: {book.published_year}</CCol>
                  </CRow>
                  {book.alias && <CCardText>Tên khác: {book.alias}</CCardText>}
                  <CRow className="mb-3">
                    <CCol>
                      Nhà phát hành:{' '}
                      <Link to={`/issuing-house/${book.issuing_house_id}`}>
                        {book.issuing_house}
                      </Link>
                    </CCol>
                  </CRow>
                  <CRow className="mb-3">
                    <CCol>
                      Nhà xuất bản:&nbsp;
                      <Link to={`/publisher/${book.publisher_id}`}>{book.publisher}</Link>
                    </CCol>
                  </CRow>
                  {userData && roles.includes(userData.role_code) && (
                    <CRow className="mb-2">
                      <CCol>
                        <CButton onClick={() => setShowModal(true)}>Chỉnh sửa</CButton>
                      </CCol>
                    </CRow>
                  )}
                </CCardText>
                {/* <CCardText dangerouslySetInnerHTML={{ __html: book.description }}></CCardText> */}
                {book.description && <Description description={book.description} />}
                <CCardText>Thể loại: {printGenres()}</CCardText>
                {token && !userReview && (
                  <CForm>
                    <CRow className="mb-3">
                      <CFormLabel className="col-sm-2 col-form-label">Đánh giá</CFormLabel>
                      <CCol sm={5}>
                        <Select options={ratings} onChange={(e) => setRating(e.value)} />
                      </CCol>
                    </CRow>
                    <CFormTextarea
                      className="mb-3"
                      placeholder="Review.."
                      floatingLabel="Review..."
                      style={{ height: '100px' }}
                      onChange={(e) => setContent(e.target.value)}
                    ></CFormTextarea>
                    <CButton color="primary" onClick={createReview}>{` Lưu`}</CButton>
                  </CForm>
                )}
              </CCardBody>
            </CCard>
            {token && userReview && <Review review={userReview} />}
          </CCol>
        </CRow>
        <span>
          <strong>{`${totalElements} đánh giá:`}</strong>
        </span>
        <ReviewList reviews={reviews}></ReviewList>
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
        {error && (
          <ErrorModal visible={error !== null} onHide={() => setError(null)} message={error} />
        )}
        {book.image_url && (
          <CModal visible={showPreview} onClose={() => setShowPreview(!showPreview)}>
            <CModalHeader closeButton>
              <h5>Bìa sách</h5>
            </CModalHeader>
            <CModalBody>
              <CImage src={book.image_url} style={{ width: '100%' }}></CImage>
            </CModalBody>
          </CModal>
        )}
        {userData && roles.includes(userData.role_code) && (
          <UpdateBookModal
            visible={showModal}
            onHide={() => setShowModal(false)}
            book={book}
            authors={authors}
          />
        )}
      </CContainer>
    </>
  );
};

export default BookDetail;
