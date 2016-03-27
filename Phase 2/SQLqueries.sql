create table log
(
algorithm number,
Run number,
message_no number,
message varchar2(100),
sender number,
receiver number,
write_timestamp date,
message_type number,
sequence_no number,
original_sender number
);

drop sequence message_no;
drop sequence run_no;

CREATE sequence message_no start with 1 increment by 1;
select message_no.nextval from dual;

CREATE sequence run_no start with 1 increment by 1;
select run_no.nextval from dual;


delete from log;

insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 1, 2, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 1, 3, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 1, 4, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 1, 5, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 1, 9, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 1, 13, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 2, 1, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 2, 3, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 2, 4, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 2, 6, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 2, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 2, 14, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 3, 1, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 3, 2, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 3, 4, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 3, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 3, 11, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 3, 15, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 4, 1, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 4, 2, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 4, 3, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 4, 8, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 4, 12, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 4, 16, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 5, 6, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 5, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 5, 8, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 5, 1, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 5, 9, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 5, 13, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 6, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 6, 14, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 6, 2, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 6, 5, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 6, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 6, 8, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 7, 11, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 7, 15, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 7, 3, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 7, 5, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 7, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 7, 8, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 8, 4, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 8, 12, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 8, 16, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 8, 5, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 8, 6, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 8, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 9, 1, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 9, 5, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 9, 13, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 9, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 9, 11, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 9, 12, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 10, 14, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 10, 6, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 10, 2, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 10, 9, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 10, 11, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 10, 12, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 11, 15, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 11, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 11, 3, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 11, 9, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 11, 12, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 11, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 12, 4, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 12, 8, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 12, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 12, 9, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 12, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 12, 11, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 13, 14, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 13, 15, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 13, 16, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 13, 1, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 13, 5, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 13, 9, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 14, 2, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 14, 6, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 14, 10, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 14, 15, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 14, 13, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 14, 16, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 15, 11, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 15, 7, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 15, 3, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 15, 13, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 15, 14, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 15, 16, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 16, 12, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 16, 4, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 16, 13, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 16, 8, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 16, 14, sysdate, 0, 1, null);
insert into log values(1, run_no.currval, message_no.nextval, 'Requesting Critical Section', 16, 15, sysdate, 0, 1, null);

select 1 
from log
where message = 'Initiate'
and run_no = <run_no>
and algorithm = <algorithm>;

insert into log 
values
(1, run_no.currval, null, 'Initiate', null, null, sysdate, null, null, null);

select message_type
from log
where receiver = <processid>
and algorithm = <algorithm>
and run_no = <run_no>;


SELECT COUNT(*) FROM log WHERE MESSAGE = 'Initiate' AND run = 1 AND algorithm = 2;
INSERT INTO log VALUES (2, 1, NULL, 'Initiate', NULL, NULL, SYSDATE, NULL, NULL, NULL);