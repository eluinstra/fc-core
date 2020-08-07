package dev.luin.fc.core.querydsl.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUploadTask is a Querydsl query type for QUploadTask
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUploadTask extends com.querydsl.sql.RelationalPathBase<QUploadTask> {

    private static final long serialVersionUID = -2093892482;

    public static final QUploadTask uploadTask = new QUploadTask("upload_task");

    public final SimplePath<java.net.URL> creationUrl = createSimple("creationUrl",java.net.URL.class);

    public final NumberPath<Long> fileId = createNumber("fileId", Long.class);

    public final NumberPath<Integer> retries = createNumber("retries", Integer.class);

    public final DateTimePath<java.time.Instant> scheduleTime = createDateTime("scheduleTime", java.time.Instant.class);

    public final DateTimePath<java.time.Instant> timestamp = createDateTime("timestamp", java.time.Instant.class);

    public final com.querydsl.sql.ForeignKey<QFile> sysFk10151 = createForeignKey(fileId, "id");

    public QUploadTask(String variable) {
        super(QUploadTask.class, forVariable(variable), "PUBLIC", "upload_task");
        addMetadata();
    }

    public QUploadTask(String variable, String schema, String table) {
        super(QUploadTask.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUploadTask(String variable, String schema) {
        super(QUploadTask.class, forVariable(variable), schema, "upload_task");
        addMetadata();
    }

    public QUploadTask(Path<? extends QUploadTask> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "upload_task");
        addMetadata();
    }

    public QUploadTask(PathMetadata metadata) {
        super(QUploadTask.class, metadata, "PUBLIC", "upload_task");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(creationUrl, ColumnMetadata.named("creation_url").withIndex(2).ofType(Types.VARCHAR).withSize(256).notNull());
        addMetadata(fileId, ColumnMetadata.named("file_id").withIndex(1).ofType(Types.INTEGER).withSize(32).notNull());
        addMetadata(retries, ColumnMetadata.named("retries").withIndex(5).ofType(Types.TINYINT).withSize(8).notNull());
        addMetadata(scheduleTime, ColumnMetadata.named("schedule_time").withIndex(4).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(timestamp, ColumnMetadata.named("timestamp").withIndex(3).ofType(Types.TIMESTAMP).withSize(26).notNull());
    }

}

