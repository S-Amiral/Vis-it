INSERT INTO `GROUPS` (`GROUPNAME`) VALUES ('admin');
INSERT INTO `USERS` (`USERNAME`, `EMAIL`, `NAME`, `PASSWORD`, `SURNAME`) VALUES ('angelkiro', 'angelkiro@visit.ch', 'Neto da Silva', '4b358ed84b7940619235a22328c584c7bc4508d4524e75231d6f450521d16a17', 'André');
INSERT INTO `USERS` (`USERNAME`, `EMAIL`, `NAME`, `PASSWORD`, `SURNAME`) VALUES ('toto', 'toto@visit.ch', 'toto', '4b358ed84b7940619235a22328c584c7bc4508d4524e75231d6f450521d16a17', 'Toto');
INSERT INTO `USER_GROUP` (`username`, `groupName`) VALUES ('angelkiro', 'admin');
INSERT INTO `PLACES` (`ID`, `DESCRIPTION`, `LATITUDE`, `LONGITUDE`, `PUBLISHED_DATE`, `TITLE`, `PUBLISHED_BY_USERNAME`) VALUES (NULL, 'Ma maison', '52.3', '246.12', '2018-04-18 00:00:00', 'Malleray', 'angelkiro');
INSERT INTO `PLACES` (`ID`, `DESCRIPTION`, `LATITUDE`, `LONGITUDE`, `PUBLISHED_DATE`, `TITLE`, `PUBLISHED_BY_USERNAME`) VALUES (NULL, 'Ma maison', '52.3', '246.12', '2018-04-18 00:00:00', 'Totoland', 'toto');
