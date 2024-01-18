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

const UpdateAuthorModal = (props) => {
  const author = props.author;
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [data, setData] = useState({
    name: author.name,
    alias: author.alias,
    photo: author.photo,
    born: author.born,
    died: author.died,
    website: author.website,
    description: author.description,
  });

  const token = useSelector((state) => state.auth.token);

  useEffect(() => {
    setData({
      name: author.name,
      alias: author.alias,
      photo: author.photo,
      born: author.born,
      died: author.died,
      website: author.website,
      description: author.description,
    });
  }, [props]);

  const handlePhotoUpload = (event) => {
    const file = event.target.files[0];
    setLoading(true);
    if (file) {
      const storageRef = storage.ref();
      const photoRef = storageRef.child(`authors/${file.name}`);

      photoRef.put(file).then(() => {
        photoRef.getDownloadURL().then((url) => {
          setData({ ...data, photo: url });
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

  const updateAuthor = (event) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    const apiEndpoint = `${BASE_URL}/author/${author.author_id}`;

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
        console.error('Registration error:', err);
        setError(err.message);
      });
  };

  return (
    <CModal backdrop="static" visible={props.visible} onClose={props.onHide} size="lg">
      <CModalHeader>
        <CModalTitle>Chỉnh sửa tác giả</CModalTitle>
      </CModalHeader>
      <CModalBody>
        {data.photo && (
          <CImage thumbnail className="mb-3" align="center" src={data.photo} width={200}></CImage>
        )}
        <CForm>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={author.name}
                onChange={(e) => setData({ ...data, name: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Tên khác</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={author.alias}
                onChange={(e) => setData({ ...data, alias: e.target.value })}
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
            <CFormLabel className="col-sm-2 col-form-label">Website</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={author.website}
                onChange={(e) => setData({ ...data, website: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Ngày sinh</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={author.born}
                onChange={(e) => setData({ ...data, born: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Ngày mất</CFormLabel>
            <CCol>
              <CFormInput
                type="text"
                defaultValue={author.died}
                onChange={(e) => setData({ ...data, died: e.target.value })}
              />
            </CCol>
          </CRow>
          <CRow className="mb-3">
            <CFormLabel className="col-sm-2 col-form-label">Mô tả</CFormLabel>
            <CCol>
              <CFormTextarea
                type="text"
                style={{ height: '150px' }}
                defaultValue={author.description.replace('<br></br>', '\n')}
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
        <CButton color="primary" onClick={updateAuthor} disabled={loading}>
          Lưu
        </CButton>
      </CModalFooter>
    </CModal>
  );
};

UpdateAuthorModal.propsType = {
  onHide: PropTypes.func,
  visible: PropTypes.bool,
  author: PropTypes.object,
};

export default UpdateAuthorModal;
