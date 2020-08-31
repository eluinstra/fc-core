--
-- Copyright 2020 E.Luinstra
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE TABLE fs_file
(
	id								SERIAL					PRIMARY KEY,
	url								VARCHAR(256)		NULL,
	path							VARCHAR(256)		NOT NULL UNIQUE,
	name							VARCHAR(256)		NULL,
	content_type			VARCHAR(256)		NOT NULL,
	md5_checksum			VARCHAR(32)			NULL,
	sha256_checksum		VARCHAR(64)			NULL,
	time_stamp				TIMESTAMP				NOT NULL,
	length						BIGINT					NULL
);

CREATE TABLE upload_task
(
	file_id						INTEGER					NOT NULL UNIQUE,
	creation_url			VARCHAR(256)		NOT NULL,
	time_stamp				TIMESTAMP				NOT NULL,
	status						SMALLINT				NOT NULL,
	status_time				TIMESTAMP				NOT NULL,
	schedule_time			TIMESTAMP				NOT NULL,
	retries						SMALLINT				NOT NULL,
	FOREIGN KEY (file_id) REFERENCES file(id)
);

CREATE TABLE download_task
(
	file_id						INTEGER					NOT NULL UNIQUE,
	url								VARCHAR(256)		NOT NULL,
	start_date				TIMESTAMP				NULL,
	end_date					TIMESTAMP				NULL,
	time_stamp				TIMESTAMP				NOT NULL,
	status						SMALLINT				NOT NULL,
	status_time				TIMESTAMP				NOT NULL,
	schedule_time			TIMESTAMP				NOT NULL,
	retries						SMALLINT				NOT NULL,
	FOREIGN KEY (file_id) REFERENCES file(id)
);
