package com.xixi.bet.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	CustomThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "jobs线程组-" + poolNumber.getAndIncrement() + "-";
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(group, r, namePrefix+threadNumber.getAndIncrement());
	}

}
