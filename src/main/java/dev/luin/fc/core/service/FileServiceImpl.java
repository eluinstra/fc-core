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
package dev.luin.fc.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.activation.DataHandler;

import dev.luin.fc.core.download.DownloadStatus;
import dev.luin.fc.core.download.DownloadTaskManager;
import dev.luin.fc.core.file.FSFile;
import dev.luin.fc.core.file.FileSystem;
import dev.luin.fc.core.service.model.DownloadTask;
import dev.luin.fc.core.service.model.DownloadTaskMapper;
import dev.luin.fc.core.service.model.File;
import dev.luin.fc.core.service.model.FileInfo;
import dev.luin.fc.core.service.model.FileInfoMapper;
import dev.luin.fc.core.service.model.FileMapper;
import dev.luin.fc.core.service.model.UploadTask;
import dev.luin.fc.core.service.model.UploadTaskMapper;
import dev.luin.fc.core.transaction.TransactionTemplate;
import dev.luin.fc.core.upload.UploadStatus;
import dev.luin.fc.core.upload.UploadTaskManager;
import io.vavr.Function0;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
class FileServiceImpl implements FileService
{
	@NonNull
	TransactionTemplate transactionTemplate;
	@NonNull
	FileSystem fs;
	@NonNull
	UploadTaskManager uploadTaskManager;
	@NonNull
	DownloadTaskManager downloadTaskManager;

	@Override
	public UploadTask uploadFile(@NonNull final File file, final String creationUrl) throws ServiceException
	{
		return Try.of(() -> 
		{
			Function0<UploadTask> transaction = () ->
			{
				try
				{
					val fsFile = createFile(file);
					val task = uploadTaskManager.createTask(fsFile.getId(),creationUrl);
					return UploadTaskMapper.INSTANCE.toUploadTask(task);
				}
				catch (Exception e)
				{
					throw new ServiceException(e);
				}
			};
			return transactionTemplate.executeTransactionWithResult(transaction);
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public DownloadTask downloadFile(final String url) throws ServiceException
	{
		return Try.of(() -> 
		{
			Function0<DownloadTask> transaction = () ->
			{
				try
				{
					val fsFile = fs.createEmptyFile(url);
					val task = downloadTaskManager.createTask(fsFile.getId(),url);
					return DownloadTaskMapper.INSTANCE.toDownloadTask(task);
				}
				catch (IOException e)
				{
					throw new ServiceException(e);
				}
			};
			return transactionTemplate.executeTransactionWithResult(transaction);
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public File getFile(Long id) throws ServiceException
	{
		return Try.of(() ->
		{
			val fsFile = fs.findFile(id);
			val dataSource = fsFile.map(f -> fs.createDataSource(f));
			return fsFile.filter(f -> f.isCompleted())
					.flatMap(f -> dataSource.map(d -> FileMapper.INSTANCE.toFile(f,new DataHandler(d))))
					.getOrElseThrow(() -> new ServiceException("File " + id + " not found!"));
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public List<UploadTask> getUploadTasks(List<UploadStatus> status) throws ServiceException
	{
		return Try.of(() -> 
		{
			return uploadTaskManager.getTasks(status != null ? io.vavr.collection.List.ofAll(status) : io.vavr.collection.List.empty())
					.map(t -> UploadTaskMapper.INSTANCE.toUploadTask(t))
					.asJava();
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public void deleteUploadTask(Long fileId) throws ServiceException
	{
		Try.of(() -> 
		{
			Runnable transaction = () ->
			{
				try
				{
					val fsFile = fs.findFile(fileId).getOrElseThrow(() -> new FileNotFoundException("File " + fileId + " not found"));
					fs.deleteFile(fsFile,true);
					uploadTaskManager.deleteTask(fileId);
				}
				catch (FileNotFoundException e)
				{
					throw new ServiceException(e);
				}
			};
			transactionTemplate.executeTransaction(transaction);
			return null;
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public List<DownloadTask> getDownloadTasks(List<DownloadStatus> status) throws ServiceException
	{
		return Try.of(() -> 
		{
			return downloadTaskManager.getTasks(status != null ? io.vavr.collection.List.ofAll(status) : io.vavr.collection.List.empty())
					.map(t -> DownloadTaskMapper.INSTANCE.toDownloadTask(t))
					.asJava();
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public void deleteDownloadTask(Long fileId) throws ServiceException
	{
		Try.of(() -> 
		{
			Runnable transaction = () ->
			{
				try
				{
					val fsFile = fs.findFile(fileId).getOrElseThrow(() -> new FileNotFoundException("File " + fileId + " not found"));
					fs.deleteFile(fsFile,true);
					downloadTaskManager.deleteTask(fileId);
				}
				catch (FileNotFoundException e)
				{
					throw new ServiceException(e);
				}
			};
			transactionTemplate.executeTransaction(transaction);
			return null;
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	@Override
	public List<FileInfo> getFiles() throws ServiceException
	{
		return Try.of(() -> 
		{
			val fsFile = fs.getFiles();
			return fsFile.map(f -> FileInfoMapper.INSTANCE.toFileInfo(f))
					.asJava();
		})
		.getOrElseThrow(ServiceException.defaultExceptionProvider);
	}

	private FSFile createFile(final File file) throws IOException
	{
		return fs.createFile(file.getName(),file.getContentType(),file.getSha256Checksum(),file.getStartDate(),file.getEndDate(),file.getContent().getInputStream());
	}
}
