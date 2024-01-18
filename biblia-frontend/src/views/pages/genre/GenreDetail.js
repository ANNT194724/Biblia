import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { BASE_URL, DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from 'src/Constants';
import BookList from 'src/components/book/BookList';
import { useParams } from 'react-router-dom';
const { CPagination, CPaginationItem } = require('@coreui/react');

const GenreDetail = () => {
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [genre, setGenre] = useState([]);
  const [books, setBooks] = useState([]);
  const { genre_id } = useParams();
  const size = DEFAULT_PAGE_SIZE;

  const getGenres = () => {
    axios.get(`${BASE_URL}/genre/${genre_id}`).then((response) => {
      setGenre(response.data);
    });
  };

  const getBooks = () => {
    const params = {
      page,
      size,
    };

    const genre_ids = [genre_id];

    axios.post(`${BASE_URL}/book/genre`, genre_ids, { params }).then(function (response) {
      setBooks(response.data.data);
      setTotalpages(response.data.total_pages);
      setTotalElements(response.data.total_elements);
    });
  };

  useEffect(() => {
    getGenres();
    getBooks();
  }, [page]);

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  return (
    <>
      <h5 className="mb-3">{`Thể loại ${genre.genre}`}</h5>
      <h6 className="mb-3">{`${totalElements} đầu sách`}</h6>
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
    </>
  );
};

export default GenreDetail;
