import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';
import storage from 'src/firebase';
import axios from 'axios';
import { BASE_URL } from 'src/Constants';
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

const CreateAuthorModal = (props) => {
  const [loading, setLoading] = useState(false);
  const [name, setName] = useState('');
  const [alias, setAlias] = useState('');
  const [photo, setPhoto] = useState('');
  const [born, setBorn] = useState('');
  const [died, setDied] = useState('');
  const [website, setWebsite] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const token = useSelector((state) => state.auth.token);

  const resetState = () => {
    setName('');
    setAlias('');
    setPhoto('');
    setBorn('');
    setDied('');
    setWebsite('');
    setDescription('');
    setError('');
  };

  const handlePhotoUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const storageRef = storage.ref();
      const coverRef = storageRef.child(`authors/${file.name}`);

      coverRef.put(file).then(() => {
        coverRef.getDownloadURL().then((url) => {
          setPhoto(url);
        });
      });
    }
  };

  const handleDescriptionChange = (event) => {
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(event.target.value, 'text/html');
    setDescription(parsedHtml.body.innerHTML);
  };

  const createAuthor = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = `${BASE_URL}/author`;

    const data = {
      name,
      alias,
      photo,
      born,
      died,
      website,
      description,
    };

    if (name === '') {
      setError('Tên tác giả không được bỏ trống !');
      setLoading(false);
      resetState();
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
        props.onHide();
      })
      .catch((err) => {
        setLoading(false);
        console.error('Registration error:', err);
        setError(err.message);
      });
  };

  return (
    <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
      <CModalHeader>
        <CModalTitle>Thêm tác giả mới</CModalTitle>
      </CModalHeader>
      <CModalBody>
        {photo && (
          <CImage thumbnail className="mb-3" align="center" src={photo} width={200}></CImage>
        )}
        <CForm>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setName(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên khác</CFormLabel>
            <CCol>
              <CFormInput type="text" name="alias" onChange={(e) => setAlias(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Ảnh</CFormLabel>
            <CCol>
              <CFormInput type="file" accept="image/*" onChange={handlePhotoUpload} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Website</CFormLabel>
            <CCol>
              <CFormInput type="text" name="website" onChange={(e) => setWebsite(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Ngày sinh</CFormLabel>
            <CCol>
              <CFormInput type="text" name="born" onChange={(e) => setBorn(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Ngày mất</CFormLabel>
            <CCol>
              <CFormInput type="text" name="died" onChange={(e) => setDied(e.target.value)} />
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
      {error && (
        <ErrorModal visible={error !== null} onHide={() => setError(null)} message={error} />
      )}
      <CModalFooter>
        <CButton color="secondary" onClick={props.onHide}>
          Đóng
        </CButton>
        <CButton color="primary" disabled={loading} onClick={createAuthor}>
          Thêm tác giả
        </CButton>
      </CModalFooter>
    </CModal>
  );
};

CreateAuthorModal.propsType = {
  onHide: PropTypes.func,
  visible: PropTypes.bool,
};

export default CreateAuthorModal;
