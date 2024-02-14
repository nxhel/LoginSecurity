DROP TABLE JLUSERS;
DROP TYPE jluser_type;
CREATE TABLE JLUSERS (
    userId VARCHAR2(60)     PRIMARY KEY NOT NULL, 
    salt RAW(16)            NOT NULL,  
    passwordHash RAW(64)    NOT NULL ,
    failedLoginCount NUMBER(1)
);
/
CREATE OR REPLACE TYPE jluser_type AS OBJECT(
    userId      VARCHAR2(60),
    salt        RAW(16),
    passwordHash RAW(64),
    failedLoginCount NUMBER(1)
);
/

CREATE OR REPLACE PACKAGE nxhelAssignment9 AS
    user_not_found EXCEPTION;
    FUNCTION getTheUser (theUserId JLUSERS.userId%TYPE ) return jluser_type;
    PROCEDURE UpdateUser (passedUser IN jluser_type);   
END nxhelAssignment9;
/
CREATE OR REPLACE PACKAGE BODY nxhelAssignment9 AS
    FUNCTION getTheUser(theUserId JLUSERS.userId%TYPE) RETURN jluser_type IS
        userObj jluser_type;
            BEGIN 
                SELECT jluser_type(userId, salt, passwordHash, failedLoginCount) INTO userObj
                FROM JLUSERS
                WHERE userId = theUserId;

                IF userObj IS NULL THEN 
                    raise user_not_found;
                END IF;

                RETURN userObj;
        
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    DBMS_OUTPUT.PUT_LINE('ERROR OCCURRED');
                    RETURN NULL; 
    END getTheUser;

    PROCEDURE addUser(newUser IN jluser_type) IS
        BEGIN
            INSERT INTO JLUSERS VALUES (
                newUser.userId,
                newUser.salt,
                newUser.passwordHash,
                newUser.failedLoginCount
            );
            COMMIT;
    END addUser;

    PROCEDURE UpdateUser(passedUser IN jluser_type) AS
        checkIfUserExists NUMBER;
            BEGIN 
                SELECT COUNT(*) INTO checkIfUserExists FROM JLUSERS WHERE userId = passedUser.userId;
    
                IF checkIfUserExists = 0 THEN 
                    addUser(passedUser);
                ELSE 
                    UPDATE JLUSERS SET 
                        salt = passedUser.salt,
                        passwordHash = passedUser.passwordHash,
                        failedLoginCount = passedUser.failedLoginCount 
                    WHERE userId = passedUser.userId;
                END IF;
        COMMIT;
    END UpdateUser;
END nxhelAssignment9;
/