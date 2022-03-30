package com.gidi.bio_console.progress;

public interface ProgressListener {

	void onProgress (long contentBytes, long contentLength, boolean done);
}
