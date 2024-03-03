INSERT INTO User(userID, username, hashedPassword, userType) VALUES
(1, 'doctor1', 'XrZ/n4QJucP3OXNWM8vfkhITk9DhO9D0ZLGypqFa0tw=', 'Doctor'),
(2, 'doctor2', 'DpLWn5uVGxgpnzSy5T27p+mtCwcIg8BAwPzB904Zhe8=', 'Doctor'),
(3, 'doctor3', 'AY+pakRxXJC/k74UgGnLKN1F05jyzHWqFWUxH25V0XQ=', 'Doctor'),
(4, 'doctor4', 'Fkd2iMDgBpnGz6RJejYS1+g8UyBitkslD+2JCBKO1Ug=', 'Doctor'),
(5, 'doctor5', 'pnpByLx51dqRe1BR8fDT9a60tjuiRrNUapYe96PH2TE=', 'Doctor'),
(6, 'doctor6', 'G0yRM9pzpxEyJAQxRAJ2WrDSP9NioWfW8MZbshURPZQ=', 'Doctor'),
(7, 'doctor7', 'ukeIsiaqjcLm3HQki7n2GM+oyVngwmwUe+SPaDmgsIg=', 'Doctor'),
(8, 'doctor8', 'xoWiybqyNczdKrDqkigaUhyKrzeJVJPQgAcOoA/H9dc=', 'Doctor'),
(9, 'doctor9', 'wAbH46sU1ob2NSQTbx7HxeVT2Dm8AchR5Nyd4r2/xYk=', 'Doctor'),
(10,'doctor10','jgobCtpCFyiG/RKX4lq/mfFDlqlACsvV8g2iAonP8C8=', 'Doctor'),
(11, 'nurse1', 'mFYEwrYCQxIsJNThg2PmQ0xTWSO+BfdgIEoa7wI6rps=', 'Nurse'),
(12, 'nurse2', 'ntFRWBnexh/TYdX9q7V/QezOGl/h/iY7mMDWlDubIy4=', 'Nurse'),
(13, 'nurse3', 'X6tfQ9Cy+XUm59qsseQv/ReOIGfFmgA20NmEo77Cifs=', 'Nurse'),
(14, 'nurse4', 'SXQrS43Tp/8qKjJBDjT1WlfQmiMn7bJtcmow5ACWCWY=', 'Nurse'),
(15, 'nurse5', 'Ck7yU+P+8iy507xSgDhvny0c81HaZYLYJUZZ0bJ+oLM=', 'Nurse'),
(16, 'nurse6', 'sN96yisUlIB+RIoYC7BYAArWg6v90MLRwDhtb0NxURk=', 'Nurse'),
(17, 'nurse7', 'L2M3G+o8Ydn9ukRpmEvSLyzCOB0j4DFjTwOHvdl70o8=', 'Nurse'),
(18, 'nurse8', 'zIbYZeR6Ja9Lj9rSZ+KMMKYE9yjnPuItL7VbTnqSzpY=', 'Nurse'),
(19, 'nurse9', 'r/DzE7VWivwZS+m/w3h2Wivk9tyAS4DSJrxx28HYJfA=', 'Nurse'),
(20, 'nurse10', 'xsP6aJ4pG7pvdDbudtxULsRnikEKKtuya77f0eaoqoU=','Nurse'),
(21, 'patient1', '6Hi1IZUIsOHJ/jW5WFhVFKk9QbvPL8WX/vNW16ZbJUo=', 'Patient'),
(22, 'patient2', 'uBWENCX2rGwBjvnuQ0tKQBICqLF13tS1O0ug/YBpiH8=', 'Patient'),
(23, 'patient3', 'SRkGNxnow4iLcgNuB0VyzWZp6HKkfPt/mHoXOi5vJ7U=', 'Patient'),
(24, 'patient4', 'zjh9Xgopct6p5RKaUqw7jVik0YD8nuzllG2SZkOj0sA=', 'Patient'),
(25, 'patient5', 'J8rFUDg2dlzRB1HSerSm4X16gNTJSEMKWoFROXP5tR4=', 'Patient'),
(26, 'patient6', '1uIShmIahYb35Ucgq1o5yTrML0uPunoW7Bwk1poIxhM=', 'Patient'),
(27, 'patient7', 'MSF3G5xZc+DwifwAlBmXUjL283Tx+VoRjoJZyY3V3tQ=', 'Patient'),
(28, 'patient8', 'zO/NkUBcco8Jz9/Rluf3THmns+sZO9rseAxOYSdLDuQ=', 'Patient'),
(29, 'patient9', 'FErIbQXAD1WZScXbnRKmxHcpL2kGBqwui9ssn57gNAY=', 'Patient'),
(30, 'patient10', 'jVOj42cpRr2ALNIDfx1dqKYQgZEMtAVKiCuQWlFVASU=','Patient'),
(31, 'admin1', '68/JmqiBiD/ZoGt4tQsUDfZfJ5RHDkRNV0cDRdrNtTY=','Admin'),
(32, 'admin2', 'gE2i28K51zMbMZmVt4/spWVyqtACQ9sPKxC+ukwiTSk=','Admin'),
(33, 'admin3','GcXVmyT5+E07SmkK8BM3+SbE2D3dpdX4Tte2cj7lTmQ=', 'Admin');




