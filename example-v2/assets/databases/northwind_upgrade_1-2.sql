-- add a FullNames column to Employees
ALTER TABLE "Employees" RENAME TO 'Employees_ME_TMP';
CREATE TABLE "Employees" (
"EmployeeID" int NOT NULL,
"LastName" varchar(20) NOT NULL,
"FirstName" varchar(10) NOT NULL,
"Title" varchar(30),
"TitleOfCourtesy" varchar(25),
"BirthDate" timestamp,
"HireDate" timestamp,
"Address" varchar(60),
"City" varchar(15),
"Region" varchar(15),
"PostalCode" varchar(10),
"Country" varchar(15),
"HomePhone" varchar(24),
"Extension" varchar(4),
"Photo" blob,
"Notes" text,
"ReportsTo" int,
"PhotoPath" varchar(255),
"FullName" varchar(150),
PRIMARY KEY ("EmployeeID")
);
INSERT INTO "Employees"  ("EmployeeID", "LastName", "FirstName", "Title", "TitleOfCourtesy", "BirthDate", "HireDate", "Address", "City", "Region", "PostalCode", "Country", "HomePhone", "Extension", "Photo", "Notes", "ReportsTo", "PhotoPath", "FullName") SELECT "EmployeeID", "LastName", "FirstName", "Title", "TitleOfCourtesy", "BirthDate", "HireDate", "Address", "City", "Region", "PostalCode", "Country", "HomePhone", "Extension", "Photo", "Notes", "ReportsTo", "PhotoPath", "FirstName" || ' ' || "LastName" FROM "Employees_ME_TMP";
DROP TABLE "Employees_ME_TMP";