package dev.luin.fc.core.querydsl.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QDownloadTask is a Querydsl query type for QDownloadTask
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDownloadTask extends com.querydsl.sql.RelationalPathBase<QDownloadTask> {

    private static final long serialVersionUID = 600040837;

    public static final QDownloadTask downloadTask = new QDownloadTask("download_task");

    public final DateTimePath<java.time.Instant> endDate = createDateTime("endDate", java.time.Instant.class);

    public final NumberPath<Long> fileId = createNumber("fileId", Long.class);

    public final NumberPath<Integer> retries = createNumber("retries", Integer.class);

    public final DateTimePath<java.time.Instant> scheduleTime = createDateTime("scheduleTime", java.time.Instant.class);

    public final DateTimePath<java.time.Instant> startDate = createDateTime("startDate", java.time.Instant.class);

    public final DateTimePath<java.time.Instant> timestamp = createDateTime("timestamp", java.time.Instant.class);

    public final SimplePath<java.net.URL> url = createSimple("url",java.net.URL.class);

    public final com.querydsl.sql.ForeignKey<QFile> sysFk10167 = createForeignKey(fileId, "id");

    public QDownloadTask(String variable) {
        super(QDownloadTask.class, forVariable(variable), "PUBLIC", "download_task");
        addMetadata();
    }

    public QDownloadTask(String variable, String schema, String table) {
        super(QDownloadTask.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDownloadTask(String variable, String schema) {
        super(QDownloadTask.class, forVariable(variable), schema, "download_task");
        addMetadata();
    }

    public QDownloadTask(Path<? extends QDownloadTask> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "download_task");
        addMetadata();
    }

    public QDownloadTask(PathMetadata metadata) {
        super(QDownloadTask.class, metadata, "PUBLIC", "download_task");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(endDate, ColumnMetadata.named("end_date").withIndex(3).ofType(Types.TIMESTAMP).withSize(26));
        addMetadata(fileId, ColumnMetadata.named("file_id").withIndex(5).ofType(Types.INTEGER).withSize(32));
        addMetadata(retries, ColumnMetadata.named("retries").withIndex(7).ofType(Types.TINYINT).withSize(8).notNull());
        addMetadata(scheduleTime, ColumnMetadata.named("schedule_time").withIndex(6).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(startDate, ColumnMetadata.named("start_date").withIndex(2).ofType(Types.TIMESTAMP).withSize(26));
        addMetadata(timestamp, ColumnMetadata.named("timestamp").withIndex(4).ofType(Types.TIMESTAMP).withSize(26).notNull());
        addMetadata(url, ColumnMetadata.named("url").withIndex(1).ofType(Types.VARCHAR).withSize(256).notNull());
    }

}

