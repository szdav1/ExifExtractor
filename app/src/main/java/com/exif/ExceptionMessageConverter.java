package com.exif;

import java.util.Arrays;

public final class ExceptionMessageConverter {
	public static String convertExceptionToString(final Exception exception) {
		StringBuilder builder = new StringBuilder();

		Arrays.asList(exception.getStackTrace())
		.forEach(stackTraceElement -> {
			builder.append(stackTraceElement.getFileName())
			.append(stackTraceElement.getClassName())
			.append(":")
			.append(stackTraceElement.getMethodName())
			.append(":")
			.append(stackTraceElement.getLineNumber())
			.append(":\n");
		});

		return builder.append(exception.getMessage())
			.append("\n")
			.toString();
	}
}