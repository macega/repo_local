/* %Z% generic/sproc/%M% %I% %G% */

/* installpubs2 1.0 4/5/91 */
/* has primary and foreign keys, plus text and image tables*/

/* Deletions made to build database on Sql Anywhere 5.5 */

/* Set date-time formats to match Sql Server */


SET temporary OPTION date_format = 'mm/dd/yyyy'
go

SET temporary OPTION date_order='MDY'
go

SET temporary OPTION quoted_identifier='off'
go

if not exists (select * from sysusertype where type_name = 'id')
	create datatype id varchar(11) not null
go

if not exists (select * from sysusertype where type_name = 'tid') 
	create datatype tid varchar(6) not null
go

if exists (select * from systable where table_name ='authors')
	drop table authors
go

if exists (select * from systable where table_name ='publishers') 
  	drop table publishers
go

if exists (select * from systable where table_name ='roysched')
	drop table roysched
go

if exists (select * from systable where table_name ='sales')
	drop table sales
go

if exists (select * from systable where table_name ='salesdetail') 
	drop table salesdetail
go

if exists (select * from systable where table_name ='titleauthor')
	drop table titleauthor
go

if exists (select * from systable where table_name ='titles')
	drop table titles

if exists(select * from systable where table_name ='stores') 
	drop table stores
go

if exists (select * from systable where table_name ='discounts')
	drop table discounts

if exists (select * from systable where table_name ='au_pix')
	drop table au_pix
go

if exists (select * from systable where table_name ='blurbs')
	drop table blurbs
go

create table authors
	(au_id id not null,
	au_lname varchar(40) not null,
	au_fname varchar(20) not null,
	phone char(12) not null default 'UNKNOWN',
	address varchar(40) null,
	city varchar(20) null,
	state char(2) null,
	country varchar(12) null,
	postalcode char(10) null,
primary key(au_id))
go
grant select on authors to PUBLIC
go

create table publishers
	(pub_id char(4) not null,
	pub_name varchar(40) null,
	city varchar(20) null,
	state char(2) null,
primary key (pub_id))
go
grant select on publishers to PUBLIC
go

create table roysched
	(title_id tid not null,
	lorange int null,
	hirange int null,
	royalty int null)
go
grant select on roysched to PUBLIC
go

create table titles
	(title_id tid not null,
	title varchar(80) not null,
	type char(12) not null default 'UNDECIDED',
	pub_id char(4) null,
	price money null,
	advance money null,
	total_sales int null,
	notes varchar(200) null,
	pubdate datetime not null default getdate(),
	contract bit not null)
go
grant select on titles to PUBLIC
go

create table stores
	(stor_id char(4) not null,
	stor_name varchar(40) null,
	stor_address varchar(40) null,
	city varchar(20) null,
	state char(2) null,
	country varchar(12) null,
	postalcode char(10) null,
	payterms varchar(12) null,
primary key (stor_id))
go
grant select on stores to PUBLIC
go


create table sales
	(stor_id char(4) not null,
	ord_num varchar(20) not null,
	saledate datetime not null,
primary key (stor_id,ord_num))
go
grant select on sales to PUBLIC
go

create table salesdetail
	(stor_id char(4) not null,
	ord_num varchar(20) not null,
	title_id tid not null,
	qty smallint not null,
	discount float not null)
go
grant select on salesdetail to PUBLIC
go

create table titleauthor
	(au_id id not null,
	title_id tid not null,
	au_ord tinyint null,
	royaltyper int null)
go
grant select on titleauthor to PUBLIC
go

create table discounts
	(discounttype   varchar(40) not null,
	stor_id         char(4) null,   
	lowqty          smallint null,
	highqty         smallint null,
	discount        float null)
go
grant select on discounts to PUBLIC
go

create table au_pix
	(au_id		id not null,
	pic		image null,
	format_type	char(11) null,
	bytesize	int null,
	pixwidth_hor	char(14) null,
	pixwidth_vert	char(14) null,
foreign key (au_id) references authors(au_id) 
	on update cascade on delete cascade,
primary key (au_id))
go
grant select on au_pix to PUBLIC
go

create table blurbs
	(au_id	id not null,
	 copy	text null,
foreign key (au_id) references authors(au_id) 
	on update cascade on delete cascade,
primary key (au_id))
go
grant select on blurbs to PUBLIC
go


if not exists (select * from dbo.sysindexes where name='pubind')
	create unique  index pubind on publishers (pub_id)
go
if not exists(select * from dbo.sysindexes where name='auidind')
	create unique  index auidind on authors (au_id)
go
if not exists(select * from dbo.sysindexes where name='aunmind')
	create index aunmind on authors (au_lname, au_fname)
go

if not exists(select * from dbo.sysindexes where name='taind')
	create unique  index taind on titleauthor (au_id, title_id)
go
if not exists(select * from dbo.sysindexes where name='auidtind')
	create index auidtind on titleauthor (au_id)
go
if not exists(select * from dbo.sysindexes where name='titleidind')
	create  index titleidind on titleauthor (title_id)
go
if not exists(select * from dbo.sysindexes where name='salesind')
	create unique  index salesind on sales (stor_id, ord_num)
go
if not exists(select * from dbo.sysindexes where name='salesdetailind')
	create  index salesdetailind on salesdetail (stor_id, ord_num)
go
if not exists(select * from dbo.sysindexes where name='royschedind')
	create index royschedind on roysched (title_id)
