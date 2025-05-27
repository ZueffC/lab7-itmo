
CREATE TYPE Furnish AS ENUM ('DESIGNER', 'FINE', 'BAD');
CREATE TYPE View AS ENUM ('STREET', 'PARK', 'NORMAL', 'GOOD');
CREATE TYPE Transport AS ENUM ('FEW', 'NONE', 'LITTLE', 'NORMAL');

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
    name VARCHAR(50) UNIQUE NOT NULL,                   
    password VARCHAR(255) NOT NULL                 
);

CREATE TABLE IF NOT EXISTS Coordinates (
    id SERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS Houses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    year INT NOT NULL CHECK (year > 0 AND year <= 959),
    number_of_floors BIGINT NOT NULL CHECK (number_of_floors > 0 AND number_of_floors <= 77)
);

CREATE TABLE IF NOT EXISTS Flats (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    coordinates_id INT NOT NULL REFERENCES Coordinates(id) ON DELETE CASCADE,
    
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    area DECIMAL(10,2) NOT NULL CHECK (area > 0 AND area <= 626),
    number_of_rooms INT NOT NULL CHECK (number_of_rooms > 0),
    furnish Furnish NOT NULL,
    view View, 
    transport Transport NOT NULL,
    
    house_id INT REFERENCES Houses(id) ON DELETE SET NULL,
    owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE
);