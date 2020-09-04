-- user accounts
INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User1', 'test1@test.com', '$2y$10$G6hQmtguq4vxs7fMcYskWelfJHLylYMID02qHByIuzQ2MFbF.6bJ2', 'USER', true);

INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User2', 'test2@test.com', '$2y$10$qfmHR6lfU79PvPZZIx7r1eu/AA7qCQrAVJShRk5qmLQZKA4Bk79X2', 'USER', true);


-- read only accounts
INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User3', 'test3@test.com', '$2y$10$wZ3A61SbRgxrYW2FyUEFdOu7xLfzMIySFhSobRqN0cMNYBnzcHBiy', 'READ-ONLY', true);

INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User4', 'test4@test.com', '$2y$10$OBJxywdy1LsKwXItfeToh.J3/ANBSUQhLIM21IcwZCoKBidypIr46', 'READ-ONLY', true);


-- admin accounts
INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User5', 'test5@test.com', '$2y$10$IuKc54fpMK0D.t8UCySSHuJ6fiJRFL4uMPf.eN/NAXh4WnPRxRt96', 'ADMIN', true);

INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User6', 'test6@test.com', '$2y$10$NchK52qXMPscGLXrsHJJs.PoeUA48ddMq3L0wsxuRdAV2XlboOS26', 'ADMIN', true);


-- super user accounts
INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User7', 'test7@test.com', '$2y$10$FwPjOdaMInAYZ1ei91P88OTNrJuCkd2R6nAqVg6qCN/3dhj7iM4bi', 'SUPER-USER', true);

INSERT INTO users (forename, surname, email, password, permission, verified)
values ('Test', 'User8', 'test8@test.com', '$2y$10$lTHaWspzFE.dl7MQXp9YhuFAQQpBK0KgsFMmsUTgUQuy2QlHb23Va', 'SUPER-USER', true);