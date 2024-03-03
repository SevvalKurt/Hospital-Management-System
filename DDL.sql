DROP DATABASE hms;
CREATE DATABASE hms;
USE hms;


CREATE TABLE User (
  userID INT PRIMARY KEY auto_increment,
  username VARCHAR(50) UNIQUE,
  hashedPassword VARCHAR(80) CHECK (LENGTH(hashedPassword) > 3),
  userType VARCHAR(20) CHECK (userType IN ('Patient', 'Doctor', 'Nurse', 'Admin'))
);

CREATE TABLE Patient (
  patientID INT PRIMARY KEY auto_increment,
  userID INT UNIQUE,
  DOB DATE NOT NULL,
  gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
  patientName VARCHAR(100) NOT NULL,
  FOREIGN KEY (userID) REFERENCES User(userID) ON DELETE CASCADE
);

CREATE TABLE Department (
  departmentID INT PRIMARY KEY auto_increment,
  depName VARCHAR(100)
);

CREATE TABLE Doctor (
  doctorID INT PRIMARY KEY auto_increment,
  departmentID INT,
  userID INT UNIQUE,
  doctorName VARCHAR(100),
  fieldOfExpertise VARCHAR(80),
  FOREIGN KEY (departmentID) REFERENCES Department(departmentID) ON DELETE CASCADE,
  FOREIGN KEY (userID) REFERENCES User(userID) ON DELETE CASCADE
);


CREATE TABLE Room (
  roomID INT PRIMARY KEY auto_increment,
  departmentID INT,
  roomType VARCHAR(60),
  FOREIGN KEY (departmentID) REFERENCES Department(departmentID) ON DELETE CASCADE
);


CREATE TABLE Nurse (
  nurseID INT PRIMARY KEY auto_increment,
  nurseName VARCHAR(100),
  userID INT UNIQUE,
  FOREIGN KEY (userID) REFERENCES User(userID) ON DELETE CASCADE
);


CREATE TABLE Admin (
  adminID INT PRIMARY KEY auto_increment,
  adminName VARCHAR(100),
  userID INT UNIQUE,
  FOREIGN KEY (userID) REFERENCES User(userID) ON DELETE CASCADE
);

CREATE TABLE Appointment (
  appointmentID INT PRIMARY KEY auto_increment,
  time TIME,
  date DATE NOT NULL,
  patientID INT,
  doctorID INT,
  roomID INT,
  nurseID INT,
  FOREIGN KEY (patientID) REFERENCES Patient(patientID) ON DELETE CASCADE,
  FOREIGN KEY (doctorID) REFERENCES Doctor(doctorID) ON DELETE CASCADE,
  FOREIGN KEY (roomID) REFERENCES Room(roomID) ON DELETE CASCADE,
  FOREIGN KEY (nurseID) REFERENCES Nurse(nurseID) ON DELETE CASCADE
);
CREATE TABLE Availability (
  availabilityID INT PRIMARY KEY auto_increment,
  appointmentID INT,
  doctorID INT,
  roomID INT,
  date DATE,
  availableHours TIME,
  status BOOL,
  nurseID INT,
  FOREIGN KEY (appointmentID) REFERENCES Appointment(appointmentID) ON DELETE CASCADE,
  FOREIGN KEY (doctorID) REFERENCES Doctor(doctorID) ON DELETE CASCADE,
  FOREIGN KEY (roomID) REFERENCES Room(roomID) ON DELETE CASCADE,
  FOREIGN KEY (nurseID) REFERENCES Nurse(nurseID) ON DELETE CASCADE
);

CREATE VIEW NurseRoomAvailability AS
SELECT
  R.roomID,
  R.roomType,
  D.depName
FROM
  Room R
  JOIN Department D ON R.departmentID = D.departmentID
WITH CHECK OPTION;

CREATE VIEW NurseAssignedRooms AS
SELECT
  A.appointmentID,
  A.date,
  A.time,
  R.roomType
FROM
  Appointment A
  JOIN Room R ON A.roomID = R.roomID
WITH CHECK OPTION;

CREATE VIEW AppointmentsView AS
SELECT
  A.appointmentID,
  A.time,
  A.date,
  P.patientName,
  D.doctorName,
  R.roomType,
  N.nurseName
FROM
  Appointment A
  JOIN Patient P ON A.patientID = P.patientID
  JOIN Doctor D ON A.doctorID = D.doctorID
  JOIN Room R ON A.roomID = R.roomID
  JOIN Nurse N ON A.nurseID = N.nurseID
WITH CHECK OPTION;


