/* ============================================================
 * JRobin : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.jrobin.org
 * Project Lead:  Sasa Markovic (saxon@jrobin.org);
 *
 * (C) Copyright 2003-2005, by Sasa Markovic.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * Developers:    Sasa Markovic (saxon@jrobin.org)
 *
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package org.jrobin.core;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;

/**
 * JRobin backend which is used to store RRD data to ordinary files on the disk. This backend
 * is SAFE: it locks the underlying RRD file during update/fetch operations, and caches only static
 * parts of a RRD file in memory. Therefore, this backend is safe to be used when RRD files should
 * be shared between several JVMs at the same time. However, this backend is a little bit slow
 * since it does not use fast java.nio.* package (it's still based on the RandomAccessFile class).
 */
public class RrdSafeFileBackend extends RrdFileBackend {
	private static final Counters counters = new Counters();

	private FileLock lock;

	/**
	 * Creates RrdFileBackend object for the given file path, backed by RandomAccessFile object.
	 *
	 * @param path Path to a file
	 * @throws IOException Thrown in case of I/O error
	 */
	public RrdSafeFileBackend(String path, long lockWaitTime, long lockRetryPeriod)
			throws IOException {
		super(path, false);
		try {
			lockFile(lockWaitTime, lockRetryPeriod);
		}
		catch (IOException ioe) {
			super.close();
			throw ioe;
		}
	}

	private void lockFile(long lockWaitTime, long lockRetryPeriod) throws IOException {
		long entryTime = System.currentTimeMillis();
		FileChannel channel = file.getChannel();
		lock = channel.tryLock(0, Long.MAX_VALUE, false);
		if (lock != null) {
			counters.registerQuickLock();
			return;
		}
		do {
			try {
				Thread.sleep(lockRetryPeriod);
			}
			catch (InterruptedException e) {
				// NOP
			}
			lock = channel.tryLock(0, Long.MAX_VALUE, false);
			if (lock != null) {
				counters.registerDelayedLock();
				return;
			}
		} while (System.currentTimeMillis() - entryTime <= lockWaitTime);
		counters.registerError();
		throw new IOException("Could not obtain exclusive lock on file: " + getPath() +
				"] after " + lockWaitTime + " milliseconds");
	}

	public void close() throws IOException {
		try {
			if (lock != null) {
				lock.release();
				lock = null;
				counters.registerUnlock();
			}
		}
		finally {
			super.close();
		}
	}

	/**
	 * Defines the caching policy for this backend.
	 *
	 * @return <code>false</code>
	 */
	protected boolean isCachingAllowed() {
		return false;
	}

	public static String getLockInfo() {
		return counters.getInfo();
	}

	static class Counters {
		long locks, quickLocks, unlocks, locked, errors;

		synchronized void registerQuickLock() {
			locks++;
			quickLocks++;
			locked++;
		}

		synchronized void registerDelayedLock() {
			locks++;
			locked++;
		}

		synchronized void registerUnlock() {
			unlocks++;
			locked--;
		}

		synchronized void registerError() {
			errors++;
		}

		synchronized String getInfo() {
			return "LOCKS=" + locks + ", " + "UNLOCKS=" + unlocks + ", " +
					"DELAYED_LOCKS=" + (locks - quickLocks) + ", " + "LOCKED=" + locked + ", " +
					"ERRORS=" + errors;
		}
	}
}
