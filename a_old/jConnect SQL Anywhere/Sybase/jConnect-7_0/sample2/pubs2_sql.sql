/* These stored procedures are extensions to pubs2, 
 * specifically for use in executing the sample programs
 * on your local server
 */
use pubs2
go

/* 
 * Stored procedure used for Callable.java & MyCallable.java Sample
 */
if exists (select * from sysobjects where name = 'sp_callableSample')
  begin
    drop procedure sp_callableSample
  end
go
create procedure sp_callableSample 
   (@p1 int, @p2 varchar(255) out)
   as 
   begin
     select @p1, @p2 
     select @p2 = 'The Answer to Life, the Universe, and Everything.'
     return  42
   end
go
grant execute on sp_callableSample to public
go

/* 
 * Stored procedure used for Raiserror.java Sample
 */
if exists (select * from sysobjects where name = 'sp_raiserrorSample')
  begin
    drop procedure sp_raiserrorSample
  end
go
create procedure sp_raiserrorSample
 as
 begin
     raiserror 24000 'I raised this error'
     raiserror 24001 'I raised this error'
     raiserror 25000 'I raised this error'
     select au_id,au_fname, au_lname from authors 
     raiserror 26000 'I raised this error'
     select title_id, type, price from titles
 end
go

sp_procxmode sp_raiserrorSample 'anymode'
go
grant execute on sp_raiserrorSample to public
go

/*
 * The following sql statement are used with the SybTimestamp.java Sample
 * spt_timestamp - table defined to include a timestamp type
 */
if exists (select * from sysobjects where name = 'spt_timestampSample')
  begin
     drop table spt_timestampSample
  end
go
create table spt_timestampSample(f1 int, f2 char(5), f3 timestamp )
go
commit
go
grant select on spt_timestampSample to public
go

insert spt_timestampSample(f1,f2) values(1, 'Hello')
go

if exists (select * from sysobjects where name = 'sp_timestampSample')
  begin
    drop procedure sp_timestampSample
  end
go
create procedure sp_timestampSample
  (@p1 int, @p2 timestamp out)
   as
   begin
      select 'p1='  + convert(varchar(10),@p1) 
      select @p2 = f3 from spt_timestampSample where f1=1
      select * from spt_timestampSample
      return 21
   end
go
grant execute on sp_timestampSample to public
go
commit
go
/*
 * Stored procedure used for PrintExample.java  Sample
 */
if exists (select * from sysobjects where name = 'sp_printExampleSample')
  begin
    drop procedure sp_printExampleSample
  end
go
create procedure sp_printExampleSample
   as
   begin
     print 'print statement 1'
     print 'print statement 2'
     select au_id, au_fname, au_lname from authors
     print 'print statement 3'
     select title_id, type, price from titles
   end
go
grant execute on sp_printExampleSample to public
go
/*
 * Stored procedure used for NameBINDRPC.java  Sample
 */
if exists (select * from sysobjects where name = 'sp_nameBindRPCSample')
  begin
    drop procedure sp_nameBindRPCSample
  end
go
create procedure sp_nameBindRPCSample
(@p2 int, @p3 int = 47, @p4 char(30))
   as
   begin
     print 'This is a print statement'
     select @p2, @p3, @p4
     return 1
   end
go
grant execute on sp_nameBindRPCSample to public
go

/** Tables for BatchUpdate Sample */
if exists (select * from sysobjects where name = 'batchTable')
  begin
     drop table batchTable
  end
go
create table batchTable (empl_no int, company varchar(20), lname varchar(20))
go

/* 
   Public needs insert, updates, select permissions 
*/
grant all on batchTable to public
go

if exists (select * from sysobjects where name = 'sp_updateBatch')
  begin
    drop procedure sp_updateBatch
  end
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

sp_procxmode sp_updateBatch, 'anymode'
go
grant execute on sp_updateBatch to public
go