go

insert titles
values ('PC8888', 'Secrets of Silicon Valley',
'popular_comp', '1389', $20.00, $8000.00, 4095,
"Muckraking reporting by two courageous women on the world's largest computer ha
rdware and software manufacturers.",
'06/12/87', 1)
go
insert titles
values ('BU1032', "The Busy Executive's Database Guide",
'business', '1389', $19.99, $5000.00, 4095,
"An overview of available database systems with emphasis on common business appl
ications.  Illustrated.",
'06/12/86', 1)
go
insert titles
values ('PS7777', 'Emotional Security: A New Algorithm',
'psychology', '0736', $7.99, $4000.00, 3336,
"Protecting yourself and your loved ones from undue emotional stress in the mode
rn world.  Use of computer and nutritional aids emphasized.",
'06/12/88', 1)
go
insert titles
values ('PS3333', 'Prolonged Data Deprivation: Four Case Studies',
'psychology', '0736', $19.99, $2000.00, 4072,
'What happens when the data runs dry?  Searching evaluations of information-shor
tage effects on heavy users.',
'06/12/88', 1)
go
insert titles
values ('BU1111', 'Cooking with Computers: Surreptitious Balance Sheets',
'business', '1389', $11.95, $5000.00, 3876,
'Helpful hints on how to use your electronic resources to the best advantage.', 
'06/09/88', 1)
go
insert titles
values ('MC2222', 'Silicon Valley Gastronomic Treats',
'mod_cook', '0877', $19.99, $0.00, 2032,
'Favorite recipes for quick, easy, and elegant meals, tried and tested by people
 who never have time to eat, let alone cook.',
'06/09/89', 1)
go
insert titles
values ('TC7777', 'Sushi, Anyone?',
'trad_cook', '0877', $14.99, $8000.00, 4095,
'Detailed instructions on improving your position in life by learning how to mak
e authentic Japanese sushi in your spare time.  5-10% increase in number of frie
nds per recipe reported from beta test.',
'06/12/87', 1)
go
insert titles
values ('TC4203', 'Fifty Years in Buckingham Palace Kitchens',
'trad_cook', '0877', $11.95, $4000.00, 15096,
"More anecdotes from the Queen's favorite cook describing life among English roy
alty.  Recipes, techniques, tender vignettes.",
'06/12/85', 1)
go
insert titles
values ('PC1035', 'But Is It User Friendly?',
'popular_comp', '1389', $22.95, $7000.00, 8780,
"A survey of software for the naive user, focusing on the 'friendliness' of each
.",
'06/30/86', 1)
go
insert titles
values('BU2075', 'You Can Combat Computer Stress!',
'business', '0736', $2.99, $10125.00, 18722,
'The latest medical and psychological techniques for living with the electronic 
office.  Easy-to-understand explanations.',
'06/30/85', 1)
go
insert titles
values('PS2091', 'Is Anger the Enemy?',
'psychology', '0736', $10.95, $2275.00, 2045,
'Carefully researched study of the effects of strong emotions on the body.  Meta
bolic charts included.',
'06/15/89', 1)
go
insert titles
values('PS2106', 'Life Without Fear',
'psychology', '0736', $7.00, $6000.00, 111,
'New exercise, meditation, and nutritional techniques that can reduce the shock 
of daily interactions. Popular audience.  Sample menus included, exercise video 
available separately.',
'10/05/90', 1)
go
insert titles
values('MC3021', 'The Gourmet Microwave',
'mod_cook', '0877', $2.99, $15000.00, 22246,
'Traditional French gourmet recipes adapted for modern microwave cooking.',
'06/18/85', 1)
go
insert titles
values('TC3218',
'Onions, Leeks, and Garlic: Cooking Secrets of the Mediterranean',
'trad_cook', '0877', $20.95, $7000.00, 375,
'Profusely illustrated in color, this makes a wonderful gift book for a cuisine-oriented friend.',
'10/21/90', 1)
go
insert titles (title_id, title, pub_id, contract)
values('MC3026', 'The Psychology of Computer Cooking', '0877', 0)
go
insert titles
values ('BU7832', 'Straight Talk About Computers',
'business', '1389', $19.99, $5000.00, 4095,
'Annotated analysis of what computers can do for you: a no-hype guide for the cr
itical user.',
'06/22/87', 1)
go
insert titles
values('PS1372',
'Computer Phobic and Non-Phobic Individuals: Behavior Variations',
'psychology', '0877', $21.59, $7000.00, 375,
'A must for the specialist, this book examines the difference between those who 
hate and fear computers and those who think they are swell.',
'10/21/90', 1)
go
insert titles (title_id, title, type, pub_id, notes, contract)
values('PC9999', 'Net Etiquette', 'popular_comp', '1389',
'A must-read for computer conferencing debutantes!', 0)
go
insert authors
values('409-56-7008', 'Bennet', 'Abraham',
'415 658-9932', '6223 Bateman St.', 'Berkeley', 'CA', 'USA', '94705')
go
insert authors
values ('213-46-8915', 'Green', 'Marjorie',
'415 986-7020', '309 63rd St. #411', 'Oakland', 'CA', 'USA', '94618')
go
insert authors
values('238-95-7766', 'Carson', 'Cheryl',
'415 548-7723', '589 Darwin Ln.', 'Berkeley', 'CA', 'USA', '94705')
go
insert authors
values('998-72-3567', 'Ringer', 'Albert',
'801 826-0752', '67 Seventh Av.', 'Salt Lake City', 'UT', 'USA', '84152')
go
insert authors
values('899-46-2035', 'Ringer', 'Anne',
'801 826-0752', '67 Seventh Av.', 'Salt Lake City', 'UT', 'USA', '84152')
go
insert authors
values('722-51-5454', 'DeFrance', 'Michel',
'219 547-9982', '3 Balding Pl.', 'Gary', 'IN', 'USA', '46403')
go
insert authors
values('807-91-6654', 'Panteley', 'Sylvia',
'301 946-8853', '1956 Arlington Pl.', 'Rockville', 'MD', 'USA', '20853')
go
insert authors
values('893-72-1158', 'McBadden', 'Heather',
'707 448-4982', '301 Putnam', 'Vacaville', 'CA', 'USA', '95688')
go
insert authors
values('724-08-9931', 'Stringer', 'Dirk',
'415 843-2991', '5420 Telegraph Av.', 'Oakland', 'CA', 'USA', '94609')
go
insert authors
values('274-80-9391', 'Straight', 'Dick',
'415 834-2919', '5420 College Av.', 'Oakland', 'CA', 'USA', '94609')
go
insert authors
values('756-30-7391', 'Karsen', 'Livia',
'415 534-9219', '5720 McAuley St.', 'Oakland', 'CA', 'USA', '94609')
go
insert authors
values('724-80-9391', 'MacFeather', 'Stearns',
'415 354-7128', '44 Upland Hts.', 'Oakland', 'CA', 'USA', '94612')
go
insert authors
values('427-17-2319', 'Dull', 'Ann',
'415 836-7128', '3410 Blonde St.', 'Palo Alto', 'CA', 'USA', '94301')
go
insert authors
values('672-71-3249', 'Yokomoto', 'Akiko',
'415 935-4228', '3 Silver Ct.', 'Walnut Creek', 'CA', 'USA', '94595')
go
insert authors
values('267-41-2394', 'O`Leary', 'Michael',
'408 286-2428', '22 Cleveland Av. #14', 'San Jose', 'CA', 'USA', '95128')
go
insert authors
values('472-27-2349', 'Gringlesby', 'Burt',
'707 938-6445', 'PO Box 792', 'Covelo', 'CA', 'USA', '95428')
go
insert authors
values('527-72-3246', 'Greene', 'Morningstar',
'615 297-2723', '22 Graybar House Rd.', 'Nashville', 'TN', 'USA', '37215')
go

