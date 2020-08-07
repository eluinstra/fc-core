package dev.luin.fc.core.upload;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;

import dev.luin.fc.core.file.FileSystem;
import dev.luin.fc.core.transaction.TransactionTemplate;
import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusExecutor;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UploadTaskHandler
{
	private static final int chunkSize = 1024 * 1024 * 100;
	private static int maxRetries = Integer.MAX_VALUE;
	@NonNull
	SSLFactoryManager sslFactoryManager;
	@NonNull
	FileSystem fs;
	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	UploadTaskManager uploadTaskManager;

	@Scheduled(fixedDelayString = "${uploadTaskHandler.delay}")
	public void run()
	{
		val task = uploadTaskManager.getNextTask();
		task.map(t -> Try.of(() -> run(t)).onFailure(e -> log.error("",e)));
	}

	public Future<Void> run(UploadTask task) throws ProtocolException, IOException
	{
		log.info("Start task " + task);
		val file = fs.findFile(task.getFileId()).getOrElseThrow(() -> new IllegalStateException("File " + task.getFileId() + " not found!"));
		val client = new Client(sslFactoryManager.getSslSocketFactory());
		client.setUploadCreationURL(task.getCreationUrl());
		client.enableResuming(uploadTaskManager);
		val upload = Try.of(() -> new TusUpload(file.getFile())).get();
		upload.setFingerprint(file.getId().toString());
		log.info("Start uploading %i",file.getId());
		TusExecutor executor = new TusExecutor()
		{
			@Override
			protected void makeAttempt() throws ProtocolException, IOException
			{
				TusUploader uploader = client.resumeOrCreateUpload(upload);
				uploader.setChunkSize(chunkSize);
				do
				{
					if (log.isDebugEnabled())
						log.debug("Upload %i at %06.2f%%",file.getId(),getProgress(upload,uploader));
				} while (uploader.uploadChunk() > -1);
				file.withUrl(uploader.getUploadURL());
				CheckedRunnable runnable = () ->
				{
					fs.updateFile(file);
					uploadTaskManager.deleteTask(task.getFileId());
					uploader.finish();
				};
				transactionTemplate.executeTransaction(runnable);
				log.info("Upload %i finished",file.getId());
				log.debug("Upload available at: %s",uploader.getUploadURL().toString());
			}
		};
		try
		{
			if (!executor.makeAttempts())
			{
				if (task.getRetries() < maxRetries)
					uploadTaskManager.updateTask(createNextTask(task));
				else
					uploadTaskManager.deleteTask(task.getFileId());
			}
		}
		catch (Exception e)
		{
			uploadTaskManager.updateTask(createNextTask(task));
		}
		log.info("End task " + task);
		return new AsyncResult<Void>(null);
	}

	private double getProgress(final TusUpload upload, TusUploader uploader)
	{
		long totalBytes = upload.getSize();
		long bytesUploaded = uploader.getOffset();
		return (double)bytesUploaded / totalBytes * 100;
	}

	private UploadTask createNextTask(UploadTask task)
	{
		return task
				.withScheduleTime(task.getScheduleTime().plus(Duration.ofSeconds((task.getRetries() + 1) * 1800)))
				.withRetries(task.getRetries() + 1);
	}
}
