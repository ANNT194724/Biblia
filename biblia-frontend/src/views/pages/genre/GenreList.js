import React, { useEffect, useState } from 'react';
import { BASE_URL } from 'src/Constants';
import { useNavigate } from 'react-router-dom';
const { CRow, CCard, CCardBody, CContainer, CCardLink } = require('@coreui/react');

const GenreList = () => {
  const [genres, setGenres] = useState([]);
  // const userData = useSelector((state) => state.auth.userData);
  const navigate = useNavigate();

  const getGenres = () => {
    fetch(`${BASE_URL}/genre`)
      .then((response) => response.json())
      .then((data) => {
        setGenres(data);
      });
  };

  useEffect(() => {
    getGenres();
  }, []);

  return (
    <>
      <h6 className="mb-3">{`${genres.length} thể loại`}</h6>
      <CContainer className="mt-3 mb-3">
        <CRow>
          {genres.map((genre) => {
            return (
              <CCard
                className="col-lg-3 col-md-4 col-sm-6 col-12 g-3 mb-1 text-center"
                key={genre.genre_id}
              >
                <CCardBody>
                  <CCardLink onClick={() => navigate(`${genre.genre_id}`)}>{genre.genre}</CCardLink>
                </CCardBody>
              </CCard>
            );
          })}
        </CRow>
      </CContainer>
    </>
  );
};

export default GenreList;
