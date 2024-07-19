DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS film_userlikes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS film;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS rating;

CREATE TABLE rating(id serial NOT NULL,
                    name varchar NOT NULL,
                    CONSTRAINT rating_pkey PRIMARY KEY (id));

CREATE TABLE genre(id serial NOT NULL,
                   name varchar NOT NULL,
                   CONSTRAINT genre_pkey PRIMARY KEY (id));



CREATE TABLE film(id serial NOT NULL,
                  name varchar NOT NULL,
                  description varchar NOT NULL,
                  release_date date NOT NULL,
                  duration int4 NOT NULL,
                  rating_id int4 NOT NULL,
                  CONSTRAINT film_pkey PRIMARY KEY (id),
                  CONSTRAINT film_rating_id_fkey FOREIGN KEY (rating_id) REFERENCES rating(id));

CREATE TABLE film_genre(id serial NOT NULL,
                        film_id int4 NOT NULL,
                        genre_id int4 NOT NULL,
                        CONSTRAINT film_genre_pkey PRIMARY KEY (id),
                        CONSTRAINT film_genre_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(id),
                        CONSTRAINT film_genre_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES genre(id));

CREATE TABLE users(id bigserial NOT NULL,
                   email varchar NOT NULL,
                   login varchar NOT NULL,
                   name varchar NOT NULL,
                   birthday date NOT NULL,
                   CONSTRAINT users_pkey PRIMARY KEY (id));

CREATE TABLE film_userlikes(id bigserial NOT NULL,
                            film_id int4 NOT NULL,
                            user_id int8 NOT NULL,
                            CONSTRAINT film_userlikes_pkey PRIMARY KEY (id),
                            CONSTRAINT film_userlikes_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(id),
                            CONSTRAINT film_userlikes_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id));

CREATE TABLE user_friends(id bigserial NOT NULL,
                          user_id int8 NOT NULL,
                          user_friend_id int8 NOT NULL,
                          CONSTRAINT user_friends_pkey PRIMARY KEY (id),
                          CONSTRAINT user_friends_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT user_friends_user_friend_id_fkey FOREIGN KEY (user_friend_id) REFERENCES users(id));
