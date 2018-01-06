/*************************************************************************** 
   Copyright 2017 Federico Ricca
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/
package org.koinkoin.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.knowm.xchange.dto.marketdata.Ticker;

/**
 * Warning: this class is NOT thread safe.
 * 
 * @author fede
 *
 */
public class MarketDataLog {
	private static final int MIN_FLUSH_CYCLE = 10;

	private String exchangeId;
	private Map<String, StreamInfo> logs;
	private AtomicBoolean writing;

	class StreamInfo {
		public PrintStream stream;
		public int flushCycle;
		public long records;
	}

	public MarketDataLog(String exchangeId) {
		this.exchangeId = exchangeId;
		logs = new HashMap<>();
		writing = new AtomicBoolean(false);
	}

	public void add(List<Ticker> tickers) throws IOException {
		writing.set(true);

		try {
			for (Ticker t : tickers) {
				String pairName = t.getCurrencyPair().base + "_" + t.getCurrencyPair().counter;

				StreamInfo streamInfo = logs.get(pairName);

				if (streamInfo == null) {
					streamInfo = new StreamInfo();
					streamInfo.stream = initPrintStream(exchangeId + "-" + pairName);
					streamInfo.flushCycle = MIN_FLUSH_CYCLE;
					streamInfo.records = 0;

					logs.put(pairName, streamInfo);
				}

				Date d = GregorianCalendar.getInstance().getTime();

				streamInfo.stream.println(d + ", " + t.getBid() + ", " + t.getAsk() + ", " + t.getHigh() + ", " + t.getLow());
				streamInfo.records++;

				if (streamInfo.records % streamInfo.flushCycle == 0) {
					streamInfo.stream.flush();
				}
			}
		} finally {
			writing.set(false);
		}
	}

	private PrintStream initPrintStream(String name) throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

		File folder = new File("data/" + simpleDateFormat.format(GregorianCalendar.getInstance().getTime()));
	
		folder.mkdirs();
		
		String fileName = new StringBuilder(folder.getAbsolutePath()).append(name).append("-")
				.append(simpleDateFormat.format(GregorianCalendar.getInstance().getTime())).append(".csv").toString();

		File file = new File(fileName);

		PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

		return stream;
	}

	public void close() {
		while (writing.get())
			;

		for (StreamInfo streamInfo : logs.values()) {
			streamInfo.stream.close();
		}

		logs.clear();
	}
}
