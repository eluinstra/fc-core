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
package dev.luin.file.client.core.upload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;

import dev.luin.file.client.core.file.FSFile;
import dev.luin.file.client.core.file.FileSystem;
import dev.luin.file.client.core.transaction.TransactionException;
import dev.luin.file.client.core.transaction.TransactionTemplate;
import io.tus.java.client.ProtocolException;
import io.tus.java.client.TusExecutor;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;
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
	@NonNull
	SSLFactoryManager sslFactoryManager;
	@NonNull
	FileSystem fs;
	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	UploadTaskManager uploadTaskManager;
	int maxRetries;

	@Scheduled(fixedDelayString = "${uploadTaskHandler.delay}")
	public void run()
	{
		val task = uploadTaskManager.getNextTask();
		task.map(t -> Try.of(() -> run(t)).onFailure(e -> log.error("",e)));
	}

	public Future<Void> run(UploadTask task) throws ProtocolException, IOException
	{
		log.info("Start task {}",task);
		val file = fs.findFile(task.getFileId()).getOrElseThrow(() -> new IllegalStateException("File " + task.getFileId() + " not found"));
		val client = new Client(sslFactoryManager.getSslSocketFactory());
		client.setUploadCreationURL(task.getCreationUrl());
		client.enableResuming(uploadTaskManager);
		val upload = Try.of(() -> new TusUpload(file.getFile())).get();
		upload.setFingerprint(task.getFileId().toString());
		upload.setMetadata(createMetaData(file));
		log.info("Uploading {}",file);
		val executor = new TusExecutor()
		{
			@Override
			protected void makeAttempt() throws ProtocolException, IOException
			{
				val uploader = client.resumeOrCreateUpload(upload);
				do
				{
					if (log.isDebugEnabled())
						log.debug("Upload {} at {}%",file,getProgress(upload,uploader));
				} while (uploader.uploadChunk() > -1);
				val newFile = file.withUrl(uploader.getUploadURL());
				Runnable runnable = () ->
				{
					try
					{
						fs.updateFile(newFile);
						uploadTaskManager.createSucceededTask(task);
						uploader.finish();
					}
					catch (ProtocolException | IOException e)
					{
						throw new TransactionException(e);
					}
				};
				transactionTemplate.executeTransaction(runnable);
				log.info("Uploaded {}",newFile);
			}
		};
		try
		{
			if (!executor.makeAttempts())
			{
				if (task.getRetries() < maxRetries)
					uploadTaskManager.createNextTask(task);
				else
					uploadTaskManager.createFailedTask(task);
			}
		}
		catch (Exception e)
		{
			uploadTaskManager.createNextTask(task);
		}
		log.info("Finished task {}",task);
		return new AsyncResult<Void>(null);
	}

	private Map<String,String> createMetaData(FSFile file)
	{
		val result = new HashMap<String,String>();
		result.put("filename",file.getName());
		result.put("content-type",file.getContentType());
		return result;
	}

	private double getProgress(final TusUpload upload, TusUploader uploader)
	{
		val totalBytes = upload.getSize();
		val bytesUploaded = uploader.getOffset();
		return (double)bytesUploaded / totalBytes * 100;
	}
}