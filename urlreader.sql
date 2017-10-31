CREATE TABLE "content" (
"content_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 
"project_id" INTEGER, 
"text" VARCHAR(255), 
"type" VARCHAR(50), 
"site_id" INTEGER);
CREATE TABLE "project" (
"project_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE ,
"text" VARCHAR(50) DEFAULT (null) );
CREATE TABLE "site" (
"site_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE ,
"project_id" INTEGER DEFAULT (null) ,
"text" VARCHAR(255) DEFAULT (null) );