INSERT INTO Department (departmentID, depName) VALUES
(1, 'Internal Medicine'),
(2, 'Dermatology'),
(3, 'Immunology'),
(4, 'Pathology'),
(5, 'Pediatrics'),
(6, 'Radiology'),
(7, 'Psychiatry'),
(8, 'Oncology'),
(9, 'Urology'),
(10, 'Surgery');

INSERT INTO Doctor (doctorID, userID, fieldOfExpertise,departmentID, doctorName) VALUES
(10, 1, 'Gastroenterology',1, 'John'),
(11, 2, 'Dermatopathology', 2, 'David'),
(12, 3, 'Allergy', 3,'Chris'),
(13, 4, 'Hematology',  4,'Jane'),
(14, 5, 'Pediatric cardiology',5, 'Lady Gaga'),
(15, 6, 'Cardiovascular Radiology', 6,'Rihanna'),
(16, 7, 'Mental retardation psychiatry', 7, 'Shakira'),
(17, 8, 'Pain medicine', 8,'Steve Jobs'),
(18, 9, 'Female urology', 9, 'Freddie Mercury'),
(19, 10,'Plastic Surgery' ,10, 'Glenn Hughes');

INSERT INTO Patient (patientID, userID, DOB, patientName, gender) VALUES
(1, 21, '2001-01-01', 'Tupac', 'MALE'),
(2, 22, '2002-02-02', 'Eminem', 'MALE'),
(3, 23, '2003-03-03', 'Dr.Dre', 'MALE'),
(4, 24, '1980-04-04', 'Jay Z', 'MALE'),
(5, 25, '2000-05-05', 'Kanye West','MALE'),
(6, 26, '2003-06-06', 'J.Cole','MALE'),
(7, 27, '1997-07-07', 'Kendrick Lamar','MALE'),
(8, 28, '2008-08-08', 'Notorious BIG','MALE'),
(9, 29, '1999-09-09', 'Nicki Minaj','FEMALE'),
(10, 30, '2001-10-10','Post Malone','MALE');

INSERT INTO Room (roomID, roomType,departmentID) VALUES
(401, 'CT', 1),
(402, 'X-Ray', 2),
(403, 'Ultrasound', 3),
(404, 'MRI', 4),
(405, 'OR', 5);

INSERT INTO Admin (adminID, userID, adminName) VALUES
(501, 31, 'Ali Üçler'),
(502, 32, 'Halenur Arpacık'),
(503, 33, 'Zeynep Şevval Kurt');

INSERT INTO Nurse (nurseID, nurseName, userID) VALUES
(201, 'Chandler Bing', 11),
(202, 'Joey Tribbiani', 12),
(203, 'Ross Geller', 13),
(204, 'Rachel Green', 14),
(205, 'Monica Geller', 15),
(206, 'Phoebe Buffay', 16),
(207, 'Mike Hannigan', 17),
(208, 'Janice Hosenstein', 18),
(209, 'Gunther', 19),
(210, 'Carol Willick', 20);

INSERT INTO Appointment (appointmentID, patientID, doctorID, roomID, nurseID, date, time) VALUES
(401, 1, 10, 401, 201, '2024-01-30', '15:00'),
(402, 2, 11, 402, 202, '2024-01-29', '13:00'),
(403, 3, 12, 403, 203, '2024-01-27', '11:00'),
(404, 4, 13, 404, 204, '2024-02-21', '15:00'),
(405, 5, 14, 405, 205, '2024-01-20', '10:00'),
(406, 6, 15, 401, 206, '2024-02-17', '11:00'),
(407, 7, 16, 402, 207, '2024-02-13', '13:00'),
(408, 8, 17, 403, 208, '2024-01-11', '14:00'),
(409, 9, 18, 404, 209, '2024-01-02', '15:00'),
(410, 10, 19, 405, 210,'2024-02-01', '16:00'),
(411, 2, 19, NULL, NULL,'2024-02-01', '16:00');



INSERT INTO Availability (availabilityID, appointmentID, doctorID, roomID, nurseID, date, availableHours, status) VALUES
(900,401, 10, 401, 201, '2024-01-30', '15:00', FALSE),
(901,402, 11, 402, 202, '2024-01-29', '13:00', FALSE),
(902,403, 12, 403, 203, '2024-01-27', '11:00', FALSE),
(903,404, 13, 404, 204, '2024-02-21', '15:00', FALSE),
(904,405, 14, 405, 205, '2024-01-20', '10:00', FALSE),
(905,406, 15, 401, 206, '2024-02-17', '11:00', FALSE),
(906,407, 16, 402, 207, '2024-02-13', '13:00', FALSE),
(907,408, 17, 403, 208, '2024-01-11', '14:00', FALSE),
(908,409, 18, 404, 209, '2024-01-02', '15:00', FALSE),
(909,410, 19, 405, 210, '2024-02-01', '16:00', FALSE),
(910,NULL, 15, 405, 210, '2024-03-01', '13:00', TRUE),
(911,NULL, 12, 405, 210, '2024-03-02', '16:00', TRUE);






