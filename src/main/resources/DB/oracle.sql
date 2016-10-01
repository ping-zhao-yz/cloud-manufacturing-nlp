CREATE TABLE LocalDictionary (
	term VARCHAR2(256) NOT NULL,
	synonyms VARCHAR2(256) NOT NULL,
	CONSTRAINT PK_local_dictionary PRIMARY KEY (term)
)
/
create SEQUENCE local_dictionary_seq INCREMENT BY 1 START WITH 1 CACHE 20
/
CREATE OR REPLACE TRIGGER local_dictionary_trig
BEFORE INSERT ON LocalDictionary
FOR EACH ROW
WHEN (new.term IS NULL)
BEGIN
	SELECT local_dictionary_seq.NEXTVAL into :new.term FROM dual;
END;
/