import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { cilPlus, cilX } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import { useSelector } from 'react-redux';
import Select from 'react-select';
import AsyncSelect from 'react-select/async';
import axios from 'axios';
import debounce from 'debounce-promise';
import CreateAuthorModal from './CreateAuthorModal';
import { BASE_URL, DEFAULT_PAGE, DEFAULT_PAGE_SIZE, SERIES_STATUS } from 'src/Constants';
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
} = require('@coreui/react');

const CreateSeriesModal = (props) => {
  const [showAuthorModal, setShowAuthorModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [title, setTitle] = useState('');
  const [alias, setAlias] = useState('');
  const [series_authors, setSeriesAuthors] = useState([]);
  const [issuing_house_id, setIssuingHouseId] = useState(0);
  const [issuing_house, setIssuingHouse] = useState('');
  const [status, setStatus] = useState(0);
  const [description, setDescription] = useState('');
  const [book_ids, setBookIds] = useState([]);
  const [error, setError] = useState('');
  const [issuingHouseOptions, setIssuingHouseOptions] = useState([]);
  const [authorComponents, setAuthorComponents] = useState([]);
  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [componentId, setComponentId] = useState(0);
  const token = useSelector((state) => state.auth.token);

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
      getIssuingHouses();
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

  const getBookOptions = debounce(async (keyword) => {
    if (!keyword) {
      return Promise.resolve({ options: [] });
    }
    const params = { keyword };
    const response = await axios.get(`${BASE_URL}/book/view`, { params });
    const options = await response.data.map((element) => ({
      value: element.book_id,
      label: element.title,
    }));
    return options;
  }, 1000);

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

  const roles = ['Tác giả', 'Nguyên tác', 'Minh họa', 'Dịch giả', 'Thiết kế nhân vật'].map(
    (role) => ({
      value: role,
      label: role,
    }),
  );

  const completion = [
    { label: 'Đang phát hành', value: SERIES_STATUS.ONGOING },
    { label: 'Đã hoàn thành', value: SERIES_STATUS.COMPLETE },
    { label: 'Tạm dừng', value: SERIES_STATUS.ON_HOLD },
  ];

  const handleBookChange = (e) => setBookIds(Array.isArray(e) ? e.map((x) => x.value) : []);

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
    setSeriesAuthors(combinedArray);
  };

  useEffect(() => {
    combineInputs();
  }, [selectedAuthors, selectedRoles]);

  const handleDescriptionChange = (event) => {
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(event.target.value, 'text/html');
    setDescription(parsedHtml.body.innerHTML);
  };

  const createSeries = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = BASE_URL + '/series';

    const data = {
      title,
      alias,
      issuing_house_id,
      issuing_house,
      status,
      description,
      series_authors,
      book_ids,
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
          <CModalTitle>Thêm series mới</CModalTitle>
        </CModalHeader>
        <CModalBody>
          <CForm>
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
            <CRow className="mb-3">
              <CCol>
                <CButton onClick={() => setShowAuthorModal(true)}>Thêm tác giả</CButton>
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Nhà phát hành</CFormLabel>
              <CCol sm={5}>
                <Select
                  placeholder=""
                  options={issuingHouseOptions}
                  onChange={(e) => {
                    setIssuingHouseId(e.value);
                    setIssuingHouse(e.label);
                  }}
                />
              </CCol>
              <CFormLabel className="col-sm-2 col-form-label">Trạng thái</CFormLabel>
              <CCol sm={3}>
                <Select options={completion} onChange={(e) => setStatus(e.value)} />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Mô tả</CFormLabel>
              <CCol>
                <CFormTextarea
                  type="text"
                  style={{ height: '100px' }}
                  onChange={handleDescriptionChange}
                />
              </CCol>
            </CRow>
            <CRow className="mb-3">
              <CFormLabel className="col-sm-2 col-form-label">Danh sách tập</CFormLabel>
              <CCol>
                <AsyncSelect
                  isMulti
                  closeMenuOnSelect={false}
                  placeholder=""
                  loadOptions={getBookOptions}
                  onChange={handleBookChange}
                />
              </CCol>
            </CRow>
          </CForm>
        </CModalBody>
        <CModalFooter>
          <CButton color="secondary" onClick={props.onHide}>
            Đóng
          </CButton>
          <CButton color="primary" onClick={createSeries} disabled={loading}>
            Thêm series
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

CreateSeriesModal.propsType = {
  onHide: PropTypes.func,
  visible: PropTypes.bool,
};

export default CreateSeriesModal;
