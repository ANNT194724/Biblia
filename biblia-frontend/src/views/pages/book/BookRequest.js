import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { BASE_URL, DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from 'src/Constants';
import BookRequestList from 'src/components/book/BookRequestList';
const { CPagination, CPaginationItem } = require('@coreui/react');

const BookRequest = () => {
  const [page, setPage] = useState(DEFAULT_PAGE);
  const [totalPages, setTotalpages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [books, setBooks] = useState([]);
  const size = DEFAULT_PAGE_SIZE;
  const token = useSelector((state) => state.auth.token);

  const getBooks = () => {
    axios
      .get(`${BASE_URL}/book/request`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          page,
          size,
        },
      })
      .then(function (response) {
        setBooks(response.data.data);
        setTotalpages(response.data.total_pages);
        setTotalElements(response.data.total_elements);
      });
  };

  useEffect(() => {
    getBooks();
  }, [page]);

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  return (
    <>
      <h6 className="mb-3">{`${totalElements} yêu cầu`}</h6>
      <BookRequestList books={books}></BookRequestList>
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

export default BookRequest;
