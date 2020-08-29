package dev.luin.fc.core.download;

import java.time.Instant;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Transactional(transactionManager = "dataSourceTransactionManager")
public class DownloadTaskDAOImpl implements DownloadTaskDAO
{
	@NonNull
	SQLQueryFactory queryFactory;
	QDownloadTask table = QDownloadTask.downloadTask;
	Expression<?>[] downloadTaskColumns = {table.fileId,table.url,table.startDate,table.endDate,table.status,table.scheduleTime,table.retries};
	ConstructorExpression<DownloadTask> downloadTaskProjection = Projections.constructor(DownloadTask.class,downloadTaskColumns);

	@Override
	public Option<DownloadTask> getNextTask()
	{
		return Option.of(queryFactory.select(downloadTaskProjection)
				.from(table)
				.where(table.scheduleTime.before(Instant.now())
						.and(table.status.eq(DownloadStatus.CREATED)))
				.orderBy(table.scheduleTime.asc())
				.fetchFirst());
	}

	@Override
	public Seq<DownloadTask> getTasks()
	{
		return List.ofAll(queryFactory.select(downloadTaskProjection)
				.from(table)
				.orderBy(table.scheduleTime.desc())
				.fetch());
	}

	@Override
	public Seq<DownloadTask> getTasks(List<DownloadStatus> statuses)
	{
		return List.ofAll(queryFactory.select(downloadTaskProjection)
				.from(table)
				.where(table.status.in(statuses.asJava()))
				.orderBy(table.scheduleTime.asc())
				.fetch());
	}

	@Override
	public DownloadTask insert(DownloadTask task)
	{
		queryFactory.insert(table)
				.set(table.url,task.getUrl())
				.set(table.startDate,task.getStartDate())
				.set(table.endDate,task.getEndDate())
				.set(table.fileId,task.getFileId())
				.set(table.scheduleTime,task.getScheduleTime())
				.set(table.retries,task.getRetries())
				.execute();
		return task;
	}

	@Override
	public long update(DownloadTask task)
	{
		return queryFactory.update(table)
				.set(table.status,task.getStatus())
				.set(table.scheduleTime,task.getScheduleTime())
				.set(table.retries,task.getRetries())
				.where(table.fileId.eq(task.getFileId()))
				.execute();
	}

	@Override
	public long delete(long fileId)
	{
		return queryFactory.delete(table)
				.where(table.fileId.eq(fileId))
				.execute();
	}
}
