-- An additional test script.
-- Can be ran after test.sql to add more users to the database
-- and test interaction between users.


-- -------------------------------------------------------------------
-- Prisoner #2:

insert into `Prisoners`(`username`, `pass`, `name`, `info`)
values("simon", "222", "Саймон", "Я второй тестовый пользователь");

insert into `Contacts`(`prisoner`, `type`, `data`)
values(2, 0, "+370 2 2334455");

insert into `Contacts`(`prisoner`, `type`, `data`)
values(2, 5, "vk.com/simondebug");


-- 08.11.2020 - 18.11.2020:
insert into `Arests`(`prisoner`, `start`, `end`)
values(2, 1604786400000, 1605650400000);

-- Окрестина ИВС, 3
insert into `ScheduleCellEntries`(`arest`, `jail`, `cell`)
values(3, 1, 3);

-- Окрестина ЦИП, 1
insert into `ScheduleCellEntries`(`arest`, `jail`, `cell`)
values(3, 2, 1);

-- 08.11.2020 - 11.11.2020: Окрестина ИВС, 3
insert into `Periods`(`arest`, `start`, `end`, `jail`, `cell`)
values(3, 1604786400000, 1605045600000, 1, 3);

-- 11.11.2020 - 18.11.2020: Окрестина ЦИП, 1
-- Intersects with user 1.
insert into `Periods`(`arest`, `start`, `end`, `jail`, `cell`)
values(3, 1605045600000, 1605650400000, 2, 1);


-- -------------------------------------------------------------------
-- Prisoner #3:

insert into `Prisoners`(`username`, `pass`, `name`, `info`)
values("john", "333", "Džonas", "Я никакой не Йонас, а тестовый пользователь Джонас!");

insert into `Contacts`(`prisoner`, `type`, `data`)
values(3, 2, "+370 60 555443");

insert into `Contacts`(`prisoner`, `type`, `data`)
values(3, 6, "fb.com/jonaspage");


-- 15.07.2020 - 30.07.2020:
insert into `Arests`(`prisoner`, `start`, `end`)
values(3, 1594760400000, 1596056400000);

-- Окрестина ЦИП, 2:
insert into `ScheduleCellEntries`(`arest`, `jail`, `cell`)
values(4, 2, 2);

-- Жодино, 4:
insert into `ScheduleCellEntries`(`arest`, `jail`, `cell`)
values(4, 3, 4);

-- 15.07.2020 - 21.07.2020: Жодино, 4
insert into `Periods`(`arest`, `start`, `end`, `jail`, `cell`)
values(4, 1594760400000, 1595278800000, 3, 4);

-- 21.07.2020 - 30.07.2020: Окрестина ЦИП, 2
insert into `Periods`(`arest`, `start`, `end`, `jail`, `cell`)
values(4, 1595278800000, 1596056400000, 2, 2);