DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS groups;

DROP SEQUENCE IF EXISTS global_seq;
DROP TYPE IF EXISTS user_flag;
DROP TYPE IF EXISTS GROUP_TYPE;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE GROUP_TYPE AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE SEQUENCE global_seq START 100000;

CREATE TABLE users
(
  id        INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  flag      user_flag NOT NULL
);
CREATE UNIQUE INDEX email_idx ON users (email);

CREATE TABLE cities
(
  id   TEXT PRIMARY KEY NOT NULL,
  name TEXT NOT NULL
);
ALTER TABLE users
  ADD column city_id TEXT REFERENCES cities (id) on UPDATE CASCADE;

CREATE TABLE projects
(
  id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
  name TEXT UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE groups
(
  id INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
  name TEXT UNIQUE NOT NULL,
  type GROUP_TYPE NOT NULL
);