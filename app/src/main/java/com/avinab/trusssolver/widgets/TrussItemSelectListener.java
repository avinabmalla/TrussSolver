package com.avinab.trusssolver.widgets;

import com.avinab.trusssolver.data.Load;
import com.avinab.trusssolver.data.Member;
import com.avinab.trusssolver.data.Node;


/**
 * Avinab
 * 11/15/2014.
 */
public interface TrussItemSelectListener
{
	boolean onNodePressed(Node n);

	boolean onNodeLongPressed(Node n);

	boolean onMemberPressed(Member m);

	boolean onMemberlongPressed(Member m);

	boolean onPointLoadPressed(Load dl);

	void onTouchWithoutSelection();

	void onLongTouchWithoutSelection();
}
