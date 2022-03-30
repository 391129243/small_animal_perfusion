package com.gidi.bio_console.progress;


public class ProgressBean{

	private long currentBytes;
	
	private long contentLength;
	
	private boolean done;
	
	public ProgressBean(long contentbytes, long contentlength, boolean done){
		this.currentBytes = contentbytes;
		this.contentLength = contentlength;
		this.done = done;
	}
	
	public long getCurrentBytes() {
		return currentBytes;
	}
	public void setCurrentBytes(long currentBytes) {
		this.currentBytes = currentBytes;
	}
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	
	
	
}
