/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.html.path2;

import java.util.Arrays;

/**
 * Author : Nhu Dinh Thuan Email:nhudinhthuan@yahoo.com Aug 15, 2006
 */
public class NodePath {

	protected INode[] nodes;

	public NodePath(INode[] nodes) {
		this.nodes = nodes;
	}

	public INode[] getNodes() {
		return nodes;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (INode node : nodes) {
			if (builder.length() > 0)
				builder.append('.');
			node.buildString(builder);
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(nodes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodePath other = (NodePath) obj;
		if (!Arrays.equals(nodes, other.nodes))
			return false;
		return true;
	}

}
