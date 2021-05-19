CREATE TABLE task (
  id          INTEGER PRIMARY KEY,
  user_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  is_notificable   BIT NOT NULL);