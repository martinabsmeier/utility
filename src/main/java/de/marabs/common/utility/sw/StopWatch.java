/*
 * Copyright 2022 Martin Absmeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.marabs.common.utility.sw;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of a stop watch.
 * 
 * <p>
 * Note that "StopWatch" is not designed to be thread-safe and does not use synchronization.
 * </p>
 * 
 * @author Martin Absmeier
 */
public class StopWatch {

	private enum State {
		NOT_RUNNING, RUNNING, STOPPED;
	}

	private class Task {

		private long startTime;

		private State state;

		private long stopTime;

		private String taskName;

		protected Task(String taskName) {
			this.taskName = taskName;
			this.state = State.NOT_RUNNING;
		}

		protected String getTaskName() {
			return taskName;
		}

		protected long getTimeInMillis() {
			return stopTime - startTime;
		}

		protected double getTimeInSeconds() {
			return Double.valueOf(getTimeInMillis()) / 1000D;
		}

		protected boolean isRunning() {
			return State.RUNNING == state;
		}

		protected void start() {
			this.state = State.RUNNING;
			this.startTime = System.currentTimeMillis();
		}

		protected void stop() {
			this.state = State.STOPPED;
			this.stopTime = System.currentTimeMillis();
		}
	}

	private String stopWatchName;

	private List<Task> tasks = new ArrayList<>();

	private Task currentTask = null;

	public StopWatch() {
		this("");
	}

	public StopWatch(String stopWatchName) {
		super();
		this.stopWatchName = stopWatchName;
	}

	public long getTotalTimeInMillis() {
		long totalTimeInMillis = 0;
		for (Task aTask : tasks) {
			totalTimeInMillis += aTask.getTimeInMillis();
		}
		return totalTimeInMillis;
	}

	public double getTotalTimeInSeconds() {
		return Double.valueOf(getTotalTimeInMillis()) / 1000D;
	}

	public String prettyPrint() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(7);
		nf.setGroupingUsed(false);

		NumberFormat pf = NumberFormat.getPercentInstance();
		pf.setMinimumIntegerDigits(3);
		pf.setGroupingUsed(true);

		String nl = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder("StopWatch:");
		if (stopWatchName != null && stopWatchName.trim().length() > 0) {
			sb.append(" ").append(stopWatchName);
		}
		sb.append(" totalRunningTime = ").append(getTotalTimeInMillis()).append(" ms").append(nl);
		sb.append("     ms     %  TaskName").append(nl);
		sb.append("-------------------------------------------------------------------------------").append(nl);

		for (Task aTask : tasks) {
			if (aTask.isRunning()) {
				throw new IllegalStateException("Task " + aTask.getTaskName()
						+ " is running, please stop it before calling pretty print.");
			}
			sb.append(nf.format(aTask.getTimeInMillis())).append("  ");
			sb.append(pf.format(aTask.getTimeInSeconds() / getTotalTimeInSeconds())).append("  ");
			sb.append(aTask.getTaskName()).append(nl);
		}

		return sb.toString();
	}

	public void start() {
		start("");
	}

	public void start(String taskName) throws IllegalStateException {
		if (currentTask == null || !currentTask.isRunning()) {
			currentTask = new Task(taskName);
			currentTask.start();
		} else {
			throw new IllegalStateException("Can't start StopWatch: it's already running!");
		}
	}

	public void stop() throws IllegalStateException {
		if (currentTask == null || !currentTask.isRunning()) {
			throw new IllegalStateException("Can't start StopWatch: it's not running!");
		} else {
			currentTask.stop();
			tasks.add(currentTask);
			currentTask = null;
		}
	}
}
