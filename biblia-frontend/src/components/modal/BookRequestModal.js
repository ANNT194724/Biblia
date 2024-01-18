import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { cilPlus, cilX } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import { useSelector } from 'react-redux';
import storage from 'src/firebase';
import Select from 'react-select';
import AsyncSelect from 'react-select/async';
import makeAnimated from 'react-select/animated';
import axios from 'axios';
import debounce from 'debounce-promise';
import CreateAuthorModal from './CreateAuthorModal';
import { AUTHOR_ROLE, BASE_URL, DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from 'src/Constants';
import ErrorModal from './ErrorModal';
import ConfirmModal from './ConfirmModal';
const {
  CRow,
  CForm,
  CCol,
  CFormInput,
  CButton,
  CModal,
  CModalHeader,
  CModalTitle,
  CModalBody,
  CFormLabel,
  CModalFooter,
  CFormTextarea,
  CImage,
  CSpinner,
} = require('@coreui/react');

const BookRequestModal = (props) => {
  const book = props.book;
  const authors = props.authors;
  const [showAuthorModal, setShowAuthorModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [genreOptions, setGenreOptions] = useState([]);
  const [languageOptions, setLanguageOptions] = useState([]);
  const [publisherOptions, setPublisherOptions] = useState([]);
  const [issuingHouseOptions, setIssuingHouseOptions] = useState([]);
  const [authorComponents, setAuthorComponents] = useState([]);
  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [componentId, setComponentId] = useState(0);
  const [editAuthor, setEditAuthor] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const token = useSelector((state) => state.auth.token);

  const defaultIssuingHouse = () => {
    return { value: book.issuing_house_id, label: book.issuing_house };
  };

  const defaultPublisher = () => {
    return { value: book.publisher_id, label: book.publisher };
  };

  const defaultLanguage = () => {
    return { value: book.language, label: book.language };
  };

  const defaultYear = () => {
    return { value: book.published_year, label: book.published_year };
  };

  const defaultGenre = () => {
    if (book.genres) {
      return book.genres.map((genre) => {
        return { value: genre.genre_id, label: genre.genre };
      });
    }
    return [];
  };

  const defaultGenreIds = () => {
    if (book.genres) {
      return book.genres.map((genre) => {
        return genre.genre_id;
      });
    }
    return [];
  };

  const [data, setData] = useState({
    isbn: book.isbn,
    title: book.title,
    image_url: book.image_url,
    alias: book.alias,
    book_authors: authors,
    genre_ids: defaultGenreIds(),
    publisher_id: book.publisher_id,
    publisher: book.publisher,
    issuing_house_id: book.issuing_house_id,
    issuing_house: book.issuing_house,
    published_year: book.published_year,
    language: book.language,
    pages_no: book.pages_no,
    description: book.description,
    status: 1,
  });

  const handleCoverUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const storageRef = storage.ref();
      const coverRef = storageRef.child(`covers/${file.name}`);

      coverRef.put(file).then(() => {
        coverRef.getDownloadURL().then((url) => {
          setData({ ...data, image_url: url });
        });
      });
    }
  };

  const handleRemoveAuthor = (id) => {
    setAuthorComponents((components) => components.filter((component) => component.id !== id));
    setSelectedAuthors((authors) => authors.filter((_, index) => index !== id));
    setSelectedRoles((roles) => roles.filter((_, index) => index !== id));
  };

  const renderAuthorComponents = () => {
    return authorComponents.map((component) => (
      <div key={component.id}>
        <CRow className="mb-3">
          <CFormLabel className="col-sm-2 col-form-label"></CFormLabel>
          <CCol sm={4}>
            <AsyncSelect
              placeholder=""
              loadOptions={getAuthorOptions}
              onChange={(e) => {
                handleAuthorChange(e.value, component.id);
              }}
            />
          </CCol>
          <CFormLabel className="col-sm-2 col-form-label">Vai trò</CFormLabel>
          <CCol sm={3}>
            <Select options={roles} onChange={(e) => handleRoleChange(e.value, component.id)} />
          </CCol>
          <CCol>
            <CButton
              onClick={() => {
                handleRemoveAuthor(component.id);
                combineInputs();
              }}
            >
              <CIcon icon={cilX} />
            </CButton>
          </CCol>
        </CRow>
      </div>
    ));
  };

  const printAuthors = () => {
    if (!authors) {
      return '';
    }
    const sortedAuthors = authors.sort((a, b) => {
      if (a.role === AUTHOR_ROLE.AUTHOR) return -1;
      if (b.role !== AUTHOR_ROLE.AUTHOR) return 1;
      return 0;
    });

    return sortedAuthors.map((author) => {
      if (author.role === AUTHOR_ROLE.AUTHOR) {
        return `${author.name}`;
      } else {
        return `, ${author.name} (${author.role})`;
      }
    });
  };

  const handleEditAuthor = () => {
    if (editAuthor) {
      setData({ ...data, book_authors: authors });
    }
    setEditAuthor(!editAuthor);
  };

  useEffect(() => {
    getGenres();
    getIssuingHouses();
    getPublishers();
    getLanguages();
  }, []);

  const getAuthorOptions = debounce(async (name) => {
    if (!name) {
      return Promise.resolve({ options: [] });
    }

    const params = {
      page: DEFAULT_PAGE,
      size: DEFAULT_PAGE_SIZE,
      name,
    };

    const response = await axios.get(`${BASE_URL}/author`, { params });
    const options = await response.data.data.map((element) => ({
      value: element.author_id,
      label: element.name,
    }));
    return options;
  }, 1000);

  const getGenres = () => {
    fetch(`${BASE_URL}/genre`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.genre_id,
          label: element.genre,
        }));
        setGenreOptions(options);
      });
  };

  const getIssuingHouses = () => {
    fetch(`${BASE_URL}/issuing-house`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.issuing_house_id,
          label: element.name,
        }));
        setIssuingHouseOptions(options);
      });
  };

  const getPublishers = () => {
    fetch(`${BASE_URL}/publisher`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.publisher_id,
          label: element.name,
        }));
        setPublisherOptions(options);
      });
  };

  const getLanguages = () => {
    fetch(`${BASE_URL}/language`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.local ? element.local : element.name,
          label: element.local ? element.local : element.name,
        }));
        setLanguageOptions(options);
      });
  };

  const years = Array.from({ length: 50 }, (_, index) => new Date().getFullYear() - index).map(
    (year) => ({
      value: year,
      label: year,
    }),
  );

  const roles = ['Tác giả', 'Nguyên tác', 'Minh họa', 'Dịch giả', 'Thiết kế nhân vật'].map(
    (role) => ({
      value: role,
      label: role,
    }),
  );

  const handleGenreChange = (e) => {
    const genreIds = Array.isArray(e) ? e.map((x) => x.value) : [];
    setData({ ...data, genre_ids: genreIds });
  };

  const handleAuthorChange = (authorId, index) => {
    setSelectedAuthors((prevAuthors) => {
      const newAuthors = [...prevAuthors];
      newAuthors[index] = authorId;
      return newAuthors;
    });
  };

  const handleRoleChange = (role, index) => {
    setSelectedRoles((prevRoles) => {
      const newRoles = [...prevRoles];
      newRoles[index] = role;
      return newRoles;
    });
  };

  const combineInputs = () => {
    let combinedArray = selectedAuthors.map((authorId, index) => ({
      author_id: authorId,
      role: selectedRoles[index],
    }));
    combinedArray = combinedArray.filter((item) => !!item);
    setData({ ...data, book_authors: combinedArray });
  };

  const handleDescriptionChange = (event) => {
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(event.target.value, 'text/html');
    setData({ ...data, description: parsedHtml.body.innerHTML });
  };

  useEffect(() => {
    combineInputs();
  }, [selectedAuthors, selectedRoles]);

  useEffect(() => {
    setData({
      isbn: props.book.isbn,
      title: props.book.title,
      image_url: props.book.image_url,
      alias: props.book.alias,
      book_authors: authors,
      genre_ids: defaultGenreIds(),
      publisher_id: props.book.publisher_id,
      publisher: props.book.publisher,
      issuing_house_id: props.book.issuing_house_id,
      issuing_house: props.book.issuing_house,
      published_year: props.book.published_year,
      language: props.book.language,
      pages_no: props.book.pages_no,
      description: props.book.description,
      status: 1,
    });
  }, [props]);

  const animatedComponents = makeAnimated();

  const updateBook = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    const apiEndpoint = `${BASE_URL}/book/${book.book_id}`;
    if (data.title === '') {
      setError('Tiêu đề không được bỏ trống !');
      setLoading(false);
      return;
    }

    axios
      .put(apiEndpoint, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setLoading(false);
        props.onHide();
        window.location.reload();
      })
      .catch((err) => {
        setLoading(false);
        setError(err.message);
      });
  };

  const deleteBook = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);
    const apiEndpoint = `${BASE_URL}/book/${book.book_id}`;

    axios
      .delete(apiEndpoint, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setLoading(false);
        props.onHide();
        window.location.reload();
      })
      .catch((err) => {
        setLoading(false);
        setError(err.message);
      });
  };

  return (
    <>
      <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
        <CModalHeader>
          <CModalTitle>Chỉnh sửa</CModalTitle>
        </CModalHeader>
        <CModalBody>
          {book.image_url && (
            <CImage
              thumbnail
              className="mb-3"
              align="center"
              src={book.image_url}
              width={200}
            ></CImage>
          )}
          <CForm>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">ISBN</CFormLabel>
              <CCol>
                <CFormInput
                  type="text"
                  defaultValue={book.isbn}
                  onChange={(e) => setData({ ...data, isbn: e.target.value })}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Tiêu đề</CFormLabel>
              <CCol>
                <CFormInput
                  type="text"
                  name="title"
                  defaultValue={book.title}
                  onChange={(e) => setData({ ...data, title: e.target.value })}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Tên khác</CFormLabel>
              <CCol>
                <CFormInput
                  type="text"
                  name="alias"
                  defaultValue={book.alias}
                  onChange={(e) => setData({ ...data, alias: e.target.value })}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CCol>
                <CButton onClick={handleEditAuthor}>
                  {editAuthor ? `Hủy` : `Chỉnh sửa tác giả`}
                </CButton>
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Tác giả</CFormLabel>
              {!editAuthor && <CCol>{printAuthors()}</CCol>}
              {editAuthor && (
                <>
                  <CCol sm={4}>
                    <AsyncSelect
                      placeholder=""
                      loadOptions={getAuthorOptions}
                      onChange={(e) => {
                        handleAuthorChange(e.value, 0);
                      }}
                    />
                  </CCol>
                  <CFormLabel className="col-sm-2 col-form-label">Vai trò</CFormLabel>
                  <CCol sm={3}>
                    <Select options={roles} onChange={(e) => handleRoleChange(e.value, 0)} />
                  </CCol>
                  <CCol>
                    <CButton>
                      <CIcon
                        icon={cilPlus}
                        onClick={() => {
                          setAuthorComponents((prevComponents) => [
                            ...prevComponents,
                            { id: componentId + 1 },
                          ]);
                          setComponentId(componentId + 1);
                        }}
                      />
                    </CButton>
                  </CCol>
                </>
              )}
            </CRow>
            {editAuthor && renderAuthorComponents()}
            {editAuthor && (
              <CRow className="mb-3">
                <CCol>
                  <CButton onClick={() => setShowAuthorModal(true)}>Thêm tác giả</CButton>
                </CCol>
              </CRow>
            )}
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Ảnh bìa sách</CFormLabel>
              <CCol>
                <CFormInput type="file" accept="image/*" onChange={handleCoverUpload} />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Nhà phát hành</CFormLabel>
              <CCol sm={4}>
                <Select
                  placeholder=""
                  options={issuingHouseOptions}
                  defaultValue={defaultIssuingHouse()}
                  onChange={(e) => {
                    setData({ ...data, issuing_house_id: e.value, issuing_house: e.label });
                  }}
                />
              </CCol>
              <CFormLabel className="col-sm-2 col-form-label">Năm xuất bản</CFormLabel>
              <CCol sm={4}>
                <Select
                  options={years}
                  defaultValue={defaultYear()}
                  onChange={(e) => setData({ ...data, published_year: e.value })}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Nhà xuất bản</CFormLabel>
              <CCol>
                <Select
                  placeholder=""
                  options={publisherOptions}
                  defaultValue={defaultPublisher()}
                  onChange={(e) => {
                    setData({ ...data, publisher_id: e.value, publisher: e.label });
                  }}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Thể loại</CFormLabel>
              <CCol>
                <Select
                  isMulti
                  placeholder=""
                  options={genreOptions}
                  defaultValue={defaultGenre()}
                  onChange={handleGenreChange}
                  components={animatedComponents}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Ngôn ngữ</CFormLabel>
              <CCol sm={4}>
                <Select
                  placeholder=""
                  options={languageOptions}
                  defaultValue={defaultLanguage()}
                  onChange={(e) => setData({ ...data, language: e.value })}
                />
              </CCol>
              <CFormLabel className="col-sm-2 col-form-label">Số trang</CFormLabel>
              <CCol sm={4}>
                <CFormInput
                  type="number"
                  name="pages_no"
                  defaultValue={book.pages_no}
                  onChange={(e) => setData({ ...data, pages_no: e.target.value })}
                ></CFormInput>
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Mô tả</CFormLabel>
              <CCol>
                <CFormTextarea
                  type="text"
                  style={{ height: '200px' }}
                  defaultValue={book.description}
                  onChange={handleDescriptionChange}
                />
              </CCol>
            </CRow>
          </CForm>
          {error && (
            <ErrorModal visible={error !== null} onHide={() => setError(null)} message={error} />
          )}
          <ConfirmModal
            visible={showConfirm}
            onHide={() => setShowConfirm(false)}
            message={`Bạn muốn xóa yêu cầu này ?`}
            onConfirm={deleteBook}
          />
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={() => setShowConfirm(true)}>
            Xóa
          </CButton>
          <CButton color="primary" onClick={updateBook}>
            {loading && <CSpinner component="span" size="sm" aria-hidden="true" />}
            {` Chấp nhận`}
          </CButton>
        </CModalFooter>
      </CModal>
      <CreateAuthorModal visible={showAuthorModal} onHide={() => setShowAuthorModal(false)} />
    </>
  );
};

BookRequestModal.propsType = {
  visible: PropTypes.bool,
  onHide: PropTypes.func,
  book: PropTypes.object,
  authors: PropTypes.array,
};

export default BookRequestModal;
