import React from 'react';

const Home = React.lazy(() => import('./views/pages/home/Home'));
const Profile = React.lazy(() => import('./views/pages/profile/Profile'));
const BookDetail = React.lazy(() => import('./views/pages/book/BookDetail'));
const AuthorList = React.lazy(() => import('./views/pages/author/AuthorList'));
const AuthorDetail = React.lazy(() => import('./views/pages/author/AuthorDetail'));
const PublisherList = React.lazy(() => import('./views/pages/publisher/PublisherList'));
const PublisherDetail = React.lazy(() => import('./views/pages/publisher/PublisherDetail'));
const IssuingHouseList = React.lazy(() => import('./views/pages/issuing_house/IssuingHouseList'));
const IssuingHouseDetail = React.lazy(() =>
  import('./views/pages/issuing_house/IsuingHouseDetail'),
);
const GenreList = React.lazy(() => import('./views/pages/genre/GenreList'));
const GenreDetail = React.lazy(() => import('./views/pages/genre/GenreDetail'));
const BookRequest = React.lazy(() => import('./views/pages/book/BookRequest'));
const User = React.lazy(() => import('./views/pages/user/User'));

const routes = [
  { path: '/', exact: true, name: 'Home', element: Home },
  { path: '/home', exact: true, name: 'Home', element: Home },
  { path: '/book/:book_id', exact: true, name: 'Sách', element: BookDetail },
  { path: '/book-request', exact: true, name: 'Sách', element: BookRequest },
  { path: '/author', exact: true, name: 'Tác giả', element: AuthorList },
  { path: '/author/:author_id', exact: true, name: 'Tác giả', element: AuthorDetail },
  { path: '/publisher', exact: true, name: 'Nhà xuất bản', element: PublisherList },
  { path: '/publisher/:publisher_id', exact: true, name: 'Nhà xuất bản', element: PublisherDetail },
  { path: '/issuing-house', exact: true, name: 'Nhà phát hành', element: IssuingHouseList },
  {
    path: '/issuing-house/:issuing_house_id',
    exact: true,
    name: 'Nhà phát hành',
    element: IssuingHouseDetail,
  },
  { path: '/genre', exact: true, name: 'Thể loại', element: GenreList },
  { path: '/genre/:genre_id', exact: true, name: 'Thể loại', element: GenreDetail },
  { path: '/profile', exact: true, name: 'Profile', element: Profile },
  { path: '/user', exact: true, name: 'User', element: User },
];

export default routes;
