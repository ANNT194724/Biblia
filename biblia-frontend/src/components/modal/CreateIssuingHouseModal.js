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

const CreateIssuingHouseModal = (props) => {
  const [loading, setLoading] = useState(false);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [logo_url, setLogoUrl] = useState('');
  const [phone_number, setPhoneNumber] = useState('');
  const [address, setAddress] = useState('');
  const [facebook, setFacebook] = useState('');
  const [website, setWebsite] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const token = useSelector((state) => state.auth.token);

  const handlePhotoUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const storageRef = storage.ref();
      const coverRef = storageRef.child(`isuing-houses/${file.name}`);

      coverRef.put(file).then(() => {
        coverRef.getDownloadURL().then((url) => {
          setLogoUrl(url);
        });
      });
    }
  };

  const handleDescriptionChange = (event) => {
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(event.target.value, 'text/html');
    setDescription(parsedHtml.body.innerHTML);
  };

  const createIssuingHouse = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = `${BASE_URL}/issuing-house`;

    const data = {
      name,
      email,
      phone_number,
      address,
      facebook,
      website,
      logo_url,
      description,
    };

    if (name === '') {
      setError('Tên tác giả không được bỏ trống !');
      setLoading(false);
      // resetState();
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
        window.location.reload();
      })
      .catch((err) => {
        setLoading(false);
        setError(err.message);
      });
  };

  return (
    <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
      <CModalHeader>
        <CModalTitle>Thêm nhà phát hành</CModalTitle>
      </CModalHeader>
      <CModalBody>
        {logo_url && (
          <CImage thumbnail className="mb-3" align="center" src={logo_url} width={200}></CImage>
        )}
        <CForm>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setName(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Email</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setEmail(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Ảnh</CFormLabel>
            <CCol>
              <CFormInput type="file" accept="image/*" onChange={handlePhotoUpload} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Số điện thoại</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setPhoneNumber(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Địa chỉ</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setAddress(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Website</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setWebsite(e.target.value)} />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Facebook</CFormLabel>
            <CCol>
              <CFormInput type="text" onChange={(e) => setFacebook(e.target.value)} />
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
        <CButton color="primary" onClick={createIssuingHouse} disabled={loading}>
          Thêm nhà phát hành
        </CButton>
      </CModalFooter>
    </CModal>
  );
};

CreateIssuingHouseModal.propsType = {
  onHide: PropTypes.func,
  visible: PropTypes.bool,
};

export default CreateIssuingHouseModal;
