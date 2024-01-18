import React, { useEffect, useState } from 'react';
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

const UpdatePublisherModal = (props) => {
  const publisher = props.publisher;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [data, setData] = useState({
    name: publisher.name,
    email: publisher.email,
    phone_number: publisher.phone_number,
    address: publisher.address,
    facebook: publisher.facebook,
    website: publisher.website,
    logo_url: publisher.logo_url,
    description: publisher.description,
  });

  const token = useSelector((state) => state.auth.token);

  useEffect(() => {
    setData({
      name: publisher.name,
      email: publisher.email,
      phone_number: publisher.phone_number,
      address: publisher.address,
      facebook: publisher.facebook,
      website: publisher.website,
      logo_url: publisher.logo_url,
      description: publisher.description,
    });
  }, [props]);

  const handlePhotoUpload = (event) => {
    const file = event.target.files[0];
    setLoading(true);
    if (file) {
      const storageRef = storage.ref();
      const photoRef = storageRef.child(`publishers/${file.name}`);

      photoRef.put(file).then(() => {
        photoRef.getDownloadURL().then((url) => {
          setData({ ...data, logo_url: url });
          setLoading(false);
        });
      });
    }
  };

  const handleDescriptionChange = (event) => {
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(event.target.value, 'text/html');
    setData({ ...data, description: parsedHtml.body.innerHTML });
  };

  const updatePublisher = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = `${BASE_URL}/publisher/${publisher.publisher_id}`;

    if (!data.name) {
      setError('Tên tác giả không được bỏ trống !');
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

  return (
    <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
      <CModalHeader>
        <CModalTitle>Chỉnh sửa nhà phát hành</CModalTitle>
      </CModalHeader>
      <CModalBody>
        {data.logo_url && (
          <CImage
            thumbnail
            className="mb-3"
            align="center"
            src={data.logo_url}
            width={200}
          ></CImage>
        )}
        <CForm>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={publisher.name}
                onChange={(e) => setData({ ...data, name: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Email</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={publisher.email}
                onChange={(e) => setData({ ...data, email: e.target.value })}
              />
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
              <CFormInput
                type="text"
                defaultValue={publisher.phone_number}
                onChange={(e) => setData({ ...data, phone_number: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Địa chỉ</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={publisher.address}
                onChange={(e) => setData({ ...data, address: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Website</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={publisher.website}
                onChange={(e) => setData({ ...data, website: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Facebook</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={publisher.facebook}
                onChange={(e) => setData({ ...data, facebook: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Mô tả</CFormLabel>
            <CCol>
              <CFormTextarea
                type="text"
                defaultValue={publisher.description}
                onChange={handleDescriptionChange}
              />
            </CCol>
          </CRow>
        </CForm>
        {error && (
          <ErrorModal visible={error !== null} onHide={() => setError(null)} message={error} />
        )}
      </CModalBody>
      <CModalFooter>
        <CButton color="secondary" onClick={props.onHide}>
          Đóng
        </CButton>
        <CButton color="primary" onClick={updatePublisher} disabled={loading}>
          Lưu
        </CButton>
      </CModalFooter>
    </CModal>
  );
};

UpdatePublisherModal.propsType = {
  onHide: PropTypes.func,
  visible: PropTypes.bool,
  publisher: PropTypes.object,
};

export default UpdatePublisherModal;
