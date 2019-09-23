package cc.co.llabor.system;

import java.io.IOException;

import org.jrobin.core.RrdException;

public abstract class Merger {
	public abstract boolean merge(String rrdname) throws IOException, RrdException;
}