insert authors
values('172-32-1176', 'White', 'Johnson',
'408 496-7223', '10932 Bigge Rd.', 'Menlo Park', 'CA', 'USA', '94025')
go
insert authors
values('712-45-1867', 'del Castillo', 'Innes',
'615 996-8275', '2286 Cram Pl. #86', 'Ann Arbor', 'MI', 'USA', '48105')
go
insert authors
values('846-92-7186', 'Hunter', 'Sheryl',
'415 836-7128', '3410 Blonde St.', 'Palo Alto', 'CA', 'USA', '94301')
go
insert authors
values('486-29-1786', 'Locksley', 'Chastity',
'415 585-4620', '18 Broadway Av.', 'San Francisco', 'CA', 'USA', '94130')
go
insert authors
values('648-92-1872', 'Blotchet-Halls', 'Reginald',
'503 745-6402', '55 Hillsdale Bl.', 'Corvallis', 'OR', 'USA', '97330')
go
insert authors
values('341-22-1782', 'Smith', 'Meander',
'913 843-0462', '10 Mississippi Dr.', 'Lawrence', 'KS', 'USA', '66044')
go
insert publishers
values('0736', 'New Age Books', 'Boston', 'MA')
go
insert publishers
values('0877', 'Binnet & Hardley', 'Washington', 'DC')
go
insert publishers
values('1389', 'Algodata Infosystems', 'Berkeley', 'CA')
go
insert roysched
values('BU1032', 0, 5000, 10)
go
insert roysched
values('BU1032', 5001, 50000, 12)
go
insert roysched
values('PC1035', 0, 2000, 10)
go
insert roysched
values('PC1035', 2001, 3000, 12)
go
insert roysched
values('PC1035', 3001, 4000, 14)
go
insert roysched
values('PC1035', 4001, 10000, 16)
go
insert roysched
values('PC1035', 10001, 50000, 18)
go
insert roysched
values('BU2075', 0, 1000, 10)
go
insert roysched
values('BU2075', 1001, 3000, 12)
go
insert roysched
values('BU2075', 3001, 5000, 14)
go
insert roysched
values('BU2075', 5001, 7000, 16)
go
insert roysched
values('BU2075', 7001, 10000, 18)
go
insert roysched
values('BU2075', 10001, 12000, 20)
go
insert roysched
values('BU2075', 12001, 14000, 22)
go
insert roysched
values('BU2075', 14001, 50000, 24)
go
insert roysched
values('PS2091', 0, 1000, 10)
go
insert roysched
values('PS2091', 1001, 5000, 12)
go
insert roysched
values('PS2091', 5001, 10000, 14)
go
insert roysched
values('PS2091', 10001, 50000, 16)
go
insert roysched
values('PS2106', 0, 2000, 10)
go
insert roysched
values('PS2106', 2001, 5000, 12)
go
insert roysched
values('PS2106', 5001, 10000, 14)
go
insert roysched
values('PS2106', 10001, 50000, 16)
go
insert roysched
values('MC3021', 0, 1000, 10)
go
insert roysched
values('MC3021', 1001, 2000, 12)
go
insert roysched
values('MC3021', 2001, 4000, 14)
go
insert roysched
values('MC3021', 4001, 6000, 16)
go
insert roysched
values('MC3021', 6001, 8000, 18)
go
insert roysched
values('MC3021', 8001, 10000, 20)
go
insert roysched
values('MC3021', 10001, 12000, 22)
go
insert roysched
values('MC3021', 12001, 50000, 24)
go
insert roysched
values('TC3218', 0, 2000, 10)
go
insert roysched
values('TC3218', 2001, 4000, 12)
go
insert roysched
values('TC3218', 4001, 6000, 14)
go
insert roysched
values('TC3218', 6001, 8000, 16)
go
insert roysched
values('TC3218', 8001, 10000, 18)
go
insert roysched
values('TC3218', 10001, 12000, 20)
go
insert roysched
values('TC3218', 12001, 14000, 22)
go
insert roysched
values('TC3218', 14001, 50000, 24)
go
insert roysched
values('PC8888', 0, 5000, 10)
go
insert roysched
values('PC8888', 5001, 10000, 12)
go
insert roysched
values('PC8888', 10001, 15000, 14)
go
insert roysched
values('PC8888', 15001, 50000, 16)
go
insert roysched
values('PS7777', 0, 5000, 10)
go
insert roysched
values('PS7777', 5001, 50000, 12)
go
insert roysched
values('PS3333', 0, 5000, 10)
go
insert roysched
values('PS3333', 5001, 10000, 12)
go
insert roysched
values('PS3333', 10001, 15000, 14)
go
insert roysched
values('PS3333', 15001, 50000, 16)
go
insert roysched
values('BU1111', 0, 4000, 10)
go
insert roysched
values('BU1111', 4001, 8000, 12)
go
insert roysched
values('BU1111', 8001, 10000, 14)
go
insert roysched
values('BU1111', 12001, 16000, 16)
go
insert roysched
values('BU1111', 16001, 20000, 18)
go
insert roysched
values('BU1111', 20001, 24000, 20)
go
insert roysched
values('BU1111', 24001, 28000, 22)
go
insert roysched
values('BU1111', 28001, 50000, 24)
go
insert roysched
values('MC2222', 0, 2000, 10)
go
insert roysched
values('MC2222', 2001, 4000, 12)
go
insert roysched
values('MC2222', 4001, 8000, 14)
go
insert roysched
values('MC2222', 8001, 12000, 16)
go
insert roysched
values('MC2222', 8001, 12000, 16)
go
insert roysched
values('MC2222', 12001, 20000, 18)
go
insert roysched
values('MC2222', 20001, 50000, 20)
go
insert roysched
values('TC7777', 0, 5000, 10)
go
insert roysched
values('TC7777', 5001, 15000, 12)
go
insert roysched
values('TC7777', 15001, 50000, 14)
go
insert roysched
values('TC4203', 0, 2000, 10)
go
insert roysched
values('TC4203', 2001, 8000, 12)
go
insert roysched
values('TC4203', 8001, 16000, 14)
go
insert roysched
values('TC4203', 16001, 24000, 16)
go
insert roysched
values('TC4203', 24001, 32000, 18)
go
insert roysched
values('TC4203', 32001, 40000, 20)
go
insert roysched
values('TC4203', 40001, 50000, 22)
go
insert roysched
values('BU7832', 0, 5000, 10)
go
insert roysched
values('BU7832', 5001, 10000, 12)
go
insert roysched
values('BU7832', 10001, 15000, 14)
go
insert roysched
values('BU7832', 15001, 20000, 16)
go
insert roysched
values('BU7832', 20001, 25000, 18)
go
insert roysched
values('BU7832', 25001, 30000, 20)
go
insert roysched
values('BU7832', 30001, 35000, 22)
go
insert roysched
values('BU7832', 35001, 50000, 24)
go
insert roysched
values('PS1372', 0, 10000, 10)
go
insert roysched
values('PS1372', 10001, 20000, 12)
go
insert roysched
values('PS1372', 20001, 30000, 14)
go
insert roysched
values('PS1372', 30001, 40000, 16)
go
insert roysched
values('PS1372', 40001, 50000, 18)
go
insert stores
values('7066', 'Barnum`s', '567 Pasadena Ave.', 'Tustin', 'CA',
	'USA', '92789', 'Net 30')
