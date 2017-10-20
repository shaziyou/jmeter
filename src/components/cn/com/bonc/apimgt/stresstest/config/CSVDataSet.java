package cn.com.bonc.apimgt.stresstest.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.util.JOrphanUtils;

public class CSVDataSet extends ConfigTestElement implements TestBean, LoopIterationListener {

	private static final long serialVersionUID = -3279565957280761984L;

	private List<Map<String, String>> variablesList = new ArrayList<Map<String, String>>();

	private transient static final Charset FILE_ENCODE_UTF_8 = Charset.forName("UTF-8");

	private transient static final String DELIMITE = ",";

	private CSVDataSet() {
	}

	public static CSVDataSet generateCSVDateSet(InputStream input, String variableNames) throws IOException {
		CSVDataSet dataSet = new CSVDataSet();
		Reader reader = new InputStreamReader(input, FILE_ENCODE_UTF_8);
		BufferedReader bufferReader = new BufferedReader(reader);
		String[] vars = JOrphanUtils.split(variableNames, DELIMITE);
		String buff = null;
		while ((buff = bufferReader.readLine()) != null) {
			if (buff.isEmpty())
				continue;
			String[] values = JOrphanUtils.split(buff, DELIMITE);
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < vars.length; i++) {
				if (i < values.length)
					map.put(vars[i], values[i]);
				else
					map.remove(vars[i]);
			}
			dataSet.variablesList.add(map);
		}
		return dataSet;
	}

	@Override
	public void iterationStart(LoopIterationEvent iterEvent) {
		JMeterContext context = getThreadContext();
		JMeterVariables variables = context.getVariables();
		Random random = new Random();
		int listSize = variablesList.size();
		if (0 == listSize)
			return;
		int index = random.nextInt(listSize);
		Map<String, String> variable = variablesList.get(index);
		variables.putAll(variable);
	}

	@Override
	public Object clone() {
		CSVDataSet clonedObj;
		try {
			clonedObj = this.getClass().newInstance();
			PropertyIterator iter = propertyIterator();
			while (iter.hasNext()) {
				clonedObj.setProperty(iter.next().clone());
			}
			clonedObj.setRunningVersion(this.isRunningVersion());
			clonedObj.variablesList = this.variablesList;// TODO 是否需要克隆该 List
			return clonedObj;
		} catch (Exception e) {
			throw new AssertionError(e);
		}

	}

}
