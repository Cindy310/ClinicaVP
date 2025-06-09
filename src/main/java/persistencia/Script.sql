-- Crear la base de datos
CREATE DATABASE ClinicaVeterinariaDB;
GO

-- Usar la base de datos
USE ClinicaVeterinariaDB;
GO
CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL,
    passwordHash VARCHAR(64) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    status TINYINT NOT NULL
);
-- Tabla de dueños de mascotas
CREATE TABLE Duenos (
    id_dueno INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100),
    telefono VARCHAR(20),
    correo VARCHAR(100)
);

-- Tabla de mascotas
CREATE TABLE Mascotas (
    id_mascota INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100),
    especie VARCHAR(50),
    edad INT,
    id_dueno INT,
    FOREIGN KEY (id_dueno) REFERENCES Duenos(id_dueno)
);

-- Tabla de veterinarios
CREATE TABLE Veterinarios (
    id_veterinario INT PRIMARY KEY IDENTITY(1,1),
    nombre VARCHAR(100),
    especialidad VARCHAR(100),
    correo VARCHAR(100)
);

-- Tabla de consultas o citas veterinarias
CREATE TABLE Consultas (
    id_consulta INT PRIMARY KEY IDENTITY(1,1),
    fecha DATE,
    motivo VARCHAR(150),
    costo FLOAT,
    id_mascota INT,
    id_veterinario INT,
    FOREIGN KEY (id_mascota) REFERENCES Mascotas(id_mascota),
    FOREIGN KEY (id_veterinario) REFERENCES Veterinarios(id_veterinario)
);
