
-- START TITLE REPORT ALL

SELECT t.job_title_id, t.job_title, p.pay_date, p.earnings FROM job_titles t
    LEFT JOIN employee_job_titles ejt ON t.job_title_id = ejt.job_title_id
    LEFT JOIN employees e ON ejt.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    ORDER BY t.job_title_id, p.pay_date;

-- END TITLE REPORT ALL

-- START TITLE REPORT WHERE

SELECT t.job_title_id, t.job_title, p.pay_date, p.earnings FROM job_titles t
    LEFT JOIN employee_job_titles ejt ON t.job_title_id = ejt.job_title_id
    LEFT JOIN employees e ON ejt.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    WHERE (p.pay_date BETWEEN date(/*START DATE*/) AND date(/*END DATE*/)) AND
        t.job_title LIKE /*TITLE NAME*/
    ORDER BY t.job_title_id, p.pay_date;

-- END TITLE REPORT WHERE

-- START TITLE REPORT DATE WHERE

SELECT t.job_title_id, t.job_title, p.pay_date, p.earnings FROM job_titles t
    LEFT JOIN employee_job_titles ejt ON t.job_title_id = ejt.job_title_id
    LEFT JOIN employees e ON ejt.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    WHERE p.pay_date BETWEEN date(/*START DATE*/) AND date(/*END DATE*/)
    ORDER BY t.job_title_id, p.pay_date;

-- END TITLE REPORT DATE WHERE

-- START TITLE REPORT NAME WHERE

SELECT t.job_title_id, t.job_title, p.pay_date, p.earnings FROM job_titles t
    LEFT JOIN employee_job_titles ejt ON t.job_title_id = ejt.job_title_id
    LEFT JOIN employees e ON ejt.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    WHERE t.job_title LIKE /*TITLE NAME*/
    ORDER BY t.job_title_id, p.pay_date;

-- END TITLE REPORT NAME WHERE

-- Further processing necessary on this data in java
-- START DIV REPORT ALL

SELECT d.ID, d.Name, p.pay_date, p.earnings FROM division d
    LEFT JOIN employee_division ed ON d.ID = ed.div_ID
    LEFT JOIN employees e ON ed.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    ORDER BY d.ID, p.pay_date;

-- END DIV REPORT ALL

-- START DIV REPORT WHERE

SELECT d.ID, d.Name, p.pay_date, p.earnings FROM division d
    LEFT JOIN employee_division ed ON d.ID = ed.div_ID
    LEFT JOIN employees e ON ed.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    WHERE (p.pay_date BETWEEN date(/*START DATE*/) AND date(/*END DATE*/)) AND
          d.Name LIKE /*DIV NAME*/
    ORDER BY d.ID, p.pay_date;

-- END DIV REPORT WHERE

-- START DIV REPORT NAME WHERE

SELECT d.ID, d.Name, p.pay_date, p.earnings FROM division d
    LEFT JOIN employee_division ed ON d.ID = ed.div_ID
    LEFT JOIN employees e ON ed.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    WHERE d.Name LIKE /*DIV NAME*/
    ORDER BY d.ID, p.pay_date;

-- END DIV REPORT NAME WHERE

-- START DIV REPORT DATE WHERE

SELECT d.ID, d.Name, p.pay_date, p.earnings FROM division d
    LEFT JOIN employee_division ed ON d.ID = ed.div_ID
    LEFT JOIN employees e ON ed.empid = e.empid
    LEFT JOIN payroll p ON e.empid = p.empid
    WHERE p.pay_date BETWEEN date(/*START DATE*/) AND date(/*END DATE*/)
    ORDER BY d.ID, p.pay_date;

-- END DIV REPORT DATE WHERE

-- START EMP REPORT WHERE

SELECT * FROM employees e LEFT JOIN payroll p ON e.empid = p.empid WHERE e./*COL*/ LIKE /*PARAM*/;

-- END EMP REPORT WHERE

-- START EMP REPORT ALL

SELECT * FROM employees e LEFT JOIN payroll p ON e.empid = p.empid;

-- END EMP REPORT ALL

-- This next one is somewhat experimental. We may end up not using it.
-- START INIT DEFAULT

CREATE TABLE IF NOT EXISTS employees (
                           empid INT NOT NULL AUTO_INCREMENT,
                           Fname VARCHAR(65) NOT NULL,
                           Lname VARCHAR(65) NOT NULL,
                           email VARCHAR(65) NOT NULL,
                           HireDate DATE,
                           Salary DECIMAL(10,2) NOT NULL,
                           PRIMARY KEY (empid)
);

CREATE TABLE IF NOT EXISTS payroll (
                         payID INT,
                         pay_date DATE,
                         earnings DECIMAL(8,2),
                         fed_tax DECIMAL(7,2),
                         fed_med DECIMAL(7,2),
                         fed_SS DECIMAL(7,2),
                         state_tax DECIMAL(7,2),
                         retire_401k DECIMAL(7,2),
                         health_care DECIMAL(7,2),
                         empid INT
);

CREATE TABLE IF NOT EXISTS job_titles (
                            job_title_id INT,
                            job_title VARCHAR(125) NOT NULL
);

CREATE TABLE IF NOT EXISTS employee_job_titles (
                                     empid INT NOT NULL,
                                     job_title_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS division (
                          ID int NOT NULL,
                          Name varchar(100) DEFAULT NULL,
                          city varchar(50) NOT NULL,
                          addressLine1 varchar(50) NOT NULL,
                          addressLine2 varchar(50) DEFAULT NULL,
                          state varchar(50) DEFAULT NULL,
                          country varchar(50) NOT NULL,
                          postalCode varchar(15) NOT NULL
) COMMENT='company divisions';

CREATE TABLE IF NOT EXISTS employee_division (
                                   empid int NOT NULL,
                                   div_ID int NOT NULL
) COMMENT='links employee to a division';

DELETE FROM employees;
DELETE FROM employee_division;
DELETE FROM employee_job_titles;
DELETE FROM division;
DELETE FROM job_titles;
DELETE FROM employee_job_titles;

ALTER TABLE employees AUTO_INCREMENT = 1;
ALTER TABLE employee_division AUTO_INCREMENT = 1;
ALTER TABLE employee_job_titles AUTO_INCREMENT = 1;
ALTER TABLE division AUTO_INCREMENT = 1;
ALTER TABLE job_titles AUTO_INCREMENT = 1;
ALTER TABLE payroll AUTO_INCREMENT = 1;

ALTER TABLE employee_division
    ADD INDEX (div_ID),
ADD FOREIGN KEY (empid) REFERENCES employees(empid);

ALTER TABLE division
    ADD FOREIGN KEY (ID) REFERENCES employee_division(div_ID);

ALTER TABLE employee_job_titles
    ADD INDEX (job_title_id),
ADD FOREIGN KEY (empid) REFERENCES employees(empid);

ALTER TABLE job_titles
    ADD FOREIGN KEY (job_title_id) REFERENCES  employee_job_titles(job_title_id);

ALTER TABLE payroll
    ADD FOREIGN KEY (empid) REFERENCES employees(empid);

-- END INIT DEFAULT