go
insert stores
values('7067', 'News & Brews', '577 First St.', 'Los Gatos', 'CA',
	'USA', '96745', 'Net 30')
go
insert stores
values('7131', 'Doc-U-Mat: Quality Laundry and Books', '24-A Avrogado Way',
	'Remulade', 'WA', 'USA', '98014', 'Net 60')
go
insert stores
values('8042', 'Bookbeat', '679 Carson St.', 'Portland', 'OR', 'USA',
	'89076', 'Net 30')
go
insert stores
values('6380', 'Eric the Read Books', '788 Catamaugus Ave.', 'Seattle', 
'WA',
	'USA', '98056', 'Net 60')
go
insert stores
values('7896', 'Fricative Bookshop', '89 Madison St.', 'Fremont', 'CA',
	'USA', '90019', 'Net 60')
go
insert stores
values('5023', 'Thoreau Reading Discount Chain', '20435 Walden Expressway',
	'Concord', 'MA',
	'USA', '01776', 'Net 60')
go
insert sales values ('7066', 'BA27618', '10/12/85')
go
insert sales values ('5023', 'AB-123-DEF-425-1Z3', '10/31/85')
go
insert sales values ('5023', 'AB-872-DEF-732-2Z1', '11/06/85')
go
insert sales values ('8042', '12-F-9', '07/13/86')
go
insert sales values ('7896', '124152', '08/14/86')
go
insert sales values ('7131', 'Asoap132', '11/16/86')
go
insert sales values ('5023', 'BS-345-DSE-860-1F2', '12/12/86')
go
insert sales values ('7067', 'NB-1.142', '01/02/87')
go
insert sales values ('5023', 'AX-532-FED-452-2Z7', '12/01/90')
go
insert sales values ('5023', 'NF-123-ADS-642-9G3', '07/18/87')
go
insert sales values ('7131', 'Fsoap867', '09/08/87')
go
insert sales values ('7066', 'BA52498', '10/27/87')
go
insert sales values ('8042', '91-A-7', '03/20/91')
go
insert sales values ('8042', '91-V-7', '03/20/91')
go
insert sales values ('8042', '55-V-7', '03/20/91')
go
insert sales values ('8042', '13-J-9', '01/13/88')
go
insert sales values ('7896', '234518', '02/14/91')
go
insert sales values ('5023', 'GH-542-NAD-713-9F9', '03/15/87')
go
insert sales values ('7131', 'Asoap432', '12/20/90')
go
insert sales values ('5023', 'ZA-000-ASD-324-4D1', '07/27/88')
go
insert sales values ('7066', 'BA71224', '08/05/88')
go
insert sales values ('5023', 'ZD-123-DFG-752-9G8', '03/21/91')
go
insert sales values ('8042', '13-E-7', '05/23/89')
go
insert sales values ('7067', 'NB-3.142', '06/13/90')
go
insert sales values ('5023', 'ZS-645-CAT-415-1B2', '03/21/91')
go
insert sales values ('5023', 'XS-135-DER-432-8J2', '03/21/91')
go
insert sales values ('5023', 'ZZ-999-ZZZ-999-0A0', '03/21/91')
go
insert sales values ('6380', '342157', '12/13/85')
go
insert sales values ('6380', '356921', '02/17/91')
go
insert sales values ('6380', '234518', '09/30/87')
go
insert salesdetail values ('7896', '234518', 'TC3218', 75, 40)
go
insert salesdetail values ('7896', '234518', 'TC7777', 75, 40)
go
insert salesdetail values ('7131', 'Asoap432', 'TC3218', 50, 40)
go
insert salesdetail values ('7131', 'Asoap432', 'TC7777', 80, 40)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'TC3218', 85, 40)
go
insert salesdetail values ('8042', '91-A-7', 'PS3333', 90, 45)
go
insert salesdetail values ('8042', '91-A-7', 'TC3218', 40, 45)
go
insert salesdetail values ('8042', '91-A-7', 'PS2106', 30, 45)
go
insert salesdetail values ('8042', '91-V-7', 'PS2106', 50, 45)
go
insert salesdetail values ('8042', '55-V-7', 'PS2106', 31, 45)
go
insert salesdetail values ('8042', '91-A-7', 'MC3021', 69, 45)
go
insert salesdetail values ('5023', 'BS-345-DSE-860-1F2', 'PC1035', 1000, 
46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'BU2075', 500, 
46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'BU1032', 200, 
46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'BU7832', 150, 
46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'PS7777', 125, 
46.7)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'TC7777', 1000, 
46.7)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'BU1032', 1000, 
46.7)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'PC1035', 750, 
46.7)
go
insert salesdetail values ('7131', 'Fsoap867', 'BU1032', 200, 46.7)
go
insert salesdetail values ('7066', 'BA52498', 'BU7832', 100, 46.7)
go
insert salesdetail values ('7066', 'BA71224', 'PS7777', 200, 46.7)
go
insert salesdetail values ('7066', 'BA71224', 'PC1035', 300, 46.7)
go
insert salesdetail values ('7066', 'BA71224', 'TC7777', 350, 46.7)
go
insert salesdetail values ('5023', 'ZD-123-DFG-752-9G8', 'PS2091', 1000, 
46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'PS2091', 200, 46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'PS7777', 250, 46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'PS3333', 345, 46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'BU7832', 360, 46.7)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'PS2091', 845, 
46.7)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'PS7777', 581, 
46.7)
go
insert salesdetail values ('5023', 'ZZ-999-ZZZ-999-0A0', 'PS1372', 375, 
46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'BU1111', 175, 46.7)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'BU7832', 885, 
46.7)
go
insert salesdetail values ('5023', 'ZD-123-DFG-752-9G8', 'BU7832', 900, 
46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'TC4203', 550, 
46.7)
go
insert salesdetail values ('7131', 'Fsoap867', 'TC4203', 350, 46.7)
go
insert salesdetail values ('7896', '234518', 'TC4203', 275, 46.7)
go
insert salesdetail values ('7066', 'BA71224', 'TC4203', 500, 46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'TC4203', 512, 46.7)
go
insert salesdetail values ('7131', 'Fsoap867', 'MC3021', 400, 46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'PC8888', 105, 
46.7)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'PC8888', 300, 
46.7)
go
insert salesdetail values ('7066', 'BA71224', 'PC8888', 350, 46.7)
go
insert salesdetail values ('7067', 'NB-3.142', 'PC8888', 335, 46.7)
go
insert salesdetail values ('7131', 'Asoap432', 'BU1111', 500, 46.7)
go
insert salesdetail values ('7896', '234518', 'BU1111', 340, 46.7)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'BU1111', 370, 
46.7)
go
insert salesdetail values ('5023', 'ZD-123-DFG-752-9G8', 'PS3333', 750, 
46.7)
go
insert salesdetail values ('8042', '13-J-9', 'BU7832', 300, 51.7)
go
insert salesdetail values ('8042', '13-E-7', 'BU2075', 150, 51.7)
go
insert salesdetail values ('8042', '13-E-7', 'BU1032', 300, 51.7)
go
insert salesdetail values ('8042', '13-E-7', 'PC1035', 400, 51.7)
go
insert salesdetail values ('8042', '91-A-7', 'PS7777', 180, 51.7)
go
insert salesdetail values ('8042', '13-J-9', 'TC4203', 250, 51.7)
go
insert salesdetail values ('8042', '13-E-7', 'TC4203', 226, 51.7)
go
insert salesdetail values ('8042', '13-E-7', 'MC3021', 400, 51.7)
go
insert salesdetail values ('8042', '91-V-7', 'BU1111', 390, 51.7)
go
insert salesdetail values ('5023', 'AB-872-DEF-732-2Z1', 'MC3021', 5000, 50)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'PC8888', 2000, 50)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'BU2075', 2000, 50)
go
insert salesdetail values ('5023', 'GH-542-NAD-713-9F9', 'PC1035', 2000, 50)
go
insert salesdetail values ('5023', 'ZA-000-ASD-324-4D1', 'PC1035', 2000, 50)
go
insert salesdetail values ('5023', 'ZA-000-ASD-324-4D1', 'PS7777', 1500, 50)
go
insert salesdetail values ('5023', 'ZD-123-DFG-752-9G8', 'BU2075', 3000, 50)
go
insert salesdetail values ('5023', 'ZD-123-DFG-752-9G8', 'TC7777', 1500, 50)
go
insert salesdetail values ('5023', 'ZS-645-CAT-415-1B2', 'BU2075', 3000, 50)
go
insert salesdetail values ('5023', 'ZS-645-CAT-415-1B2', 'BU2075', 3000, 50)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'PS3333', 2687, 50)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'TC7777', 1090, 50)
go
insert salesdetail values ('5023', 'XS-135-DER-432-8J2', 'PC1035', 2138, 50)
go
insert salesdetail values ('5023', 'ZZ-999-ZZZ-999-0A0', 'MC2222', 2032, 50)
go
insert salesdetail values ('5023', 'ZZ-999-ZZZ-999-0A0', 'BU1111', 1001, 50)
go
insert salesdetail values ('5023', 'ZA-000-ASD-324-4D1', 'BU1111', 1100, 50)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'BU7832', 1400, 50)
go
insert salesdetail values ('5023', 'BS-345-DSE-860-1F2', 'TC4203', 2700, 50)
go
insert salesdetail values ('5023', 'GH-542-NAD-713-9F9', 'TC4203', 2500, 50)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'TC4203', 3500, 50)
go
insert salesdetail values ('5023', 'BS-345-DSE-860-1F2', 'MC3021', 4500, 50)
go
insert salesdetail values ('5023', 'AX-532-FED-452-2Z7', 'MC3021', 1600, 50)
go
insert salesdetail values ('5023', 'NF-123-ADS-642-9G3', 'MC3021', 2550, 50)
go
insert salesdetail values ('5023', 'ZA-000-ASD-324-4D1', 'MC3021', 3000, 
50)
go
insert salesdetail values ('5023', 'ZS-645-CAT-415-1B2', 'MC3021', 3200, 50)
go
insert salesdetail values ('5023', 'BS-345-DSE-860-1F2', 'BU2075', 2200, 50)
go
insert salesdetail values ('5023', 'GH-542-NAD-713-9F9', 'BU1032', 1500, 50)
go
insert salesdetail values ('5023', 'ZZ-999-ZZZ-999-0A0', 'PC8888', 1005, 50)
go
insert salesdetail values ('7896', '124152', 'BU2075', 42, 50.5)
go
insert salesdetail values ('7896', '124152', 'PC1035', 25, 50.5)
go
insert salesdetail values ('7131', 'Asoap132', 'BU2075', 35, 50.5)
go
insert salesdetail values ('7067', 'NB-1.142', 'PC1035', 34, 50.5)
go
insert salesdetail values ('7067', 'NB-1.142', 'TC4203', 53, 50.5)
go
insert salesdetail values ('8042', '12-F-9', 'BU2075', 30, 55.5)
go
insert salesdetail values ('8042', '12-F-9', 'BU1032', 94, 55.5)
go
insert salesdetail values ('7066', 'BA27618', 'BU2075', 200, 57.2)
go
insert salesdetail values ('7896', '124152', 'TC4203', 350, 57.2)
go
insert salesdetail values ('7066', 'BA27618', 'TC4203', 230, 57.2)
go
insert salesdetail values ('7066', 'BA27618', 'MC3021', 200, 57.2)
go
insert salesdetail values ('7131', 'Asoap132', 'MC3021', 137, 57.2)
go
insert salesdetail values ('7067', 'NB-1.142', 'MC3021', 270, 57.2)
go
insert salesdetail values ('7067', 'NB-1.142', 'BU2075', 230, 57.2)
go
insert salesdetail values ('7131', 'Asoap132', 'BU1032', 345, 57.2)
go
insert salesdetail values ('7067', 'NB-1.142', 'BU1032', 136, 57.2)
go
insert salesdetail values ('8042', '12-F-9', 'TC4203', 300, 62.2)
go
insert salesdetail values ('8042', '12-F-9', 'MC3021', 270, 62.2)
go
insert salesdetail values ('8042', '12-F-9', 'PC1035', 133, 62.2)
go
insert salesdetail values ('5023', 'AB-123-DEF-425-1Z3', 'TC4203', 2500, 
60.5)
go
insert salesdetail values ('5023', 'AB-123-DEF-425-1Z3', 'BU2075', 4000, 
60.5)
go
insert salesdetail values ('6380', '342157', 'BU2075', 200, 57.2)
go
insert salesdetail values ('6380', '342157', 'MC3021', 250, 57.2)
go
insert salesdetail values ('6380', '356921', 'PS3333', 200, 46.7)
go
insert salesdetail values ('6380', '356921', 'PS7777', 500, 46.7)
go
insert salesdetail values ('6380', '356921', 'TC3218', 125, 46.7)
go
insert salesdetail values ('6380', '234518', 'BU2075', 135, 46.7)
go
insert salesdetail values ('6380', '234518', 'BU1032', 320, 46.7)
go
insert salesdetail values ('6380', '234518', 'TC4203', 300, 46.7)
go
insert salesdetail values ('6380', '234518', 'MC3021', 400, 46.7)
go
insert titleauthor
values('409-56-7008', 'BU1032', 1, 60)
go
insert titleauthor
values('486-29-1786', 'PS7777', 1, 100)
go
insert titleauthor
values('486-29-1786', 'PC9999', 1, 100)
go
insert titleauthor
values('712-45-1867', 'MC2222', 1, 100)
go
insert titleauthor
values('172-32-1176', 'PS3333', 1, 100)
go
insert titleauthor
values('213-46-8915', 'BU1032', 2, 40)
go
insert titleauthor
values('238-95-7766', 'PC1035', 1, 100)
go
insert titleauthor
values('213-46-8915', 'BU2075', 1, 100)
go
insert titleauthor
values('998-72-3567', 'PS2091', 1, 50)
go
insert titleauthor
values('899-46-2035', 'PS2091', 2, 50)
go
insert titleauthor
values('998-72-3567', 'PS2106', 1, 100)
go
insert titleauthor
values('722-51-5454', 'MC3021', 1, 75)
go
insert titleauthor
values('899-46-2035', 'MC3021', 2, 25)
go
insert titleauthor
values('807-91-6654', 'TC3218', 1, 100)
go
insert titleauthor
values('274-80-9391', 'BU7832', 1, 100)
go
insert titleauthor
values('427-17-2319', 'PC8888', 1, 50)
go
insert titleauthor
values('846-92-7186', 'PC8888', 2, 50)
go
insert titleauthor
values('756-30-7391', 'PS1372', 1, 75)
go
insert titleauthor
values('724-80-9391', 'PS1372', 2, 25)
go
insert titleauthor
values('724-80-9391', 'BU1111', 1, 60)
go
insert titleauthor
values('267-41-2394', 'BU1111', 2, 40)
go
insert titleauthor
values('672-71-3249', 'TC7777', 1, 40)
go
insert titleauthor
values('267-41-2394', 'TC7777', 2, 30)
go
insert titleauthor
values('472-27-2349', 'TC7777', 3, 30)
go
insert titleauthor
values('648-92-1872', 'TC4203', 1, 100)
go
insert discounts
values('Initial Customer', NULL, NULL, NULL, 10.5)
go
insert discounts
values('Volume Discount', NULL, 100, 1000, 6.7)
go
insert discounts
values('Huge Volume Discount', NULL, 1001, NULL, 10)
go
insert discounts
values('Customer Discount', '8042', NULL, NULL, 5.0)
go
insert blurbs values ('486-29-1786', 'If Chastity Locksley didn`t
exist, this troubled world would have created her!  Not only did she
master the mystic secrets of inner strength to conquer adversity when
she encountered it in life, but, after reinventing herself, as she
says, by writing Emotional Security: A New Algorithm following the
devastating loss of her cat Old Algorithm, she also founded Publish or
Perish, the page-by-page, day-by-day, write-yourself-to-wellness
encounter workshops franchise empire, the better to share her inspiring
discoveries with us all.  Her Net Etiquette, a brilliant social
treatise in its own right and a fabulous pun, is the only civilized
alternative to the gross etiquette often practiced on the PUBLIC
networks.')
insert blurbs values ('648-92-1872', 'A chef`s chef and a raconteur`s
raconteur, Reginald Blotchet-Halls calls London his second home. Th`
palace kitchen`s me first `ome, act`lly! Blotchet-Halls` astounding
ability to delight our palates with palace delights is matched only by
his equal skill in satisfying our perpetual hunger for delicious
back-stairs gossip by serving up tidbits and entrees literally fit for
a king!')
insert blurbs values ('998-72-3567', 'Albert Ringer was born
in a trunk to circus parents, but another kind of circus trunk played a
more important role in his life years later.  He grew up as an
itinerant wrestler and roustabout in the reknowned Ringer Brothers and
Betty and Bernie`s Circus.  Once known in the literary world only as
Anne Ringer`s wrestling brother, he became a writer while recuperating
from a near-fatal injury received during a charity benefit bout with a
gorilla.  Slingshotting himself from the ring ropes, Albert flew
over the gorilla`s head and would have landed head first on the
concrete.  He was saved from certain death by Nana, an elephant he
befriended as a child, who caught him in her trunk.  Nana held him so
tightly that three ribs cracked and he turned blue from lack of
oxygen.  I was delirious.  I had an out-of-body experience!  My whole
life passed before me.  I promised myself `If I get through this, I`ll
use my remaining time to share what I learned out there.`  I owe it all
to Nana!')
insert blurbs values ('899-46-2035', 'Anne Ringer ran away from the
circus as a child.  A university creative writing professor and her
family took Anne in and raised her as one of their own.  In this warm
and television-less setting she learned to appreciate the great
classics of literature.  The stream of aspiring and accomplished
writers that flowed constantly through the house confirmed her
repudiation of the circus family she`d been born into: Barbarians!
The steadily growing recognition of her literary work was, to her,
vindication.  When her brother`s brush with death brought them together
after many years, she took advantage of life`s crazy chance thing and
broke the wall of anger that she had constructed to separate them.
Together they wrote, Is Anger the Enemy? an even greater
blockbuster than her other collaborative work, with Michel DeFrance,
The Gourmet Microwave.')
insert blurbs values ('672-71-3249', 'They asked me to write about
myself and my book, so here goes:  I started a restaurant called de
Gustibus with two of my friends.  We named it that because you really
can`t discuss taste.  We`re very popular with young business types
because we`re young business types ourselves.  Whenever we tried to go
out to eat in a group we always got into these long tiresome
negotiations: I just ate Italian, or I ate Greek yesterday, or
I NEVER eat anything that`s not organic!  Inefficient.  Not what
business needs today.  So, it came to us that we needed a restaurant we
could all go to every day and not eat the same thing twice in a row
maybe for a year!  We thought, Hey, why make people choose one kind
of restaurant over another, when what they really want is a different
kind of food?  At de Gustibus you can eat Italian, Chinese, Japanese,
Greek, Russian, Tasmanian, Iranian, and on and on all at the same
time.  You never have to choose.  You can even mix and match!  We just
pooled our recipes, opened the doors, and never looked back.  We`re a
big hit, what can I say?  My recipes in Sushi, Anyone? are used at
de Gustibus.  They satisfy crowds for us every day.  They will work for
you, too.  Period!')
insert blurbs values ('409-56-7008', 'Bennet was the classic too-busy
executive.  After discovering computer databases he now has the time to
run several successful businesses and sit on three major corporate
boards.  Bennet also donates time to community service organizations.
Miraculously, he also finds time to write and market executive-oriented
in-depth computer hardware and software reviews.  I`m hyperkinetic,
so being dynamic and fast-moving is a piece of cake.  But being
organized isn`t easy for me or for anyone I know.  There`s just one
word for that: `databases!` Databases can cure you or kill you.  If you
get the right one, you can be like me.  If you get the wrong one, watch
out.  Read my book!')
go

if exists ( select * from systriggers where trigname = 'deltitle')
	drop trigger deltitle
go

create trigger deltitle
	on titles
	for delete
	as
	if (select count(*) from deleted, salesdetail
	where salesdetail.title_id = deleted.title_id) >0
	begin
		rollback transaction
		print 'You cant delete a title with sales.'
	end
go


if exists ( select * from systriggers where trigname = 'totalsales_trig')
	drop trigger deltitle
go
create trigger totalsales_trig
	on salesdetail
	for insert, update, delete
	  as
	if @@rowcount = 0
	begin
		return
	end

	update titles
        set total_sales = isnull(total_sales, 0) + (select sum(qty)
		from inserted
		where titles.title_id = inserted.title_id)
	where title_id in (select title_id from inserted)

	update titles
        set total_sales = isnull(total_sales, 0) - (select sum(qty)
		from deleted
		where titles.title_id = deleted.title_id)
	where title_id in (select title_id from deleted)
go

if exists ( select * from systable where table_name = 'titleview')
	drop view titleview
go
create view titleview
	as
	select title, au_ord, au_lname,
	price, total_sales, pub_id
	from authors, titles, titleauthor
	where authors.au_id = titleauthor.au_id
	and titles.title_id = titleauthor.title_id
go

if exists ( select * from sysprocedure where proc_name = 'byroyalty')
	drop procedure byroyalty
go
create procedure byroyalty @percentage int
	as
	select au_id from titleauthor
	where royaltyper = @percentage
go
grant execute on byroyalty to PUBLIC
go

if exists (select * from systable where table_name ='batchTable')
     drop table batchTable
go

create table batchTable (empl_no int, company varchar(20), lname varchar(20))
go

grant all on batchTable to PUBLIC
go

if exists ( select * from sysprocedure where proc_name = 'sp_updateBatch')
    drop procedure sp_updateBatch
go
create proc sp_updateBatch (@type int, @empl_no int, @lname varchar(20))
as
begin 
    if (@type = 1) 
	    insert batchTable values (@empl_no,'Our Company',@lname)
    else
	    update batchTable set company = 'Sybase' where lname like 'C%' 
end
go
grant execute on sp_updateBatch to PUBLIC
go
