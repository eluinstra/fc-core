/**
 * Copyright 2020 E.Luinstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.luin.file.client.core.file;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;

import dev.luin.file.client.core.download.QDownloadTask;
import dev.luin.file.client.core.upload.QUploadTask;

import java.sql.Types;

/**
 * QFile is a Querydsl query type for QFile
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFile extends com.querydsl.sql.RelationalPathBase<QFile>
{
	private static final long serialVersionUID = -2054044460;

	public static final QFile file = new QFile("file");

	public final StringPath contentType = createString("contentType");

	public final NumberPath<Long> id = createNumber("id",Long.class);

	public final NumberPath<Long> length = createNumber("length",Long.class);

	public final StringPath md5Checksum = createString("md5Checksum");

	public final StringPath name = createString("name");

	public final StringPath path = createString("path");

	public final StringPath sha256Checksum = createString("sha256Checksum");

	public final DateTimePath<java.time.Instant> timestamp = createDateTime("time_stamp",java.time.Instant.class);

	public final SimplePath<java.net.URL> url = createSimple("url",java.net.URL.class);

	public final com.querydsl.sql.PrimaryKey<QFile> sysPk10137 = createPrimaryKey(id);

	public final com.querydsl.sql.ForeignKey<QDownloadTask> _sysFk10167 = createInvForeignKey(id,"file_id");

	public final com.querydsl.sql.ForeignKey<QUploadTask> _sysFk10151 = createInvForeignKey(id,"file_id");

	public QFile(String variable)
	{
		super(QFile.class,forVariable(variable),"PUBLIC","file");
		addMetadata();
	}

	public QFile(String variable, String schema, String table)
	{
		super(QFile.class,forVariable(variable),schema,table);
		addMetadata();
	}

	public QFile(String variable, String schema)
	{
		super(QFile.class,forVariable(variable),schema,"file");
		addMetadata();
	}

	public QFile(Path<? extends QFile> path)
	{
		super(path.getType(),path.getMetadata(),"PUBLIC","file");
		addMetadata();
	}

	public QFile(PathMetadata metadata)
	{
		super(QFile.class,metadata,"PUBLIC","file");
		addMetadata();
	}

	public void addMetadata()
	{
		addMetadata(contentType,ColumnMetadata.named("content_type").withIndex(5).ofType(Types.VARCHAR).withSize(256).notNull());
		addMetadata(id,ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
		addMetadata(length,ColumnMetadata.named("length").withIndex(9).ofType(Types.BIGINT).withSize(64));
		addMetadata(md5Checksum,ColumnMetadata.named("md5_checksum").withIndex(6).ofType(Types.VARCHAR).withSize(32));
		addMetadata(name,ColumnMetadata.named("name").withIndex(4).ofType(Types.VARCHAR).withSize(256));
		addMetadata(path,ColumnMetadata.named("path").withIndex(3).ofType(Types.VARCHAR).withSize(256).notNull());
		addMetadata(sha256Checksum,ColumnMetadata.named("sha256_checksum").withIndex(7).ofType(Types.VARCHAR).withSize(64));
		addMetadata(timestamp,ColumnMetadata.named("time_stamp").withIndex(8).ofType(Types.TIMESTAMP).withSize(26).notNull());
		addMetadata(url,ColumnMetadata.named("url").withIndex(2).ofType(Types.VARCHAR).withSize(256));
	}
}
