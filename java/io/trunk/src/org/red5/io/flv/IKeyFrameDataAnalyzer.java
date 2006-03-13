package org.red5.io.flv;

import java.io.Serializable;


public interface IKeyFrameDataAnalyzer {

	public KeyFrameMeta analyzeKeyFrames();
	
	public static class KeyFrameMeta implements Serializable {
		private static final long serialVersionUID = 5436632873705625365L;
		public int timestamps[];
		public int positions[];
	}
}
