// Import the functions you need from the SDKs you need
import firebase from 'firebase/compat/app';
import 'firebase/compat/storage';
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: 'AIzaSyBwkxtuidJ-ID7OqyEhxl05CVc5Ta5SIEI',
  authDomain: 'biblia-1528f.firebaseapp.com',
  projectId: 'biblia-1528f',
  storageBucket: 'biblia-1528f.appspot.com',
  messagingSenderId: '329024428248',
  appId: '1:329024428248:web:a0ee3faf319eeb0e979fad',
  measurementId: 'G-HW74J0KHHD',
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const storage = firebase.storage();
export default storage;
