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
import { BASE_URL, DEFAULT_PAGE, DEFAULT_PAGE_SIZE } from 'src/Constants';
import ErrorModal from './ErrorModal';
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
} = require('@coreui/react');

const CreateBookModal = (props) => {
  const [showAuthorModal, setShowAuthorModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isbn, setISBN] = useState('');
  const [title, setTitle] = useState('');
  const [alias, setAlias] = useState('');
  const [image_url, setImageUrl] = useState('');
  const [book_authors, setBookAuthors] = useState([]);
  const [genre_ids, setGenreIds] = useState([]);
  const [publisher_id, setPublisherId] = useState(null);
  const [publisher, setPublisher] = useState('');
  const [issuing_house_id, setIssuingHouseId] = useState(0);
  const [issuing_house, setIssuingHouse] = useState('');
  const [published_year, setPublishedYear] = useState(null);
  const [language, setLanguage] = useState('');
  const [pages_no, setPagesNo] = useState(null);
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [genreOptions, setGenreOptions] = useState([]);
  const [languageOptions, setLanguageOptions] = useState([]);
  const [publisherOptions, setPublisherOptions] = useState([]);
  const [issuingHouseOptions, setIssuingHouseOptions] = useState([]);
  const [authorComponents, setAuthorComponents] = useState([]);
  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [componentId, setComponentId] = useState(0);
  const token = useSelector((state) => state.auth.token);

  const handleCoverUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const storageRef = storage.ref();
      const coverRef = storageRef.child(`covers/${file.name}`);

      coverRef.put(file).then(() => {
        coverRef.getDownloadURL().then((url) => {
          setImageUrl(url);
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

  useEffect(() => {
    if (props.visible) {
      getGenres();
      getIssuingHouses();
      getPublishers();
      getLanguages();
    }
  }, [props.visible]);

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

  const getGenres = async () => {
    await fetch(`${BASE_URL}/genre`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.genre_id,
          label: element.genre,
        }));
        setGenreOptions(options);
      });
  };

  const getIssuingHouses = async () => {
    await fetch(`${BASE_URL}/issuing-house`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.issuing_house_id,
          label: element.name,
        }));
        setIssuingHouseOptions(options);
      });
  };

  const getPublishers = async () => {
    await fetch(`${BASE_URL}/publisher`)
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((element) => ({
          value: element.publisher_id,
          label: element.name,
        }));
        setPublisherOptions(options);
      });
  };

  const getLanguages = async () => {
    await fetch(`${BASE_URL}/language`)
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

  const handleGenreChange = (e) => setGenreIds(Array.isArray(e) ? e.map((x) => x.value) : []);

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
    setBookAuthors(combinedArray);
  };

  useEffect(() => {
    combineInputs();
  }, [selectedAuthors, selectedRoles]);

  const animatedComponents = makeAnimated();

  const handleDescriptionChange = (event) => {
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(event.target.value, 'text/html');
    setDescription(parsedHtml.body.innerHTML);
  };

  const createBook = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = 'http://localhost:8080/book';

    const data = {
      isbn,
      title,
      image_url,
      alias,
      book_authors,
      genre_ids,
      publisher_id,
      publisher,
      issuing_house_id,
      issuing_house,
      published_year,
      language,
      pages_no,
      description,
    };

    if (title === '') {
      setError('Tiêu đề không được bỏ trống !');
      setLoading(false);
      return;
    }

    axios
      .post(apiEndpoint, data, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(() => {
        setLoading(false);
        window.location.reload();
      })
      .catch((err) => {
        setLoading(false);
        console.error('Registration error:', err);
        setError(err.message);
      });
  };

  return (
    <>
      <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
        <CModalHeader>
          <CModalTitle>Thêm sách mới</CModalTitle>
        </CModalHeader>
        <CModalBody>
          {image_url && (
            <CImage thumbnail className="mb-3" align="center" src={image_url} width={200}></CImage>
          )}
          <CForm>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">ISBN</CFormLabel>
              <CCol>
                <CFormInput type="text" onChange={(e) => setISBN(e.target.value)} />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Tiêu đề</CFormLabel>
              <CCol>
                <CFormInput type="text" name="title" onChange={(e) => setTitle(e.target.value)} />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Tên khác</CFormLabel>
              <CCol>
                <CFormInput type="text" name="alias" onChange={(e) => setAlias(e.target.value)} />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Tác giả</CFormLabel>
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
            </CRow>
            {renderAuthorComponents()}
            {(props.role === 'MODERATOR' || props.role === 'ADMIN') && (
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
                  onChange={(e) => {
                    setIssuingHouseId(e.value);
                    setIssuingHouse(e.label);
                  }}
                />
              </CCol>
              <CFormLabel className="col-sm-2 col-form-label">Năm xuất bản</CFormLabel>
              <CCol sm={4}>
                <Select options={years} onChange={(e) => setPublishedYear(e.value)} />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Nhà xuất bản</CFormLabel>
              <CCol>
                <Select
                  placeholder=""
                  options={publisherOptions}
                  onChange={(e) => {
                    setPublisherId(e.value);
                    setPublisher(e.label);
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
                  onChange={(e) => setLanguage(e.value)}
                />
              </CCol>
              <CFormLabel className="col-sm-2 col-form-label">Số trang</CFormLabel>
              <CCol sm={4}>
                <CFormInput
                  type="number"
                  name="pages_no"
                  onChange={(e) => setPagesNo(e.target.value)}
                ></CFormInput>
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Mô tả</CFormLabel>
              <CCol>
                <CFormTextarea type="text" onChange={handleDescriptionChange} />
              </CCol>
            </CRow>
          </CForm>
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={props.onHide}>
            Đóng
          </CButton>
          <CButton color="primary" onClick={createBook} disabled={loading}>
            Thêm sách
          </CButton>
        </CModalFooter>
      </CModal>
      <CreateAuthorModal visible={showAuthorModal} onHide={() => setShowAuthorModal(false)} />
      {error && (
        <ErrorModal visible={error !== null} onHide={() => setError(null)} message={error} />
      )}
    </>
  );
};

CreateBookModal.propsType = {
  role: PropTypes.string,
  onHide: PropTypes.func,
  visible: PropTypes.bool,
};

export default CreateBookModal;
