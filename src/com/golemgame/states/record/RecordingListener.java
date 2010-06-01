package com.golemgame.states.record;

public interface RecordingListener {
	public void frameDrawn(RecordingSession session,RecordingManager source);
	public void recordingState(boolean recording, boolean paused);
}
