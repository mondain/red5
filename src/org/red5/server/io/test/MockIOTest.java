package org.red5.server.io.test;

import java.util.LinkedList;
import java.util.List;

import org.red5.server.io.mock.Input;
import org.red5.server.io.mock.Mock;
import org.red5.server.io.mock.Output;

public class MockIOTest extends AbstractIOTest {

	protected List list;
	
	void setupIO() {
		list = new LinkedList();
		in = new Input(list);
		out = new Output(list);
	}
	
	void dumpOutput() {
		System.out.println(Mock.listToString(list));
	}

	void resetOutput() {
		setupIO();
	}

}
